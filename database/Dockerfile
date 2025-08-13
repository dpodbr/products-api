FROM dpage/pgadmin4

# Set environment variables.
ENV PGADMIN_DEFAULT_EMAIL=${PGADMIN_DEFAULT_EMAIL}
ENV PGADMIN_DEFAULT_PASSWORD=${PGADMIN_DEFAULT_PASSWORD}
ENV PGADMIN_CONFIG_SERVER_MODE=False
ENV PGADMIN_CONFIG_MASTER_PASSWORD_REQUIRED=False

# Switch to root to install the wrapper script.
USER root

# Copy the runtime entrypoint.
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Drop privileges back to the image's default user
USER pgadmin

# Execute our script which in turn executes original entrypoint.
ENTRYPOINT ["/docker-entrypoint.sh"]

EXPOSE 80
