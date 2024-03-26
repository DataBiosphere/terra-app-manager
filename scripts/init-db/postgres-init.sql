CREATE ROLE dbuser WITH LOGIN ENCRYPTED PASSWORD 'dbpwd';
CREATE DATABASE appmanager_db OWNER dbuser;
-- GRANT CREATE ON DATABASE appmanager_db TO dbuser;
-- GRANT ALL PRIVILEGES ON DATABASE appmanager_db TO dbuser;
