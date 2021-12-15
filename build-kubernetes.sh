cd customer-service
./mvnw clean install jib:build -P kubernetes 
cd ../name-service
./mvnw clean install jib:build -P kubernetes 
cd ../person-service
./mvnw clean install jib:build -P kubernetes 
cd ..