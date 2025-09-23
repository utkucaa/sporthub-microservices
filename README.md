# 🏟️ SportHub - Multi-Sport Court Booking System

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)
![CI/CD](https://img.shields.io/badge/CI/CD-GitHub%20Actions-green.svg)
[![Build Status](https://github.com/utkucaa/sporthub-microservices/actions/workflows/main.yml/badge.svg)](https://github.com/utkucaa/sporthub-microservices/actions/workflows/main.yml)

*Modern microservices architecture for sports court booking system*

</div>

## 🎯 About the Project

SportHub is a modern microservices application designed to manage digital booking processes for sports courts. Users can make court reservations for different sports, process payments, and track their reservation status.

## 🏗️ Microservices Architecture

```
                    ┌─────────────────┐
                    │   Web Client    │
                    └─────────┬───────┘
                              │
                    ┌─────────▼───────┐
                    │  API Gateway    │
                    │     :8090       │
                    └─────────┬───────┘
                              │
              ┌───────────────┼───────────────┐
              │               │               │
    ┌─────────▼───────┐ ┌─────▼─────┐ ┌─────▼─────┐
    │ Service Discovery│ │User Service│ │Court Service│
    │   Eureka :8763   │ │   :9081    │ │   :9082   │
    └─────────────────┘ └─────┬─────┘ └─────┬─────┘
                              │             │
          ┌───────────────────┼─────────────┼───────────────┐
          │                   │             │               │
    ┌─────▼─────┐ ┌──────────▼──────┐ ┌────▼────┐ ┌──────▼──────┐
    │Reservation│ │Payment Service  │ │ MySQL   │ │    Redis    │
    │  :9083    │ │     :9084       │ │ :3307   │ │   :6379     │
    └───────────┘ └─────────────────┘ └─────────┘ └─────────────┘
```

## 🚀 Services

| Service | Port | Description |
|---------|------|-------------|
| **API Gateway** | 8090 | Central entry point for all requests |
| **Eureka Server** | 8763 | Service discovery and registration center |
| **User Service** | 9081 | User management and authentication |
| **Court Service** | 9082 | Court information and management |
| **Reservation Service** | 9083 | Reservation operations |
| **Payment Service** | 9084 | Payment processing |
| **Notification Service** | 9085 | Notifications and messaging |
| **MySQL Database** | 3307 | Main database |
| **Redis Cache** | 6379 | Cache and session management |
| **Kafka** | 9092 | Message queue |
| **Kafka UI** | 8086 | Kafka management interface |

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.5.5** - Main framework
- **Spring Cloud** - Microservices infrastructure
- **Spring Security** - Security and authentication
- **Spring Data JPA** - Data access layer
- **Netflix Eureka** - Service discovery
- **Spring Cloud Gateway** - API Gateway

### Database & Cache
- **MySQL 8.0** - Main database
- **Redis** - Cache and session management

### DevOps & CI/CD
- **Docker & Docker Compose** - Containerization
- **GitHub Actions** - CI/CD pipeline
- **DockerHub** - Container registry

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Git

### 1. Clone the Repository
```bash
git clone https://github.com/utkucaa/sporthub-microservices.git
cd sporthub-microservices
```

### 2. Quick Start (Recommended)
```bash
# Automatic setup and startup
./start-project.sh
```

### 3. Manual Docker Setup
```bash
# Copy environment file
cp env.example .env

# Start all services
docker-compose up -d --build

# Follow logs
docker-compose logs -f
```

### 4. Check Services
- **Eureka Dashboard:** http://localhost:8763
- **API Gateway:** http://localhost:8090
- **User Service:** http://localhost:9081
- **Court Service:** http://localhost:9082
- **Reservation Service:** http://localhost:9083
- **Payment Service:** http://localhost:9084
- **Notification Service:** http://localhost:9085
- **Kafka UI:** http://localhost:8086
- **MySQL Database:** localhost:3307
- **Redis Cache:** localhost:6379

## 📋 API Endpoints

### 🔐 User Service
```http
# Authentication & User Management
POST   /api/users/register              # User registration
POST   /api/users/login                 # User login
POST   /api/users/logout                # User logout
GET    /api/users/profile               # Get user profile
PUT    /api/users/profile               # Update user profile
DELETE /api/users/{id}                   # Delete user account

# Password Management
POST   /api/users/forgot-password        # Forgot password
POST   /api/users/reset-password         # Reset password
PUT    /api/users/change-password        # Change password

# User Verification
POST   /api/users/verify-email          # Verify email
POST   /api/users/resend-verification   # Resend verification
```

### 🏟️ Court Service
```http
# Court Management
GET    /api/courts                      # Get all courts
GET    /api/courts/{id}                 # Get court details
POST   /api/courts                      # Create new court
PUT    /api/courts/{id}                 # Update court
DELETE /api/courts/{id}                 # Delete court

# Court Search & Filtering
GET    /api/courts/search               # Search courts
GET    /api/courts/filter               # Filter courts by criteria
GET    /api/courts/available            # Get available courts
GET    /api/courts/{id}/schedule        # Get court schedule

# Court Categories
GET    /api/courts/categories           # Get court categories
GET    /api/courts/category/{category}  # Get courts by category
```

### 📅 Reservation Service
```http
# Reservation Management
POST   /api/reservations                # Create reservation
GET    /api/reservations                # Get user reservations
GET    /api/reservations/{id}           # Get reservation details
PUT    /api/reservations/{id}           # Update reservation
DELETE /api/reservations/{id}           # Cancel reservation

# Reservation Status
PUT    /api/reservations/{id}/confirm   # Confirm reservation
PUT    /api/reservations/{id}/cancel    # Cancel reservation
GET    /api/reservations/status/{status} # Get reservations by status

# Availability Check
GET    /api/reservations/availability   # Check court availability
GET    /api/reservations/calendar       # Get reservation calendar
```

### 💳 Payment Service
```http
# Payment Processing
POST   /api/payments                    # Process payment
GET    /api/payments/{id}               # Get payment details
GET    /api/payments/user/{userId}      # Get user payments

# Payment Methods
GET    /api/payments/methods              # Get payment methods
POST   /api/payments/methods           # Add payment method
DELETE /api/payments/methods/{id}       # Remove payment method

# Refunds & Invoices
POST   /api/payments/{id}/refund        # Process refund
GET    /api/payments/{id}/invoice       # Get payment invoice
GET    /api/payments/invoices           # Get user invoices
```

### 🔔 Notification Service
```http
# Notifications
GET    /api/notifications              # Get user notifications
POST   /api/notifications/mark-read     # Mark notification as read
DELETE /api/notifications/{id}          # Delete notification

# Email Notifications
POST   /api/notifications/email         # Send email notification
GET    /api/notifications/email/templates # Get email templates

# SMS Notifications
POST   /api/notifications/sms           # Send SMS notification

# Push Notifications
POST   /api/notifications/push          # Send push notification
GET    /api/notifications/subscriptions # Get notification subscriptions
```

### 🚪 API Gateway
```http
# Health & Status
GET    /actuator/health                 # Gateway health check
GET    /actuator/info                    # Gateway information

# Service Discovery
GET    /discovery/services              # List all registered services
GET    /discovery/services/{service}    # Get service details

# Load Balancing
GET    /api/load-balancer/status        # Load balancer status
```

### 📊 Monitoring Endpoints
```http
# Health Checks (All Services)
GET    /actuator/health                 # Service health
GET    /actuator/info                    # Service information
GET    /actuator/metrics                 # Service metrics
GET    /actuator/env                     # Environment variables

# Eureka Dashboard
GET    /eureka/apps                      # All registered applications
GET    /eureka/apps/{appName}            # Specific application info
```

### 🔍 Search & Filtering
```http
# Global Search
GET    /api/search?q={query}             # Global search across services
GET    /api/search/courts?q={query}      # Search courts
GET    /api/search/users?q={query}       # Search users

# Advanced Filtering
GET    /api/courts/filter?location={location}&sport={sport}&price={price}
GET    /api/reservations/filter?date={date}&status={status}
```

## 🔧 Development

### Local Development
```bash
# Run each service in separate terminal
cd eureka-server && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
cd user-service && mvn spring-boot:run
# ... other services
```


## 🔄 CI/CD Pipeline

The project has a fully automated CI/CD process with GitHub Actions:

```
Push to main → Test → Build → Docker Push → Deploy Ready
```

**Pipeline Features:**
- ✅ Automatic test execution
- ✅ Parallel service builds
- ✅ Docker image creation
- ✅ Automatic push to DockerHub
- ✅ Deployment simulation

**Pipeline Flow:**
```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐    ┌──────────────┐
│   Code Push │───▶│ Test (7x)    │───▶│ Docker Build│───▶│ Deploy Ready │
│   (Trigger) │    │ (Parallel)   │    │ & Push      │    │ (Production) │
└─────────────┘    └──────────────┘    └─────────────┘    └──────────────┘
```

## 📊 Monitoring & Health

### Health Check Endpoints
```http
GET /actuator/health        # Service health
GET /actuator/info          # Service information
GET /actuator/metrics       # Metrics
```

### Eureka Dashboard
You can monitor all services status at http://localhost:8763

## 🔐 Security

- **Spring Security** for endpoint protection
- **JWT Token** based authentication
- **Redis** for session management
- **CORS** configuration

## 🐳 Docker Configuration

### Production Deployment
```bash
# For production environment
docker-compose up -d --build
```

### Database Schema
The project automatically starts MySQL database and creates all tables with `init-database.sql` script:
- ✅ User Service tables
- ✅ Court Service tables  
- ✅ Reservation Service tables
- ✅ Payment Service tables
- ✅ Notification Service tables

### Port Mapping
Services run on different ports to prevent conflicts:
- **API Gateway**: 8090 (host) → 8080 (container)
- **Eureka**: 8763 (host) → 8762 (container)
- **User Service**: 9081 (host) → 8081 (container)
- **Court Service**: 9082 (host) → 8082 (container)
- **Reservation Service**: 9083 (host) → 8083 (container)
- **Payment Service**: 9084 (host) → 8084 (container)
- **Notification Service**: 9085 (host) → 8085 (container)
- **MySQL**: 3307 (host) → 3306 (container)


<div align="center">


</div>
