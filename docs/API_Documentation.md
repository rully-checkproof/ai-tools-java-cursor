# AI Tools Java Cursor - API Documentation

## Overview

This document provides comprehensive documentation for the AI Tools Java Cursor application, focusing on the Task Management and Event Management APIs. The application is built using Spring Boot with a layered architecture that includes controllers, services, repositories, and validation layers.

## Architecture Overview

```mermaid
graph TB
    Client[Client Application] --> Controller[Controller Layer]
    Controller --> Service[Service Layer]
    Service --> Repository[Repository Layer]
    Service --> Validator[Validation Layer]
    Repository --> Database[(Database)]
    
    subgraph "Validation Layer"
        Validator --> EventValidator[Event Validator]
        Validator --> TaskValidator[Task Validator]
        Validator --> BusinessHoursValidator[Business Hours Validator]
        Validator --> ValidDateRangeValidator[Date Range Validator]
    end
    
    subgraph "Service Layer"
        Service --> EventService[Event Service]
        Service --> TaskService[Task Service]
    end
    
    subgraph "Repository Layer"
        Repository --> EventRepository[Event Repository]
        Repository --> TaskRepository[Task Repository]
        Repository --> ParticipantRepository[Participant Repository]
    end
```

## Event Creation Flow

The event creation process involves multiple validation layers and conflict checking to ensure data integrity and prevent scheduling conflicts.

### Event Creation Sequence Diagram

```mermaid
sequenceDiagram
    participant Client
    participant EventController
    participant EventService
    participant EventValidator
    participant EventRepository
    participant Database
    
    Client->>EventController: POST /api/events
    Note over Client,EventController: EventDto with validation annotations
    
    EventController->>EventValidator: @Valid validation
    EventValidator->>EventValidator: Validate basic fields
    EventValidator->>EventValidator: Validate event date
    EventValidator->>EventValidator: Validate duration
    EventValidator->>EventValidator: Validate category
    EventValidator->>EventValidator: Validate participants
    EventValidator->>EventValidator: Validate business rules
    
    alt Validation fails
        EventValidator-->>EventController: Validation errors
        EventController-->>Client: 400 Bad Request
    else Validation passes
        EventController->>EventService: createEvent(eventDto)
        
        EventService->>EventService: Validate DTO for creation
        EventService->>EventService: Check for time conflicts
        EventService->>EventService: hasTimeConflict(eventDto)
        
        alt Time conflict detected
            EventService-->>EventController: EventOverlapException
            EventController-->>Client: 409 Conflict
        else No conflicts
            EventService->>EventService: Convert DTO to entity
            EventService->>EventRepository: save(event)
            EventRepository->>Database: INSERT event
            Database-->>EventRepository: Saved event
            EventRepository-->>EventService: Event entity
            EventService->>EventService: Convert entity to DTO
            EventService-->>EventController: EventDto
            EventController-->>Client: 201 Created + EventDto
        end
    end
```

### Event Creation Flow Diagram

```mermaid
flowchart TD
    A[Client sends POST request] --> B{Request validation}
    B -->|Invalid| C[Return 400 Bad Request]
    B -->|Valid| D[EventValidator.validateEvent]
    
    D --> E{Basic field validation}
    E -->|Fail| F[Add field errors]
    E -->|Pass| G{Event date validation}
    
    G -->|Fail| F
    G -->|Pass| H{Duration validation}
    
    H -->|Fail| F
    H -->|Pass| I{Category validation}
    
    I -->|Fail| F
    I -->|Pass| J{Participant validation}
    
    J -->|Fail| F
    J -->|Pass| K{Business rules validation}
    
    K -->|Fail| F
    K -->|Pass| L[EventService.createEvent]
    
    L --> M{Time conflict check}
    M -->|Conflict found| N[Throw EventOverlapException]
    M -->|No conflict| O[Save event to database]
    
    N --> P[Return 409 Conflict]
    O --> Q[Return 201 Created]
    
    F --> R[Return 400 with error details]
```

### Event Validation Rules

