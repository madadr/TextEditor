CREATE TABLE IF NOT EXISTS address (
  id_user int(10) NOT NULL,
  address varchar(50) NOT NULL,
  region varchar(50) NOT NULL,
  zip_code varchar(6) NOT NULL,
  KEY id_user (id_user)
);

CREATE TABLE IF NOT EXISTS user_data (
  id_user int(10) NOT NULL,
  first_name varchar(30) NOT NULL,
  last_name varchar(40) NOT NULL,
  email varchar(50) NOT NULL,
  KEY id_user (id_user)
);

CREATE TABLE IF NOT EXISTS friend_list (
  id_user int(10) NOT NULL,
  id_friend int(10) NOT NULL,
  KEY id_user (id_user),
  KEY id_friend (id_friend)
);

CREATE TABLE IF NOT EXISTS project (
  id_project int(10) NOT NULL AUTO_INCREMENT,
  name varchar(30) NOT NULL,
  description varchar(250) NOT NULL,
  date_of_creation date NOT NULL,
  PRIMARY KEY (id_project)
);

CREATE TABLE IF NOT EXISTS users (
  id_user int(10) NOT NULL AUTO_INCREMENT,
  login varchar(30) NOT NULL,
  password varchar(50) NOT NULL,
  PRIMARY KEY (id_user)
);

CREATE TABLE IF NOT EXISTS user_project (
  id_user int(10) NOT NULL,
  id_project int(10) NOT NULL,
  KEY id_user (id_user),
  KEY id_project (id_project)
);


ALTER TABLE address
  ADD CONSTRAINT address_ibfk_1 FOREIGN KEY IF NOT EXISTS(id_user) REFERENCES users (id_user);

ALTER TABLE user_data
  ADD CONSTRAINT user_data_ibfk_1 FOREIGN KEY IF NOT EXISTS(id_user) REFERENCES users (id_user);

ALTER TABLE friend_list
  ADD CONSTRAINT friend_list_ibfk_1 FOREIGN KEY IF NOT EXISTS(id_user) REFERENCES users (id_user),
  ADD CONSTRAINT friend_list_ibfk_2 FOREIGN KEY IF NOT EXISTS(id_friend) REFERENCES users (id_user);

ALTER TABLE user_project
  ADD CONSTRAINT user_project_ibfk_1 FOREIGN KEY IF NOT EXISTS(id_user) REFERENCES users (id_user),
  ADD CONSTRAINT user_project_ibfk_2 FOREIGN KEY IF NOT EXISTS(id_project) REFERENCES project (id_project);
COMMIT;
