# Spring Boot WebSocket Demo

A demonstration application that implements WebSockets with Spring Boot to create a real-time chat application.

## Description

This project is a simple demonstration of how to implement WebSockets in a Spring Boot application. It allows users to connect to a chat room and send messages that are transmitted in real-time to all connected users. The application is fully internationalized and supports multiple languages.

## Features

- Real-time communication using WebSockets
- Simple user interface for sending and receiving messages
- User connection management
- Internationalization (i18n) support with the modern JavaScript Intl API
- Message types for chat, join/leave notifications, and errors
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
- HTML/CSS/JavaScript (frontend)
- Modern JavaScript Intl API for internationalization
- YAML for application configuration
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
│   │               ├── ChatMessage.java (Message model)
│   │               ├── ChatMessageHandler.java (Chat message handler)
│   │               ├── MessageUtils.java (Internationalization utils)
│   │               ├── config (Configuration directory)
│   │               ├── controller
│   │               │   ├── HomeController.java (Home page controller)
│   │               │   └── MessageController.java (REST API for messages)
│   │               ├── SpringBootWebSocketApplication.java (Main class)
│   │               └── WebSocketConfig.java (WebSocket configuration)
│   └── resources
│       ├── application.yml
│       ├── application-docker.yml
│       ├── i18n
│       │   └── messages_en.properties (English language strings)
│       └── static
│           ├── css
│           │   └── styles.css (Application styles)
│           ├── js
│           │   ├── chat.js (Chat functionality)
│           │   └── i18n.js (Internationalization logic)
│           └── index.html (Chat user interface)
└── test
    ├── java
    │   └── com
    │       └── example
    │           └── springbootwebsocket
    │               ├── ChatMessageHandlerTest.java (Unit tests for handler)
    │               ├── ChatMessageTest.java (Tests for message model)
    │               ├── DirectHtmlIntegrationTest.java (Direct HTML file tests)
    │               ├── MessageUtilsTest.java (Tests for i18n utils)
    │               ├── SimpleHtmlTest.java (Simple HTML structure tests)
    │               ├── TestWebSocketConfig.java (Test-specific WebSocket config)
    │               ├── WebSocketConfigTest.java (Unit tests for config)
    │               ├── config
    │               │   └── TestMessageSourceConfig.java (Test-specific i18n config)
    │               └── controller
    │                   ├── MessageControllerTest.java (Unit tests for REST API)
    │                   └── MessageControllerIntegrationTest.java (API integration tests)
    └── resources
        └── application-test.yml (Consolidated test-specific properties)
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
6. Change language using the language selector (currently English only, more languages can be added)

## Internationalization (i18n)

The application supports internationalization with the following features:

- Language detection based on browser preferences
- Language selection through UI
- Language persistence using localStorage
- Server-side message resources with Spring's MessageSource (configured in MessageConfig.java)
- Message files located in src/main/resources/i18n/
- Modern client-side implementation using JavaScript's Intl API
- Properly formatted dates according to locale
- User-friendly notification messages
- Pluralization support for varying counts (e.g., users online)

### Adding a New Language

To add a new language:

1. Create a new properties file in `src/main/resources/i18n/`, for example `messages_es.properties` for Spanish
2. Translate all messages from the English file
3. Add the language option to the dropdown in `index.html`
4. No JavaScript changes are needed as translations are loaded dynamically

## Message Types

The application supports different types of messages:

- **CHAT**: Regular chat messages from users
- **JOIN**: Notifications when a user joins the chat
- **LEAVE**: Notifications when a user leaves the chat
- **ERROR**: Error messages for error handling
- **USER_COUNT**: Internal message type for updating the online users counter

## Running Tests

The project includes a comprehensive test suite with both unit tests and integration tests. The test framework is designed to handle WebSocket testing in both single-test and multiple-test execution scenarios.

### Running All Tests

```bash
./mvnw test
```

### Running Single Tests

```bash
./mvnw test -Dtest=TestClassName
```

For example:

```bash
./mvnw test -Dtest=ChatMessageTest
```

### Test Categories

- **Unit Tests**: Test individual components in isolation
  - `ChatMessageHandlerTest`: Tests the WebSocket message handler functionality
  - `WebSocketConfigTest`: Tests the WebSocket configuration
  - `ChatMessageTest`: Tests the message model
  - `MessageUtilsTest`: Tests the internationalization utilities
  - `MessageControllerTest`: Tests the REST API for translations

