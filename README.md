# AI Tools Java Cursor

A Spring Boot application for managing scheduled tasks and events with date/period-based functionality. This project provides rich context for testing AI tools across different layers of a Java application.

## Project Description

This is a comprehensive scheduling and event management system built with Spring Boot. The application provides robust functionality for managing scheduled tasks, recurring events, and calendar-based operations, making it an excellent foundation for testing AI-assisted development tools and practices.

## Core Features to Implement

### Event Management
- Create, update, delete events with date ranges
- Event categorization and tagging
- Event status tracking (pending, active, completed, cancelled)

### Recurring Tasks
- Support for daily, weekly, monthly recurring tasks
- Custom recurrence patterns
- Task completion tracking and history

### Calendar Integration
- View events by date periods (day, week, month)
- Calendar export functionality
- Integration with external calendar systems

### Notification System
- Alert users about upcoming events
- Email and in-app notifications
- Customizable notification preferences

### Task Dependencies
- Link tasks with prerequisite relationships
- Dependency chain validation
- Automatic task scheduling based on dependencies

## Technology Stack

- **Framework**: Spring Boot 3.2+ (JDK 21)
- **Database**: H2 (for testing) / PostgreSQL (production)
- **Testing**: JUnit 5, Mockito, TestContainers
- **Documentation**: Spring REST Docs
- **Build Tool**: Maven
- **Additional Tools**: 
  - Spring Data JPA
  - Spring Security (planned)
  - Spring WebFlux (for reactive features)
  - Swagger/OpenAPI for API documentation

## Project Structure

```
ai-tools-java-cursor/
├── src/
│   ├── main/
│   │   ├── java/com/checkproof/explore/ai_tools_java_cursor/
│   │   │   ├── AiToolsJavaCursorApplication.java
│   │   │   ├── controller/          # REST API controllers
│   │   │   ├── service/             # Business logic layer
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── model/               # Entity classes
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── util/                # Utility classes
│   │   │   └── config/              # Configuration classes
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/              # Static resources
│   └── test/
│       └── java/com/checkproof/ai_tool_java_cursor/
│           └── ai_tool_java_cursor/
│               └── AiToolJavaCursorApplicationTests.java
├── docs/                            # Documentation
├── README.md
└── pom.xml
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- Git

### Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd ai-tools-java-cursor
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Development Setup

1. Import the project into your preferred IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Ensure you have Java 21 configured
3. Run `mvn clean install` to download dependencies
4. Start the application using your IDE or command line

## API Documentation

Once the application is running, you can access:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/api-docs`

## Testing

Run the test suite:
```bash
mvn test
```

Run tests with coverage:
```bash
mvn test jacoco:report
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Development Guidelines

### Code Style
- Follow Java coding conventions
- Use meaningful variable and method names
- Add comprehensive Javadoc comments
- Keep methods small and focused

### Testing
- Write unit tests for all business logic
- Include integration tests for API endpoints
- Maintain high test coverage (>80%)

### Git Workflow
- Use conventional commit messages
- Create feature branches for new development
- Keep commits atomic and focused

## Future Enhancements

- [ ] User authentication and authorization
- [ ] Real-time notifications using WebSockets
- [ ] Mobile API endpoints
- [ ] Advanced reporting and analytics
- [ ] Integration with external calendar services
- [ ] Multi-tenant support
- [ ] Performance monitoring and metrics

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions, please open an issue in the GitHub repository or contact the development team.

---

**Note**: This project is designed to provide a comprehensive testing ground for AI-assisted development tools. The modular architecture and diverse feature set make it ideal for exploring various aspects of Java development with AI assistance. 