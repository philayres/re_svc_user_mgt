re_svc_user_mgt
===============

RE Service for User Management

Source code
-----------

See doc directory.

Use db/mysql.sql to init DB. It also contains bootstrap data (user etc.).

Create Eclipse project for development
--------------------------------------

::

  sbt/sbt eclipse

Create IntelliJ project for development
---------------------------------------

::

  sbt/sbt gen-idea

Run in development mode
-----------------------

::

  sbt/sbt run

Log:

* is output to log/re_svc_user_mgt.log
* is rolled daily
* level is DEBUG

Release for production mode
---------------------------

Prepare deployment files:

::

  sbt/sbt xitrum-package

Directory target/xitrum will be created, ready for deployment.

::

  config/
    re_svc_user_mgt.conf

  bin/
    start
    start.bat

  lib/
    <many .jar files>

  log/
    re_svc_user_mgt.log

To start the server:

::

  bin/start

To tune JVM memory, modify ``JAVA_OPTS`` in the start script.
