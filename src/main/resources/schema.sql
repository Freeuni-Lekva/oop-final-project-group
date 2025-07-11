SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS UserAnswers;
DROP TABLE IF EXISTS AnswerOptionsMC;
DROP TABLE IF EXISTS AnswerOptionsPR;
DROP TABLE IF EXISTS FillInBlankAnswers;
DROP TABLE IF EXISTS Messages;
DROP TABLE IF EXISTS FriendRequests;
DROP TABLE IF EXISTS Friendships;
DROP TABLE IF EXISTS UserQuizAttempts;
DROP TABLE IF EXISTS Questions;
DROP TABLE IF EXISTS Quizzes;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Achievements;
DROP TABLE IF EXISTS UserAchievements;
DROP TABLE IF EXISTS Announcements;

-- Users Table
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
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
    creation_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
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
    question_type ENUM('QUESTION_RESPONSE', 'FILL_IN_BLANK', 'MULTIPLE_CHOICE', 'PICTURE_RESPONSE', 'MULTIPLE_CHOICE_WITH_MULTIPLE_ANSWERS') NOT NULL,
    image_url VARCHAR(512),
    order_in_quiz INT,
    max_score DOUBLE,
    FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id) ON DELETE CASCADE
);

-- AnswerOptions Table (for Multiple Choice)
CREATE TABLE AnswerOptionsMC (
    option_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE
);

-- AnswerOptions Table (for Picture Response)
CREATE TABLE AnswerOptionsPR (
    option_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    option_text TEXT NOT NULL,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE
);
-- AnswerOptions Table (for Fill in the Blank)
CREATE TABLE FillInBlankAnswers (
    answer_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    blank_index INT NOT NULL,
    acceptable_answer TEXT NOT NULL,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE
);


