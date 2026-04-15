# user-service

User Service cho Movie Ticket System theo Event-Driven Architecture.

## Tech Stack
- Java 21
- Spring Boot 4.x
- Maven
- Spring Web
- Spring Security
- Spring Data MongoDB
- Spring AMQP (RabbitMQ)
- JWT Authentication
- Lombok
- Validation

## Cac endpoint
- POST /api/users/register
- POST /api/users/login
- GET /api/users/me

## Tai khoan admin mac dinh
Khi service khoi dong, he thong tu tao 1 admin neu email chua ton tai:
- fullName: System Admin
- email: admin@movie.com
- password: Admin@123456
- role: ADMIN

Co the override bang environment variables:
- DEFAULT_ADMIN_ENABLED
- DEFAULT_ADMIN_FULL_NAME
- DEFAULT_ADMIN_EMAIL
- DEFAULT_ADMIN_PASSWORD

## Event-Driven
Sau khi register thanh cong, service publish event vao RabbitMQ:
- Exchange: movie.exchange
- Queue: user.registered.queue
- Routing key: user.registered
- Event type: USER_REGISTERED

Payload event:
```json
{
  "eventType": "USER_REGISTERED",
  "userId": "...",
  "email": "a@gmail.com",
  "fullName": "Nguyen Van A",
  "createdAt": "..."
}
```

## Chay local
Yeu cau:
- Java 21
- Maven 3.9+

Chay bang Maven Wrapper:
```bash
./mvnw spring-boot:run
```
Windows PowerShell:
```powershell
.\mvnw.cmd spring-boot:run
```

Service mac dinh chay tai http://localhost:3002

## Docker
Build image:
```bash
docker build -t user-service:latest .
```

Run compose:
```bash
docker compose up --build
```

Neu muon bat Mongo local (optional):
```bash
docker compose --profile local-mongo up --build
```

## Cau hinh
Trong file src/main/resources/application.yml co san default value cho:
- MongoDB Atlas
- CloudAMQP
- JWT secret va expiration

Co the override bang environment variables:
- SPRING_MONGODB_URI
- SPRING_MONGODB_DATABASE
- RABBITMQ_HOST
- RABBITMQ_PORT
- RABBITMQ_USERNAME
- RABBITMQ_PASSWORD
- RABBITMQ_VHOST
- JWT_SECRET
- JWT_EXPIRATION
- SERVER_PORT

## Postman
Import file:
- postman-user-service.json

## Huong dan test full chuc nang
1. Khoi dong service:
```powershell
.\mvnw.cmd spring-boot:run
```

2. Test ADMIN mac dinh:
- Goi request `Admin Login (Default)`
- Copy `token` tu response vao bien Postman `adminToken`
- Goi `Get Profile (Admin)`
- Ky vong role la `ADMIN`

3. Test dang ky USER moi:
- Goi `Register` voi email moi (vd: user1@gmail.com)
- Ky vong response: `Register success`
- He thong publish event `USER_REGISTERED` vao RabbitMQ exchange `movie.exchange`

4. Test login USER:
- Goi `Login` voi tai khoan vua dang ky
- Copy `token` vao bien Postman `token`
- Goi `Get Profile`
- Ky vong role la `USER`

5. Test auth fail:
- Goi `Get Profile` khong co Bearer token -> ky vong 401
- Goi `Login` sai password -> ky vong 401
- Dang ky email trung -> ky vong 400

## Luu y production
- Khong hardcode secrets trong source code.
- Dat JWT_SECRET dai va an toan hon (it nhat 32 ky tu).
- Su dung secret manager cho cloud deployment.
