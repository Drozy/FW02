-- БД для программы, имитирующей работу реестра домашних животных
DROP DATABASE IF EXISTS pet_registry;
CREATE DATABASE pet_registry;
USE pet_registry;

-- виды домашних животных
DROP TABLE IF EXISTS animal_types;
CREATE TABLE animal_types
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	type_name VARCHAR (20)
);

INSERT INTO animal_types (type_name)
VALUES 
	('Кошка'),
	('Собака'),
	('Хомяк');

-- домашние животные
DROP TABLE IF EXISTS pet_list;
CREATE TABLE pet_list
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	animal_name VARCHAR(20),
	dateofbirth DATE,
	type_id int,
	FOREIGN KEY (type_id) REFERENCES animal_types (id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO pet_list (animal_name, dateofbirth, type_id)
VALUES ('Мурзик', '2019-01-01', 1),
	('Васька', '2016-01-01', 1),
	('Леопольд', '2012-01-01', 1),
    ('Дик', '2020-01-01', 2),
	('Граф', '2021-06-12', 2),  
	('Шарик', '2018-05-01', 2), 
	('Босс', '2021-05-10', 2),
    ('Малой', '2020-10-12', 3),
	('Медведь', '2021-03-12', 3),
	('Ниндзя', '2022-07-11', 3),
	('Бурый', '2022-05-10', 3);


-- перечень команд
DROP TABLE IF EXISTS commands;
CREATE TABLE commands
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	command_name VARCHAR (20)
);

INSERT INTO commands (command_name)
VALUES ('сидеть'),
	('лежать'),
    ('место'),
    ('рядом'),
    ('ко мне'),
    ('лапу'),
    ('голос'),
    ('фас'),
    ('замри');

-- команды, выполнимые видом
DROP TABLE IF EXISTS spec_commands;
CREATE TABLE spec_commands
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	type_id INT,
    command_id INT,
	FOREIGN KEY (type_id) REFERENCES animal_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (command_id) REFERENCES commands(id) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO spec_commands (type_id, command_id)
VALUES (1, 1), (2, 1), (1, 2), (2, 2), (2, 3), (2, 4), (1, 5), (2, 5), (2, 6), (2, 7), (2, 8), (3, 9);

-- команды животных
DROP TABLE IF EXISTS pet_commands;
CREATE TABLE pet_commands
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	pet_id INT,
    command_id INT,
	FOREIGN KEY (pet_id) REFERENCES pet_list(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (command_id) REFERENCES commands(id) ON UPDATE CASCADE ON DELETE CASCADE
);
