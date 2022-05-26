-- removes EVERYTHING!!! to be run by postgres user
\c postgres
DROP DATABASE IF EXISTS pid_rp_db;
DROP USER IF EXISTS pid_rp_usr;
DROP USER IF EXISTS pid_rp_adm;
