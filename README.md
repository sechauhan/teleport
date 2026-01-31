# Teleport - Truck Load Optimizer

A Spring Boot application that optimizes truck load planning by selecting the best combination of orders that maximize payout while respecting weight and volume constraints.

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

## Health Check

### GET `/actuator/health`

Check the health status of the application.

**Using curl:**
```bash
curl http://localhost:8080/actuator/health
```

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
