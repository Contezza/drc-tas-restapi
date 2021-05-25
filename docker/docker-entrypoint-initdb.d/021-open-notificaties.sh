psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname opennotificaties <<-EOSQL
    CREATE EXTENSION pg_trgm;
EOSQL
