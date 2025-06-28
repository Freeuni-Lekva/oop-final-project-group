SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS UserAnswers;
DROP TABLE IF EXISTS AnswerOptions;
DROP TABLE IF EXISTS Messages;
DROP TABLE IF EXISTS FriendRequests;
DROP TABLE IF EXISTS Friendships;
DROP TABLE IF EXISTS UserQuizAttempts;
DROP TABLE IF EXISTS Questions;
DROP TABLE IF EXISTS Quizzes;
DROP TABLE IF EXISTS Users;

-- Users Table
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE DEFAULT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(100) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER' NOT NULL
);

-- Friendships Table
CREATE TABLE Friendships (
    user_id1 INT NOT NULL,
    user_id2 INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id1, user_id2),
    FOREIGN KEY (user_id1) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id2) REFERENCES Users(user_id) ON DELETE CASCADE,
    CHECK (user_id1 < user_id2)
);

-- FriendRequests Table
CREATE TABLE FriendRequests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    requester_id INT NOT NULL,
    recipient_id INT NOT NULL,
    status ENUM('pending', 'accepted', 'rejected') NOT NULL DEFAULT 'pending',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    CHECK (requester_id <> recipient_id)
);

-- Quizzes Table
CREATE TABLE Quizzes (
    quiz_id INT AUTO_INCREMENT PRIMARY KEY,
    creator_user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_random_order BOOLEAN DEFAULT FALSE,
    display_type ENUM('SINGLE_PAGE', 'MULTI_PAGE_QUESTION') DEFAULT 'SINGLE_PAGE',
    is_immediate_correction BOOLEAN DEFAULT FALSE,
    is_practice_mode_enabled BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (creator_user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- Questions Table
CREATE TABLE Questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    question_text TEXT NOT NULL,
    question_type ENUM('QUESTION_RESPONSE', 'FILL_IN_BLANK', 'MULTIPLE_CHOICE', 'PICTURE_RESPONSE', 'MULTI_ANSWER_ORDERED', 'MULTI_ANSWER_UNORDERED') NOT NULL,
    image_url VARCHAR(512),
    order_in_quiz INT,
    FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id) ON DELETE CASCADE
);

-- AnswerOptions Table (for Multiple Choice)
CREATE TABLE AnswerOptions (
    option_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE
);

-- UserQuizAttempts Table
CREATE TABLE UserQuizAttempts (
    attempt_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    quiz_id INT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    score INT,
    time_taken_seconds INT,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id) ON DELETE CASCADE
);

-- UserAnswers Table
CREATE TABLE UserAnswers (
    user_answer_id INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id INT NOT NULL,
    question_id INT NOT NULL,
    answer_given_text TEXT,
    selected_option_id INT,
    is_correct BOOLEAN,
    FOREIGN KEY (attempt_id) REFERENCES UserQuizAttempts(attempt_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE,
    FOREIGN KEY (selected_option_id) REFERENCES AnswerOptions(option_id) ON DELETE SET NULL
);

-- Messages Table
CREATE TABLE Messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    message_type ENUM('FRIEND_REQUEST', 'CHALLENGE', 'NOTE') NOT NULL,
    content_text TEXT,
    related_quiz_id INT,
    sent_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (sender_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (related_quiz_id) REFERENCES Quizzes(quiz_id) ON DELETE SET NULL
);

-- Achievements Table
-- CREATE TABLE Achievements (
--     achievement_id INT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(100) NOT NULL UNIQUE,
--     description TEXT NOT NULL,
--     icon_url VARCHAR(255)
-- );

-- UserAchievements Table
-- CREATE TABLE UserAchievements (
--     user_achievement_id INT AUTO_INCREMENT PRIMARY KEY,
--     user_id INT NOT NULL,
--     achievement_id INT NOT NULL,
--     earned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
--     FOREIGN KEY (achievement_id) REFERENCES Achievements(achievement_id) ON DELETE CASCADE,
--     UNIQUE (user_id, achievement_id)
-- );

-- Announcements Table
-- CREATE TABLE Announcements (
--     announcement_id INT AUTO_INCREMENT PRIMARY KEY,
--     admin_user_id INT NOT NULL,
--     title VARCHAR(255) NOT NULL,
--     content_text TEXT NOT NULL,
--     creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     is_active BOOLEAN DEFAULT TRUE,
--     FOREIGN KEY (admin_user_id) REFERENCES Users(user_id)
-- );

-- Initial data for Achievements
-- INSERT INTO Achievements (name, description) VALUES
-- ('Amateur Author', 'The user created a quiz.'),
-- ('Prolific Author', 'The user created five quizzes.'),
-- ('Prodigious Author', 'The user created ten quizzes.'),
-- ('Quiz Machine', 'The user took ten quizzes.'),
-- ('I am the Greatest', 'The user had the highest score on a quiz.'),
-- ('Practice Makes Perfect', 'The user took a quiz in practice mode.');
SET FOREIGN_KEY_CHECKS=1;