```mermaid
graph LR
    A[Event Input] --> B[Basic Validation]
    B --> C[Date Validation]
    C --> D[Duration Validation]
    D --> E[Category Validation]
    E --> F[Participant Validation]
    F --> G[Business Rules]
    
    B --> B1[Name required & ≤255 chars]
    B --> B2[Description ≤1000 chars]
    B --> B3[Event date required]
    
    C --> C1[Date not in past]
    C --> C2[Date not >2 years future]
    C --> C3[Business events not on weekends]
    
    D --> D1[Duration 15-1440 minutes]
    D --> D2[No overnight events]
    D --> D3[Category-specific duration rules]
    
    E --> E1[Category required]
    E --> E2[Conference: ≥60 min, ≥3 participants]
    E --> E3[Workshop: ≥30 min, ≤50 participants]
    E --> E4[Meeting: ≤480 min, ≤20 participants]
    
    F --> F1[≤100 participants]
    F --> F2[No duplicate participants]
    
    G --> G1[Business hours 8AM-6PM]
    G --> G2[Minimum 2 hours notice]
    G --> G3[Social events 6AM-11PM]
```

## Task Creation Flow

The task creation process includes validation for business rules, priority/status combinations, and recurrence patterns.

### Task Creation Sequence Diagram

```mermaid
sequenceDiagram
    participant Client
    participant TaskController
    participant TaskService
    participant TaskValidator
    participant TaskRepository
    participant Database
    
    Client->>TaskController: POST /api/tasks
    Note over Client,TaskController: TaskDto with validation annotations
    
    TaskController->>TaskValidator: @Valid validation
    TaskValidator->>TaskValidator: Validate basic fields
    TaskValidator->>TaskValidator: Validate date range
    TaskValidator->>TaskValidator: Validate priority & status
    TaskValidator->>TaskValidator: Validate recurrence pattern
    TaskValidator->>TaskValidator: Validate business rules
    
    alt Validation fails
        TaskValidator-->>TaskController: Validation errors
        TaskController-->>Client: 400 Bad Request
    else Validation passes
        TaskController->>TaskService: createTask(taskDto)
        
        TaskService->>TaskService: Convert DTO to entity
        TaskService->>TaskService: validateTask(task)
        TaskService->>TaskService: validateNoOverlappingTasks(task)
        
        alt Validation fails
            TaskService-->>TaskController: InvalidTaskException
            TaskController-->>Client: 400 Bad Request
        else Validation passes
            TaskService->>TaskRepository: save(task)
            TaskRepository->>Database: INSERT task
            Database-->>TaskRepository: Saved task
            TaskRepository-->>TaskService: Task entity
            TaskService->>TaskService: Convert entity to DTO
            TaskService-->>TaskController: TaskDto
            TaskController-->>Client: 201 Created + TaskDto
        end
    end
```

### Task Creation Flow Diagram

```mermaid
flowchart TD
    A[Client sends POST request] --> B{Request validation}
    B -->|Invalid| C[Return 400 Bad Request]
    B -->|Valid| D[TaskValidator.validateTask]
    
    D --> E{Basic field validation}
    E -->|Fail| F[Add field errors]
    E -->|Pass| G{Date range validation}
    
    G -->|Fail| F
    G -->|Pass| H{Priority & status validation}
    
    H -->|Fail| F
    H -->|Pass| I{Recurrence pattern validation}
    
    I -->|Fail| F
    I -->|Pass| J{Business rules validation}
    
    J -->|Fail| F
    J -->|Pass| K[TaskService.createTask]
    
    K --> L{Task entity validation}
    L -->|Fail| M[Throw InvalidTaskException]
    L -->|Pass| N{Overlap validation}
    
    N -->|Overlap found| O[Throw TaskOverlapException]
    N -->|No overlap| P[Save task to database]
    
    M --> Q[Return 400 Bad Request]
    O --> R[Return 409 Conflict]
    P --> S[Return 201 Created]
    
    F --> T[Return 400 with error details]
```

### Task Validation Rules

