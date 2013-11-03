Authenticate
------------

Correct (use the bootstrap user):

::

  curl -v -X POST --data 'username=opadmin&auth_type=999&password=test123!' http://localhost:8000/authenticate

Credential not found:

::

  curl -v -X POST --data 'username=xxx&auth_type=999&password=test123!' http://localhost:8000/authenticate

Credential not validated:

::

  Set "validated" in DB to 0, then:
  curl -v -X POST --data 'username=opadmin&auth_type=999&password=test123!' http://localhost:8000/authenticate

Wrong password:

::

  curl -v -X POST --data 'username=opadmin&auth_type=999&password=xxx' http://localhost:8000/authenticate

User not found:

::

  In theory, this can't happen because of the foreign key constraint

User disabled:

::

  Set "enabled" in DB to 0, then:
  curl -v -X POST --data 'username=opadmin&auth_type=999&password=test123!' http://localhost:8000/authenticate
