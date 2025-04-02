# Spring Boot WebSocket Demo

A demonstration application that implements WebSockets with Spring Boot to create a real-time chat application.

## Description

This project is a simple demonstration of how to implement WebSockets in a Spring Boot application. It allows users to connect to a chat room and send messages that are transmitted in real-time to all connected users. The application is fully internationalized and supports multiple languages. Messages are persisted in an H2 database for history retention.

## Features

- Real-time communication using WebSockets
- Simple user interface for sending and receiving messages
- User connection management
- Client-side internationalization (i18n) with JSON translation files
- Multiple language support (English, Catalan)
- Message types for chat, join/leave notifications, and errors
- Message persistence with H2 database
- Database schema management with Flyway migrations
- Externalized WebSocket configuration
- Message history display when joining the chat
- Lightweight and easy to understand codebase
- Comprehensive test suite for both unit and integration testing
- Microservices-friendly architecture with frontend/backend separation
- Improved user experience with visual separation of username and timestamp
- Prevention of duplicate join messages
- Dynamic UI updates when changing language
- In-memory database configuration for development
- **Robust XSS protection and message validation**
- **Connect/Disconnect functionality with dynamic button state**
- **Dynamic button activation based on input validation**
- **Message formatting options with HTML tags**
- **Improved error handling and display**

## Prerequisites

- Java 21 or higher
- Maven 3.6+ or use the included Maven wrapper
- A modern web browser that supports WebSockets

## Technologies Used

- Java 21
- Spring Boot 3.4.4
- Spring WebSocket
- Spring Data JPA
- H2 Database
- Flyway for database migrations
- HTML/CSS/JavaScript (frontend)
- Modern JavaScript Intl API for internationalization
- JSON for translation files
- YAML for application configuration
- JUnit 5 and Mockito for testing
- Docker for deployment
- **OWASP Java HTML Sanitizer for XSS protection**
- **Apache Commons Text for HTML escaping**

## Project Structure

```
src
├── main
│   ├── java
│   │   └── com
│   │       └── example
│   │           └── springbootwebsocket
│   │               ├── ChatMessage.java (Message model/entity)
│   │               ├── ChatMessageHandler.java (Chat message handler)
│   │               ├── MessageUtils.java (Internationalization utils)
│   │               ├── config (Configuration directory)
│   │               ├── controller
│   │               │   ├── ChatMessageController.java (REST API for message history)
│   │               │   └── HomeController.java (Home page controller)
│   │               ├── repository
│   │               │   └── ChatMessageRepository.java (JPA repository for messages)
│   │               ├── security
│   │               │   └── MessageValidator.java (XSS protection and message validation)
│   │               ├── service
│   │               │   └── ChatMessageService.java (Service for message operations)
│   │               ├── SpringBootWebSocketApplication.java (Main class)
│   │               └── WebSocketConfig.java (WebSocket configuration)
│   └── resources
│       ├── application.yml
│       ├── application-docker.yml
│       ├── db
│       │   └── migration
│       │       ├── V1__Create_chat_messages_table.sql (Flyway migration script)
│       │       └── V2__Add_count_column_to_chat_messages.sql (Adds count column for user count)
│       ├── i18n
│       │   └── messages_en.properties (Backend messages for system logs)
│       └── static
│           ├── css
│           │   └── styles.css (Application styles)
│           ├── i18n
│           │   ├── messages_en.json (English translations)
│           │   └── messages_ca.json (Catalan translations)
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
    │               └── controller
    │                   └── ChatMessageControllerTest.java (Unit tests for REST API)
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
java -jar target/spring-boot-web-socket-1.2.0-SNAPSHOT.jar
```

4. Open your browser and go to `http://localhost:8080`

## How to Use the Application

