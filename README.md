# 微服务 Jenkins-Docker 部署实践项目

## 项目简介

本项目是一个基于 Spring Cloud 的微服务架构实践项目，演示了如何使用 Jenkins + Docker 实现微服务的 CI/CD 持续集成与部署。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 微服务框架 |
| Spring Cloud | 2021.0.8 | 微服务治理 |
| Spring Cloud Alibaba | 2021.0.5.0 | 阿里巴巴微服务组件 |
| Nacos | 2.1.2 | 服务发现与配置中心 |
| Spring Cloud Gateway | - | API 网关 |
| OpenFeign | - | 服务间调用 |
| MySQL | 8.0 | 数据库 |
| MyBatis | 2.3.1 | ORM 框架 |
| Druid | 1.2.16 | 数据库连接池 |
| Docker | - | 容器化 |
| Jenkins | LTS | CI/CD 持续集成 |

## 项目结构

```
微服务Jenkins-Docker项目实践/
├── pom.xml                              # 父 POM
├── docker-compose.yml                   # Docker 编排
├── Jenkinsfile                          # Jenkins 流水线
├── README.md                            # 项目说明
├── 架构说明.md                            # 架构设计文档
├── API接口文档.md                          # API 接口文档
├── 操作指南.md                             # 操作使用说明
├── jenkins/vars/                        # Jenkins 共享流水线
│   └── microservicePipeline.groovy
├── sql/init.sql                         # 数据库初始化
├── config/nacos/                        # Nacos 配置
│   ├── gateway-service.yml
│   ├── user-service.yml
│   └── order-service.yml
├── gateway-service/                     # API 网关 (8080)
├── user-service/                        # 用户服务 (8081)
└── order-service/                       # 订单服务 (8082)
```

## 微服务说明

| 服务 | 端口 | 说明 |
|------|------|------|
| gateway-service | 8080 | API 网关，统一入口，JWT 鉴权 |
| user-service | 8081 | 用户管理，CRUD 操作 |
| order-service | 8082 | 订单管理，通过 Feign 调用 user-service |

## 快速开始

### 1. 一键启动（推荐）

```bash
cd 微服务Jenkins-Docker项目实践
docker-compose up -d
```

### 2. 手动构建启动

```bash
# 编译项目
mvn clean package -DskipTests

# 启动基础设施
docker-compose up -d nacos mysql

# 启动微服务
docker-compose up -d gateway-service user-service order-service
```

### 3. 验证服务

```bash
# 检查服务状态
docker-compose ps

# 测试 API
curl http://localhost:8080/api/users
curl http://localhost:8080/api/orders
```

## 服务地址

| 服务 | 地址 |
|------|------|
| Gateway | http://localhost:8080 |
| Nacos 控制台 | http://localhost:8848/nacos |
| Jenkins | http://localhost:9090 |
| MySQL | localhost:3306 |

## 文档

- [架构说明](架构说明.md) - 系统架构设计
- [API 接口文档](API接口文档.md) - REST API 详细说明
- [操作指南](操作指南.md) - 完整部署操作手册
