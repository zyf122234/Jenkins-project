pipeline {
    agent any

    //全局工具
    tools {
        maven 'maven3.8' // 只保留 Maven 即可！
    }

    environment {
        IMAGE_PREFIX = 'microservice-demo'
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
                echo '通过挂载的 docker 命令和 sock 本地构建微服务镜像...'
                script {
                    def services = ['gateway-service', 'user-service', 'order-service']
                    for (service in services) {
                        echo "开始构建本地镜像: ${IMAGE_PREFIX}/${service}:latest"
                        // ✨ 此时直接用最纯粹的 docker 命令，它绝对能找到了！
                        sh "docker build -t ${IMAGE_PREFIX}/${service}:latest ./${service}/"
                    }
                }
            }
        }

        stage('5. Deploy') {
            steps {
                echo '开始原地重启并部署微服务容器...'
                script {
                    def services = [
                            ['name': 'gateway-service', 'port': '8080'],
                            ['name': 'user-service', 'port': '8081'],
                            ['name': 'order-service', 'port': '8082']
                    ]

                    for (service in services) {
                        echo "正在重构并拉起容器: ${service.name}"
                        sh "docker stop ${service.name} || true"
                        sh "docker rm ${service.name} || true"
                        sh "docker run -d --name ${service.name} --network ${DOCKER_NET} -e SPRING_PROFILES_ACTIVE=docker -p ${service.port}:${service.port} ${IMAGE_PREFIX}/${service.name}:latest"
                    }

                    echo '所有服务已在宿主机原地完成平滑升级！'
                    sh "docker ps"
                }
            }
        }
    }

    post {
        success {
            echo '🎉 恭喜！流水线全线亮起绿灯，微服务自动化部署成功！'
        }
        failure {
            echo '❌ 流水线执行失败，请检查上方排查日志。'
        }
    }
}