1. Enter your name in the text field
2. Click "Connect" to join the chat (button will be disabled until a valid name is entered)
3. The application will load and display message history
4. Type a message in the bottom text field
5. Click "send" to send the message (button will be disabled until a message is entered)
6. All connected users will see your message immediately
7. Click "Disconnect" to leave the chat
8. Change language using the language selector (English and Catalan available)
9. Click "Show formatting options" to see available HTML formatting tags

## Database Access

The application uses an H2 in-memory database to store chat messages. You can access the H2 console to view and manage the database:

1. Go to `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:mem:chatdb` (for development environment)
3. Username: `sa`
4. Password: `password`

## Database Migrations

The application uses Flyway for database schema migrations. This ensures that the database schema is always in a consistent state and allows for version-controlled database changes.

### Migration Files

Migration files are located in `src/main/resources/db/migration` and follow the naming convention `V{version}__{description}.sql`. The current migrations include:

- `V1__Create_chat_messages_table.sql`: Creates the initial chat_messages table with appropriate indexes
- `V2__Add_count_column_to_chat_messages.sql`: Adds a count column to the chat_messages table for user count messages

### Adding New Migrations

To add a new migration:

1. Create a new SQL file in the `src/main/resources/db/migration` directory
2. Name it following the convention `V{next_version}__{description}.sql`
3. Write the SQL statements for your schema changes
4. Restart the application - Flyway will automatically apply the new migration

## WebSocket Configuration

The application uses externalized WebSocket configuration in the YAML files. This allows for easy modification of WebSocket settings without changing the code.

### Configuration Properties

The following WebSocket properties can be configured in the application.yml file:

```yaml
websocket:
  endpoint: /ws/chat           # WebSocket endpoint path
  allowed-origins: "*"           # Allowed origins for CORS (restrict in production)
  max-text-message-size: 8192    # Maximum text message size in bytes
  max-binary-message-size: 65536 # Maximum binary message size in bytes
  max-session-idle-timeout: 600000 # Maximum session idle timeout in milliseconds
```

### Customizing WebSocket Settings

To customize WebSocket settings for different environments:

1. Modify the appropriate YAML file (application.yml, application-docker.yml, etc.)
2. Adjust the values as needed
3. Restart the application for the changes to take effect

## Message Persistence

The application persists the following types of messages:

- Regular chat messages (CHAT)
- Join notifications (JOIN)
- Leave notifications (LEAVE)

System messages and user count updates are not persisted.

## REST API Endpoints

The application provides the following REST API endpoints for accessing chat message history:

- `GET /api/chat/messages` - Get all chat messages
- `GET /api/chat/messages/chat` - Get regular chat messages
- `GET /api/chat/messages/type/{type}` - Get messages by type (CHAT, JOIN, LEAVE, ERROR)
- `GET /api/chat/messages/sender/{name}` - Get messages by sender name

## Internationalization (i18n)

The application supports multiple languages through client-side internationalization. Translation files are loaded directly from JSON files in the frontend, eliminating the need for backend API calls.

### Available Languages

- English (en)
- Catalan (ca)

### Translation Files

Translation files are located in `src/main/resources/static/i18n/` as JSON files:

- `messages_en.json` - English translations
- `messages_ca.json` - Catalan translations

### Adding a New Language

To add a new language:

1. Create a new JSON file in the `src/main/resources/static/i18n/` directory named `messages_[language-code].json`
2. Copy the structure from an existing translation file
3. Translate all the strings to the new language
4. Add the language code to the `availableLocales` array in `i18n.js`
5. Add a new option to the language selector in `index.html` if desired

### Internationalization Implementation

The application uses a custom JavaScript internationalization implementation with the following features:

- Automatic language detection based on browser settings
- Manual language selection through UI
- Persistence of language preference in localStorage
- Support for message formatting with placeholders
- Support for plural forms
- Date and time formatting according to the selected locale
- Dynamic UI updates when changing language

## Enhanced User Experience Features

The application includes several features to enhance the user experience:

### Dynamic Button States

- The "Connect" button is disabled until a valid name is entered
- The "Connect" button changes to "Disconnect" when connected
- The "Send" button is disabled until a message is entered

