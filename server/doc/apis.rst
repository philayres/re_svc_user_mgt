Nonce check
-----------

Each client has its own unique client name and shared secret (the server knows
client name and shared secret pairs).

For all requests, client must send this Authorization header:

::

 <nonce> <client name> <timestamp in miliseconds>

Client names must be printable ASCII character, containing no spaces.

Creating nonce
~~~~~~~~~~~~~~

::

  nonce = sha256Hex(method + path + content + clientName + sharedSecret + timestamp)

For requests that does not contain body content (e.g. GET), ``content`` is empty
string.

For example, when the client wants to send this:

* Method: POST
* Path: /client_machines?foo=1&bar=2
* Content (POST body): username=opadmin&auth_type=999&password=test123%21&client_name=c1&client_type=1

It creates this nonce:

::

  timestamp = <current time in miliseconds>

  nonce = sha256Hex(
    "POST" +
    "/client_machines?foo=1&bar=2" +
    "username=opadmin&auth_type=999&client_name=c1&client_type=1&password=test123%21" +
    clientName +
    sharedSecret +
    timestamp
  )

The full request will look like this:

::

  POST /client_machines?foo=1&bar=2 HTTP/1.1
  Authorization: <nonce> <clientName> <timestamp>
  Host: localhost:8000
  Content-Type: application/x-www-form-urlencoded
  Content-Length: 79

  username=opadmin&auth_type=999&client_name=c1&client_type=1&password=test123%21

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

Create client machine
~~~~~~~~~~~~~~~~~~~~~

POST /client_machines

Request body params:

* username, auth_type, password (must be admin)
* client_name, client_type

client_name must be printable ASCII character, containing no spaces.

Reseponse body: {"client_id": clientId, "shared_secret": sharedSecret}

Delete client machine
~~~~~~~~~~~~~~~~~~~~~

DELETE /client_machines/:clientName

Request body params:

* username, auth_type, password (must be admin)

User APIs
---------

Create user
~~~~~~~~~~~

POST /users

Request body params:

* username, auth_type, password
* [validated: true | false], assume false

Response body: {"user_id": userId}

Enable/disable user
~~~~~~~~~~~~~~~~~~~

PATCH /users/:userId/enable

Request body params:

* enabled: true | false

Credential APIs
---------------

Check existence
~~~~~~~~~~~~~~~

GET /credentials/:username/:authType

Response body: {"user_id": userId}

Authenticate
~~~~~~~~~~~~

POST /credentials/authenticate

Request body params:

* username, auth_type, password

Response body: {"user_id": userId}

Create credential
~~~~~~~~~~~~~~~~~

POST /credentials

Request body params:

* username, auth_type, password
* new_username, new_auth_type, new_password

Validate/Invalidate
~~~~~~~~~~~~~~~~~~~

PATCH /credentials/:username/:authType/validate

Request body params:

* validated: true | false

Update password
~~~~~~~~~~~~~~~

PATCH /credentials/:username/:authType/update_password

Request body params:

* new_password
* password or force_new=true

Delete credential
~~~~~~~~~~~~~~~~~

DELETE /credentials/:username/:authType
