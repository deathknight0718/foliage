--
-- PostgreSQL database dump
--

-- Dumped from database version 14.12 (Debian 14.12-1.pgdg120+1)
-- Dumped by pg_dump version 16.1

-- Started on 2025-07-22 19:37:47 CST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 6 (class 2615 OID 20376)
-- Name: flow; Type: SCHEMA; Schema: -; Owner: vlsp
--

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 252 (class 1259 OID 21587)
-- Name: act_evt_log; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_evt_log (
    log_nr_ integer NOT NULL,
    type_ character varying(64),
    proc_def_id_ character varying(64),
    proc_inst_id_ character varying(64),
    execution_id_ character varying(64),
    task_id_ character varying(64),
    time_stamp_ timestamp without time zone NOT NULL,
    user_id_ character varying(255),
    data_ bytea,
    lock_owner_ character varying(255),
    lock_time_ timestamp without time zone,
    is_processed_ smallint DEFAULT 0
);


ALTER TABLE flow.act_evt_log OWNER TO vlsp;

--
-- TOC entry 251 (class 1259 OID 21586)
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE; Schema: flow; Owner: vlsp
--

CREATE SEQUENCE flow.act_evt_log_log_nr__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE flow.act_evt_log_log_nr__seq OWNER TO vlsp;

--
-- TOC entry 4061 (class 0 OID 0)
-- Dependencies: 251
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE OWNED BY; Schema: flow; Owner: vlsp
--

ALTER SEQUENCE flow.act_evt_log_log_nr__seq OWNED BY flow.act_evt_log.log_nr_;


--
-- TOC entry 227 (class 1259 OID 21279)
-- Name: act_ge_bytearray; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ge_bytearray (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    deployment_id_ character varying(64),
    bytes_ bytea,
    generated_ boolean
);


ALTER TABLE flow.act_ge_bytearray OWNER TO vlsp;

--
-- TOC entry 226 (class 1259 OID 21274)
-- Name: act_ge_property; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ge_property (
    name_ character varying(64) NOT NULL,
    value_ character varying(300),
    rev_ integer
);


ALTER TABLE flow.act_ge_property OWNER TO vlsp;

--
-- TOC entry 256 (class 1259 OID 21821)
-- Name: act_hi_actinst; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_hi_actinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_def_id_ character varying(64) NOT NULL,
    proc_inst_id_ character varying(64) NOT NULL,
    execution_id_ character varying(64) NOT NULL,
    act_id_ character varying(255) NOT NULL,
    task_id_ character varying(64),
    call_proc_inst_id_ character varying(64),
    act_name_ character varying(255),
    act_type_ character varying(255) NOT NULL,
    assignee_ character varying(255),
    start_time_ timestamp without time zone NOT NULL,
    end_time_ timestamp without time zone,
    transaction_order_ integer,
    duration_ bigint,
    delete_reason_ character varying(4000),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.act_hi_actinst OWNER TO vlsp;

--
-- TOC entry 259 (class 1259 OID 21844)
-- Name: act_hi_attachment; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_hi_attachment (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    user_id_ character varying(255),
    name_ character varying(255),
    description_ character varying(4000),
    type_ character varying(255),
    task_id_ character varying(64),
    proc_inst_id_ character varying(64),
    url_ character varying(4000),
    content_id_ character varying(64),
    time_ timestamp without time zone
);


ALTER TABLE flow.act_hi_attachment OWNER TO vlsp;

--
-- TOC entry 258 (class 1259 OID 21837)
-- Name: act_hi_comment; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_hi_comment (
    id_ character varying(64) NOT NULL,
    type_ character varying(255),
    time_ timestamp without time zone NOT NULL,
    user_id_ character varying(255),
    task_id_ character varying(64),
    proc_inst_id_ character varying(64),
    action_ character varying(255),
    message_ character varying(4000),
    full_msg_ bytea
);


ALTER TABLE flow.act_hi_comment OWNER TO vlsp;

--
-- TOC entry 257 (class 1259 OID 21830)
-- Name: act_hi_detail; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_hi_detail (
    id_ character varying(64) NOT NULL,
    type_ character varying(255) NOT NULL,
    proc_inst_id_ character varying(64),
    execution_id_ character varying(64),
    task_id_ character varying(64),
    act_inst_id_ character varying(64),
    name_ character varying(255) NOT NULL,
    var_type_ character varying(64),
    rev_ integer,
    time_ timestamp without time zone NOT NULL,
    bytearray_id_ character varying(64),
    double_ double precision,
    long_ bigint,
    text_ character varying(4000),
    text2_ character varying(4000)
);


ALTER TABLE flow.act_hi_detail OWNER TO vlsp;

--
-- TOC entry 231 (class 1259 OID 21320)
-- Name: act_hi_entitylink; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_hi_entitylink (
    id_ character varying(64) NOT NULL,
    link_type_ character varying(255),
    create_time_ timestamp without time zone,
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    parent_element_id_ character varying(255),
    ref_scope_id_ character varying(255),
    ref_scope_type_ character varying(255),
    ref_scope_definition_id_ character varying(255),
    root_scope_id_ character varying(255),
    root_scope_type_ character varying(255),
    hierarchy_type_ character varying(255)
);


ALTER TABLE flow.act_hi_entitylink OWNER TO vlsp;

--
-- TOC entry 229 (class 1259 OID 21298)
-- Name: act_hi_identitylink; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_hi_identitylink (
    id_ character varying(64) NOT NULL,
    group_id_ character varying(255),
    type_ character varying(255),
    user_id_ character varying(255),
    task_id_ character varying(64),
    create_time_ timestamp without time zone,
    proc_inst_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255)
);


ALTER TABLE flow.act_hi_identitylink OWNER TO vlsp;

--
-- TOC entry 255 (class 1259 OID 21810)
-- Name: act_hi_procinst; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_hi_procinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_inst_id_ character varying(64) NOT NULL,
    business_key_ character varying(255),
    proc_def_id_ character varying(64) NOT NULL,
    start_time_ timestamp without time zone NOT NULL,
    end_time_ timestamp without time zone,
    duration_ bigint,
    start_user_id_ character varying(255),
    start_act_id_ character varying(255),
    end_act_id_ character varying(255),
    super_process_instance_id_ character varying(64),
    delete_reason_ character varying(4000),
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    name_ character varying(255),
    callback_id_ character varying(255),
    callback_type_ character varying(255),
    reference_id_ character varying(255),
    reference_type_ character varying(255),
    propagated_stage_inst_id_ character varying(255),
    business_status_ character varying(255)
);


ALTER TABLE flow.act_hi_procinst OWNER TO vlsp;

--
-- TOC entry 234 (class 1259 OID 21354)
-- Name: act_hi_taskinst; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_hi_taskinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_def_id_ character varying(64),
    task_def_id_ character varying(64),
    task_def_key_ character varying(255),
    proc_inst_id_ character varying(64),
    execution_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    propagated_stage_inst_id_ character varying(255),
    name_ character varying(255),
    parent_task_id_ character varying(64),
    description_ character varying(4000),
    owner_ character varying(255),
    assignee_ character varying(255),
    start_time_ timestamp without time zone NOT NULL,
    claim_time_ timestamp without time zone,
    end_time_ timestamp without time zone,
    duration_ bigint,
    delete_reason_ character varying(4000),
    priority_ integer,
    due_date_ timestamp without time zone,
    form_key_ character varying(255),
    category_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    last_updated_time_ timestamp without time zone
);


ALTER TABLE flow.act_hi_taskinst OWNER TO vlsp;

--
-- TOC entry 236 (class 1259 OID 21364)
-- Name: act_hi_tsk_log; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_hi_tsk_log (
    id_ integer NOT NULL,
    type_ character varying(64),
    task_id_ character varying(64) NOT NULL,
    time_stamp_ timestamp without time zone NOT NULL,
    user_id_ character varying(255),
    data_ character varying(4000),
    execution_id_ character varying(64),
    proc_inst_id_ character varying(64),
    proc_def_id_ character varying(64),
    scope_id_ character varying(255),
    scope_definition_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.act_hi_tsk_log OWNER TO vlsp;

--
-- TOC entry 235 (class 1259 OID 21363)
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE; Schema: flow; Owner: vlsp
--

CREATE SEQUENCE flow.act_hi_tsk_log_id__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE flow.act_hi_tsk_log_id__seq OWNER TO vlsp;

--
-- TOC entry 4062 (class 0 OID 0)
-- Dependencies: 235
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE OWNED BY; Schema: flow; Owner: vlsp
--

ALTER SEQUENCE flow.act_hi_tsk_log_id__seq OWNED BY flow.act_hi_tsk_log.id_;


--
-- TOC entry 238 (class 1259 OID 21391)
-- Name: act_hi_varinst; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_hi_varinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_inst_id_ character varying(64),
    execution_id_ character varying(64),
    task_id_ character varying(64),
    name_ character varying(255) NOT NULL,
    var_type_ character varying(100),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    bytearray_id_ character varying(64),
    double_ double precision,
    long_ bigint,
    text_ character varying(4000),
    text2_ character varying(4000),
    create_time_ timestamp without time zone,
    last_updated_time_ timestamp without time zone
);


ALTER TABLE flow.act_hi_varinst OWNER TO vlsp;

--
-- TOC entry 218 (class 1259 OID 21198)
-- Name: act_id_bytearray; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_id_bytearray (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    bytes_ bytea
);


ALTER TABLE flow.act_id_bytearray OWNER TO vlsp;

--
-- TOC entry 219 (class 1259 OID 21205)
-- Name: act_id_group; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_id_group (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    type_ character varying(255)
);


ALTER TABLE flow.act_id_group OWNER TO vlsp;

--
-- TOC entry 222 (class 1259 OID 21225)
-- Name: act_id_info; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_id_info (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    user_id_ character varying(64),
    type_ character varying(64),
    key_ character varying(255),
    value_ character varying(255),
    password_ bytea,
    parent_id_ character varying(255)
);


ALTER TABLE flow.act_id_info OWNER TO vlsp;

--
-- TOC entry 220 (class 1259 OID 21212)
-- Name: act_id_membership; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_id_membership (
    user_id_ character varying(64) NOT NULL,
    group_id_ character varying(64) NOT NULL
);


