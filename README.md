## 🚀 How to Run the App with One Command (via Docker)

Make sure you have [Docker](https://www.docker.com/) installed.

Then build the app and run it with:

```bash
./gradlew bootJar && docker build -t electronic-store . && docker run -p 8080:8080 electronic-store
