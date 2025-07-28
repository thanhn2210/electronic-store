## 🚀 How to Run the App with One Command (via Docker)

Make sure you have [Docker](https://www.docker.com/) installed.
To build the app and run it with:
```bash
./gradlew bootJar && docker build -t electronic-store . && docker run -p 8080:8080 electronic-store
```
### API cURLs:

#### Create Product:
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

#### Add deals to a product:
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

#### Filter products:
```bash
curl --request GET \
--url 'http://localhost:8080/products/search?category=PHONE&minPrice=100&maxPrice=500&available=true&page=0&size=1' \
--header 'Content-Type: application/json'
```
#### Create a basket:
```bash
curl --request POST \
  --url http://localhost:8080/baskets \
  --header 'Content-Type: application/json' \
  --data '{
  "basketItems": [
    {
      "productId": "b368e98a-633c-40ef-8b1b-9a8ed5c6e436",
      "quantity": 1
    }
  ],
  "status": "ACTIVE"
}'
```

#### Add items to basket:
```bash
curl --request POST \
  --url http://localhost:8080/baskets/{BASKET_ID}/add-items \
  --header 'Content-Type: application/json' \
  --data '[
    {
      "productId": "PRODUCT_ID",
      "basketId": "BASKET_ID",
      "quantity": 1
    }
  ]'
```

#### Remove items from basket:
```bash
curl --request POST \
  --url http://localhost:8080/baskets/{BASKET_ID}/delete-items \
  --header 'Content-Type: application/json' \
  --data '[
  "BASKET_ITEM_ID_1",
  "BASKET_ITEM_ID_2"
]'
```
## How to run tests:
```bash
./gradlew test
```


