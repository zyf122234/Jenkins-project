# API 接口文档

所有接口通过 Gateway (8080) 统一访问。

## 通用响应格式

```json
{
    "code": 200,
    "message": "success",
    "data": {}
}
```

## 1. 用户接口 (User Service)

### 1.1 查询所有用户

```
GET /api/users
```

**响应示例**:
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "username": "张三",
            "email": "zhangsan@example.com",
            "phone": "13800001111",
            "createTime": "2024-01-01T10:00:00"
        }
    ]
}
```

### 1.2 查询单个用户

```
GET /api/users/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户ID |

**响应示例**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "张三",
        "email": "zhangsan@example.com",
        "phone": "13800001111",
        "createTime": "2024-01-01T10:00:00"
    }
}
```

**错误响应**:
```json
{
    "code": 404,
    "message": "用户不存在, id=1",
    "data": null
}
```

### 1.3 创建用户

```
POST /api/users
```

**请求体**:
```json
{
    "username": "新用户",
    "email": "new@example.com",
    "phone": "13800006666"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "用户创建成功",
    "data": {
        "id": 6,
        "username": "新用户",
        "email": "new@example.com",
        "phone": "13800006666",
        "createTime": "2024-01-15T14:30:00"
    }
}
```

### 1.4 更新用户

```
PUT /api/users/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户ID |

**请求体**:
```json
{
    "username": "更新后的用户名",
    "email": "updated@example.com"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "用户更新成功",
    "data": {
        "id": 1,
        "username": "更新后的用户名",
        "email": "updated@example.com",
        "phone": "13800001111",
        "createTime": "2024-01-01T10:00:00"
    }
}
```

### 1.5 删除用户

```
DELETE /api/users/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户ID |

**响应示例**:
```json
{
    "code": 200,
    "message": "用户删除成功",
    "data": null
}
```

---

## 2. 订单接口 (Order Service)

### 2.1 查询所有订单

```
GET /api/orders
```

**响应示例**:
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "userId": 1,
            "productName": "iPhone 15",
            "quantity": 1,
            "amount": 6999.00,
            "status": "CREATED",
            "createTime": "2024-01-10T09:00:00"
        }
    ]
}
```

### 2.2 查询单个订单（包含用户信息）

```
GET /api/orders/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 订单ID |

**响应示例**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "order": {
            "id": 1,
            "userId": 1,
            "productName": "iPhone 15",
            "quantity": 1,
            "amount": 6999.00,
            "status": "CREATED",
            "createTime": "2024-01-10T09:00:00"
        },
        "user": {
            "id": 1,
            "username": "张三",
            "email": "zhangsan@example.com",
            "phone": "13800001111",
            "createTime": "2024-01-01T10:00:00"
        }
    }
}
```

### 2.3 查询用户的所有订单

```
GET /api/orders/user/{userId}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |

**响应示例**:
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "userId": 1,
            "productName": "iPhone 15",
            "quantity": 1,
            "amount": 6999.00,
            "status": "CREATED",
            "createTime": "2024-01-10T09:00:00"
        }
    ]
}
```

### 2.4 创建订单

```
POST /api/orders
```

**请求体**:
```json
{
    "userId": 1,
    "productName": "Apple Watch",
    "quantity": 1,
    "amount": 3999.00
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "订单创建成功",
    "data": {
        "id": 8,
        "userId": 1,
        "productName": "Apple Watch",
        "quantity": 1,
        "amount": 3999.00,
        "status": "CREATED",
        "createTime": "2024-01-15T15:00:00"
    }
}
```

**错误响应**:
```json
{
    "code": 400,
    "message": "用户不存在, userId=999",
    "data": null
}
```

### 2.5 更新订单

```
PUT /api/orders/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 订单ID |

**请求体**:
```json
{
    "status": "PAID"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "订单更新成功",
    "data": {
        "id": 1,
        "userId": 1,
        "productName": "iPhone 15",
        "quantity": 1,
        "amount": 6999.00,
        "status": "PAID",
        "createTime": "2024-01-10T09:00:00"
    }
}
```

### 2.6 删除订单

```
DELETE /api/orders/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 订单ID |

**响应示例**:
```json
{
    "code": 200,
    "message": "订单删除成功",
    "data": null
}
```

---

## 3. 测试命令

```bash
# 查询所有用户
curl http://localhost:8080/api/users

# 查询单个用户
curl http://localhost:8080/api/users/1

# 创建用户
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"测试用户","email":"test@example.com","phone":"13800007777"}'

# 查询所有订单
curl http://localhost:8080/api/orders

# 查询单个订单（包含用户信息）
curl http://localhost:8080/api/orders/1

# 查询用户的所有订单
curl http://localhost:8080/api/orders/user/1

# 创建订单
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"productName":"测试商品","quantity":2,"amount":999.00}'

# 更新订单状态
curl -X PUT http://localhost:8080/api/orders/1 \
  -H "Content-Type: application/json" \
  -d '{"status":"PAID"}'

# 健康检查
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

---

## 4. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证（缺少或无效的 JWT 令牌） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 503 | 服务不可用（Feign 降级） |
