FROM eclipse-temurin:21-jdk as build
WORKDIR /workspace/app

COPY pom.xml .
COPY src src

# Build the application
RUN apt-get update && apt-get install -y maven
RUN mvn install -DskipTests

# Extract the layers
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Runtime stage
FROM eclipse-temurin:21-jre
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency

# Copy the dependency application layer by layer
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Create directory for H2 database files and set permissions
RUN mkdir -p /app/data && chmod 777 /app/data

# Set Spring profile to docker
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.example.springbootwebsocket.SpringBootWebSocketApplication"]
