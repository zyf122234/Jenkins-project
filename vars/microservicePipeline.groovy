/**
 * 微服务 Jenkins 共享流水线脚本
 *
 * 提供可复用的构建、测试、部署、通知函数
 * 版本: 2.0 - 修复端口硬编码 bug + 添加企业微信通知
 */

// ============================================
// 服务配置
// ============================================

// 服务端口映射
def getPort(String serviceName) {
    def config = [
        'gateway-service': 8080,
        'user-service': 8081,
        'order-service': 8082
    ]
    return config[serviceName] ?: 8080
}

// 获取所有服务列表
def getAllServices() {
    return ['gateway-service', 'user-service', 'order-service']
}

// ============================================
// 构建相关函数
// ============================================

// 构建单个服务
def buildService(String serviceName, String pomPath = '.') {
    echo "构建服务: ${serviceName}"
    sh "mvn clean package -DskipTests -f ${pomPath}/pom.xml -pl ${serviceName}"
}

// 测试单个服务
def testService(String serviceName, String pomPath = '.') {
    echo "测试服务: ${serviceName}"
    sh "mvn test -f ${pomPath}/pom.xml -pl ${serviceName}"
}

// ============================================
// Docker 相关函数
// ============================================

// 构建 Docker 镜像
def buildDockerImage(String serviceName, String tag, String contextPath = '.') {
    def imageName = "${env.IMAGE_PREFIX ?: 'microservice-demo'}/${serviceName}"
    echo "构建 Docker 镜像: ${imageName}:${tag}"
    sh "docker build -t ${imageName}:${tag} -t ${imageName}:latest ${contextPath}/${serviceName}/"
    return "${imageName}:${tag}"
}

// 推送 Docker 镜像
def pushDockerImage(String imageName, String tag) {
    echo "推送镜像: ${imageName}:${tag}"
    sh "docker push ${imageName}:${tag}"
    sh "docker push ${imageName}:latest"
}

// ============================================
// 部署相关函数
// ============================================

// 部署服务（使用直接 docker 命令）
def deployService(String serviceName, String dockerNet = 'jenkins-docker_microservice-net') {
    def port = getPort(serviceName)
    def imageName = "${env.IMAGE_PREFIX ?: 'microservice-demo'}/${serviceName}"

    echo "部署服务: ${serviceName} (端口: ${port})"

    sh "docker stop ${serviceName} || true"
    sh "docker rm ${serviceName} || true"
    sh "docker run -d --name ${serviceName} --network ${dockerNet} -e SPRING_PROFILES_ACTIVE=docker -p ${port}:${port} ${imageName}:latest"
}

// 部署所有服务
def deployAllServices(List services, String dockerNet = 'jenkins-docker_microservice-net') {
    echo "部署所有服务..."
    for (service in services) {
        deployService(service, dockerNet)
    }
}

// ============================================
// 健康检查函数（已修复端口硬编码 bug）
// ============================================

// 检查服务健康状态
def checkServiceHealth(String serviceName, int timeout = 60) {
    def port = getPort(serviceName)

    echo "检查服务健康状态: ${serviceName} (端口: ${port}, 超时: ${timeout}s)"

    sh """
        for i in \$(seq 1 ${timeout}); do
            if docker exec ${serviceName} curl -sf http://localhost:${port}/actuator/health > /dev/null 2>&1; then
                echo "${serviceName} 启动成功 (端口: ${port})"
                exit 0
            fi
            echo "等待 ${serviceName} 启动... (\${i}/${timeout})"
            sleep 2
        done
        echo "${serviceName} 启动超时"
        exit 1
    """
}

// 批量检查所有服务健康状态
def checkAllServicesHealth(List services) {
    echo "检查所有服务健康状态..."
    def failedServices = []

    for (service in services) {
        try {
            checkServiceHealth(service)
        } catch (Exception e) {
            failedServices.add(service)
        }
    }

    if (failedServices) {
        error "以下服务健康检查失败: ${failedServices.join(', ')}"
    }

    echo "所有服务健康检查通过"
}

// ============================================
// 等待 Nacos 就绪
// ============================================

def waitForNacos(int timeout = 30) {
    echo "等待 Nacos 就绪..."
    sh """
        for i in \$(seq 1 ${timeout}); do
            if curl -sf http://nacos:8848/nacos/v1/console/health/readiness > /dev/null 2>&1; then
                echo "Nacos 已就绪"
                exit 0
            fi
            echo "等待 Nacos 启动... (\${i}/${timeout})"
            sleep 2
        done
        echo "Nacos 启动超时"
        exit 1
    """
}

