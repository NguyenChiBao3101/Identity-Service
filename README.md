# Identity Service

Dịch vụ quản lý danh tính người dùng với các chức năng xác thực, phân quyền và quản lý vai trò.

## Tính năng

- **Xác thực người dùng**: Đăng nhập, đăng xuất, refresh token
- **Quản lý người dùng**: CRUD operations cho người dùng
- **Quản lý vai trò**: Tạo, cập nhật, xem danh sách vai trò
- **Quản lý quyền hạn**: Tạo, xóa, xem danh sách quyền hạn
- **JWT Authentication**: Sử dụng JWT token cho bảo mật

## Công nghệ sử dụng

- **Spring Boot 3.3.0**
- **Spring Security**
- **Spring Data JPA**
- **MySQL Database**
- **JWT (JSON Web Token)**
- **OpenAPI 3.0 (Swagger)**
- **MapStruct**
- **Lombok**

## Cài đặt và chạy

### Yêu cầu hệ thống
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### Cấu hình database
1. Tạo database MySQL:
```sql
CREATE DATABASE identity_service;
```

2. Cập nhật thông tin kết nối trong `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/identity_service
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Thiết lập JWT secret key:
```bash
export JWT_SIGNER_KEY=your_jwt_secret_key_here
```

### Chạy ứng dụng
```bash
# Clone repository
git clone https://github.com/NguyenChiBao3101/Identity-Service.git
cd Identity-Service

# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

Ứng dụng sẽ chạy tại: `http://localhost:8080`

## API Documentation

### Truy cập Swagger UI
- **URL**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Các nhóm API

#### 1. Authentication APIs (`/auth`)
- `POST /auth/log-in` - Đăng nhập người dùng
- `POST /auth/introspect` - Kiểm tra tính hợp lệ của token
- `POST /auth/refresh` - Làm mới token
- `POST /auth/logout` - Đăng xuất người dùng

#### 2. User Management APIs (`/user`)
- `POST /user/register` - Đăng ký người dùng mới
- `GET /user/all` - Lấy danh sách tất cả người dùng
- `GET /user/getById/{id}` - Lấy thông tin người dùng theo ID
- `GET /user/{username}` - Lấy thông tin người dùng theo username
- `PUT /user/update/{username}` - Cập nhật thông tin người dùng
- `PUT /user/updateRole/{username}` - Cập nhật vai trò người dùng
- `DELETE /user/deleteByUsername/{username}` - Xóa người dùng theo username
- `DELETE /user/deleteById/{id}` - Xóa người dùng theo ID

#### 3. Role Management APIs (`/role`)
- `POST /role/add` - Tạo vai trò mới
- `GET /role/all` - Lấy danh sách tất cả vai trò
- `PUT /role/update/{name}` - Cập nhật vai trò

#### 4. Permission Management APIs (`/permission`)
- `POST /permission/add` - Tạo quyền hạn mới
- `GET /permission/all` - Lấy danh sách tất cả quyền hạn
- `DELETE /permission/{name}` - Xóa quyền hạn

### Authentication

Hầu hết các API (trừ đăng nhập và đăng ký) yêu cầu JWT token trong header:
```
Authorization: Bearer <your_jwt_token>
```

### Ví dụ sử dụng

#### 1. Đăng nhập
```bash
curl -X POST "http://localhost:8080/auth/log-in" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password"
  }'
```

#### 2. Sử dụng API với token
```bash
curl -X GET "http://localhost:8080/user/all" \
  -H "Authorization: Bearer <your_jwt_token>"
```

## Cấu hình OpenAPI

### Các tính năng đã tích hợp:
- **Swagger UI**: Giao diện web để test API
- **API Documentation**: Mô tả chi tiết từng endpoint
- **Request/Response Examples**: Ví dụ dữ liệu đầu vào/ra
- **Security Schemes**: Cấu hình JWT authentication
- **Response Codes**: Mô tả các mã phản hồi
- **Parameter Documentation**: Mô tả các tham số

### Cấu hình trong application.properties:
```properties
# OpenAPI/Swagger Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.display-request-duration=true
springdoc.swagger-ui.doc-expansion=none
springdoc.swagger-ui.filter=true
springdoc.swagger-ui.try-it-out-enabled=true
springdoc.swagger-ui.deep-linking=true
```

## Cấu trúc dự án

```
src/main/java/com/example/identityservice/
├── configuration/          # Cấu hình ứng dụng
│   ├── OpenAPIConfig.java  # Cấu hình OpenAPI
│   └── SecurityConfig.java # Cấu hình bảo mật
├── controller/             # REST Controllers
├── dto/                    # Data Transfer Objects
├── entity/                 # JPA Entities
├── service/                # Business Logic
├── repository/             # Data Access Layer
└── mapper/                 # Object Mappers
```

## Đóng góp

1. Fork dự án
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## License

Dự án này được phân phối dưới giấy phép MIT. Xem file `LICENSE` để biết thêm chi tiết.

## Liên hệ

- **Team**: Team 11
- **Email**: team11@gmail.com
- **GitHub**: https://github.com/NguyenChiBao3101/Identity-Service 