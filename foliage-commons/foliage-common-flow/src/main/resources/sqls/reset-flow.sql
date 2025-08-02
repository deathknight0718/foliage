-------------------------------------------------------------------------------
-- DROP SCHEMA AND RECREATE
-------------------------------------------------------------------------------

DROP SCHEMA IF EXISTS flow CASCADE;

CREATE SCHEMA flow;

GRANT ALL ON SCHEMA flow TO PUBLIC;