Check request nonce
-------------------

Each client has its own unique client name and shared secret. The server knows
client name and shared secret pairs.

For all requests, client must send this Authorization header:

::

 <nonce> <client name> <timestamp in miliseconds>

Client names must be printable ASCII character, containing no spaces.

Create nonce at client side
~~~~~~~~~~~~~~~~~~~~~~~~~~~

::

  nonce = sha256_hex(method + path + content + client_name + shared_secret + timestamp)

For requests that does not contain body content (e.g. GET), ``content`` is empty
string.

For example, when the client wants to send this:

* Method: POST
* Path: /client_machines?foo=1&bar=2
* Content (POST body): username=opadmin&auth_type=999&password=test123%21&client_name=c1&client_type=1

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
  Authorization: <nonce> <client_name> <timestamp>
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

Common info about response
--------------------------

When response body is non-empty, its Content-Type header is set to:
application/json;charset=utf-8

Success:

* Status: 200 OK
* Body: Empty or JSON data

Missing param:

* Status: 400 Bad Request
* Body: {"error": "Missing param: <param name>"}

Nonce check failure:

* Status: 401 Unauthorized
* Body: {"error": "Nonce check failed (<reason>)"}

Wrong user info in request (username, auth_type, password):

* Status: 403 Forbidden
* Body: {"error": reason}

Failure (try to create duplicate username + auth_type pair etc.):

* Status: 400 Bad Request
* Body: {"error": reason}

Server error:

* Status: 500 Internal Server Error
* Body: {"error": "Internal Server Error"}

Client machine APIs
-------------------

Users and credentials be bound to client machines. Any client can be used to add
and delete users.

Create client machine
~~~~~~~~~~~~~~~~~~~~~

POST /client_machines

Request body params:

* username, auth_type, password (must be admin)
* client_name, client_type

client_name must be printable ASCII character, containing no spaces.

Reseponse body: {"client_id": client_id, "shared_secret": shared_secret}

Delete client machine
~~~~~~~~~~~~~~~~~~~~~

DELETE /client_machines/:client_name

Request body params:

* username, auth_type, password (must be admin)

User APIs
---------

Create user (and one credential)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

POST /users

Request body params:

* username, auth_type, password
* [validated: true | false], assume false

Response body: {"user_id": user_id}

Enable user
~~~~~~~~~~~

PATCH /users/:user_id/enable

Disable user
~~~~~~~~~~~~

PATCH /users/:user_id/disable

Credential APIs
---------------

Check existence
~~~~~~~~~~~~~~~

GET /credentials/:username/:auth_type

Response body: {"user_id": user_id}

Authenticate
~~~~~~~~~~~~

POST /credentials/authenticate

Request body params:

* username, auth_type, password

Response body: {"user_id": user_id}

Create credential
~~~~~~~~~~~~~~~~~

POST /credentials

Request body params:

* username, auth_type, password
* new_username, new_auth_type, new_password

Validate credential
~~~~~~~~~~~~~~~~~~~

PATCH /credentials/:username/:auth_type/validate

Invalidate credential
~~~~~~~~~~~~~~~~~~~~~

PATCH /credentials/:username/:auth_type/invalidate

Update password
~~~~~~~~~~~~~~~

PATCH /credentials/:username/:auth_type/update_password

Request body params:

* new_password
* password or force_new=true

Delete credential
~~~~~~~~~~~~~~~~~

DELETE /credentials/:username/:auth_type

Log
---

See doc about DB.

All requests are log to a DB table:

* Access time (indexed)
* Client ID (indexed)
* User ID (if there's a matched user)
* Request type
* Response code

No other identifying information should be logged.

Authentication and username existence check requests are logged to another table.
Compared to the above table, this table has these additional fields:

* Username
* Authentication type
* Credential ID
