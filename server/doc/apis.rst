Check request nonce
-------------------

Each client has its own unique client name and shared secret. The server knows
client name and shared secret pairs.

For all requests, client must send this X-Nonce header:

::

 <nonce> <client name> <timestamp in miliseconds>

Client names must be printable ASCII character, containing no spaces.

Create nonce at client side
~~~~~~~~~~~~~~~~~~~~~~~~~~~

::

  nonce = sha256_hex(method + path + content + client_name + shared_secret + timestamp)

For requests that does not contain content (e.g. GET), ``content`` is empty
string.

For example, when the client wants to send this:

* Method: POST
* Path: /client_machines?foo=1&bar=2
* Content: username=opadmin&auth_type=999&password=test123%21&client_name=c1&client_type=1

It creates this nonce:

::

  timestamp = <current time in miliseconds>

  nonce = sha256_hex(
    "POST" +
    "/client_machines?foo=1&bar=2" +
    "username=opadmin&auth_type=999&client_name=c1&client_type=1&password=test123%21" +
    client_name +
    shared_secret +
    timestamp
  )

The full request will look like this:

::

  POST /client_machines?foo=1&bar=2 HTTP/1.1
  X-Nonce: <nonce> <client_name> <timestamp>
  Host: localhost:8000
  Content-Type: application/x-www-form-urlencoded
  Content-Length: 79

  username=opadmin&auth_type=999&client_name=c1&client_type=1&password=test123%21

Check nonce at server side
~~~~~~~~~~~~~~~~~~~~~~~~~~

From the request, the server knows:

* method
* path
* content
* client_name
* timestamp
* nonce

The server compares timestamp with current time. If the difference is larger
than 1 minute, the server will deny the request. Because time on different
systems can be slightly different, use ``abs(current time - timestamp)``, not
just ``current time - timestamp``.

The server uses client_name to lookup shared_secret. Then it recreates nonce:

::

  recreated_nonce = sha256_hex(method + path + content + client_name + shared_secret + timestamp)

If nonce does not match recreated_nonce, or it has been used within 1 minute,
the server will deny the request. This is to avoid replay attack.

General info about response
---------------------------

When response content is non-empty, Content-Type response header is set to:
application/json;charset=utf-8

Success:

* Status: 200 OK
* Content: Empty or JSON data

Nonce check failure:

* Status: 403 Forbidden
* Content: {"error": "Nonce check failed (<reason>)"}

Missing param:

* Status: 400 Bad Request
* Content: {"error": "Missing param: <param name>"}

Server error:

* Status: 500 Internal Server Error
* Content: {"error": "Internal Server Error"}

Service API logic error:

* Status: 409 Conflict
* Content: {"error": msg}

Client machine APIs
-------------------

Users and credentials are not bound to client machines. Any registered client
can be used to manipulate users and credentials.

Create client machine
~~~~~~~~~~~~~~~~~~~~~

Request
^^^^^^^

POST /client_machines

Content params:

* username, auth_type, password (must be admin)
* client_name, client_type

client_name must be printable ASCII character, containing no spaces.

Reseponse
^^^^^^^^^

Success:

* Status: 200 OK
* Content: {"client_id": client_id, "shared_secret": shared_secret}

Failure:

* Status: 409 Conflict
* Content: {"error": msg}

Error:

* username + auth_type pair does not exist
* username + auth_type pair is not validated
* Password is incorrect
* User is disabled
* User is not admin
* Invalid client name
* Duplicate client name

Delete client machine
~~~~~~~~~~~~~~~~~~~~~

Request
^^^^^^^

DELETE /client_machines/:client_name

Content params:

* username, auth_type, password (must be admin)

Reseponse
^^^^^^^^^

Success:

* Status: 200 OK
* Content: empty

Failure:

* Status: 409 Conflict
* Content: {"error": msg}

Error:

