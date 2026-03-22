FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy Maven wrapper and pom first for dependency caching
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw && ./mvnw -q dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN ./mvnw -q clean package -DskipTests -B

EXPOSE 8080
CMD ["java", "-jar", "target/smarthire-0.0.1-SNAPSHOT.jar"]
