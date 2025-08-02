-------------------------------------------------------------------------------
-- TABLES
-------------------------------------------------------------------------------

-- ldap.fli_ldap_domain definition

CREATE TABLE IF NOT EXISTS ldap.fli_ldap_domain (
    id_ int8 NOT NULL,
    identifier_ varchar(255) NOT NULL,
    display_name_ varchar(255) NOT NULL,
    update_time_ timestamp NOT NULL,
    CONSTRAINT fli_ldap_domain_pk PRIMARY KEY (id_)
);

CREATE UNIQUE INDEX IF NOT EXISTS fli_ldap_domain_idx1 ON ldap.fli_ldap_domain (identifier_);

-- ldap.fli_ldap_role definition

CREATE TABLE IF NOT EXISTS ldap.fli_ldap_role (
    id_ int8 NOT NULL,
    name_ varchar(255) NOT NULL,
    display_name_ varchar(255) NOT NULL,
    update_time_ timestamp NOT NULL,
    CONSTRAINT fli_ldap_role_pk PRIMARY KEY (id_)
);

CREATE UNIQUE INDEX IF NOT EXISTS fli_ldap_role_idx1 ON ldap.fli_ldap_role (name_);

-- ldap.fli_ldap_user definition

CREATE TABLE IF NOT EXISTS ldap.fli_ldap_user (
    id_ int8 NOT NULL,
    name_ varchar(255) NOT NULL,
    display_name_ varchar(255) NOT NULL,
    email_ varchar(255) NOT NULL,
    domain_id_ int8 NOT NULL,
    update_time_ timestamp NOT NULL,
    CONSTRAINT fli_ldap_user_pk PRIMARY KEY (id_)
);

CREATE UNIQUE INDEX IF NOT EXISTS fli_ldap_user_idx1 ON ldap.fli_ldap_user (email_);

-- ldap.fli_ldap_user definition

CREATE TABLE IF NOT EXISTS ldap.fli_ldap_user_role (
    user_id_ int8 NOT NULL,
    role_id_ int8 NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS fli_ldap_user_role_idx1 ON ldap.fli_ldap_user_role (user_id_,role_id_);

-- ldap.fli_ldap_repository definition

CREATE TABLE IF NOT EXISTS ldap.fli_ldap_repository (
    id_ int8 NOT NULL,
    name_ varchar(255) NOT NULL,
    display_name_ varchar(255) NOT NULL,
    domain_id_ int8 NOT NULL,
    host_and_port_ varchar(255) NOT NULL,
    update_time_ timestamp NOT NULL,
    CONSTRAINT fli_ldap_repository_pk PRIMARY KEY (id_)
);

-- ldap.fli_ldap_contract definition

CREATE TABLE IF NOT EXISTS ldap.fli_ldap_contract (
    id_ int8 NOT NULL,
    name_ varchar(255) NOT NULL,
    domain_id_ int8 NOT NULL,
    repository_id_ int8 NOT NULL,
    expiration_ timestamp NOT NULL,
    payload_ bytea,
    snapshot_ bytea,
    update_time_ timestamp NOT NULL,
    CONSTRAINT fli_ldap_contract_pk PRIMARY KEY (id_)
);

-- ldap.fli_ldap_dashboard definition

CREATE TABLE IF NOT EXISTS ldap.fli_ldap_dashboard (
    id_ int8 NOT NULL,
    name_ varchar(255) NOT NULL,
    domain_id_ int8 NOT NULL,
    dashboard_id_ varchar(255) NOT NULL,
    dashboard_token_ varchar(255) NOT NULL,
    update_time_ timestamp NOT NULL,
    CONSTRAINT fli_ldap_dashboard_pk PRIMARY KEY (id_)
);

-------------------------------------------------------------------------------
-- CONSTRAINT FUNCTIONS
-------------------------------------------------------------------------------

DO $$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fli_ldap_user_domain_id_fk') THEN
        ALTER TABLE ldap.fli_ldap_user ADD CONSTRAINT fli_ldap_user_domain_id_fk FOREIGN KEY (domain_id_) REFERENCES ldap.fli_ldap_domain(id_);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fli_ldap_user_role_role_id_fk') THEN
        ALTER TABLE ldap.fli_ldap_user_role ADD CONSTRAINT fli_ldap_user_role_role_id_fk FOREIGN KEY (role_id_) REFERENCES ldap.fli_ldap_role(id_);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fli_ldap_user_role_user_id_fk') THEN
        ALTER TABLE ldap.fli_ldap_user_role ADD CONSTRAINT fli_ldap_user_role_user_id_fk FOREIGN KEY (user_id_) REFERENCES ldap.fli_ldap_user(id_);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fli_ldap_repository_domain_id_fk') THEN
        ALTER TABLE ldap.fli_ldap_repository ADD CONSTRAINT fli_ldap_repository_domain_id_fk FOREIGN KEY (domain_id_) REFERENCES ldap.fli_ldap_domain(id_);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fli_ldap_contract_domain_id_fk') THEN
        ALTER TABLE ldap.fli_ldap_contract ADD CONSTRAINT fli_ldap_contract_domain_id_fk FOREIGN KEY (domain_id_) REFERENCES ldap.fli_ldap_domain(id_);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fli_ldap_contract_repository_id_fk') THEN
        ALTER TABLE ldap.fli_ldap_contract ADD CONSTRAINT fli_ldap_contract_repository_id_fk FOREIGN KEY (repository_id_) REFERENCES ldap.fli_ldap_repository(id_);
    END IF;
    
END $$;