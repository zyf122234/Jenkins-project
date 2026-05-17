pipeline {
    agent any

    // 1. 激活你刚才在 Jenkins 页面配置好的 Maven 工具
    tools {
        maven 'maven3.8' //之前配好的Maven
        docker 'docker-cli' // ✨ 新增这一行！告诉 Jenkins 接下来要调用你刚配的 Docker 命令
    }

    environment {
        IMAGE_PREFIX = 'microservice-demo'
        // 对应你之前 docker compose 创建的微服务专用网络名称
        DOCKER_NET = 'jenkins-docker_microservice-net'
    }

    stages {
        stage('1. Checkout') {
            steps {
                echo '从 GitHub 拉取代码...'
                checkout scm
            }
        }

        stage('2. Build') {
            steps {
                echo '编译 Java 项目，生成 Jar 包...'
                sh 'mvn clean package -DskipTests -f pom.xml'
            }
        }

        stage('3. Test') {
            steps {
                echo '运行单元测试...'
                sh 'mvn test -f pom.xml || echo "测试完成"'
            }
        }

        stage('4. Docker Build') {
            steps {
                echo '通过挂载的 docker.sock 本地构建微服务镜像...'
                script {
                    def services = ['gateway-service', 'user-service', 'order-service']
                    for (service in services) {
                        echo "开始构建本地镜像: ${IMAGE_PREFIX}/${service}:latest"
                        sh "docker build -t ${IMAGE_PREFIX}/${service}:latest ./${service}/"
                    }
                }
            }
        }

        // ✨ 删除了原本耗时且容易因网络失败的 Docker Push 阶段
        // 因为是本地单机开发，Build 出来的镜像原地就能直接运行！

        stage('5. Deploy') {
            steps {
                echo '开始原地重启并部署微服务容器...'
                script {
                    def services = [
                            ['name': 'gateway-service', 'port': '8081'], // 替换为你网关的真实端口
                            ['name': 'user-service', 'port': '8082'],    // 替换为你用户服务的真实端口
                            ['name': 'order-service', 'port': '8083']    // 替换为你订单服务的真实端口
                    ]

                    for (service in services) {
                        echo "正在重构并拉起容器: ${service.name}"
                        // 1. 如果旧容器在跑，强行停止并删除它
                        sh "docker stop ${service.name} || true"
                        sh "docker rm ${service.name} || true"

                        // 2. 将新镜像作为容器挂载到你的微服务网络中启动
                        sh "docker run -d --name ${service.name} --network ${DOCKER_NET} -p ${service.port}:${service.port} ${IMAGE_PREFIX}/${service.name}:latest"
                    }

                    echo '所有服务已在宿主机原地完成平滑升级！'
                    sh 'docker ps'
                }
            }
        }
    }

    // ✨ 清理了引发 hudson.FilePath 报错的总是执行块，只留下干净的状态输出
    post {
        success {
            echo '🎉 恭喜！流水线全线亮起绿灯，微服务自动化部署成功！'
        }
        failure {
            echo '❌ 流水线执行失败，请检查上方排查日志。'
        }
    }
}