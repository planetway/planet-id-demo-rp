-- Relying party database
CREATE TABLE rp_user (
    id bigserial NOT NULL,
    email varchar(250) NOT NULL,
    encrypted_password varchar(250) NULL,
    deleted_at timestamp with time zone
);

CREATE TABLE planet_id (
    id varchar(32) NOT NULL,
    rp_user_id bigint NOT NULL,
    created_at timestamp with time zone NOT NULL
);

ALTER SEQUENCE rp.rp_user_id_seq START 1000 RESTART;

/* Create Primary Keys, Indexes, Uniques, Checks */
ALTER TABLE rp_user ADD CONSTRAINT rp_user_id_pk PRIMARY KEY (id);
ALTER TABLE planet_id ADD CONSTRAINT planet_id_pk PRIMARY KEY (id);

/* Create Foreign Key Constraints */
ALTER TABLE planet_id ADD CONSTRAINT planet_id_user_id_fk
    FOREIGN KEY (rp_user_id) REFERENCES rp_user (id) ON DELETE No Action ON UPDATE No Action;

/* Create Table Comments */
COMMENT ON COLUMN rp_user.id IS 'User identifier';
COMMENT ON COLUMN rp_user.email IS 'User email address';
COMMENT ON COLUMN rp_user.encrypted_password IS 'Encrypted password';
COMMENT ON COLUMN rp_user.deleted_at IS 'Timestamp, when the user was (logically) deleted';

COMMENT ON COLUMN planet_id.id IS 'User planet id';
COMMENT ON COLUMN planet_id.rp_user_id IS 'User identifier in RP';
COMMENT ON COLUMN planet_id.created_at IS 'Timestamp, when the planet id was linked';
