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

/*
created_at and updated_at:
MySQL does not support 2 columns both having CURRENT_TIMESTAMP by default.
Thus we use CURRENT_TIMESTAMP for updated_at.
For created_at, use DEFAULT 0 and NOW() when inserting.
*/

/* Core tables ---------------------------------------------------------------*/

CREATE TABLE clients(
  id INT NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

  name VARCHAR(1024) NOT NULL,
  type INT NOT NULL,
  shared_secret VARCHAR(255) NOT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY index_name (name(255))  /* Select by client name */
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
  UNIQUE KEY index_username_auth_type (username(255), auth_type),  /* Select by username and auth_type */
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

/*
Make sure that nonce is not reused to avoid replay attack.
Periodically, expired nonces (TTL is 1 minute) are deleted.
*/
CREATE TABLE nonces(
  nonce VARCHAR(64) NOT NULL,
  created_at BIGINT NOT NULL,  /* Need BIGINT: milisecond precision; TIMESTAMP/INT: second precision */

  PRIMARY KEY (nonce),
  KEY index_created_at (created_at)  /* Delete expired nonces faster */
) ENGINE=InnoDB;

CREATE TABLE accesses(
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  client_id INT NOT NULL,  /* To save space, use client_id instead of client_name */

  /*
  0: Create client machine
  1: Delete client machine

  20: Create user
  21: Enable/disable user

  (Check existence and Authenticate are logged at table auth_accesses)
  42: Create credential
  43: Validate/Invalidate
  44: Update password
  45: Delete credential
  */
  request_type TINYINT NOT NULL,

  response_status SMALLINT NOT NULL,

  user_id INT,             /* Non-null if there's a matched user */

  KEY index_created_at (created_at),
  KEY index_client_id (client_id)
) ENGINE=InnoDB;

/*
Compared with table accesses, this table has these additional fields:
- username
- auth_type
- credential_id
*/
CREATE TABLE auth_accesses(
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  client_id INT NOT NULL,  /* To save space, use client_id instead of client_name */

  /*
  40: Check existence
  41: Authenticate
  */
  request_type TINYINT NOT NULL,

  response_status SMALLINT NOT NULL,

  username VARCHAR(1024),  /* To save space, set to NULL if credential_id is non-NULL */
  auth_type INT,           /* To save space, set to NULL if credential_id is non-NULL */
  credential_id INT,       /* Non-null if there's a matched credential */
  user_id INT,             /* Non-null if there's a matched user */

  KEY index_created_at (created_at),
  KEY index_client_id (client_id)
) ENGINE=InnoDB;

/* Bootstrap data ------------------------------------------------------------*/

INSERT INTO clients(created_at, name, type, shared_secret)
 VALUES (NOW(), 'opclient1', 999, 'test123!');

INSERT INTO users(created_at, enabled)
 VALUES (NOW(), 1);

/* Password: test123! */
INSERT INTO credentials(created_at, user_id, username, auth_type, password, salt, validated)
 VALUES (NOW(), 1, 'opadmin', 999, '1dbc26cbf94bfd0002a8aca964a7afc906301a4665e2a7b38b45214ad64a703e', 'test123!', 1);

INSERT INTO admins(created_at, user_id)
 VALUES (NOW(), 1);
