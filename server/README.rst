Scala server side for RE Service for User Management.

Source code
-----------

See doc directory.

Use db/mysql.sql to init DB. It also contains bootstrap data (user etc.).

Create Eclipse project for development
--------------------------------------

::

  sbt eclipse

Create IntelliJ project for development
---------------------------------------

::

  sbt gen-idea

Run in development mode
-----------------------

::

  sbt run

Release for production mode
---------------------------

Prepare deployment files:

::

  sbt xitrum-package

Directory target/xitrum will be created, ready for deployment.

::

  config/
    re_svc_user_mgt.properties

  script/
    start
    start.bat

  lib/
    <many .jar files>

  log/
    bonecp.log
    re_svc_user_mgt.log

To start the server:

::

  script/start

To tune JVM memory, modify ``JAVA_OPTS`` in the start script.

Note about log
--------------

BoneCP uses SLF4J while Finagle (and re_svc_user_mgt) uses JUL.
We don't use jul-to-slf4j bridge because it's slow:
http://www.slf4j.org/legacy.html#jul-to-slf4j

* log/bonecp.log: Logback (an SLF4J implementation, see config/logback.xml)
* log/re_svc_user_mgt.log: JUL

Log files are rolled daily. Log level is DEBUG by default.
