-- pre-requisite for changelog - RUN AS postgres user

CREATE USER pid_rp_adm WITH PASSWORD 'pid_rp_adm_pwd';
CREATE DATABASE pid_rp_db OWNER pid_rp_adm;

CREATE USER pid_rp_usr WITH PASSWORD 'pid_rp_pwd';
GRANT CONNECT ON DATABASE pid_rp_db TO pid_rp_usr;

CREATE ROLE pid_rp_support_ro NOLOGIN;
CREATE ROLE pid_rp_support_rw NOLOGIN;

\c pid_rp_db
CREATE SCHEMA IF NOT EXISTS rp AUTHORIZATION pid_rp_adm;
ALTER ROLE pid_rp_adm SET search_path TO rp;
ALTER ROLE pid_rp_usr SET search_path TO rp;
