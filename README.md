# Teleport - Truck Load Optimizer

A Spring Boot application that optimizes truck load planning by selecting the best combination of orders that maximize payout while respecting weight and volume constraints.

## Features

- **Optimization Algorithm**: Uses a knapsack-based algorithm to find the optimal combination of orders
- **Order Filtering**: Automatically filters orders by compatibility (origin, destination, hazmat status, and date validity)
- **Validation**: Jakarta Bean Validation for request validation
- **SOLID Principles**: Well-structured codebase following SOLID principles
- **Comprehensive Testing**: Full test coverage with JUnit tests

## Technology Stack

- Java 21
- Spring Boot 4.0.2
- Maven
- Jakarta Bean Validation
- Lombok
- JUnit 5

## Project Structure

```
src/main/java/com/example/teleport/
├── config/
│   └── OptimizerConstants.java          # Application constants
├── controller/
│   └── TruckLoadOptimizerController.java # REST API endpoints
├── dto/
│   ├── OrderDto.java                    # Order data transfer object
│   └── TruckDto.java                    # Truck data transfer object
├── exception/
│   └── GlobalExceptionHandler.java      # Global exception handling
├── request/
│   └── TruckLoadRequest.java            # Request DTO with validation
├── response/
│   └── OptimizeResponse.java            # Response DTO
└── service/
    ├── OptimizationAlgorithm.java       # Optimization algorithm interface
    ├── OptimizerService.java            # Main service interface
    ├── OrderFilterService.java          # Order filtering interface
    ├── ResponseBuilderService.java      # Response builder interface
    └── impl/
        ├── KnapsackOptimizationAlgorithm.java
        ├── OptimizerServiceImpl.java
        ├── OrderFilterServiceImpl.java
        └── ResponseBuilderServiceImpl.java
```

## API Endpoint

### POST `/api/v1/load-optimizer/optimize`

Optimizes truck load by selecting the best combination of orders.

**Request Body:**
```json
{
    "truck": {
        "id": "truck-123",
        "max_weight_lbs": 44000,
        "max_volume_cuft": 3000
    },
    "orders": [
        {
            "id": "ord-001",
            "payout_cents": 250000,
            "weight_lbs": 18000,
            "volume_cuft": 1200,
            "origin": "Los Angeles, CA",
            "destination": "Dallas, TX",
            "pickup_date": "2025-12-05",
            "delivery_date": "2025-12-09",
            "is_hazmat": false
        }
    ]
}
```

**Response:**
```json
{
    "truck_id": "truck-123",
    "selected_order_ids": ["ord-001", "ord-002"],
    "total_payout_cents": 430000,
    "total_weight_lbs": 30000,
    "total_volume_cuft": 2100,
    "utilization_weight_percent": 68.18,
    "utilization_volume_percent": 70.0
}
```

## Validation Rules

- Truck ID cannot be null or blank
- Truck max weight and volume must be at least 1
- Orders list cannot be null or empty
- Maximum 22 orders allowed per request
- Order ID, origin, and destination cannot be null or blank
- Order payout, weight, and volume must be at least 1
- Pickup and delivery dates cannot be null
- Delivery date must be after or equal to pickup date

## Order Compatibility

Orders are filtered to be compatible if they have:
- Same origin
- Same destination
- Same hazmat status
- Valid date range (delivery date >= pickup date)

## Building and Running

### Prerequisites
- Java 21
- Maven 3.6+

### Build
```bash
mvn clean package
```

### Run
```bash
mvn spring-boot:run
```

Or using the JAR:
```bash
java -jar target/teleport.jar
```

### Run Tests
```bash
mvn test
```

## Docker

### Build Docker Image
```bash
docker build -t teleport .
```

### Run with Docker Compose
```bash
docker-compose up
```

## Design Principles

This project follows SOLID principles:

- **Single Responsibility**: Each class has a single, well-defined responsibility
- **Open/Closed**: Algorithm can be extended without modifying existing code
- **Liskov Substitution**: All implementations are substitutable for their interfaces
- **Interface Segregation**: Focused, client-specific interfaces
- **Dependency Inversion**: High-level modules depend on abstractions

## License

This project is open source and available for use.
