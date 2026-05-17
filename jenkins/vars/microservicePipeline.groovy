/**
 * 微服务 Jenkins 共享流水线脚本
 *
 * 提供可复用的构建、测试、部署函数
 */

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

// 构建 Docker 镜像
def buildDockerImage(String serviceName, String tag, String contextPath = '.') {
    def imageName = "${IMAGE_PREFIX}/${serviceName}"
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

// 部署服务（重启指定容器）
def deployService(String serviceName) {
    echo "部署服务: ${serviceName}"
    sh "docker-compose stop ${serviceName} || true"
    sh "docker-compose up -d ${serviceName}"
}

// 部署所有服务
def deployAll() {
    echo "部署所有服务"
    sh 'docker-compose down'
    sh 'docker-compose up -d'
}

// 检查服务健康状态
def checkServiceHealth(String serviceName, int timeout = 60) {
    echo "检查服务健康状态: ${serviceName} (超时: ${timeout}s)"
    sh """
        for i in \$(seq 1 ${timeout}); do
            if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
                echo "${serviceName} 启动成功"
                exit 0
            fi
            echo "等待 ${serviceName} 启动... (\${i}/${timeout})"
            sleep 2
        done
        echo "${serviceName} 启动超时"
        exit 1
    """
}

// 通知构建结果
def notifyBuild(String status) {
    echo "构建通知: ${status}"
    // 可扩展为钉钉、企业微信、邮件等通知方式
    // 示例: 企业微信 webhook
    // sh "curl -X POST 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx' -H 'Content-Type: application/json' -d '{\"msgtype\":\"text\",\"text\":{\"content\":\"构建${status}\"}}'"
}

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