```mermaid
graph LR
    A[Task Input] --> B[Basic Validation]
    B --> C[Date Range Validation]
    C --> D[Priority & Status Validation]
    D --> E[Recurrence Validation]
    E --> F[Business Rules]
    
    B --> B1[Title required & ≤255 chars]
    B --> B2[Description ≤1000 chars]
    B --> B3[Start date required]
    
    C --> C1[Start date not in past]
    C --> C2[End date after start date]
    C --> C3[Duration ≤1 year]
    
    D --> D1[High priority not cancelled]
    D --> D2[Completed not urgent priority]
    
    E --> E1[Recurrence interval >0]
    E --> E2[Recurrence interval ≤100]
    E --> E3[End date suitable for pattern]
    
    F --> F1[No weekend scheduling]
    F --> F2[Business hours 8AM-6PM]
    F --> F3[≤10 participants]
```

## API Endpoints

### Event Management Endpoints

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| POST | `/api/events` | Create new event | 201, 400, 409 |
| PUT | `/api/events/{id}` | Update existing event | 200, 400, 404, 409 |
| DELETE | `/api/events/{id}` | Cancel/delete event | 204, 404 |
| GET | `/api/events/{id}` | Get event by ID | 200, 404 |
| GET | `/api/events` | Get all events (paginated) | 200, 400 |
| GET | `/api/events/search` | Search events | 200, 400 |
| GET | `/api/events/upcoming` | Get upcoming events | 200, 400 |
| GET | `/api/events/calendar/{year}/{month}` | Monthly calendar view | 200, 400 |
| GET | `/api/events/week/{date}` | Weekly calendar view | 200, 400 |
| GET | `/api/events/day/{date}` | Daily calendar view | 200, 400 |
| POST | `/api/events/check-conflicts` | Check for conflicts | 200, 400 |
| GET | `/api/events/time-slot-available` | Check time slot availability | 200, 400 |

### Task Management Endpoints

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| POST | `/api/tasks` | Create new task | 201, 400, 409 |
| PUT | `/api/tasks/{id}` | Update existing task | 200, 400, 404, 409 |
| DELETE | `/api/tasks/{id}` | Delete task | 204, 404 |
| GET | `/api/tasks/{id}` | Get task by ID | 200, 404 |
| GET | `/api/tasks` | Get all tasks (paginated) | 200, 400 |
| GET | `/api/tasks/search` | Search tasks by title | 200 |
| GET | `/api/tasks/upcoming` | Get upcoming tasks | 200, 400 |
| GET | `/api/tasks/date-range` | Get tasks in date range | 200, 400 |
| GET | `/api/tasks/status/{status}` | Get tasks by status | 200, 400 |
| GET | `/api/tasks/priority/{priority}` | Get tasks by priority | 200, 400 |
| GET | `/api/tasks/overdue` | Get overdue tasks | 200 |
| PATCH | `/api/tasks/{id}/status/{status}` | Update task status | 200, 404 |
| PATCH | `/api/tasks/{id}/complete` | Mark task as completed | 200, 404 |
| PATCH | `/api/tasks/{id}/start` | Start task | 200, 404 |
| PATCH | `/api/tasks/{id}/cancel` | Cancel task | 200, 404 |
| PATCH | `/api/tasks/{id}/hold` | Put task on hold | 200, 404 |

## Data Models

### Event Model

```mermaid
classDiagram
    class Event {
        +Long id
        +String name
        +String description
        +LocalDateTime eventDate
        +Integer durationMinutes
        +EventCategory category
        +Set~Participant~ participants
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
        +Duration getDuration()
        +void setDuration(Duration)
    }
    
    class EventCategory {
        <<enumeration>>
        GENERAL
        MEETING
        CONFERENCE
        WORKSHOP
        SOCIAL
        BUSINESS
        PERSONAL
    }
    
    Event --> EventCategory
```

### Task Model

```mermaid
classDiagram
    class Task {
        +Long id
        +String title
        +String description
        +LocalDateTime startDate
        +LocalDateTime endDate
        +Priority priority
        +TaskStatus status
        +RecurrencePattern recurrencePattern
        +Set~Participant~ participants
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
        +boolean isRecurring()
        +RecurrenceType getRecurrenceType()
    }
    
    class Priority {
        <<enumeration>>
        LOW
        MEDIUM
        HIGH
        URGENT
    }
    
    class TaskStatus {
        <<enumeration>>
        PENDING
        IN_PROGRESS
        COMPLETED
        CANCELLED
        ON_HOLD
    }
    
    class RecurrencePattern {
        +Long id
        +RecurrenceType recurrenceType
        +Integer interval
        +LocalDate startDate
        +LocalDate endDate
        +Integer maxOccurrences
        +Set~DayOfWeek~ daysOfWeek
        +Integer dayOfMonth
        +Integer weekOfMonth
        +Integer monthOfYear
    }
    
    Task --> Priority
    Task --> TaskStatus
    Task --> RecurrencePattern
```

