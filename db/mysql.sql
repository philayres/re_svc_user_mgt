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
password:  SHA-256 with salt (total length of the hex is 64)
*/

/* Core tables ---------------------------------------------------------------*/

CREATE TABLE clients(
  id INT NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

  name VARCHAR(1024) NOT NULL,
  type INT NOT NULL,
  shared_secret VARCHAR(255) NOT NULL,

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

  username VARCHAR(1024) NOT NULL,
  auth_type INT NOT NULL,
  password VARCHAR(64) NOT NULL,
  salt VARCHAR(255) NOT NULL,
  validated TINYINT NOT NULL DEFAULT 0,

  PRIMARY KEY (id),
  KEY index_user_id (user_id),
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

/*
If the user_id of the logged in user appears in the admins table then he is
authorized to perform the admin actions.
*/
CREATE TABLE admins(
  user_id INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (user_id),
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

/* Log tables ----------------------------------------------------------------*/

/* TODO */

/* Bootstrap data ------------------------------------------------------------*/

INSERT INTO clients(created_at, name, client_type, shared_secret)
 VALUES (NOW(), 'opclient1', 999, 'test123!');

INSERT INTO users(created_at, enabled)
 VALUES (NOW(), 1);

/* Password: test123! */
INSERT INTO credentials(created_at, user_id, username, auth_type, password, salt, validated)
 VALUES (NOW(), 1, 'opadmin', 999, '1dbc26cbf94bfd0002a8aca964a7afc906301a4665e2a7b38b45214ad64a703e', 'test123!', 1);

INSERT INTO admins(created_at, user_id)
 VALUES (NOW(), 1);
