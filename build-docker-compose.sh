cd customer-service
./mvnw clean install jib:build -P docker-compose 
cd ../name-service
./mvnw clean install jib:build -P docker-compose 
cd ../person-service
./mvnw clean install jib:build -P docker-compose
cd ..
docker pull murphye/customer-service
docker pull murphye/name-service
docker pull murphye/person-service