#!/bin/bash
set -e

# PostgreSQL initialization script for multiple databases
# This script creates additional databases and users for the application

# Function to create a database and user
create_database_and_user() {
    local db_name=$1
    local db_user=$2
    local db_password=$3

    echo "Creating database: ${db_name}"
    echo "Creating user: ${db_user}"

    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
        -- Create the database
        CREATE DATABASE ${db_name};

        -- Create the user
        CREATE USER ${db_user} WITH PASSWORD '${db_password}';

        -- Grant all privileges on the database to the user
        GRANT ALL PRIVILEGES ON DATABASE ${db_name} TO ${db_user};

        -- Connect to the new database and grant schema privileges
        \c ${db_name}
        GRANT ALL ON SCHEMA public TO ${db_user};
EOSQL

    echo "Database ${db_name} and user ${db_user} created successfully"
}

# Create Keycloak database and user
create_database_and_user "keycloak_db" "keycloak" "keycloak"

echo "PostgreSQL initialization completed successfully"
