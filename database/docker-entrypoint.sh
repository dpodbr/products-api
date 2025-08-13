#!/bin/bash
set -eu

# Setup auto-connect to local postgres from env variables.
cat > /var/lib/pgadmin/pgpass <<EOF
${DB_HOST}:${DB_PORT}:*:${DB_USERNAME}:${DB_PASSWORD}
EOF
chmod 600 /var/lib/pgadmin/pgpass

cat > /var/lib/pgadmin/servers.json <<EOF
{
  "Servers": {
    "1": {
      "Name": "Local Postgres",
      "Group": "Servers",
      "Host": "${DB_HOST}",
      "Port": ${DB_PORT},
      "MaintenanceDB": "postgres",
      "Username": "${DB_USERNAME}",
      "SSLMode": "prefer",
      "PassFile": "/var/lib/pgadmin/pgpass"
    }
  }
}
EOF
chmod 600 /var/lib/pgadmin/servers.json

# Chain to the original entrypoint of the base image.
exec /entrypoint.sh
