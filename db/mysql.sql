DROP SCHEMA IF EXISTS re_svc_user_mgt;
CREATE SCHEMA IF NOT EXISTS re_svc_user_mgt DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_bin;
USE re_svc_user_mgt;

/*
Note about creating index:
#1071 - Specified key was too long; max key length is 767 bytes
http://stackoverflow.com/questions/1814532/1071-specified-key-was-too-long-max-key-length-is-767-bytes
*/

/*
username:  A URI, email, or text username
auth_type: A short integer representing the type of authorization system
*/

/* Core tables ---------------------------------------------------------------*/

CREATE TABLE clients(
  id INT NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

  name VARCHAR(1024) NOT NULL,
  client_type INT NOT NULL,
  shared_secret VARCHAR(255) NOT NULL,

  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE users(
  id INT NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

  is_admin TINYINT NOT NULL DEFAULT 0,
  enabled TINYINT NOT NULL DEFAULT 0,

  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE credentials(
  id INT NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

  user_id INT NOT NULL,

  username VARCHAR(1024) NOT NULL,
  auth_type INT NOT NULL,
  password VARCHAR(255) NOT NULL,
  salt VARCHAR(255) NOT NULL,
  validated TINYINT NOT NULL DEFAULT 0,

  PRIMARY KEY (id),
  KEY index_user_id (user_id),
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

/* Log tables ----------------------------------------------------------------*/

/* TODO */

/* Bootstrap data ------------------------------------------------------------*/

INSERT INTO clients(created_at, name, client_type, shared_secret)
 VALUES (NOW(), 'opclient1', 999, 'test123!');

INSERT INTO users(created_at, is_admin, enabled)
 VALUES (NOW(), 1, 1);

/* Password: test123! (TODO: hash the password) */
INSERT INTO credentials(created_at, user_id, username, auth_type, password, salt, validated)
 VALUES (NOW(), 1, 'opadmin', 999, 'test123!', 'salt', 1);
