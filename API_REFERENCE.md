# USER-SERVICE API REFERENCE

## Service Info
- Service: user-service
- Base URL local: http://localhost:8081
- Content-Type: application/json

## Authentication
- JWT Bearer token
- Header format:
  - Authorization: Bearer <token>

## 1) Register User
- Method: POST
- Path: /api/users/register
- Auth: Public

Request body:
```json
{
  "fullName": "Nguyen Van A",
  "email": "a@gmail.com",
  "password": "123456"
}
```

Validation:
- fullName: required, max 120 chars
- email: required, valid email format
- password: required, 6-72 chars

Success response:
- Status: 201 Created
```json
{
  "message": "Register success"
}
```

Common error responses:
- Status: 400 Bad Request (email already exists)
```json
{
  "timestamp": "2026-04-15T14:10:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists",
  "path": "/api/users/register",
  "validationErrors": null
}
```

- Status: 400 Bad Request (validation failed)
```json
{
  "timestamp": "2026-04-15T14:10:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/users/register",
  "validationErrors": {
    "email": "Email is invalid",
    "password": "Password must be between 6 and 72 characters"
  }
}
```

## 2) Login
- Method: POST
- Path: /api/users/login
- Auth: Public

Request body:
```json
{
  "email": "a@gmail.com",
  "password": "123456"
}
```

Success response:
- Status: 200 OK
```json
{
  "token": "<jwt_token>",
  "user": {
    "id": "67fd33e2e3f8dd4e688ca8fa",
    "fullName": "Nguyen Van A",
    "email": "a@gmail.com",
    "role": "USER"
  }
}
```

Common error responses:
- Status: 401 Unauthorized (wrong password)
```json
{
  "timestamp": "2026-04-15T14:10:00.000Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "path": "/api/users/login",
  "validationErrors": null
}
```

- Status: 404 Not Found (email not found)
```json
{
  "timestamp": "2026-04-15T14:10:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Email does not exist",
  "path": "/api/users/login",
  "validationErrors": null
}
```

## 3) Get Current User Profile
- Method: GET
- Path: /api/users/me
- Auth: Bearer token required

Headers:
```http
Authorization: Bearer <jwt_token>
```

Success response:
- Status: 200 OK
```json
{
  "id": "67fd33e2e3f8dd4e688ca8fa",
  "fullName": "Nguyen Van A",
  "email": "a@gmail.com",
  "role": "USER"
}
```

Common error responses:
- Status: 401 Unauthorized (missing/invalid token)
```json
{
  "timestamp": "2026-04-15T14:10:00.000Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/users/me",
  "validationErrors": null
}
```

## Default Admin
Default admin is seeded when app starts (if not already exists):
- email: admin@movie.com
- password: Admin@123456
- role: ADMIN

Admin login request:
```json
{
  "email": "admin@movie.com",
  "password": "Admin@123456"
}
```

Admin profile response sample:
```json
{
  "id": "67fd3407e3f8dd4e688ca8fc",
  "fullName": "System Admin",
  "email": "admin@movie.com",
  "role": "ADMIN"
}
```

## Quick Test Flow
1. POST /api/users/login with default admin
2. GET /api/users/me with admin token
3. POST /api/users/register with new email
4. POST /api/users/login with created user
5. GET /api/users/me with user token
