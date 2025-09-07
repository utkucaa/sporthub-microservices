# SportHub - Microservices Sports Facility Reservation System

A modern, scalable microservices-based sports facility reservation system built with Spring Boot and Spring Cloud.

## 🏗️ Architecture

SportHub follows a microservices architecture pattern with the following services:

- **Eureka Server** (8762) - Service Discovery
- **API Gateway** (8080) - Single Entry Point, Load Balancing, Authentication
- **User Service** (8081) - User Management & Authentication
- **Court Service** (8083) - Sports Court Management
- **Reservation Service** (8084) - Booking & Reservation System

## 🚀 Technologies

- **Framework**: Spring Boot 3.x, Spring Cloud
- **Language**: Java 17
- **Database**: MySQL
- **Cache**: Redis
- **Messaging**: Apache Kafka
- **Authentication**: JWT
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Apache Kafka (optional, for reservation events)

## ⚙️ Configuration

### Environment Variables

Copy `.env.template` to `.env` and configure your environment:

```bash
cp .env.template .env
```

Required environment variables:

```env
# Database
DB_HOST=localhost
DB_PORT=3306
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT
JWT_SECRET=your_super_secret_jwt_key_here
JWT_EXPIRATION=86400000

# Email (for User Service)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:29092
```

### Database Setup

Create the required databases:

```sql
CREATE DATABASE sporthub_users;
CREATE DATABASE sporthub_courts;
CREATE DATABASE sporthub_rezervation;
```

## 🏃‍♂️ Running the Application

### Using Docker Compose (Recommended)

Start infrastructure services:

```bash
docker-compose up -d
```

### Manual Setup

1. **Start Infrastructure Services**:
   - MySQL Server
   - Redis Server
   - Kafka (optional)

2. **Start Services** (in order):
   ```bash
   # Start Eureka Server
   cd eureka-server && ./mvnw spring-boot:run
   
   # Start User Service
   cd user-service && ./mvnw spring-boot:run
   
   # Start Court Service
   cd court-service && ./mvnw spring-boot:run
   
   # Start Reservation Service
   cd rezervation-service && ./mvnw spring-boot:run
   
   # Start API Gateway
   cd api-gateway && ./mvnw spring-boot:run
   ```

3. **Using Helper Scripts** (if available locally):
   ```bash
   ./start-services.sh  # Start all services
   ./stop-services.sh   # Stop all services
   ```

## 📡 API Endpoints

All requests go through the API Gateway at `http://localhost:8080`

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/users/register` - User registration

### User Management
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `GET /api/users` - Get all users (Admin)

### Court Management
- `GET /api/courts` - Get all courts
- `GET /api/courts/{id}` - Get court by ID
- `POST /api/courts` - Create court (Admin)
- `PUT /api/courts/{id}` - Update court (Admin)
- `POST /api/courts/search` - Search courts

### Reservations
- `GET /api/reservations` - Get user reservations
- `POST /api/reservations` - Create reservation
- `PUT /api/reservations/{id}` - Update reservation
- `DELETE /api/reservations/{id}` - Cancel reservation

## 🔐 Security

- JWT-based authentication
- Role-based authorization (USER, ADMIN, COURT_MANAGER)
- API Gateway-level security filtering
- Environment variable-based configuration for sensitive data

## 🏛️ Service Details

### User Service
- User registration and authentication
- JWT token generation and validation
- User profile management
- Role-based access control

### Court Service
- Sports facility management
- Court search and filtering
- Redis caching for performance
- Support for multiple sport types

### Reservation Service
- Booking system with conflict resolution
- Group reservations
- Kafka integration for event-driven architecture
- Integration with User and Court services

## 🔧 Development

### Building the Project

```bash
# Build all services
mvn clean install

# Build specific service
cd user-service && mvn clean install
```

### Testing

```bash
# Run tests for all services
mvn test

# Run tests for specific service
cd user-service && mvn test
```

## 📊 Monitoring

- **Eureka Dashboard**: http://localhost:8762
- **Service Health**: http://localhost:{port}/actuator/health
- **Service Info**: http://localhost:{port}/actuator/info

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions, please open an issue in the GitHub repository.