## Error Handling

### Common Error Responses

```mermaid
graph TD
    A[API Request] --> B{Validation}
    B -->|Pass| C[Process Request]
    B -->|Fail| D[400 Bad Request]
    
    C --> E{Business Logic}
    E -->|Success| F[200/201 Success]
    E -->|Conflict| G[409 Conflict]
    E -->|Not Found| H[404 Not Found]
    E -->|Error| I[500 Internal Server Error]
    
    D --> J[Return validation errors]
    G --> K[Return conflict details]
    H --> L[Return not found message]
    I --> M[Return error message]
```

### Validation Error Structure

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "errors": [
    {
      "field": "eventDate",
      "message": "Event date cannot be in the past",
      "code": "event.date.past"
    },
    {
      "field": "durationMinutes",
      "message": "Event duration must be at least 15 minutes",
      "code": "event.duration.too.short"
    }
  ]
}
```

## Business Rules Summary

### Event Business Rules

1. **Time Constraints**
   - Events cannot be scheduled in the past
   - Events cannot be scheduled more than 2 years in advance
   - Minimum 2 hours notice required
   - No overnight events (spanning multiple days)

2. **Duration Rules**
   - Minimum duration: 15 minutes
   - Maximum duration: 24 hours (1440 minutes)
   - Category-specific duration requirements

3. **Business Hours**
   - Business events: 8 AM - 6 PM
   - Social events: 6 AM - 11 PM
   - No business events on weekends

4. **Participant Limits**
   - Maximum 100 participants per event
   - No duplicate participants
   - Category-specific participant requirements

### Task Business Rules

1. **Time Constraints**
   - Tasks cannot be scheduled in the past
   - Maximum duration: 1 year
   - No weekend scheduling
   - Business hours: 8 AM - 6 PM

2. **Priority & Status Rules**
   - High priority tasks cannot be cancelled
   - Completed tasks cannot have urgent priority
   - Valid status transitions enforced

3. **Recurrence Rules**
   - Recurrence interval: 1-100
   - End date must be suitable for recurrence pattern
   - Maximum 100 recurring instances

4. **Participant Limits**
   - Maximum 10 participants per task

## Testing Strategy

The application includes comprehensive unit tests for all validation classes:

- **BusinessHoursValidatorTest**: Tests business hours validation logic
- **ValidDateRangeValidatorTest**: Tests date range validation
- **EventValidatorTest**: Tests event-specific validation rules
- **TaskValidatorTest**: Tests task-specific validation rules

All tests use JUnit 5 and Mockito for mocking dependencies.

## Security Considerations

1. **Input Validation**: All inputs are validated using Bean Validation annotations and custom validators
2. **SQL Injection Prevention**: Using JPA repositories with parameterized queries
3. **XSS Prevention**: Input sanitization and proper content type headers
4. **Rate Limiting**: Consider implementing rate limiting for API endpoints
5. **Authentication**: Consider adding authentication and authorization layers

## Performance Considerations

1. **Database Indexing**: Ensure proper indexes on frequently queried fields
2. **Pagination**: All list endpoints support pagination
3. **Caching**: Consider implementing caching for frequently accessed data
4. **Connection Pooling**: Configure appropriate database connection pool settings
5. **Async Processing**: Consider async processing for heavy operations like conflict checking

## Monitoring and Logging

1. **Structured Logging**: Using SLF4J with proper log levels
2. **Performance Metrics**: Consider adding metrics for API response times
3. **Error Tracking**: Implement proper error tracking and alerting
4. **Health Checks**: Implement health check endpoints
5. **Audit Trail**: Consider adding audit logging for data changes 