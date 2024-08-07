-- public.fli_core_domain definition

CREATE TABLE IF NOT EXISTS public.fli_core_domain (
    id_ serial4 NOT NULL,
    identifier_ varchar(255) NOT NULL,
    display_name_ varchar(255) NOT NULL,
    update_time_ timestamp NOT NULL,
    CONSTRAINT fli_pacs_domain_pk PRIMARY KEY (id_)
);

CREATE UNIQUE INDEX IF NOT EXISTS fli_core_domain_idx1 ON public.fli_core_domain (identifier_);

-- public.fli_core_role definition

CREATE TABLE IF NOT EXISTS public.fli_core_role (
    id_ serial4 NOT NULL,
    name_ varchar(255) NOT NULL,
    display_name_ varchar(255) NOT NULL,
    update_time_ timestamp NOT NULL,
    CONSTRAINT fli_pacs_role_pk PRIMARY KEY (id_)
);

CREATE UNIQUE INDEX IF NOT EXISTS fli_core_role_idx1 ON public.fli_core_role (name_);

-- public.fli_core_user definition

CREATE TABLE IF NOT EXISTS public.fli_core_user (
    id_ serial4 NOT NULL,
    name_ varchar(255) NOT NULL,
    display_name_ varchar(255) NOT NULL,
    email_ varchar(255) NOT NULL,
    domain_id_ int4 NOT NULL,
    update_time_ timestamp NOT NULL,
    CONSTRAINT fli_pacs_user_pk PRIMARY KEY (id_)
);

CREATE UNIQUE INDEX IF NOT EXISTS fli_core_user_idx1 ON public.fli_core_user (email_);

-- public.fli_core_user definition

CREATE TABLE IF NOT EXISTS public.fli_core_user_role (
    user_id_ int4 NOT NULL,
    role_id_ int4 NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS fli_core_user_role_idx1 ON public.fli_core_user_role (user_id_,role_id_);

-- public.fli_core_repository definition

CREATE TABLE IF NOT EXISTS public.fli_core_repository (
    id_ serial4 NOT NULL,
    name_ varchar(255) NOT NULL,
    display_name_ varchar(255) NOT NULL,
    domain_id_ int4 NOT NULL,
    host_and_port_ varchar(255) NOT NULL,
    update_time_ timestamp NOT NULL,
    CONSTRAINT fli_pacs_repository_pk PRIMARY KEY (id_)
);

-- public.fli_core_contract definition

CREATE TABLE IF NOT EXISTS public.fli_core_contract (
    id_ serial4 NOT NULL,
    name_ varchar(255) NOT NULL,
    domain_id_ int4 NOT NULL,
    repository_id_ int4 NOT NULL,
    expiration_ timestamp NOT NULL,
    payload_ bytea,
    snapshot_ bytea,
    update_time_ timestamp NOT NULL,
    CONSTRAINT fli_pacs_contract_pk PRIMARY KEY (id_)
);

-- public.fli_core_dashboard definition

CREATE TABLE IF NOT EXISTS public.fli_core_dashboard (
    id_ serial4 NOT NULL,
    name_ varchar(255) NOT NULL,
    domain_id_ int4 NOT NULL,
    dashboard_id_ varchar(255) NOT NULL,
    dashboard_token_ varchar(255) NOT NULL,
    update_time_ timestamp NOT NULL,
    CONSTRAINT fli_core_dashboard_pk PRIMARY KEY (id_)
);

-- constraints

-- ALTER TABLE public.fli_core_user ADD CONSTRAINT fli_core_user_domain_id_fk FOREIGN KEY (domain_id_) REFERENCES public.fli_core_domain(id_);
-- ALTER TABLE public.fli_core_user_role ADD CONSTRAINT fli_core_user_role_role_id_fk FOREIGN KEY (role_id_) REFERENCES public.fli_core_role(id_);
-- ALTER TABLE public.fli_core_user_role ADD CONSTRAINT fli_core_user_role_user_id_fk FOREIGN KEY (user_id_) REFERENCES public.fli_core_user(id_);
-- ALTER TABLE public.fli_core_repository ADD CONSTRAINT fli_core_repository_domain_id_fk FOREIGN KEY (domain_id_) REFERENCES public.fli_core_domain(id_);
-- ALTER TABLE public.fli_core_contract ADD CONSTRAINT fli_core_contract_domain_id_fk FOREIGN KEY (domain_id_) REFERENCES public.fli_core_domain(id_);
-- ALTER TABLE public.fli_core_contract ADD CONSTRAINT fli_core_contract_repository_id_fk FOREIGN KEY (repository_id_) REFERENCES public.fli_core_repository(id_);
