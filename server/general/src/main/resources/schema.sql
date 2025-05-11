CREATE TYPE role_type AS ENUM ('PARTICIPANT', 'CAPTAIN', 'MODERATOR');
CREATE TYPE priority_type AS ENUM ('LOW', 'MEDIUM', 'HIGH');

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role role_type NOT NULL,
    name VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Links table
CREATE TABLE links (
    id UUID PRIMARY KEY,
    url VARCHAR(2048) NOT NULL,
    title VARCHAR(255) NOT NULL,
    entity_id UUID NOT NULL,
    entity_type VARCHAR(50) NOT NULL
);

-- Teams table
CREATE TABLE teams (
    id UUID PRIMARY KEY,
    link_id UUID REFERENCES links(id),
    name VARCHAR(255)
);

-- User-Team relationship table
CREATE TABLE user_teams (
    user_id UUID REFERENCES users(id),
    team_id UUID REFERENCES teams(id),
    role VARCHAR(20) NOT NULL DEFAULT 'PARTICIPANT',
    PRIMARY KEY (user_id, team_id)
);

-- Hackathons table
CREATE TABLE hackathons (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    date_of_register TIMESTAMP WITH TIME ZONE,
    date_of_start TIMESTAMP WITH TIME ZONE,
    date_of_end TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- KanbanStatus table
CREATE TABLE kanban_statuses (
    id UUID PRIMARY KEY,
    next_id UUID REFERENCES kanban_statuses(id),
    name VARCHAR(255) NOT NULL,
    color VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Tasks table
CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    numeric_id SERIAL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    priority priority_type NOT NULL,
    link_id UUID REFERENCES links(id),
    user_id UUID REFERENCES users(id),
    status_id UUID REFERENCES kanban_statuses(id) NOT NULL,
    hackathon_id UUID REFERENCES hackathons(id),
    due_date TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Deadlines table
CREATE TABLE deadlines (
    id UUID PRIMARY KEY,
    date DATE,
    link_id UUID REFERENCES links(id),
    name VARCHAR(255),
    type VARCHAR(50)
);

-- Chat messages table
CREATE TABLE chat_messages (
    id SERIAL PRIMARY KEY,
    sender VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    timestamp BIGINT NOT NULL,
    hackathon_id UUID REFERENCES hackathons(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Hackathon-Deadline many-to-many relationship
CREATE TABLE hackathon_deadlines (
    hackathon_id UUID REFERENCES hackathons(id) ON DELETE CASCADE,
    deadline_id UUID REFERENCES deadlines(id) ON DELETE CASCADE,
    PRIMARY KEY (hackathon_id, deadline_id)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_links_entity ON links(entity_id, entity_type);
CREATE INDEX idx_tasks_user ON tasks(user_id);
CREATE INDEX idx_tasks_status ON tasks(status_id);
CREATE INDEX idx_tasks_hackathon ON tasks(hackathon_id);
CREATE INDEX idx_chat_messages_hackathon ON chat_messages(hackathon_id);
CREATE INDEX idx_user_teams_user ON user_teams(user_id);
CREATE INDEX idx_user_teams_team ON user_teams(team_id);
CREATE INDEX idx_chat_messages_timestamp ON chat_messages(timestamp); 