-- UserQuizAttempts Table
CREATE TABLE UserQuizAttempts (
    attempt_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    quiz_id INT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    score DOUBLE,
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
    FOREIGN KEY (selected_option_id) REFERENCES AnswerOptionsMC(option_id) ON DELETE SET NULL
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
CREATE TABLE Achievements (
    achievement_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    icon_url VARCHAR(255)
);

-- UserAchievements Table
CREATE TABLE UserAchievements (
    user_achievement_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    achievement_id INT NOT NULL,
    earned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES Achievements(achievement_id) ON DELETE CASCADE,
    UNIQUE (user_id, achievement_id)
);

-- Announcements Table
CREATE TABLE Announcements (
    announcement_id INT AUTO_INCREMENT PRIMARY KEY,
    admin_user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content_text TEXT NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (admin_user_id) REFERENCES Users(user_id)
);

-- Initial data for Achievements
INSERT INTO Achievements (name, description) VALUES
('Amateur Author', 'The user created a quiz.'),
('Prolific Author', 'The user created five quizzes.'),
('Prodigious Author', 'The user created ten quizzes.'),
('Quiz Machine', 'The user took ten quizzes.'),
('I am the Greatest', 'The user had the highest score on a quiz.'),
('Practice Makes Perfect', 'The user took a quiz in practice mode.');
-- Sample User
INSERT INTO Users (user_id, username, email, password_hash, salt) VALUES (1, 'testuser', 'test@example.com', 'password', 'salt');

-- Sample Quiz
INSERT INTO Quizzes (quiz_id, creator_user_id, title, description) VALUES (1, 1, 'Java Basics Quiz', 'A simple quiz to test fundamental Java knowledge.');

-- Sample Questions
INSERT INTO Questions (question_id, quiz_id, question_text, question_type, order_in_quiz, max_score) VALUES (1, 1, 'What is the default value of a boolean in Java?', 'MULTIPLE_CHOICE', 0, 1);
INSERT INTO Questions (question_id, quiz_id, question_text, question_type, order_in_quiz, max_score) VALUES (2, 1, 'Which keyword is used to define a constant in Java?', 'MULTIPLE_CHOICE', 1, 1);

-- Sample Options for Question 1
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (1, 'true', FALSE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (1, 'false', TRUE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (1, 'null', FALSE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (1, '0', FALSE);

-- Sample Options for Question 2
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (2, 'const', FALSE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (2, 'static', FALSE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (2, 'final', TRUE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (2, 'let', FALSE);
-- Sample Fill-in-the-Blank Quiz
INSERT INTO Quizzes (quiz_id, creator_user_id, title, description) VALUES (2, 1, 'Georgian History', 'A fill-in-the-blank quiz about the history of Georgia.');

-- Sample Questions for Fill-in-the-Blank Quiz
INSERT INTO Questions (question_id, quiz_id, question_text, question_type, order_in_quiz, max_score) VALUES (3, 2, 'The capital of Georgia is _____ and it was founded by _____.', 'FILL_IN_BLANK', 0, 1);
INSERT INTO Questions (question_id, quiz_id, question_text, question_type, order_in_quiz, max_score) VALUES (4, 2, 'The Golden Age of Georgia was during the reign of Queen _____.', 'FILL_IN_BLANK', 1, 1);

-- Sample Answers for Question 3
INSERT INTO FillInBlankAnswers (question_id, blank_index, acceptable_answer) VALUES (3, 0, 'Tbilisi');
INSERT INTO FillInBlankAnswers (question_id, blank_index, acceptable_answer) VALUES (3, 1, 'Vakhtang Gorgasali');

-- Sample Answers for Question 4
INSERT INTO FillInBlankAnswers (question_id, blank_index, acceptable_answer) VALUES (4, 0, 'Tamar');
INSERT INTO FillInBlankAnswers (question_id, blank_index, acceptable_answer) VALUES (4, 0, 'Thamar');


-- Sample Multiple-Choice Quiz (Geography)
INSERT INTO Quizzes (quiz_id, creator_user_id, title, description) VALUES (3, 1, 'World Capitals', 'Test your knowledge of world capitals.');

-- Sample Questions for Geography Quiz
INSERT INTO Questions (question_id, quiz_id, question_text, question_type, order_in_quiz, max_score) VALUES (5, 3, 'What is the capital of Canada?', 'MULTIPLE_CHOICE', 0, 1);
INSERT INTO Questions (question_id, quiz_id, question_text, question_type, order_in_quiz, max_score) VALUES (6, 3, 'What is the capital of Australia?', 'MULTIPLE_CHOICE', 1, 1);

-- Sample Options for Question 5
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (5, 'Toronto', FALSE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (5, 'Vancouver', FALSE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (5, 'Ottawa', TRUE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (5, 'Montreal', FALSE);

-- Sample Options for Question 6
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (6, 'Sydney', FALSE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (6, 'Melbourne', FALSE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (6, 'Canberra', TRUE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (6, 'Perth', FALSE);
-- Sample Multi-Answer Quiz
INSERT INTO Quizzes (quiz_id, creator_user_id, title, description) VALUES (4, 1, 'European Geography', 'Select all correct options.');

-- Sample Questions for Multi-Answer Quiz
INSERT INTO Questions (question_id, quiz_id, question_text, question_type, order_in_quiz, max_score) VALUES (7, 4, 'Which of the following are countries in the European Union?', 'MULTIPLE_CHOICE_WITH_MULTIPLE_ANSWERS', 0, 1);
INSERT INTO Questions (question_id, quiz_id, question_text, question_type, order_in_quiz, max_score) VALUES (8, 4, 'Which of these cities are capitals of EU countries?', 'MULTIPLE_CHOICE_WITH_MULTIPLE_ANSWERS', 1, 1);

-- Sample Options for Question 7
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (7, 'Germany', TRUE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (7, 'Switzerland', FALSE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (7, 'France', TRUE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (7, 'Norway', FALSE);

-- Sample Options for Question 8
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (8, 'Paris', TRUE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (8, 'London', FALSE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (8, 'Berlin', TRUE);
INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (8, 'Zurich', FALSE);

SET FOREIGN_KEY_CHECKS=1;
