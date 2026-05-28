DROP DATABASE IF EXISTS gym_db;
CREATE DATABASE gym_db;
USE gym_db;

CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(50) NOT NULL,
                       is_active BOOLEAN NOT NULL
);

CREATE TABLE training_type (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               training_type_name VARCHAR(50) NOT NULL
);

CREATE TABLE trainee (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         user_id BIGINT NOT NULL,
                         date_of_birth DATE,
                         address VARCHAR(100),
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE trainer (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         user_id BIGINT NOT NULL,
                         specialization BIGINT,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (specialization) REFERENCES training_type(id)
);

CREATE TABLE training (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          trainee_id BIGINT NOT NULL,
                          trainer_id BIGINT NOT NULL,
                          training_name VARCHAR(255) NOT NULL,
                          training_type_id BIGINT NOT NULL,
                          training_date DATE NOT NULL,
                          training_duration INT NOT NULL,
                          FOREIGN KEY (trainee_id) REFERENCES trainee(id) ON DELETE CASCADE,
                          FOREIGN KEY (trainer_id) REFERENCES trainer(id) ON DELETE CASCADE,
                          FOREIGN KEY (training_type_id) REFERENCES training_type(id)
);

CREATE TABLE trainee_trainer (
                                 trainee_id BIGINT NOT NULL,
                                 trainer_id BIGINT NOT NULL,
                                 PRIMARY KEY (trainee_id, trainer_id),
                                 FOREIGN KEY (trainee_id) REFERENCES trainee(id) ON DELETE CASCADE,
                                 FOREIGN KEY (trainer_id) REFERENCES trainer(id) ON DELETE CASCADE
);

-- Insert sample data
INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES ('John', 'Doe', 'john.doe', 'password123', true),
       ('Alice', 'Smith', 'alice.smith', 'password123', true),
       ('Bob', 'Johnson', 'bob.johnson', 'password123', true);

INSERT INTO training_type (training_type_name)
VALUES ('Yoga'), ('Gym'), ('Cardio');

INSERT INTO trainee (user_id, date_of_birth, address)
VALUES (1, '1990-01-01', '123 Main St');

INSERT INTO trainer (user_id, specialization)
VALUES (2, 1), (3, 2);