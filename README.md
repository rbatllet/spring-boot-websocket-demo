# Spring Boot WebSocket Demo

A demonstration application that implements WebSockets with Spring Boot to create a real-time chat application.

## Description

This project is a simple demonstration of how to implement WebSockets in a Spring Boot application. It allows users to connect to a chat room and send messages that are transmitted in real-time to all connected users.

## Features

- Real-time communication using WebSockets
- Simple user interface for sending and receiving messages
- User connection management
- No database required - in-memory message handling
- Lightweight and easy to understand codebase
- Comprehensive test suite for both unit and integration testing

## Prerequisites

- Java 21 or higher
- Maven 3.6+ or use the included Maven wrapper
- A modern web browser that supports WebSockets

## Technologies Used

- Java 21
- Spring Boot 3.4.4
- Spring WebSocket
- HTML/JavaScript (frontend)
- JUnit 5 and Mockito for testing
- Docker for deployment

## Project Structure

```
src
├── main
│   ├── java
│   │   └── com
│   │       └── example
│   │           └── springbootwebsocket
│   │               ├── ChatMessageHandler.java (Chat message handler)
│   │               ├── SpringBootWebSocketApplication.java (Main class)
│   │               └── WebSocketConfig.java (WebSocket configuration)
│   └── resources
│       ├── application.properties
│       └── static
│           └── index.html (Chat user interface)
└── test
    └── java
        └── com
            └── example
                └── springbootwebsocket
                    ├── ChatMessageHandlerTest.java (Unit tests for handler)
                    ├── WebSocketConfigTest.java (Unit tests for config)
                    ├── WebSocketIntegrationTest.java (Integration tests)
                    └── SpringBootWebSocketApplicationTests.java
```

## How to Run the Application

### Using Maven Wrapper

1. Clone this repository
2. Navigate to the project directory
3. Run the application with the Maven wrapper:

```bash
./mvnw spring-boot:run
```

### Using Maven

If you have Maven installed:

```bash
mvn spring-boot:run
```

### Building and Running the JAR

```bash
./mvnw clean package
java -jar target/spring-boot-web-socket-1.0.0-SNAPSHOT.jar
```

4. Open your browser and go to `http://localhost:8080`

## How to Use the Application

1. Enter your name in the text field
2. Click "Connect" to join the chat
3. Type a message in the bottom text field
4. Click "send" to send the message
5. All connected users will see your message immediately

## Running Tests

The project includes a comprehensive test suite with unit tests and integration tests.

### Running All Tests

```bash
./mvnw test
```

### Test Categories

- **Unit Tests**: Test individual components in isolation
  - `ChatMessageHandlerTest`: Tests the WebSocket message handler functionality
  - `WebSocketConfigTest`: Tests the WebSocket configuration

- **Integration Tests**: Test the complete WebSocket communication flow
  - `WebSocketIntegrationTest`: Tests real WebSocket connections and message broadcasting

## WebSocket Endpoints

The application exposes the following WebSocket endpoint:

- `/chat` - The main chat endpoint that handles all message communications

## How It Works

The application uses WebSockets to establish a bidirectional connection between the client and server. When a user sends a message:

1. The message is sent to the server through the WebSocket connection
2. The server receives the message and forwards it to all connected clients
3. Each client receives the message and displays it in the user interface

### Key Components

- `WebSocketConfig.java`: Configures the WebSocket endpoint and registers the handler
- `ChatMessageHandler.java`: Manages WebSocket sessions and broadcasts messages to all connected users
- `index.html`: Contains the frontend logic for connecting to the WebSocket and sending/receiving messages

## Troubleshooting

- **Connection Issues**: Make sure you're running the application on the default port 8080. If you've changed the port in `application.properties`, update the WebSocket URL in `index.html` accordingly.
- **Browser Compatibility**: This demo works best with modern browsers. If you experience issues, try using the latest version of Chrome, Firefox, or Edge.
- **Test Failures**: If integration tests fail, ensure no other application is using the required ports.

## Development

### Adding Features

Some ideas for extending this application:

- Add user authentication
- Implement private messaging
- Add message persistence with a database
- Create multiple chat rooms
- Add typing indicators

## Dockerization

The project includes configuration for Docker:

- `Dockerfile`: Defines the application image
- `docker-compose.yml`: Configures the service for easy deployment
- `.dockerignore`: Excludes unnecessary files from the build context
- `application-docker.properties`: Configuration specific to the Docker environment

### Advantages of Using Docker

- **Consistency**: Ensures the application works the same in all environments
- **Isolation**: Avoids conflicts with other applications or dependencies
- **Easy Deployment**: Simplifies the deployment process
- **Scalability**: Facilitates the implementation of multiple instances

### Running with Docker

1. Build and run the application with Docker Compose:
   ```bash
   docker-compose up -d
   ```

2. To stop the application:
   ```bash
   docker-compose down
   ```

3. To rebuild the image after changes:
   ```bash
   docker-compose up -d --build
   ```

## License

This project is free software and can be used as a base for your own projects.