* username + auth_type pair does not exist
* username + auth_type pair is not validated
* Password is incorrect
* User is disabled
* User is not admin
* Client not found

User APIs
---------

Create user (and one credential)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Request
^^^^^^^

POST /users

Content params:

* username, auth_type, password
* [validated: true | false], assume false

Response
^^^^^^^^

Success:

* Status: 200 OK
* Content: {"user_id": user_id}

Failure:

* Status: 409 Conflict
* Content: {"error": msg}

Error:

* Duplicated username + auth_type pair

Enable user
~~~~~~~~~~~

Request
^^^^^^^

PATCH /users/:user_id/enable

Response
^^^^^^^^

* Status: 200 OK
* Content: empty

Disable user
~~~~~~~~~~~~

Request
^^^^^^^

PATCH /users/:user_id/disable

Response
^^^^^^^^

* Status: 200 OK
* Content: empty

Credential APIs
---------------

Check existence
~~~~~~~~~~~~~~~

Request
^^^^^^^

GET /credentials/:username/:auth_type

Response
^^^^^^^^

Success:

* Status: 200 OK
* Content: {"user_id": user_id}

Failure:

* Status: 409 Conflict
* Content: {"error": msg}

Error:

* username + auth_type pair does not exist
* username + auth_type pair is not validated
* User is disabled

Authenticate
~~~~~~~~~~~~

Request
^^^^^^^

POST /credentials/authenticate

Content params:

* username, auth_type, password

Response
^^^^^^^^

Success:

* Status: 200 OK
* Content: {"user_id": user_id}

Failure:

* Status: 409 Conflict
* Content: {"error": msg}

Error:

* username + auth_type pair does not exist
* username + auth_type pair is not validated
* Password is incorrect
* User is disabled

Create credential
~~~~~~~~~~~~~~~~~

Request
^^^^^^^

POST /credentials

Content params:

* username, auth_type, password (existing user)
* new_username, new_auth_type, new_password

Response
^^^^^^^^

Success:

* Status: 200 OK
* Content: empty

Failure:

* Status: 409 Conflict
* Content: {"error": msg}

Error:

* username + auth_type pair does not exist
* username + auth_type pair is not validated
* Password is incorrect
* User is disabled
* Duplicated new_username + new_auth_type pair

Validate credential
~~~~~~~~~~~~~~~~~~~

Request
^^^^^^^

PATCH /credentials/:username/:auth_type/validate

Response
^^^^^^^^

* Status: 200 OK
* Content: empty

Invalidate credential
~~~~~~~~~~~~~~~~~~~~~

Request
^^^^^^^

PATCH /credentials/:username/:auth_type/invalidate

Response
^^^^^^^^

* Status: 200 OK
* Content: empty

Update password
~~~~~~~~~~~~~~~

Request
^^^^^^^

PATCH /credentials/:username/:auth_type/update_password

Content params:

* password or force_new=true
* new_password

When force_new is false or not given, password is used.

Response
^^^^^^^^

Success:

* Status: 200 OK
* Content: empty

Failure:

* Status: 409 Conflict
* Content: {"error": msg}

Error when force_new is false or not given:

* username + auth_type pair does not exist
* username + auth_type pair is not validated
* Password is incorrect
* User is disabled

Error when force_new is true:

* username + auth_type pair does not exist

Delete credential
~~~~~~~~~~~~~~~~~

Request
^^^^^^^

DELETE /credentials/:username/:auth_type

Response
^^^^^^^^

* Status: 200 OK
* Content: empty

Log
---

See doc about DB.

All requests are log to a DB table:

* Access time (indexed)
* Client ID (indexed)
* Credential ID (if there's a matched credential)
* User ID (if there's a matched user)
* Request type
* Response code

No other identifying information should be logged.

Authentication and username existence check requests are logged to another table.
Compared with table accesses, this table does not have field user ID (can be
inferred from credential ID), but has these additional fields:

* Username
* Authentication type
