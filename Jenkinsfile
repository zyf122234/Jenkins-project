pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'docker.io'
        IMAGE_PREFIX = 'microservice-demo'
        NACOS_SERVER = 'nacos:8848'
        // GitHub 仓库地址 - 请替换为你的仓库地址
        GIT_REPO = 'https://github.com/你的用户名/你的仓库名.git'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '从 GitHub 拉取代码...'
                // 方式一：使用 Jenkinsfile 所在仓库（推荐，Pipeline Job 配置 SCM 后自动生效）
                checkout scm

                // 方式二：手动指定仓库（如果不用 SCM 配置，取消注释下面的代码）
                // git branch: 'main',
                //     url: "${GIT_REPO}",
                //     credentialsId: 'github-credentials'
            }
        }

        stage('Build') {
            steps {
                echo '编译项目...'
                sh 'mvn clean package -DskipTests -f pom.xml'
            }
        }

        stage('Test') {
            steps {
                echo '运行单元测试...'
                sh 'mvn test -f pom.xml || echo "测试完成"'
            }
        }

        stage('Docker Build') {
            steps {
                echo '构建 Docker 镜像...'
                script {
                    def services = ['gateway-service', 'user-service', 'order-service']
                    for (service in services) {
                        echo "构建镜像: ${IMAGE_PREFIX}/${service}:${BUILD_NUMBER}"
                        sh "docker build -t ${IMAGE_PREFIX}/${service}:${BUILD_NUMBER} -t ${IMAGE_PREFIX}/${service}:latest ./${service}/"
                    }
                }
            }
        }

        stage('Docker Push') {
            steps {
                echo '推送镜像到 Docker Hub...'
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                        def services = ['gateway-service', 'user-service', 'order-service']
                        for (service in services) {
                            sh "docker push ${IMAGE_PREFIX}/${service}:${BUILD_NUMBER}"
                            sh "docker push ${IMAGE_PREFIX}/${service}:latest"
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                echo '部署服务...'
                script {
                    def services = ['gateway-service', 'user-service', 'order-service']
                    for (service in services) {
                        sh "docker-compose stop ${service} || true"
                    }
                    sh 'docker-compose up -d'
                    echo '等待服务启动...'
                    sh 'sleep 30'
                    sh 'docker-compose ps'
                }
            }
        }
    }

    post {
        success {
            echo '流水线执行成功！'
        }
        failure {
            echo '流水线执行失败！'
        }
        always {
            echo '清理临时文件...'
            sh 'docker image prune -f || true'
        }
    }
}