// ============================================
// 企业微信通知函数
// ============================================

/**
 * 发送企业微信通知
 * @param status 构建状态: START, SUCCESS, FAILURE
 * @param webhookUrl 企业微信 Webhook URL
 * @param additionalInfo 附加信息
 */
def notifyWeChatWork(String status, String webhookUrl, Map additionalInfo = [:]) {
    if (!webhookUrl) {
        echo "警告: 未配置企业微信 Webhook URL，跳过通知"
        return
    }

    def buildNumber = env.BUILD_NUMBER ?: 'unknown'
    def jobName = env.JOB_NAME ?: 'unknown'
    def buildUrl = env.BUILD_URL ?: '#'
    def branch = additionalInfo.branch ?: env.BRANCH_NAME ?: 'unknown'
    def services = additionalInfo.services ?: []
    def duration = additionalInfo.duration ?: 'unknown'
    def failureReason = additionalInfo.failureReason ?: ''

    // 构建状态图标
    def statusInfo = [
        'START':   [icon: '🚀', title: '构建开始'],
        'SUCCESS': [icon: '✅', title: '构建成功'],
        'FAILURE': [icon: '❌', title: '构建失败']
    ]

    def info = statusInfo[status] ?: [icon: '❓', title: '未知状态']

    // 构建 Markdown 消息
    def markdownContent = "## ${info.icon} ${info.title}\n\n"
    markdownContent += "**项目名称**: ${jobName}\n"
    markdownContent += "**构建编号**: #${buildNumber}\n"
    markdownContent += "**分支**: ${branch}\n"
    markdownContent += "**状态**: ${status}\n"
    markdownContent += "**时间**: ${new Date().format('yyyy-MM-dd HH:mm:ss')}\n"

    if (services) {
        markdownContent += "\n### 构建详情\n"
        markdownContent += "**涉及服务**: ${services.join(', ')}\n"
    }

    if (duration != 'unknown') {
        markdownContent += "**构建耗时**: ${duration}\n"
    }

    if (status == 'FAILURE' && failureReason) {
        markdownContent += "\n### 失败原因\n${failureReason}\n"
    }

    markdownContent += "\n**构建链接**: [点击查看](${buildUrl})\n"
    markdownContent += "\n---\n*此消息由 Jenkins 自动发送*"

    // 失败时 @all 提醒
    if (status == 'FAILURE') {
        markdownContent += "\n<@all>"
    }

    // 构建请求体
    def requestBody = [
        msgtype: 'markdown',
        markdown: [
            content: markdownContent
        ]
    ]

    def jsonBody = groovy.json.JsonOutput.toJson(requestBody)

    echo "发送企业微信通知: ${info.title}"
    // 使用宿主机网络执行 curl，解决容器内无法访问外网的问题
    sh """
        docker run --rm --network host curlimages/curl:latest \\
            curl -s -X POST '${webhookUrl}' \\
            -H 'Content-Type: application/json' \\
            -d '${jsonBody}'
    """
}

// 发送构建开始通知
def notifyBuildStart(String webhookUrl, List services = [], String branch = 'unknown') {
    notifyWeChatWork('START', webhookUrl, [
        services: services,
        branch: branch
    ])
}

// 发送构建成功通知
def notifyBuildSuccess(String webhookUrl, List services = [], String duration = 'unknown') {
    notifyWeChatWork('SUCCESS', webhookUrl, [
        services: services,
        duration: duration
    ])
}

// 发送构建失败通知
def notifyBuildFailure(String webhookUrl, String failureReason = '', String duration = 'unknown') {
    notifyWeChatWork('FAILURE', webhookUrl, [
        failureReason: failureReason,
        duration: duration
    ])
}

// ============================================
// Git 相关函数
// ============================================

// Git 拉取代码
def checkoutCode(String branch = 'main') {
    echo "拉取代码: branch=${branch}"
    checkout([
        $class: 'GitSCM',
        branches: [[name: "*/${branch}"]],
        extensions: [],
        userRemoteConfigs: [[
            url: 'https://github.com/your-org/your-repo.git',
            credentialsId: 'git-credentials'
        ]]
    ])
}

return this
