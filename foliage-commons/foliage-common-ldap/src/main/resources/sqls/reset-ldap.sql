-------------------------------------------------------------------------------
-- DROP SCHEMA AND RECREATE
-------------------------------------------------------------------------------

DROP SCHEMA IF EXISTS ldap CASCADE;

CREATE SCHEMA ldap;

GRANT ALL ON SCHEMA ldap TO PUBLIC;