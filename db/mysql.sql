DROP SCHEMA IF EXISTS re_svc_user_mgt;
CREATE SCHEMA IF NOT EXISTS re_svc_user_mgt DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_bin;
USE re_svc_user_mgt;

/*
Note about creating index:
#1071 - Specified key was too long; max key length is 767 bytes
http://stackoverflow.com/questions/1814532/1071-specified-key-was-too-long-max-key-length-is-767-bytes
*/

/* Core tables ---------------------------------------------------------------*/

CREATE TABLE admins(
  id INT NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

  username VARCHAR(1024) NOT NULL,  /* A URI, email, or text username */
  auth_type INT NOT NULL,           /* A short integer representing the type of authorization system */
  password VARCHAR(256) NOT NULL,

  PRIMARY KEY (id),
  KEY index_username (username(255))
) ENGINE=InnoDB;

CREATE TABLE clients(
  id INT NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

  name VARCHAR(1024) NOT NULL,

  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE users(
  id INT NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

  enabled TINYINT NOT NULL DEFAULT 0,

  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE credentials(
  id INT NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

  user_id INT NOT NULL,

  username VARCHAR(1024) NOT NULL,  /* A URI, email, or text username */
  auth_type INT NOT NULL,           /* A short integer representing the type of authorization system */
  password VARCHAR(256) NOT NULL,
  validated TINYINT NOT NULL DEFAULT 0,

  PRIMARY KEY (id),
  KEY index_user_id (user_id),
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

/* Log tables ----------------------------------------------------------------*/

/* TODO */

/* Bootstrap data ------------------------------------------------------------*/

INSERT INTO clients(created_at, name) VALUES (NOW(), 'opclient1');

/* Password: test123! (TODO: hash the password */
INSERT INTO admins(created_at, username, auth_type, password)
 VALUES (NOW(), 'opadmin', 999, 'test123!');