ALTER TABLE flow.act_id_membership OWNER TO vlsp;

--
-- TOC entry 224 (class 1259 OID 21239)
-- Name: act_id_priv; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_id_priv (
    id_ character varying(64) NOT NULL,
    name_ character varying(255) NOT NULL
);


ALTER TABLE flow.act_id_priv OWNER TO vlsp;

--
-- TOC entry 225 (class 1259 OID 21244)
-- Name: act_id_priv_mapping; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_id_priv_mapping (
    id_ character varying(64) NOT NULL,
    priv_id_ character varying(64) NOT NULL,
    user_id_ character varying(255),
    group_id_ character varying(255)
);


ALTER TABLE flow.act_id_priv_mapping OWNER TO vlsp;

--
-- TOC entry 217 (class 1259 OID 21193)
-- Name: act_id_property; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_id_property (
    name_ character varying(64) NOT NULL,
    value_ character varying(300),
    rev_ integer
);


ALTER TABLE flow.act_id_property OWNER TO vlsp;

--
-- TOC entry 223 (class 1259 OID 21232)
-- Name: act_id_token; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_id_token (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    token_value_ character varying(255),
    token_date_ timestamp without time zone,
    ip_address_ character varying(255),
    user_agent_ character varying(255),
    user_id_ character varying(255),
    token_data_ character varying(2000)
);


ALTER TABLE flow.act_id_token OWNER TO vlsp;

