FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy all files
COPY . .

# Build project
RUN mvn clean package -DskipTests

EXPOSE 8080
CMD ["java", "-jar", "target/smarthire-0.0.1-SNAPSHOT.jar"]
