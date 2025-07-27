## 🚀 How to Run the App with One Command (via Docker)

Make sure you have [Docker](https://www.docker.com/) installed.
To build the app and run it with:
```bash
./gradlew bootJar && docker build -t electronic-store . && docker run -p 8080:8080 electronic-store
```
### APIs:
Create Product:
```bash
curl --request POST \
  --url http://localhost:8080/products \
  --header 'Content-Type: application/json' \
  --data '{
  "name": "Sample Product",
  "description": "This is a sample product description.",
  "category": "PHONE",
  "price": 199.99,
  "stock": 50,
  "available": true,
  "deals": [
    {
      "description": "10% off",
      "expiration": "2025-12-31T23:59:59",
      "type": "FIXED_AMOUNT_DISCOUNT",
      "discountValue": 10.0
    }
  ]
}'
```

Add deals to a product:
```bash
curl --request POST \
  --url http://localhost:8080/products/{INPUT_PRODUCT_ID}/add-deals \
  --header 'Content-Type: application/json' \
  --data '[
  {
    "description": "Black Friday Deal",
    "expiration": "2025-12-31T23:59:59",
    "type": "PERCENTAGE_DISCOUNT",
    "discountValue": 50
  }
]'
```

Filter products:
```bash
curl --request GET \
--url 'http://localhost:8080/products/search?category=PHONE&minPrice=100&maxPrice=500&available=true&page=0&size=1' \
--header 'Content-Type: application/json'
```

## How to run tests:
```bash
./gradlew test
```