--
-- TOC entry 221 (class 1259 OID 21217)
-- Name: act_id_user; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_id_user (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    first_ character varying(255),
    last_ character varying(255),
    display_name_ character varying(255),
    email_ character varying(255),
    pwd_ character varying(255),
    picture_id_ character varying(64),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.act_id_user OWNER TO vlsp;

--
-- TOC entry 253 (class 1259 OID 21596)
-- Name: act_procdef_info; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_procdef_info (
    id_ character varying(64) NOT NULL,
    proc_def_id_ character varying(64) NOT NULL,
    rev_ integer,
    info_json_id_ character varying(64)
);


ALTER TABLE flow.act_procdef_info OWNER TO vlsp;

--
-- TOC entry 247 (class 1259 OID 21553)
-- Name: act_re_deployment; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_re_deployment (
    id_ character varying(64) NOT NULL,
    name_ character varying(255),
    category_ character varying(255),
    key_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    deploy_time_ timestamp without time zone,
    derived_from_ character varying(64),
    derived_from_root_ character varying(64),
    parent_deployment_id_ character varying(255),
    engine_version_ character varying(255)
);


ALTER TABLE flow.act_re_deployment OWNER TO vlsp;

--
-- TOC entry 248 (class 1259 OID 21561)
-- Name: act_re_model; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_re_model (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    key_ character varying(255),
    category_ character varying(255),
    create_time_ timestamp without time zone,
    last_update_time_ timestamp without time zone,
    version_ integer,
    meta_info_ character varying(4000),
    deployment_id_ character varying(64),
    editor_source_value_id_ character varying(64),
    editor_source_extra_value_id_ character varying(64),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.act_re_model OWNER TO vlsp;

--
-- TOC entry 250 (class 1259 OID 21577)
-- Name: act_re_procdef; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_re_procdef (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    name_ character varying(255),
    key_ character varying(255) NOT NULL,
    version_ integer NOT NULL,
    deployment_id_ character varying(64),
    resource_name_ character varying(4000),
    dgrm_resource_name_ character varying(4000),
    description_ character varying(4000),
    has_start_form_key_ boolean,
    has_graphical_notation_ boolean,
    suspension_state_ integer,
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    derived_from_ character varying(64),
    derived_from_root_ character varying(64),
    derived_version_ integer DEFAULT 0 NOT NULL,
    engine_version_ character varying(255)
);


ALTER TABLE flow.act_re_procdef OWNER TO vlsp;

--
-- TOC entry 254 (class 1259 OID 21601)
-- Name: act_ru_actinst; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ru_actinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_def_id_ character varying(64) NOT NULL,
    proc_inst_id_ character varying(64) NOT NULL,
    execution_id_ character varying(64) NOT NULL,
    act_id_ character varying(255) NOT NULL,
    task_id_ character varying(64),
    call_proc_inst_id_ character varying(64),
    act_name_ character varying(255),
    act_type_ character varying(255) NOT NULL,
    assignee_ character varying(255),
    start_time_ timestamp without time zone NOT NULL,
    end_time_ timestamp without time zone,
    duration_ bigint,
    transaction_order_ integer,
    delete_reason_ character varying(4000),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.act_ru_actinst OWNER TO vlsp;

--
-- TOC entry 242 (class 1259 OID 21426)
-- Name: act_ru_deadletter_job; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ru_deadletter_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.act_ru_deadletter_job OWNER TO vlsp;

--
-- TOC entry 230 (class 1259 OID 21309)
-- Name: act_ru_entitylink; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ru_entitylink (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    create_time_ timestamp without time zone,
    link_type_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    parent_element_id_ character varying(255),
    ref_scope_id_ character varying(255),
    ref_scope_type_ character varying(255),
    ref_scope_definition_id_ character varying(255),
    root_scope_id_ character varying(255),
    root_scope_type_ character varying(255),
    hierarchy_type_ character varying(255)
);


ALTER TABLE flow.act_ru_entitylink OWNER TO vlsp;

--
-- TOC entry 232 (class 1259 OID 21331)
-- Name: act_ru_event_subscr; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ru_event_subscr (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    event_type_ character varying(255) NOT NULL,
    event_name_ character varying(255),
    execution_id_ character varying(64),
    proc_inst_id_ character varying(64),
    activity_id_ character varying(64),
    configuration_ character varying(255),
    created_ timestamp without time zone NOT NULL,
    proc_def_id_ character varying(64),
    sub_scope_id_ character varying(64),
    scope_id_ character varying(64),
    scope_definition_id_ character varying(64),
    scope_type_ character varying(64),
    lock_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.act_ru_event_subscr OWNER TO vlsp;

--
-- TOC entry 249 (class 1259 OID 21569)
-- Name: act_ru_execution; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ru_execution (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    proc_inst_id_ character varying(64),
    business_key_ character varying(255),
    parent_id_ character varying(64),
    proc_def_id_ character varying(64),
    super_exec_ character varying(64),
    root_proc_inst_id_ character varying(64),
    act_id_ character varying(255),
    is_active_ boolean,
    is_concurrent_ boolean,
    is_scope_ boolean,
    is_event_scope_ boolean,
    is_mi_root_ boolean,
    suspension_state_ integer,
    cached_ent_state_ integer,
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    name_ character varying(255),
    start_act_id_ character varying(255),
    start_time_ timestamp without time zone,
    start_user_id_ character varying(255),
    lock_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    is_count_enabled_ boolean,
    evt_subscr_count_ integer,
    task_count_ integer,
    job_count_ integer,
    timer_job_count_ integer,
    susp_job_count_ integer,
    deadletter_job_count_ integer,
    external_worker_job_count_ integer,
    var_count_ integer,
    id_link_count_ integer,
    callback_id_ character varying(255),
    callback_type_ character varying(255),
    reference_id_ character varying(255),
    reference_type_ character varying(255),
    propagated_stage_inst_id_ character varying(255),
    business_status_ character varying(255)
);


ALTER TABLE flow.act_ru_execution OWNER TO vlsp;

--
-- TOC entry 244 (class 1259 OID 21442)
-- Name: act_ru_external_job; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ru_external_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    lock_exp_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.act_ru_external_job OWNER TO vlsp;

--
-- TOC entry 243 (class 1259 OID 21434)
-- Name: act_ru_history_job; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ru_history_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    lock_exp_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    adv_handler_cfg_id_ character varying(64),
    create_time_ timestamp without time zone,
    scope_type_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.act_ru_history_job OWNER TO vlsp;

--
-- TOC entry 228 (class 1259 OID 21286)
-- Name: act_ru_identitylink; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ru_identitylink (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    group_id_ character varying(255),
    type_ character varying(255),
    user_id_ character varying(255),
    task_id_ character varying(64),
    proc_inst_id_ character varying(64),
    proc_def_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255)
);


ALTER TABLE flow.act_ru_identitylink OWNER TO vlsp;

--
-- TOC entry 239 (class 1259 OID 21402)
-- Name: act_ru_job; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ru_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    lock_exp_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.act_ru_job OWNER TO vlsp;

--
-- TOC entry 241 (class 1259 OID 21418)
-- Name: act_ru_suspended_job; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ru_suspended_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.act_ru_suspended_job OWNER TO vlsp;

--
-- TOC entry 233 (class 1259 OID 21342)
-- Name: act_ru_task; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ru_task (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    execution_id_ character varying(64),
    proc_inst_id_ character varying(64),
    proc_def_id_ character varying(64),
    task_def_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    propagated_stage_inst_id_ character varying(255),
    name_ character varying(255),
    parent_task_id_ character varying(64),
    description_ character varying(4000),
    task_def_key_ character varying(255),
    owner_ character varying(255),
    assignee_ character varying(255),
    delegation_ character varying(64),
    priority_ integer,
    create_time_ timestamp without time zone,
    due_date_ timestamp without time zone,
    category_ character varying(255),
    suspension_state_ integer,
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    form_key_ character varying(255),
    claim_time_ timestamp without time zone,
    is_count_enabled_ boolean,
    var_count_ integer,
    id_link_count_ integer,
    sub_task_count_ integer
);


ALTER TABLE flow.act_ru_task OWNER TO vlsp;

--
-- TOC entry 240 (class 1259 OID 21410)
-- Name: act_ru_timer_job; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ru_timer_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    lock_exp_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.act_ru_timer_job OWNER TO vlsp;

--
-- TOC entry 237 (class 1259 OID 21376)
-- Name: act_ru_variable; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.act_ru_variable (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    type_ character varying(255) NOT NULL,
    name_ character varying(255) NOT NULL,
    execution_id_ character varying(64),
    proc_inst_id_ character varying(64),
    task_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    bytearray_id_ character varying(64),
    double_ double precision,
    long_ bigint,
    text_ character varying(4000),
    text2_ character varying(4000)
);


ALTER TABLE flow.act_ru_variable OWNER TO vlsp;

--
-- TOC entry 261 (class 1259 OID 21870)
-- Name: fli_flow_form; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.fli_flow_form (
    id_ integer NOT NULL,
    name_ character varying(255),
    key_ character varying(255),
    tenant_id_ character varying(255),
    description_ character varying(1024),
    payload_ bytea,
    update_time_ timestamp without time zone DEFAULT now()
);


ALTER TABLE flow.fli_flow_form OWNER TO vlsp;

--
-- TOC entry 260 (class 1259 OID 21869)
-- Name: fli_flow_form_id__seq; Type: SEQUENCE; Schema: flow; Owner: vlsp
--

CREATE SEQUENCE flow.fli_flow_form_id__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE flow.fli_flow_form_id__seq OWNER TO vlsp;

--
-- TOC entry 4063 (class 0 OID 0)
-- Dependencies: 260
-- Name: fli_flow_form_id__seq; Type: SEQUENCE OWNED BY; Schema: flow; Owner: vlsp
--

ALTER SEQUENCE flow.fli_flow_form_id__seq OWNED BY flow.fli_flow_form.id_;


--
-- TOC entry 263 (class 1259 OID 21880)
-- Name: fli_flow_form_ref; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.fli_flow_form_ref (
    id_ integer NOT NULL,
    key_ character varying(255),
    process_id_ character varying(64) NOT NULL,
    execution_id_ character varying(255),
    update_time_ timestamp without time zone DEFAULT now(),
    payload_ jsonb
);


ALTER TABLE flow.fli_flow_form_ref OWNER TO vlsp;

--
-- TOC entry 262 (class 1259 OID 21879)
-- Name: fli_flow_form_ref_id__seq; Type: SEQUENCE; Schema: flow; Owner: vlsp
--

CREATE SEQUENCE flow.fli_flow_form_ref_id__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE flow.fli_flow_form_ref_id__seq OWNER TO vlsp;

--
-- TOC entry 4064 (class 0 OID 0)
-- Dependencies: 262
-- Name: fli_flow_form_ref_id__seq; Type: SEQUENCE OWNED BY; Schema: flow; Owner: vlsp
--

ALTER SEQUENCE flow.fli_flow_form_ref_id__seq OWNED BY flow.fli_flow_form_ref.id_;


--
-- TOC entry 216 (class 1259 OID 21185)
-- Name: flw_channel_definition; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.flw_channel_definition (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    version_ integer,
    key_ character varying(255),
    category_ character varying(255),
    deployment_id_ character varying(255),
    create_time_ timestamp(3) without time zone,
    tenant_id_ character varying(255),
    resource_name_ character varying(255),
    description_ character varying(255),
    type_ character varying(255),
    implementation_ character varying(255)
);


ALTER TABLE flow.flw_channel_definition OWNER TO vlsp;

--
-- TOC entry 212 (class 1259 OID 21158)
-- Name: flw_ev_databasechangelog; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.flw_ev_databasechangelog (
    id character varying(255) NOT NULL,
    author character varying(255) NOT NULL,
    filename character varying(255) NOT NULL,
    dateexecuted timestamp without time zone NOT NULL,
    orderexecuted integer NOT NULL,
    exectype character varying(10) NOT NULL,
    md5sum character varying(35),
    description character varying(255),
    comments character varying(255),
    tag character varying(255),
    liquibase character varying(20),
    contexts character varying(255),
    labels character varying(255),
    deployment_id character varying(10)
);


ALTER TABLE flow.flw_ev_databasechangelog OWNER TO vlsp;

--
-- TOC entry 211 (class 1259 OID 21153)
-- Name: flw_ev_databasechangeloglock; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.flw_ev_databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp without time zone,
    lockedby character varying(255)
);


ALTER TABLE flow.flw_ev_databasechangeloglock OWNER TO vlsp;

--
-- TOC entry 215 (class 1259 OID 21177)
-- Name: flw_event_definition; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.flw_event_definition (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    version_ integer,
    key_ character varying(255),
    category_ character varying(255),
    deployment_id_ character varying(255),
    tenant_id_ character varying(255),
    resource_name_ character varying(255),
    description_ character varying(255)
);


ALTER TABLE flow.flw_event_definition OWNER TO vlsp;

--
-- TOC entry 213 (class 1259 OID 21163)
-- Name: flw_event_deployment; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.flw_event_deployment (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    category_ character varying(255),
    deploy_time_ timestamp(3) without time zone,
    tenant_id_ character varying(255),
    parent_deployment_id_ character varying(255)
);


ALTER TABLE flow.flw_event_deployment OWNER TO vlsp;

--
-- TOC entry 214 (class 1259 OID 21170)
-- Name: flw_event_resource; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.flw_event_resource (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    deployment_id_ character varying(255),
    resource_bytes_ bytea
);


ALTER TABLE flow.flw_event_resource OWNER TO vlsp;

--
-- TOC entry 245 (class 1259 OID 21531)
-- Name: flw_ru_batch; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.flw_ru_batch (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    type_ character varying(64) NOT NULL,
    search_key_ character varying(255),
    search_key2_ character varying(255),
    create_time_ timestamp without time zone NOT NULL,
    complete_time_ timestamp without time zone,
    status_ character varying(255),
    batch_doc_id_ character varying(64),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.flw_ru_batch OWNER TO vlsp;

--
-- TOC entry 246 (class 1259 OID 21539)
-- Name: flw_ru_batch_part; Type: TABLE; Schema: flow; Owner: vlsp
--

CREATE TABLE flow.flw_ru_batch_part (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    batch_id_ character varying(64),
    type_ character varying(64) NOT NULL,
    scope_id_ character varying(64),
    sub_scope_id_ character varying(64),
    scope_type_ character varying(64),
    search_key_ character varying(255),
    search_key2_ character varying(255),
    create_time_ timestamp without time zone NOT NULL,
    complete_time_ timestamp without time zone,
    status_ character varying(255),
    result_doc_id_ character varying(64),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE flow.flw_ru_batch_part OWNER TO vlsp;

--
-- TOC entry 3555 (class 2604 OID 21590)
-- Name: act_evt_log log_nr_; Type: DEFAULT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_evt_log ALTER COLUMN log_nr_ SET DEFAULT nextval('flow.act_evt_log_log_nr__seq'::regclass);


--
-- TOC entry 3539 (class 2604 OID 21367)
-- Name: act_hi_tsk_log id_; Type: DEFAULT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_hi_tsk_log ALTER COLUMN id_ SET DEFAULT nextval('flow.act_hi_tsk_log_id__seq'::regclass);


--
-- TOC entry 3563 (class 2604 OID 21873)
-- Name: fli_flow_form id_; Type: DEFAULT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.fli_flow_form ALTER COLUMN id_ SET DEFAULT nextval('flow.fli_flow_form_id__seq'::regclass);


--
-- TOC entry 3565 (class 2604 OID 21883)
-- Name: fli_flow_form_ref id_; Type: DEFAULT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.fli_flow_form_ref ALTER COLUMN id_ SET DEFAULT nextval('flow.fli_flow_form_ref_id__seq'::regclass);


--
-- TOC entry 4044 (class 0 OID 21587)
-- Dependencies: 252
-- Data for Name: act_evt_log; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4019 (class 0 OID 21279)
-- Dependencies: 227
-- Data for Name: act_ge_bytearray; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4018 (class 0 OID 21274)
-- Dependencies: 226
-- Data for Name: act_ge_property; Type: TABLE DATA; Schema: flow; Owner: vlsp
--

INSERT INTO flow.act_ge_property VALUES ('common.schema.version', '6.8.0.0', 1);
INSERT INTO flow.act_ge_property VALUES ('next.dbid', '1', 1);
INSERT INTO flow.act_ge_property VALUES ('identitylink.schema.version', '6.8.0.0', 1);
INSERT INTO flow.act_ge_property VALUES ('entitylink.schema.version', '6.8.0.0', 1);
INSERT INTO flow.act_ge_property VALUES ('eventsubscription.schema.version', '6.8.0.0', 1);
INSERT INTO flow.act_ge_property VALUES ('task.schema.version', '6.8.0.0', 1);
INSERT INTO flow.act_ge_property VALUES ('variable.schema.version', '6.8.0.0', 1);
INSERT INTO flow.act_ge_property VALUES ('job.schema.version', '6.8.0.0', 1);
INSERT INTO flow.act_ge_property VALUES ('batch.schema.version', '6.8.0.0', 1);
INSERT INTO flow.act_ge_property VALUES ('schema.version', '6.8.0.0', 1);
INSERT INTO flow.act_ge_property VALUES ('schema.history', 'create(6.8.0.0)', 1);
INSERT INTO flow.act_ge_property VALUES ('cfg.execution-related-entities-count', 'true', 1);
INSERT INTO flow.act_ge_property VALUES ('cfg.task-related-entities-count', 'true', 1);


--
-- TOC entry 4048 (class 0 OID 21821)
-- Dependencies: 256
-- Data for Name: act_hi_actinst; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4051 (class 0 OID 21844)
-- Dependencies: 259
-- Data for Name: act_hi_attachment; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4050 (class 0 OID 21837)
-- Dependencies: 258
-- Data for Name: act_hi_comment; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4049 (class 0 OID 21830)
-- Dependencies: 257
-- Data for Name: act_hi_detail; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4023 (class 0 OID 21320)
-- Dependencies: 231
-- Data for Name: act_hi_entitylink; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4021 (class 0 OID 21298)
-- Dependencies: 229
-- Data for Name: act_hi_identitylink; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4047 (class 0 OID 21810)
-- Dependencies: 255
-- Data for Name: act_hi_procinst; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4026 (class 0 OID 21354)
-- Dependencies: 234
-- Data for Name: act_hi_taskinst; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4028 (class 0 OID 21364)
-- Dependencies: 236
-- Data for Name: act_hi_tsk_log; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4030 (class 0 OID 21391)
-- Dependencies: 238
-- Data for Name: act_hi_varinst; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4010 (class 0 OID 21198)
-- Dependencies: 218
-- Data for Name: act_id_bytearray; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4011 (class 0 OID 21205)
-- Dependencies: 219
-- Data for Name: act_id_group; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4014 (class 0 OID 21225)
-- Dependencies: 222
-- Data for Name: act_id_info; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4012 (class 0 OID 21212)
-- Dependencies: 220
-- Data for Name: act_id_membership; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4016 (class 0 OID 21239)
-- Dependencies: 224
-- Data for Name: act_id_priv; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4017 (class 0 OID 21244)
-- Dependencies: 225
-- Data for Name: act_id_priv_mapping; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4009 (class 0 OID 21193)
-- Dependencies: 217
-- Data for Name: act_id_property; Type: TABLE DATA; Schema: flow; Owner: vlsp
--

INSERT INTO flow.act_id_property VALUES ('schema.version', '6.8.0.0', 1);


--
-- TOC entry 4015 (class 0 OID 21232)
-- Dependencies: 223
-- Data for Name: act_id_token; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4013 (class 0 OID 21217)
-- Dependencies: 221
-- Data for Name: act_id_user; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4045 (class 0 OID 21596)
-- Dependencies: 253
-- Data for Name: act_procdef_info; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4039 (class 0 OID 21553)
-- Dependencies: 247
-- Data for Name: act_re_deployment; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4040 (class 0 OID 21561)
-- Dependencies: 248
-- Data for Name: act_re_model; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4042 (class 0 OID 21577)
-- Dependencies: 250
-- Data for Name: act_re_procdef; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4046 (class 0 OID 21601)
-- Dependencies: 254
-- Data for Name: act_ru_actinst; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4034 (class 0 OID 21426)
-- Dependencies: 242
-- Data for Name: act_ru_deadletter_job; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4022 (class 0 OID 21309)
-- Dependencies: 230
-- Data for Name: act_ru_entitylink; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4024 (class 0 OID 21331)
-- Dependencies: 232
-- Data for Name: act_ru_event_subscr; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4041 (class 0 OID 21569)
-- Dependencies: 249
-- Data for Name: act_ru_execution; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4036 (class 0 OID 21442)
-- Dependencies: 244
-- Data for Name: act_ru_external_job; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4035 (class 0 OID 21434)
-- Dependencies: 243
-- Data for Name: act_ru_history_job; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4020 (class 0 OID 21286)
-- Dependencies: 228
-- Data for Name: act_ru_identitylink; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4031 (class 0 OID 21402)
-- Dependencies: 239
-- Data for Name: act_ru_job; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4033 (class 0 OID 21418)
-- Dependencies: 241
-- Data for Name: act_ru_suspended_job; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4025 (class 0 OID 21342)
-- Dependencies: 233
-- Data for Name: act_ru_task; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4032 (class 0 OID 21410)
-- Dependencies: 240
-- Data for Name: act_ru_timer_job; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4029 (class 0 OID 21376)
-- Dependencies: 237
-- Data for Name: act_ru_variable; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4053 (class 0 OID 21870)
-- Dependencies: 261
-- Data for Name: fli_flow_form; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4055 (class 0 OID 21880)
-- Dependencies: 263
-- Data for Name: fli_flow_form_ref; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4008 (class 0 OID 21185)
-- Dependencies: 216
-- Data for Name: flw_channel_definition; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4004 (class 0 OID 21158)
-- Dependencies: 212
-- Data for Name: flw_ev_databasechangelog; Type: TABLE DATA; Schema: flow; Owner: vlsp
--

INSERT INTO flow.flw_ev_databasechangelog VALUES ('1', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', '2025-07-09 14:55:25.372037', 1, 'EXECUTED', '8:1b0c48c9cf7945be799d868a2626d687', 'createTable tableName=FLW_EVENT_DEPLOYMENT; createTable tableName=FLW_EVENT_RESOURCE; createTable tableName=FLW_EVENT_DEFINITION; createIndex indexName=ACT_IDX_EVENT_DEF_UNIQ, tableName=FLW_EVENT_DEFINITION; createTable tableName=FLW_CHANNEL_DEFIN...', '', NULL, '4.9.0', NULL, NULL, '2044124534');
INSERT INTO flow.flw_ev_databasechangelog VALUES ('2', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', '2025-07-09 14:55:25.520481', 2, 'EXECUTED', '8:0ea825feb8e470558f0b5754352b9cda', 'addColumn tableName=FLW_CHANNEL_DEFINITION; addColumn tableName=FLW_CHANNEL_DEFINITION', '', NULL, '4.9.0', NULL, NULL, '2044124534');
INSERT INTO flow.flw_ev_databasechangelog VALUES ('3', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', '2025-07-09 14:55:25.770626', 3, 'EXECUTED', '8:3c2bb293350b5cbe6504331980c9dcee', 'customChange', '', NULL, '4.9.0', NULL, NULL, '2044124534');


--
-- TOC entry 4003 (class 0 OID 21153)
-- Dependencies: 211
-- Data for Name: flw_ev_databasechangeloglock; Type: TABLE DATA; Schema: flow; Owner: vlsp
--

INSERT INTO flow.flw_ev_databasechangeloglock VALUES (1, false, NULL, NULL);


--
-- TOC entry 4007 (class 0 OID 21177)
-- Dependencies: 215
-- Data for Name: flw_event_definition; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4005 (class 0 OID 21163)
-- Dependencies: 213
-- Data for Name: flw_event_deployment; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4006 (class 0 OID 21170)
-- Dependencies: 214
-- Data for Name: flw_event_resource; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4037 (class 0 OID 21531)
-- Dependencies: 245
-- Data for Name: flw_ru_batch; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4038 (class 0 OID 21539)
-- Dependencies: 246
-- Data for Name: flw_ru_batch_part; Type: TABLE DATA; Schema: flow; Owner: vlsp
--



--
-- TOC entry 4065 (class 0 OID 0)
-- Dependencies: 251
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE SET; Schema: flow; Owner: vlsp
--

SELECT pg_catalog.setval('flow.act_evt_log_log_nr__seq', 1, false);


--
-- TOC entry 4066 (class 0 OID 0)
-- Dependencies: 235
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE SET; Schema: flow; Owner: vlsp
--

SELECT pg_catalog.setval('flow.act_hi_tsk_log_id__seq', 1, false);


--
-- TOC entry 4067 (class 0 OID 0)
-- Dependencies: 260
-- Name: fli_flow_form_id__seq; Type: SEQUENCE SET; Schema: flow; Owner: vlsp
--

SELECT pg_catalog.setval('flow.fli_flow_form_id__seq', 4, true);


--
-- TOC entry 4068 (class 0 OID 0)
-- Dependencies: 262
-- Name: fli_flow_form_ref_id__seq; Type: SEQUENCE SET; Schema: flow; Owner: vlsp
--

SELECT pg_catalog.setval('flow.fli_flow_form_ref_id__seq', 1, false);


--
-- TOC entry 3577 (class 2606 OID 21191)
-- Name: flw_channel_definition FLW_CHANNEL_DEFINITION_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.flw_channel_definition
    ADD CONSTRAINT "FLW_CHANNEL_DEFINITION_pkey" PRIMARY KEY (id_);


--
-- TOC entry 3574 (class 2606 OID 21183)
-- Name: flw_event_definition FLW_EVENT_DEFINITION_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.flw_event_definition
    ADD CONSTRAINT "FLW_EVENT_DEFINITION_pkey" PRIMARY KEY (id_);


--
-- TOC entry 3570 (class 2606 OID 21169)
-- Name: flw_event_deployment FLW_EVENT_DEPLOYMENT_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.flw_event_deployment
    ADD CONSTRAINT "FLW_EVENT_DEPLOYMENT_pkey" PRIMARY KEY (id_);


--
-- TOC entry 3572 (class 2606 OID 21176)
-- Name: flw_event_resource FLW_EVENT_RESOURCE_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.flw_event_resource
    ADD CONSTRAINT "FLW_EVENT_RESOURCE_pkey" PRIMARY KEY (id_);


--
-- TOC entry 3758 (class 2606 OID 21595)
-- Name: act_evt_log act_evt_log_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_evt_log
    ADD CONSTRAINT act_evt_log_pkey PRIMARY KEY (log_nr_);


--
-- TOC entry 3607 (class 2606 OID 21285)
-- Name: act_ge_bytearray act_ge_bytearray_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ge_bytearray
    ADD CONSTRAINT act_ge_bytearray_pkey PRIMARY KEY (id_);


--
-- TOC entry 3605 (class 2606 OID 21278)
-- Name: act_ge_property act_ge_property_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ge_property
    ADD CONSTRAINT act_ge_property_pkey PRIMARY KEY (name_);


--
-- TOC entry 3782 (class 2606 OID 21829)
-- Name: act_hi_actinst act_hi_actinst_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_hi_actinst
    ADD CONSTRAINT act_hi_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 3797 (class 2606 OID 21850)
-- Name: act_hi_attachment act_hi_attachment_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_hi_attachment
    ADD CONSTRAINT act_hi_attachment_pkey PRIMARY KEY (id_);


--
-- TOC entry 3795 (class 2606 OID 21843)
-- Name: act_hi_comment act_hi_comment_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_hi_comment
    ADD CONSTRAINT act_hi_comment_pkey PRIMARY KEY (id_);


--
-- TOC entry 3788 (class 2606 OID 21836)
-- Name: act_hi_detail act_hi_detail_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_hi_detail
    ADD CONSTRAINT act_hi_detail_pkey PRIMARY KEY (id_);


--
-- TOC entry 3634 (class 2606 OID 21326)
-- Name: act_hi_entitylink act_hi_entitylink_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_hi_entitylink
    ADD CONSTRAINT act_hi_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 3620 (class 2606 OID 21304)
-- Name: act_hi_identitylink act_hi_identitylink_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_hi_identitylink
    ADD CONSTRAINT act_hi_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 3775 (class 2606 OID 21818)
-- Name: act_hi_procinst act_hi_procinst_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 3777 (class 2606 OID 21820)
-- Name: act_hi_procinst act_hi_procinst_proc_inst_id__key; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_proc_inst_id__key UNIQUE (proc_inst_id_);


--
-- TOC entry 3654 (class 2606 OID 21362)
-- Name: act_hi_taskinst act_hi_taskinst_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_hi_taskinst
    ADD CONSTRAINT act_hi_taskinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 3660 (class 2606 OID 21372)
-- Name: act_hi_tsk_log act_hi_tsk_log_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_hi_tsk_log
    ADD CONSTRAINT act_hi_tsk_log_pkey PRIMARY KEY (id_);


--
-- TOC entry 3670 (class 2606 OID 21398)
-- Name: act_hi_varinst act_hi_varinst_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_hi_varinst
    ADD CONSTRAINT act_hi_varinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 3582 (class 2606 OID 21204)
-- Name: act_id_bytearray act_id_bytearray_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_id_bytearray
    ADD CONSTRAINT act_id_bytearray_pkey PRIMARY KEY (id_);


--
-- TOC entry 3584 (class 2606 OID 21211)
-- Name: act_id_group act_id_group_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_id_group
    ADD CONSTRAINT act_id_group_pkey PRIMARY KEY (id_);


--
-- TOC entry 3592 (class 2606 OID 21231)
-- Name: act_id_info act_id_info_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_id_info
    ADD CONSTRAINT act_id_info_pkey PRIMARY KEY (id_);


--
-- TOC entry 3586 (class 2606 OID 21216)
-- Name: act_id_membership act_id_membership_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_id_membership
    ADD CONSTRAINT act_id_membership_pkey PRIMARY KEY (user_id_, group_id_);


--
-- TOC entry 3600 (class 2606 OID 21250)
-- Name: act_id_priv_mapping act_id_priv_mapping_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_id_priv_mapping
    ADD CONSTRAINT act_id_priv_mapping_pkey PRIMARY KEY (id_);


--
-- TOC entry 3596 (class 2606 OID 21243)
-- Name: act_id_priv act_id_priv_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_id_priv
    ADD CONSTRAINT act_id_priv_pkey PRIMARY KEY (id_);


--
-- TOC entry 3580 (class 2606 OID 21197)
-- Name: act_id_property act_id_property_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_id_property
    ADD CONSTRAINT act_id_property_pkey PRIMARY KEY (name_);


--
-- TOC entry 3594 (class 2606 OID 21238)
-- Name: act_id_token act_id_token_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_id_token
    ADD CONSTRAINT act_id_token_pkey PRIMARY KEY (id_);


--
-- TOC entry 3590 (class 2606 OID 21224)
-- Name: act_id_user act_id_user_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_id_user
    ADD CONSTRAINT act_id_user_pkey PRIMARY KEY (id_);


--
-- TOC entry 3762 (class 2606 OID 21600)
-- Name: act_procdef_info act_procdef_info_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_procdef_info
    ADD CONSTRAINT act_procdef_info_pkey PRIMARY KEY (id_);


--
-- TOC entry 3738 (class 2606 OID 21560)
-- Name: act_re_deployment act_re_deployment_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_re_deployment
    ADD CONSTRAINT act_re_deployment_pkey PRIMARY KEY (id_);


--
-- TOC entry 3743 (class 2606 OID 21568)
-- Name: act_re_model act_re_model_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_re_model
    ADD CONSTRAINT act_re_model_pkey PRIMARY KEY (id_);


--
-- TOC entry 3754 (class 2606 OID 21585)
-- Name: act_re_procdef act_re_procdef_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_re_procdef
    ADD CONSTRAINT act_re_procdef_pkey PRIMARY KEY (id_);


--
-- TOC entry 3773 (class 2606 OID 21609)
-- Name: act_ru_actinst act_ru_actinst_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_actinst
    ADD CONSTRAINT act_ru_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 3721 (class 2606 OID 21433)
-- Name: act_ru_deadletter_job act_ru_deadletter_job_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_deadletter_job
    ADD CONSTRAINT act_ru_deadletter_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 3632 (class 2606 OID 21315)
-- Name: act_ru_entitylink act_ru_entitylink_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_entitylink
    ADD CONSTRAINT act_ru_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 3643 (class 2606 OID 21338)
-- Name: act_ru_event_subscr act_ru_event_subscr_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_event_subscr
    ADD CONSTRAINT act_ru_event_subscr_pkey PRIMARY KEY (id_);


--
-- TOC entry 3752 (class 2606 OID 21576)
-- Name: act_ru_execution act_ru_execution_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_execution
    ADD CONSTRAINT act_ru_execution_pkey PRIMARY KEY (id_);


--
-- TOC entry 3731 (class 2606 OID 21449)
-- Name: act_ru_external_job act_ru_external_job_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_external_job
    ADD CONSTRAINT act_ru_external_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 3723 (class 2606 OID 21441)
-- Name: act_ru_history_job act_ru_history_job_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_history_job
    ADD CONSTRAINT act_ru_history_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 3618 (class 2606 OID 21292)
-- Name: act_ru_identitylink act_ru_identitylink_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_identitylink
    ADD CONSTRAINT act_ru_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 3687 (class 2606 OID 21409)
-- Name: act_ru_job act_ru_job_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_job
    ADD CONSTRAINT act_ru_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 3710 (class 2606 OID 21425)
-- Name: act_ru_suspended_job act_ru_suspended_job_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_suspended_job
    ADD CONSTRAINT act_ru_suspended_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 3652 (class 2606 OID 21349)
-- Name: act_ru_task act_ru_task_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_task
    ADD CONSTRAINT act_ru_task_pkey PRIMARY KEY (id_);


--
-- TOC entry 3699 (class 2606 OID 21417)
-- Name: act_ru_timer_job act_ru_timer_job_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_timer_job
    ADD CONSTRAINT act_ru_timer_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 3668 (class 2606 OID 21382)
-- Name: act_ru_variable act_ru_variable_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_variable
    ADD CONSTRAINT act_ru_variable_pkey PRIMARY KEY (id_);


--
-- TOC entry 3764 (class 2606 OID 21809)
-- Name: act_procdef_info act_uniq_info_procdef; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_procdef_info
    ADD CONSTRAINT act_uniq_info_procdef UNIQUE (proc_def_id_);


--
-- TOC entry 3598 (class 2606 OID 21273)
-- Name: act_id_priv act_uniq_priv_name; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_id_priv
    ADD CONSTRAINT act_uniq_priv_name UNIQUE (name_);


--
-- TOC entry 3756 (class 2606 OID 21628)
-- Name: act_re_procdef act_uniq_procdef; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_re_procdef
    ADD CONSTRAINT act_uniq_procdef UNIQUE (key_, version_, derived_version_, tenant_id_);


--
-- TOC entry 3799 (class 2606 OID 21878)
-- Name: fli_flow_form fli_flow_form_pk; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.fli_flow_form
    ADD CONSTRAINT fli_flow_form_pk PRIMARY KEY (id_);


--
-- TOC entry 3802 (class 2606 OID 21888)
-- Name: fli_flow_form_ref fli_flow_form_ref_pk; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.fli_flow_form_ref
    ADD CONSTRAINT fli_flow_form_ref_pk PRIMARY KEY (id_);


--
-- TOC entry 3568 (class 2606 OID 21157)
-- Name: flw_ev_databasechangeloglock flw_ev_databasechangeloglock_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.flw_ev_databasechangeloglock
    ADD CONSTRAINT flw_ev_databasechangeloglock_pkey PRIMARY KEY (id);


--
-- TOC entry 3736 (class 2606 OID 21546)
-- Name: flw_ru_batch_part flw_ru_batch_part_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.flw_ru_batch_part
    ADD CONSTRAINT flw_ru_batch_part_pkey PRIMARY KEY (id_);


--
-- TOC entry 3733 (class 2606 OID 21538)
-- Name: flw_ru_batch flw_ru_batch_pkey; Type: CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.flw_ru_batch
    ADD CONSTRAINT flw_ru_batch_pkey PRIMARY KEY (id_);


--
-- TOC entry 3609 (class 1259 OID 21659)
-- Name: act_idx_athrz_procedef; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_athrz_procedef ON flow.act_ru_identitylink USING btree (proc_def_id_);


--
-- TOC entry 3608 (class 1259 OID 21614)
-- Name: act_idx_bytear_depl; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_bytear_depl ON flow.act_ge_bytearray USING btree (deployment_id_);


--
-- TOC entry 3578 (class 1259 OID 21192)
-- Name: act_idx_channel_def_uniq; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE UNIQUE INDEX act_idx_channel_def_uniq ON flow.flw_channel_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 3711 (class 1259 OID 21462)
-- Name: act_idx_deadletter_job_correlation_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_deadletter_job_correlation_id ON flow.act_ru_deadletter_job USING btree (correlation_id_);


--
-- TOC entry 3712 (class 1259 OID 21461)
-- Name: act_idx_deadletter_job_custom_values_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_deadletter_job_custom_values_id ON flow.act_ru_deadletter_job USING btree (custom_values_id_);


--
-- TOC entry 3713 (class 1259 OID 21460)
-- Name: act_idx_deadletter_job_exception_stack_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_deadletter_job_exception_stack_id ON flow.act_ru_deadletter_job USING btree (exception_stack_id_);


--
-- TOC entry 3714 (class 1259 OID 21755)
-- Name: act_idx_deadletter_job_execution_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_deadletter_job_execution_id ON flow.act_ru_deadletter_job USING btree (execution_id_);


--
-- TOC entry 3715 (class 1259 OID 21767)
-- Name: act_idx_deadletter_job_proc_def_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_deadletter_job_proc_def_id ON flow.act_ru_deadletter_job USING btree (proc_def_id_);


--
-- TOC entry 3716 (class 1259 OID 21761)
-- Name: act_idx_deadletter_job_process_instance_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_deadletter_job_process_instance_id ON flow.act_ru_deadletter_job USING btree (process_instance_id_);


--
-- TOC entry 3717 (class 1259 OID 21525)
-- Name: act_idx_djob_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_djob_scope ON flow.act_ru_deadletter_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 3718 (class 1259 OID 21527)
-- Name: act_idx_djob_scope_def; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_djob_scope_def ON flow.act_ru_deadletter_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 3719 (class 1259 OID 21526)
-- Name: act_idx_djob_sub_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_djob_sub_scope ON flow.act_ru_deadletter_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 3724 (class 1259 OID 21528)
-- Name: act_idx_ejob_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ejob_scope ON flow.act_ru_external_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 3725 (class 1259 OID 21530)
-- Name: act_idx_ejob_scope_def; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ejob_scope_def ON flow.act_ru_external_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 3726 (class 1259 OID 21529)
-- Name: act_idx_ejob_sub_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ejob_sub_scope ON flow.act_ru_external_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 3627 (class 1259 OID 21317)
-- Name: act_idx_ent_lnk_ref_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ent_lnk_ref_scope ON flow.act_ru_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 3628 (class 1259 OID 21318)
-- Name: act_idx_ent_lnk_root_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ent_lnk_root_scope ON flow.act_ru_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 3629 (class 1259 OID 21316)
-- Name: act_idx_ent_lnk_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ent_lnk_scope ON flow.act_ru_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 3630 (class 1259 OID 21319)
-- Name: act_idx_ent_lnk_scope_def; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ent_lnk_scope_def ON flow.act_ru_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 3575 (class 1259 OID 21184)
-- Name: act_idx_event_def_uniq; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE UNIQUE INDEX act_idx_event_def_uniq ON flow.flw_event_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 3639 (class 1259 OID 21340)
-- Name: act_idx_event_subscr; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_event_subscr ON flow.act_ru_event_subscr USING btree (execution_id_);


--
-- TOC entry 3640 (class 1259 OID 21339)
-- Name: act_idx_event_subscr_config_; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_event_subscr_config_ ON flow.act_ru_event_subscr USING btree (configuration_);


--
-- TOC entry 3641 (class 1259 OID 21341)
-- Name: act_idx_event_subscr_scoperef_; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_event_subscr_scoperef_ ON flow.act_ru_event_subscr USING btree (scope_id_, scope_type_);


--
-- TOC entry 3744 (class 1259 OID 21635)
-- Name: act_idx_exe_parent; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_exe_parent ON flow.act_ru_execution USING btree (parent_id_);


--
-- TOC entry 3745 (class 1259 OID 21647)
-- Name: act_idx_exe_procdef; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_exe_procdef ON flow.act_ru_execution USING btree (proc_def_id_);


--
-- TOC entry 3746 (class 1259 OID 21629)
-- Name: act_idx_exe_procinst; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_exe_procinst ON flow.act_ru_execution USING btree (proc_inst_id_);


--
-- TOC entry 3747 (class 1259 OID 21611)
-- Name: act_idx_exe_root; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_exe_root ON flow.act_ru_execution USING btree (root_proc_inst_id_);


--
-- TOC entry 3748 (class 1259 OID 21641)
-- Name: act_idx_exe_super; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_exe_super ON flow.act_ru_execution USING btree (super_exec_);


--
-- TOC entry 3749 (class 1259 OID 21610)
-- Name: act_idx_exec_buskey; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_exec_buskey ON flow.act_ru_execution USING btree (business_key_);


--
-- TOC entry 3750 (class 1259 OID 21612)
-- Name: act_idx_exec_ref_id_; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_exec_ref_id_ ON flow.act_ru_execution USING btree (reference_id_);


--
-- TOC entry 3727 (class 1259 OID 21465)
-- Name: act_idx_external_job_correlation_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_external_job_correlation_id ON flow.act_ru_external_job USING btree (correlation_id_);


--
-- TOC entry 3728 (class 1259 OID 21464)
-- Name: act_idx_external_job_custom_values_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_external_job_custom_values_id ON flow.act_ru_external_job USING btree (custom_values_id_);


--
-- TOC entry 3729 (class 1259 OID 21463)
-- Name: act_idx_external_job_exception_stack_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_external_job_exception_stack_id ON flow.act_ru_external_job USING btree (exception_stack_id_);


--
-- TOC entry 3783 (class 1259 OID 21855)
-- Name: act_idx_hi_act_inst_end; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_act_inst_end ON flow.act_hi_actinst USING btree (end_time_);


--
-- TOC entry 3784 (class 1259 OID 21865)
-- Name: act_idx_hi_act_inst_exec; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_act_inst_exec ON flow.act_hi_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 3785 (class 1259 OID 21864)
-- Name: act_idx_hi_act_inst_procinst; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_act_inst_procinst ON flow.act_hi_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 3786 (class 1259 OID 21854)
-- Name: act_idx_hi_act_inst_start; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_act_inst_start ON flow.act_hi_actinst USING btree (start_time_);


--
-- TOC entry 3789 (class 1259 OID 21857)
-- Name: act_idx_hi_detail_act_inst; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_detail_act_inst ON flow.act_hi_detail USING btree (act_inst_id_);


--
-- TOC entry 3790 (class 1259 OID 21859)
-- Name: act_idx_hi_detail_name; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_detail_name ON flow.act_hi_detail USING btree (name_);


--
-- TOC entry 3791 (class 1259 OID 21856)
-- Name: act_idx_hi_detail_proc_inst; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_detail_proc_inst ON flow.act_hi_detail USING btree (proc_inst_id_);


--
-- TOC entry 3792 (class 1259 OID 21860)
-- Name: act_idx_hi_detail_task_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_detail_task_id ON flow.act_hi_detail USING btree (task_id_);


--
-- TOC entry 3793 (class 1259 OID 21858)
-- Name: act_idx_hi_detail_time; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_detail_time ON flow.act_hi_detail USING btree (time_);


--
-- TOC entry 3635 (class 1259 OID 21328)
-- Name: act_idx_hi_ent_lnk_ref_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_ent_lnk_ref_scope ON flow.act_hi_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 3636 (class 1259 OID 21329)
-- Name: act_idx_hi_ent_lnk_root_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_ent_lnk_root_scope ON flow.act_hi_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 3637 (class 1259 OID 21327)
-- Name: act_idx_hi_ent_lnk_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_ent_lnk_scope ON flow.act_hi_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 3638 (class 1259 OID 21330)
-- Name: act_idx_hi_ent_lnk_scope_def; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_ent_lnk_scope_def ON flow.act_hi_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 3621 (class 1259 OID 21867)
-- Name: act_idx_hi_ident_lnk_procinst; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_ident_lnk_procinst ON flow.act_hi_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 3622 (class 1259 OID 21306)
-- Name: act_idx_hi_ident_lnk_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_ident_lnk_scope ON flow.act_hi_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 3623 (class 1259 OID 21308)
-- Name: act_idx_hi_ident_lnk_scope_def; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_ident_lnk_scope_def ON flow.act_hi_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 3624 (class 1259 OID 21307)
-- Name: act_idx_hi_ident_lnk_sub_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_ident_lnk_sub_scope ON flow.act_hi_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 3625 (class 1259 OID 21866)
-- Name: act_idx_hi_ident_lnk_task; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_ident_lnk_task ON flow.act_hi_identitylink USING btree (task_id_);


--
-- TOC entry 3626 (class 1259 OID 21305)
-- Name: act_idx_hi_ident_lnk_user; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_ident_lnk_user ON flow.act_hi_identitylink USING btree (user_id_);


--
-- TOC entry 3778 (class 1259 OID 21852)
-- Name: act_idx_hi_pro_i_buskey; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_pro_i_buskey ON flow.act_hi_procinst USING btree (business_key_);


--
-- TOC entry 3779 (class 1259 OID 21851)
-- Name: act_idx_hi_pro_inst_end; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_pro_inst_end ON flow.act_hi_procinst USING btree (end_time_);


--
-- TOC entry 3780 (class 1259 OID 21853)
-- Name: act_idx_hi_pro_super_procinst; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_pro_super_procinst ON flow.act_hi_procinst USING btree (super_process_instance_id_);


--
-- TOC entry 3671 (class 1259 OID 21863)
-- Name: act_idx_hi_procvar_exe; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_procvar_exe ON flow.act_hi_varinst USING btree (execution_id_);


--
-- TOC entry 3672 (class 1259 OID 21399)
-- Name: act_idx_hi_procvar_name_type; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_procvar_name_type ON flow.act_hi_varinst USING btree (name_, var_type_);


--
-- TOC entry 3673 (class 1259 OID 21861)
-- Name: act_idx_hi_procvar_proc_inst; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_procvar_proc_inst ON flow.act_hi_varinst USING btree (proc_inst_id_);


--
-- TOC entry 3674 (class 1259 OID 21862)
-- Name: act_idx_hi_procvar_task_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_procvar_task_id ON flow.act_hi_varinst USING btree (task_id_);


--
-- TOC entry 3655 (class 1259 OID 21868)
-- Name: act_idx_hi_task_inst_procinst; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_task_inst_procinst ON flow.act_hi_taskinst USING btree (proc_inst_id_);


--
-- TOC entry 3656 (class 1259 OID 21373)
-- Name: act_idx_hi_task_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_task_scope ON flow.act_hi_taskinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 3657 (class 1259 OID 21375)
-- Name: act_idx_hi_task_scope_def; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_task_scope_def ON flow.act_hi_taskinst USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 3658 (class 1259 OID 21374)
-- Name: act_idx_hi_task_sub_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_task_sub_scope ON flow.act_hi_taskinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 3675 (class 1259 OID 21400)
-- Name: act_idx_hi_var_scope_id_type; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_var_scope_id_type ON flow.act_hi_varinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 3676 (class 1259 OID 21401)
-- Name: act_idx_hi_var_sub_id_type; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_hi_var_sub_id_type ON flow.act_hi_varinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 3610 (class 1259 OID 21294)
-- Name: act_idx_ident_lnk_group; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ident_lnk_group ON flow.act_ru_identitylink USING btree (group_id_);


--
-- TOC entry 3611 (class 1259 OID 21295)
-- Name: act_idx_ident_lnk_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ident_lnk_scope ON flow.act_ru_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 3612 (class 1259 OID 21297)
-- Name: act_idx_ident_lnk_scope_def; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ident_lnk_scope_def ON flow.act_ru_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 3613 (class 1259 OID 21296)
-- Name: act_idx_ident_lnk_sub_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ident_lnk_sub_scope ON flow.act_ru_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 3614 (class 1259 OID 21293)
-- Name: act_idx_ident_lnk_user; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ident_lnk_user ON flow.act_ru_identitylink USING btree (user_id_);


--
-- TOC entry 3615 (class 1259 OID 21665)
-- Name: act_idx_idl_procinst; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_idl_procinst ON flow.act_ru_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 3677 (class 1259 OID 21452)
-- Name: act_idx_job_correlation_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_job_correlation_id ON flow.act_ru_job USING btree (correlation_id_);


--
-- TOC entry 3678 (class 1259 OID 21451)
-- Name: act_idx_job_custom_values_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_job_custom_values_id ON flow.act_ru_job USING btree (custom_values_id_);


--
-- TOC entry 3679 (class 1259 OID 21450)
-- Name: act_idx_job_exception_stack_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_job_exception_stack_id ON flow.act_ru_job USING btree (exception_stack_id_);


--
-- TOC entry 3680 (class 1259 OID 21701)
-- Name: act_idx_job_execution_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_job_execution_id ON flow.act_ru_job USING btree (execution_id_);


--
-- TOC entry 3681 (class 1259 OID 21713)
-- Name: act_idx_job_proc_def_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_job_proc_def_id ON flow.act_ru_job USING btree (proc_def_id_);


--
-- TOC entry 3682 (class 1259 OID 21707)
-- Name: act_idx_job_process_instance_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_job_process_instance_id ON flow.act_ru_job USING btree (process_instance_id_);


--
-- TOC entry 3683 (class 1259 OID 21516)
-- Name: act_idx_job_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_job_scope ON flow.act_ru_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 3684 (class 1259 OID 21518)
-- Name: act_idx_job_scope_def; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_job_scope_def ON flow.act_ru_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 3685 (class 1259 OID 21517)
-- Name: act_idx_job_sub_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_job_sub_scope ON flow.act_ru_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 3587 (class 1259 OID 21251)
-- Name: act_idx_memb_group; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_memb_group ON flow.act_id_membership USING btree (group_id_);


--
-- TOC entry 3588 (class 1259 OID 21258)
-- Name: act_idx_memb_user; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_memb_user ON flow.act_id_membership USING btree (user_id_);


--
-- TOC entry 3739 (class 1259 OID 21790)
-- Name: act_idx_model_deployment; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_model_deployment ON flow.act_re_model USING btree (deployment_id_);


--
-- TOC entry 3740 (class 1259 OID 21778)
-- Name: act_idx_model_source; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_model_source ON flow.act_re_model USING btree (editor_source_value_id_);


--
-- TOC entry 3741 (class 1259 OID 21784)
-- Name: act_idx_model_source_extra; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_model_source_extra ON flow.act_re_model USING btree (editor_source_extra_value_id_);


--
-- TOC entry 3601 (class 1259 OID 21271)
-- Name: act_idx_priv_group; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_priv_group ON flow.act_id_priv_mapping USING btree (group_id_);


--
-- TOC entry 3602 (class 1259 OID 21264)
-- Name: act_idx_priv_mapping; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_priv_mapping ON flow.act_id_priv_mapping USING btree (priv_id_);


--
-- TOC entry 3603 (class 1259 OID 21270)
-- Name: act_idx_priv_user; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_priv_user ON flow.act_id_priv_mapping USING btree (user_id_);


--
-- TOC entry 3759 (class 1259 OID 21796)
-- Name: act_idx_procdef_info_json; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_procdef_info_json ON flow.act_procdef_info USING btree (info_json_id_);


--
-- TOC entry 3760 (class 1259 OID 21802)
-- Name: act_idx_procdef_info_proc; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_procdef_info_proc ON flow.act_procdef_info USING btree (proc_def_id_);


--
-- TOC entry 3765 (class 1259 OID 21616)
-- Name: act_idx_ru_acti_end; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ru_acti_end ON flow.act_ru_actinst USING btree (end_time_);


--
-- TOC entry 3766 (class 1259 OID 21619)
-- Name: act_idx_ru_acti_exec; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ru_acti_exec ON flow.act_ru_actinst USING btree (execution_id_);


--
-- TOC entry 3767 (class 1259 OID 21620)
-- Name: act_idx_ru_acti_exec_act; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ru_acti_exec_act ON flow.act_ru_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 3768 (class 1259 OID 21617)
-- Name: act_idx_ru_acti_proc; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ru_acti_proc ON flow.act_ru_actinst USING btree (proc_inst_id_);


--
-- TOC entry 3769 (class 1259 OID 21618)
-- Name: act_idx_ru_acti_proc_act; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ru_acti_proc_act ON flow.act_ru_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 3770 (class 1259 OID 21615)
-- Name: act_idx_ru_acti_start; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ru_acti_start ON flow.act_ru_actinst USING btree (start_time_);


--
-- TOC entry 3771 (class 1259 OID 21621)
-- Name: act_idx_ru_acti_task; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ru_acti_task ON flow.act_ru_actinst USING btree (task_id_);


--
-- TOC entry 3661 (class 1259 OID 21383)
-- Name: act_idx_ru_var_scope_id_type; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ru_var_scope_id_type ON flow.act_ru_variable USING btree (scope_id_, scope_type_);


--
-- TOC entry 3662 (class 1259 OID 21384)
-- Name: act_idx_ru_var_sub_id_type; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_ru_var_sub_id_type ON flow.act_ru_variable USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 3700 (class 1259 OID 21522)
-- Name: act_idx_sjob_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_sjob_scope ON flow.act_ru_suspended_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 3701 (class 1259 OID 21524)
-- Name: act_idx_sjob_scope_def; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_sjob_scope_def ON flow.act_ru_suspended_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 3702 (class 1259 OID 21523)
-- Name: act_idx_sjob_sub_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_sjob_sub_scope ON flow.act_ru_suspended_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 3703 (class 1259 OID 21459)
-- Name: act_idx_suspended_job_correlation_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_suspended_job_correlation_id ON flow.act_ru_suspended_job USING btree (correlation_id_);


--
-- TOC entry 3704 (class 1259 OID 21458)
-- Name: act_idx_suspended_job_custom_values_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_suspended_job_custom_values_id ON flow.act_ru_suspended_job USING btree (custom_values_id_);


--
-- TOC entry 3705 (class 1259 OID 21457)
-- Name: act_idx_suspended_job_exception_stack_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_suspended_job_exception_stack_id ON flow.act_ru_suspended_job USING btree (exception_stack_id_);


--
-- TOC entry 3706 (class 1259 OID 21737)
-- Name: act_idx_suspended_job_execution_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_suspended_job_execution_id ON flow.act_ru_suspended_job USING btree (execution_id_);


--
-- TOC entry 3707 (class 1259 OID 21749)
-- Name: act_idx_suspended_job_proc_def_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_suspended_job_proc_def_id ON flow.act_ru_suspended_job USING btree (proc_def_id_);


--
-- TOC entry 3708 (class 1259 OID 21743)
-- Name: act_idx_suspended_job_process_instance_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_suspended_job_process_instance_id ON flow.act_ru_suspended_job USING btree (process_instance_id_);


--
-- TOC entry 3644 (class 1259 OID 21350)
-- Name: act_idx_task_create; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_task_create ON flow.act_ru_task USING btree (create_time_);


--
-- TOC entry 3645 (class 1259 OID 21671)
-- Name: act_idx_task_exec; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_task_exec ON flow.act_ru_task USING btree (execution_id_);


--
-- TOC entry 3646 (class 1259 OID 21683)
-- Name: act_idx_task_procdef; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_task_procdef ON flow.act_ru_task USING btree (proc_def_id_);


--
-- TOC entry 3647 (class 1259 OID 21677)
-- Name: act_idx_task_procinst; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_task_procinst ON flow.act_ru_task USING btree (proc_inst_id_);


--
-- TOC entry 3648 (class 1259 OID 21351)
-- Name: act_idx_task_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_task_scope ON flow.act_ru_task USING btree (scope_id_, scope_type_);


--
-- TOC entry 3649 (class 1259 OID 21353)
-- Name: act_idx_task_scope_def; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_task_scope_def ON flow.act_ru_task USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 3650 (class 1259 OID 21352)
-- Name: act_idx_task_sub_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_task_sub_scope ON flow.act_ru_task USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 3688 (class 1259 OID 21455)
-- Name: act_idx_timer_job_correlation_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_timer_job_correlation_id ON flow.act_ru_timer_job USING btree (correlation_id_);


--
-- TOC entry 3689 (class 1259 OID 21454)
-- Name: act_idx_timer_job_custom_values_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_timer_job_custom_values_id ON flow.act_ru_timer_job USING btree (custom_values_id_);


--
-- TOC entry 3690 (class 1259 OID 21456)
-- Name: act_idx_timer_job_duedate; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_timer_job_duedate ON flow.act_ru_timer_job USING btree (duedate_);


--
-- TOC entry 3691 (class 1259 OID 21453)
-- Name: act_idx_timer_job_exception_stack_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_timer_job_exception_stack_id ON flow.act_ru_timer_job USING btree (exception_stack_id_);


--
-- TOC entry 3692 (class 1259 OID 21719)
-- Name: act_idx_timer_job_execution_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_timer_job_execution_id ON flow.act_ru_timer_job USING btree (execution_id_);


--
-- TOC entry 3693 (class 1259 OID 21731)
-- Name: act_idx_timer_job_proc_def_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_timer_job_proc_def_id ON flow.act_ru_timer_job USING btree (proc_def_id_);


--
-- TOC entry 3694 (class 1259 OID 21725)
-- Name: act_idx_timer_job_process_instance_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_timer_job_process_instance_id ON flow.act_ru_timer_job USING btree (process_instance_id_);


--
-- TOC entry 3695 (class 1259 OID 21519)
-- Name: act_idx_tjob_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_tjob_scope ON flow.act_ru_timer_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 3696 (class 1259 OID 21521)
-- Name: act_idx_tjob_scope_def; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_tjob_scope_def ON flow.act_ru_timer_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 3697 (class 1259 OID 21520)
-- Name: act_idx_tjob_sub_scope; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_tjob_sub_scope ON flow.act_ru_timer_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 3616 (class 1259 OID 21653)
-- Name: act_idx_tskass_task; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_tskass_task ON flow.act_ru_identitylink USING btree (task_id_);


--
-- TOC entry 3663 (class 1259 OID 21385)
-- Name: act_idx_var_bytearray; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_var_bytearray ON flow.act_ru_variable USING btree (bytearray_id_);


--
-- TOC entry 3664 (class 1259 OID 21689)
-- Name: act_idx_var_exe; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_var_exe ON flow.act_ru_variable USING btree (execution_id_);


--
-- TOC entry 3665 (class 1259 OID 21695)
-- Name: act_idx_var_procinst; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_var_procinst ON flow.act_ru_variable USING btree (proc_inst_id_);


--
-- TOC entry 3666 (class 1259 OID 21613)
-- Name: act_idx_variable_task_id; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX act_idx_variable_task_id ON flow.act_ru_variable USING btree (task_id_);


--
-- TOC entry 3800 (class 1259 OID 21889)
-- Name: fli_flow_form_ref_idx1; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX fli_flow_form_ref_idx1 ON flow.fli_flow_form_ref USING btree (process_id_);


--
-- TOC entry 3734 (class 1259 OID 21547)
-- Name: flw_idx_batch_part; Type: INDEX; Schema: flow; Owner: vlsp
--

CREATE INDEX flw_idx_batch_part ON flow.flw_ru_batch_part USING btree (batch_id_);


--
-- TOC entry 3807 (class 2606 OID 21660)
-- Name: act_ru_identitylink act_fk_athrz_procedef; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_identitylink
    ADD CONSTRAINT act_fk_athrz_procedef FOREIGN KEY (proc_def_id_) REFERENCES flow.act_re_procdef(id_);


--
-- TOC entry 3806 (class 2606 OID 21622)
-- Name: act_ge_bytearray act_fk_bytearr_depl; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ge_bytearray
    ADD CONSTRAINT act_fk_bytearr_depl FOREIGN KEY (deployment_id_) REFERENCES flow.act_re_deployment(id_);


--
-- TOC entry 3832 (class 2606 OID 21501)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_custom_values; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3833 (class 2606 OID 21496)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_exception; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3834 (class 2606 OID 21756)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_execution; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_execution FOREIGN KEY (execution_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3835 (class 2606 OID 21768)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_proc_def; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES flow.act_re_procdef(id_);


--
-- TOC entry 3836 (class 2606 OID 21762)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_process_instance; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3810 (class 2606 OID 21773)
-- Name: act_ru_event_subscr act_fk_event_exec; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_event_subscr
    ADD CONSTRAINT act_fk_event_exec FOREIGN KEY (execution_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3843 (class 2606 OID 21636)
-- Name: act_ru_execution act_fk_exe_parent; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_execution
    ADD CONSTRAINT act_fk_exe_parent FOREIGN KEY (parent_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3844 (class 2606 OID 21648)
-- Name: act_ru_execution act_fk_exe_procdef; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procdef FOREIGN KEY (proc_def_id_) REFERENCES flow.act_re_procdef(id_);


--
-- TOC entry 3845 (class 2606 OID 21630)
-- Name: act_ru_execution act_fk_exe_procinst; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procinst FOREIGN KEY (proc_inst_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3846 (class 2606 OID 21642)
-- Name: act_ru_execution act_fk_exe_super; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_execution
    ADD CONSTRAINT act_fk_exe_super FOREIGN KEY (super_exec_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3837 (class 2606 OID 21511)
-- Name: act_ru_external_job act_fk_external_job_custom_values; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3838 (class 2606 OID 21506)
-- Name: act_ru_external_job act_fk_external_job_exception; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3808 (class 2606 OID 21666)
-- Name: act_ru_identitylink act_fk_idl_procinst; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_identitylink
    ADD CONSTRAINT act_fk_idl_procinst FOREIGN KEY (proc_inst_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3847 (class 2606 OID 21797)
-- Name: act_procdef_info act_fk_info_json_ba; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_procdef_info
    ADD CONSTRAINT act_fk_info_json_ba FOREIGN KEY (info_json_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3848 (class 2606 OID 21803)
-- Name: act_procdef_info act_fk_info_procdef; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_procdef_info
    ADD CONSTRAINT act_fk_info_procdef FOREIGN KEY (proc_def_id_) REFERENCES flow.act_re_procdef(id_);


--
-- TOC entry 3817 (class 2606 OID 21471)
-- Name: act_ru_job act_fk_job_custom_values; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_job
    ADD CONSTRAINT act_fk_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3818 (class 2606 OID 21466)
-- Name: act_ru_job act_fk_job_exception; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_job
    ADD CONSTRAINT act_fk_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3819 (class 2606 OID 21702)
-- Name: act_ru_job act_fk_job_execution; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_job
    ADD CONSTRAINT act_fk_job_execution FOREIGN KEY (execution_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3820 (class 2606 OID 21714)
-- Name: act_ru_job act_fk_job_proc_def; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_job
    ADD CONSTRAINT act_fk_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES flow.act_re_procdef(id_);


--
-- TOC entry 3821 (class 2606 OID 21708)
-- Name: act_ru_job act_fk_job_process_instance; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_job
    ADD CONSTRAINT act_fk_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3803 (class 2606 OID 21252)
-- Name: act_id_membership act_fk_memb_group; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_id_membership
    ADD CONSTRAINT act_fk_memb_group FOREIGN KEY (group_id_) REFERENCES flow.act_id_group(id_);


--
-- TOC entry 3804 (class 2606 OID 21259)
-- Name: act_id_membership act_fk_memb_user; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_id_membership
    ADD CONSTRAINT act_fk_memb_user FOREIGN KEY (user_id_) REFERENCES flow.act_id_user(id_);


--
-- TOC entry 3840 (class 2606 OID 21791)
-- Name: act_re_model act_fk_model_deployment; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_re_model
    ADD CONSTRAINT act_fk_model_deployment FOREIGN KEY (deployment_id_) REFERENCES flow.act_re_deployment(id_);


--
-- TOC entry 3841 (class 2606 OID 21779)
-- Name: act_re_model act_fk_model_source; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_re_model
    ADD CONSTRAINT act_fk_model_source FOREIGN KEY (editor_source_value_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3842 (class 2606 OID 21785)
-- Name: act_re_model act_fk_model_source_extra; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_re_model
    ADD CONSTRAINT act_fk_model_source_extra FOREIGN KEY (editor_source_extra_value_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3805 (class 2606 OID 21265)
-- Name: act_id_priv_mapping act_fk_priv_mapping; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_id_priv_mapping
    ADD CONSTRAINT act_fk_priv_mapping FOREIGN KEY (priv_id_) REFERENCES flow.act_id_priv(id_);


--
-- TOC entry 3827 (class 2606 OID 21491)
-- Name: act_ru_suspended_job act_fk_suspended_job_custom_values; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3828 (class 2606 OID 21486)
-- Name: act_ru_suspended_job act_fk_suspended_job_exception; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3829 (class 2606 OID 21738)
-- Name: act_ru_suspended_job act_fk_suspended_job_execution; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_execution FOREIGN KEY (execution_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3830 (class 2606 OID 21750)
-- Name: act_ru_suspended_job act_fk_suspended_job_proc_def; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES flow.act_re_procdef(id_);


--
-- TOC entry 3831 (class 2606 OID 21744)
-- Name: act_ru_suspended_job act_fk_suspended_job_process_instance; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3811 (class 2606 OID 21672)
-- Name: act_ru_task act_fk_task_exe; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_task
    ADD CONSTRAINT act_fk_task_exe FOREIGN KEY (execution_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3812 (class 2606 OID 21684)
-- Name: act_ru_task act_fk_task_procdef; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_task
    ADD CONSTRAINT act_fk_task_procdef FOREIGN KEY (proc_def_id_) REFERENCES flow.act_re_procdef(id_);


--
-- TOC entry 3813 (class 2606 OID 21678)
-- Name: act_ru_task act_fk_task_procinst; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_task
    ADD CONSTRAINT act_fk_task_procinst FOREIGN KEY (proc_inst_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3822 (class 2606 OID 21481)
-- Name: act_ru_timer_job act_fk_timer_job_custom_values; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3823 (class 2606 OID 21476)
-- Name: act_ru_timer_job act_fk_timer_job_exception; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3824 (class 2606 OID 21720)
-- Name: act_ru_timer_job act_fk_timer_job_execution; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_execution FOREIGN KEY (execution_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3825 (class 2606 OID 21732)
-- Name: act_ru_timer_job act_fk_timer_job_proc_def; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES flow.act_re_procdef(id_);


--
-- TOC entry 3826 (class 2606 OID 21726)
-- Name: act_ru_timer_job act_fk_timer_job_process_instance; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3809 (class 2606 OID 21654)
-- Name: act_ru_identitylink act_fk_tskass_task; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_identitylink
    ADD CONSTRAINT act_fk_tskass_task FOREIGN KEY (task_id_) REFERENCES flow.act_ru_task(id_);


--
-- TOC entry 3814 (class 2606 OID 21386)
-- Name: act_ru_variable act_fk_var_bytearray; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_variable
    ADD CONSTRAINT act_fk_var_bytearray FOREIGN KEY (bytearray_id_) REFERENCES flow.act_ge_bytearray(id_);


--
-- TOC entry 3815 (class 2606 OID 21690)
-- Name: act_ru_variable act_fk_var_exe; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_variable
    ADD CONSTRAINT act_fk_var_exe FOREIGN KEY (execution_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3816 (class 2606 OID 21696)
-- Name: act_ru_variable act_fk_var_procinst; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.act_ru_variable
    ADD CONSTRAINT act_fk_var_procinst FOREIGN KEY (proc_inst_id_) REFERENCES flow.act_ru_execution(id_);


--
-- TOC entry 3839 (class 2606 OID 21548)
-- Name: flw_ru_batch_part flw_fk_batch_part_parent; Type: FK CONSTRAINT; Schema: flow; Owner: vlsp
--

ALTER TABLE ONLY flow.flw_ru_batch_part
    ADD CONSTRAINT flw_fk_batch_part_parent FOREIGN KEY (batch_id_) REFERENCES flow.flw_ru_batch(id_);


-- Completed on 2025-07-22 19:38:03 CST

--
-- PostgreSQL database dump complete
--

