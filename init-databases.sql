-- init-databases.sql
-- Create separate databases for each microservice
CREATE DATABASE nexushr_auth;
CREATE DATABASE nexushr_employee;
CREATE DATABASE nexushr_attendance;
CREATE DATABASE nexushr_payroll;
CREATE DATABASE nexushr_performance;
CREATE DATABASE nexushr_notification;
CREATE DATABASE nexushr_ai;
CREATE DATABASE nexushr_keycloak;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE nexushr_auth TO nexushr;
GRANT ALL PRIVILEGES ON DATABASE nexushr_employee TO nexushr;
GRANT ALL PRIVILEGES ON DATABASE nexushr_attendance TO nexushr;
GRANT ALL PRIVILEGES ON DATABASE nexushr_payroll TO nexushr;
GRANT ALL PRIVILEGES ON DATABASE nexushr_performance TO nexushr;
GRANT ALL PRIVILEGES ON DATABASE nexushr_notification TO nexushr;
GRANT ALL PRIVILEGES ON DATABASE nexushr_ai TO nexushr;
GRANT ALL PRIVILEGES ON DATABASE nexushr_keycloak TO nexushr;
