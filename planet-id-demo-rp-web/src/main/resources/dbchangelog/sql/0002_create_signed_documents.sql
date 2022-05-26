

ALTER TABLE rp_user RENAME TO "user";
AlTER TABLE "user" RENAME encrypted_password TO password;
AlTER TABLE "user" ADD planet_id varchar(32);
UPDATE "user" SET planet_id = (SELECT id FROM planet_id WHERE rp_user_id = "user".id LIMIT 1);

DROP TABLE planet_id;

ALTER SEQUENCE rp_user_id_seq RENAME TO user_id_seq;

CREATE TABLE signed_document (
    id bigserial NOT NULL,
    uuid varchar(100) NOT NULL,
    signature_type varchar(20) NOT NULL,
    user_id bigint NOT NULL,
    data bytea NOT NULL,
    planet_id varchar(32) NOT NULL,
    has_timestamp bool NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    CONSTRAINT signed_document_pkey PRIMARY KEY (id)
);
