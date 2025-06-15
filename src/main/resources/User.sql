
DROP TABLE IF EXISTS user;

CREATE TABLE user (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(64) NOT NULL,
                       password VARBINARY(256),
                       salt VARBINARY(256)
);
