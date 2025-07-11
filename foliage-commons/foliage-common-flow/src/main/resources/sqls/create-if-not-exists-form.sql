-- ${schema}.fli_flow_form definition

CREATE TABLE IF NOT EXISTS ${schema}.fli_flow_form (
    id_ serial4 NOT NULL,
    name_ varchar(255),
    key_ varchar(255),
    tenant_id_ varchar(255),
    description_ varchar(1024),
    payload_ bytea,
    update_time_ timestamp DEFAULT now(),
    CONSTRAINT fli_flow_form_pk PRIMARY KEY (id_)
);

-- ${schema}.fli_flow_form_ref definition

CREATE TABLE IF NOT EXISTS ${schema}.fli_flow_form_ref (
    id_ serial4 NOT NULL,
    key_ varchar(255),
    process_id_ varchar(64) NOT NULL,
    execution_id_ varchar(255),
    update_time_ timestamp DEFAULT now(),
    payload_ jsonb,
    CONSTRAINT fli_flow_form_ref_pk PRIMARY KEY (id_)
);

CREATE INDEX IF NOT EXISTS fli_flow_form_ref_idx1 ON ${schema}.fli_flow_form_ref USING btree (process_id_);