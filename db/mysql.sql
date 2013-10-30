DROP SCHEMA IF EXISTS re_svc_user_mgt;
CREATE SCHEMA IF NOT EXISTS re_svc_user_mgt DEFAULT CHARACTER SET utf8;
USE re_svc_user_mgt;

CREATE TABLE clients(
  id INT NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

  name VARCHAR(1024) NOT NULL,

  PRIMARY KEY (id)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE users(
  id INT NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

  enabled TINYINT NOT NULL DEFAULT 0,

  PRIMARY KEY (id)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE credentials(
  id INT NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

  user_id INT NOT NULL,

  username VARCHAR(1024) NOT NULL,  /* A URI, email or text username */
  auth_type INT NOT NULL,           /* A short integer representing the type of authorization system */
  password VARCHAR(256) NOT NULL,
  validated TINYINT NOT NULL DEFAULT 0,

  PRIMARY KEY (id),
  KEY index_user_id (user_id)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_bin;

/* Bootstrap data */

INSERT INTO clients(created_at, name) VALUES (NOW(), 'opclient1');

INSERT INTO users(created_at, enabled) VALUES (NOW(), 1);

INSERT INTO credentials(created_at, user_id, username, auth_type, password, validated)
 VALUES (NOW(), 1, 'opadmin', 999, 'xxx', 1);
