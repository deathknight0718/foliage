-- definition: public.fli_vec_patients

CREATE TABLE IF NOT EXISTS public.fli_vec_patients (
    id_           serial4 NOT NULL,
    ref_id_       varchar(255) NOT NULL,
    name_         varchar(255) NOT NULL,
    age_          int4 NOT NULL,
    gender_       varchar(255) NOT NULL,
    embedding_id_ int4 NOT NULL,
    embeddings_   vector NULL,
    CONSTRAINT fli_vec_patients_pk PRIMARY KEY (id_)
);

-- definition: public.fli_vec_intents

CREATE TABLE IF NOT EXISTS public.fli_vec_intents (
    id_                    serial4 NOT NULL,
    question_              text NOT NULL,
    question_embeddings_   vector NULL,
    intent_                text NULL,
    tf_idf_                tsvector NULL,
    CONSTRAINT fli_vec_intents_pk PRIMARY KEY (id_)
);