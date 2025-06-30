-- Drop tables in reverse order
DROP TABLE IF EXISTS task_participants;
DROP TABLE IF EXISTS event_participants;
DROP TABLE IF EXISTS recurrence_pattern_days_of_week;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS participants;
DROP TABLE IF EXISTS recurrence_patterns;

-- Create tables in correct order
CREATE TABLE IF NOT EXISTS recurrence_patterns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recurrence_type VARCHAR(255) NOT NULL,
    interval_value INTEGER NOT NULL DEFAULT 1,
    start_date DATE NOT NULL,
    end_date DATE,
    max_occurrences INTEGER,
    day_of_month INTEGER,
    week_of_month INTEGER,
    month_of_year INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recurrence_pattern_days_of_week (
    recurrence_pattern_id BIGINT,
    day_of_week VARCHAR(255),
    FOREIGN KEY (recurrence_pattern_id) REFERENCES recurrence_patterns(id)
);

CREATE TABLE IF NOT EXISTS participants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(20),
    bio TEXT,
    type VARCHAR(255) NOT NULL DEFAULT 'INDIVIDUAL',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    priority VARCHAR(255) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    recurrence_pattern_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (recurrence_pattern_id) REFERENCES recurrence_patterns(id)
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    duration_minutes INTEGER,
    category VARCHAR(255) NOT NULL DEFAULT 'GENERAL',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS task_participants (
    task_id BIGINT,
    participant_id BIGINT,
    PRIMARY KEY (task_id, participant_id),
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (participant_id) REFERENCES participants(id)
);

CREATE TABLE IF NOT EXISTS event_participants (
    event_id BIGINT,
    participant_id BIGINT,
    PRIMARY KEY (event_id, participant_id),
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (participant_id) REFERENCES participants(id)
);