- **Integration Tests**: Test the complete application flow
  - `MessageControllerIntegrationTest`: Tests the internationalization API with Spring context
  - `DirectHtmlIntegrationTest`: Tests the HTML structure directly, bypassing Spring context
  - `SimpleHtmlTest`: Tests HTML structure and i18n attributes

### Test Architecture

The test architecture includes special configurations to allow running all tests together:

- **Profile-based Configuration**: The application uses the `test` profile during test execution
- **TestWebSocketConfig**: A special WebSocket configuration activated only during tests
- **Conditional Configuration**: The main WebSocket configuration uses `@ConditionalOnWebApplication` and `@Profile("!test")` to avoid conflicts during test execution
- **Single Test Configuration File**: All test-specific settings are consolidated in `application-test.properties` to avoid duplication and make maintenance easier
- **Resource Efficiency**: The test configuration reuses main application resources (messages, templates) instead of duplicating them

## WebSocket Endpoints

The application exposes the following endpoints:

- WebSocket: `/chat` - The main chat endpoint that handles all message communications
- REST API: `/api/messages?lang=en` - API for retrieving internationalized messages

## How It Works

The application uses WebSockets to establish a bidirectional connection between the client and server. When a user sends a message:

1. The message is sent to the server through the WebSocket connection
2. The server processes the message based on its type
3. The server broadcasts the message to all connected clients
4. Each client receives the message and displays it in the user interface with proper formatting

The application also shows an online users counter that is only visible when users are connected. This counter is updated in real-time when users join or leave the chat:

1. The server sends a USER_COUNT message to all connected clients
2. Clients process this message and update the counter display
3. The counter is hidden when no connection is established

### Key Components

- `WebSocketConfig.java`: Configures the WebSocket endpoint and registers the handler
  - Uses conditional annotations to ensure proper behavior in different environments
  - Configurable message size limits and timeouts for WebSocket connections
- `MessageConfig.java`: Configures the internationalization and message source
  - Handles the loading of message properties files
  - Provides a customized MessageSource bean for the application
- `ChatMessageHandler.java`: Manages WebSocket sessions and broadcasts messages to all connected users
- `ChatMessage.java`: Defines the message structure and types
- `MessageUtils.java`: Provides utilities for internationalization
- `HomeController.java`: Controller for serving the home page
- `MessageController.java`: REST controller for serving translated messages
- `i18n.js`: Client-side internationalization using modern JavaScript APIs
- `chat.js`: Frontend logic for WebSocket communication
- `index.html`: User interface with internationalization attributes

### Test Components

- `TestWebSocketConfig.java`: Test-specific WebSocket configuration that avoids servlet container initialization issues
- `application-test.properties`: Consolidated test environment configuration with all necessary settings
- `TestMessageSourceConfig.java`: Test-specific localization configuration that reuses the main application message resources

## Configuration

The application uses YAML configuration files for better readability and structure, with properties files for specific environments:

- `application.yml`: Main configuration file for the application
- `application-docker.yml`: Docker-specific configuration
- `application-test.yml`: Consolidated test-specific configuration with all necessary settings for the test environment

## Troubleshooting

- **Connection Issues**: Make sure you're running the application on the default port 8080. If you've changed the port in `application.yml`, update the WebSocket URL in `chat.js` accordingly.
- **Browser Compatibility**: This demo works best with modern browsers that support the Intl API. If you experience issues, try using the latest version of Chrome, Firefox, or Edge.
- **Test Failures**: 
  - If integration tests fail, ensure no other application is using the required ports.
  - If tests pass individually but fail when run all together, ensure all tests are properly using the test profile and TestWebSocketConfig.
  - WebSocket connections in tests must be properly mocked to avoid ServletContext initialization issues.
- **Internationalization Issues**: If translations don't appear, check the browser console for errors and verify that the language file exists and is properly formatted.

## Development

### Adding Features

Some ideas for extending this application:

- Add more languages (Spanish, Catalan, French, etc.)
- Implement user authentication
- Create multiple chat rooms
- Add message persistence with a database
- Add typing indicators and read receipts
- Implement emoji support and message formatting

## Dockerization

The project includes configuration for Docker:

- `Dockerfile`: Defines the application image
- `docker-compose.yml`: Configures the service for easy deployment
- `.dockerignore`: Excludes unnecessary files from the build context
- `application-docker.yml`: Configuration specific to the Docker environment

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