### Message Formatting

- Users can display formatting options by clicking the "Show formatting options" button
- Supported HTML tags include bold, italic, underline, highlight, and strikethrough
- The formatting options panel can be hidden by clicking the "Hide formatting options" button

### Error Handling

- Errors are displayed directly in the chat for better visibility
- Connection errors are clearly indicated with appropriate messages
- XSS protection prevents malicious content from being displayed

### User Count Display

- The number of online users is displayed with proper pluralization
- The count updates dynamically when users join or leave
- The display updates correctly when changing languages

## Microservices Architecture Considerations

This application is designed with a potential migration to microservices in mind. Key architectural decisions include:

1. **Frontend/Backend Separation**: The frontend loads translations directly from static JSON files, reducing coupling with the backend.

2. **Externalized Configuration**: WebSocket and database settings are externalized in YAML files, making it easier to configure different services.

3. **Stateless Communication**: The WebSocket communication is designed to be stateless, with message persistence handled separately.

4. **Service Boundaries**: The application is organized around clear service boundaries (chat, message persistence, etc.) that could become separate microservices.

5. **Consistent Message Structure**: The application uses a consistent message structure between frontend and backend, facilitating communication between different services.

6. **Reduced Dependencies**: The chat message handling logic has been simplified to reduce dependencies on external components like message utilities.

7. **In-Memory Database**: The application uses an in-memory H2 database for development, which can be easily replaced with a distributed database solution in a microservices architecture.

When migrating to microservices, consider the following potential services:

- Authentication Service
- Chat Message Service
- User Presence Service
- Message Persistence Service
- Notification Service
- Internationalization Service

## XSS Protection

The application includes robust XSS protection features to prevent malicious scripts from being executed. These features include:

- **Input Validation**: All user input is validated and sanitized to prevent malicious scripts from being injected.
- **Output Encoding**: All output is encoded to prevent malicious scripts from being executed.
- **Content Security Policy (CSP)**: A CSP is implemented to define which sources of content are allowed to be executed within a web page.
- **OWASP Java HTML Sanitizer**: The OWASP Java HTML Sanitizer is used to sanitize user input and prevent malicious scripts from being injected.

## Development Components

### Frontend Components

- `i18n.js`: Client-side internationalization using modern JavaScript APIs
- `chat.js`: Frontend logic for WebSocket communication and UI interactions
- `index.html`: User interface with internationalization attributes

### Backend Components

- `ChatMessageHandler`: Handles WebSocket messages
- `ChatMessageService`: Business logic for chat messages
- `ChatMessageRepository`: Data access for chat messages
- `WebSocketConfig`: WebSocket configuration
- `MessageValidator`: XSS protection and message validation

### Test Components

- Unit tests for backend components
- Integration tests for API endpoints
- Configuration tests for WebSocket setup
- HTML structure and attribute tests

## Adding Features

Here are some ideas for extending the application:

- User authentication and authorization
- Private messaging between users
- Chat rooms/channels
- File sharing
- Message reactions/emojis
- Read receipts
- User typing indicators
- Message search functionality
- User profiles with avatars
- Push notifications for offline users

## Docker Support

The application includes Docker support for easy containerization and deployment.

### Using Docker Compose

The easiest way to run the application is using Docker Compose:

```bash
# Start the application
docker-compose up -d

# Stop the application
docker-compose down

# Rebuild and start the application
docker-compose up -d --build
```

These commands will handle building the image and running the container with the appropriate configuration.

### Docker Configuration

The Docker configuration includes:

1. **Multi-stage build** for optimized image size
2. **Spring profile for Docker** with specific configuration in `application-docker.yml`
3. **Volume for H2 database** (when using file-based H2 database)

### Notes for Microservices

When migrating to a microservices architecture, each service can have its own Dockerfile and be deployed independently. The current Docker configuration provides a good starting point for containerizing individual microservices.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
