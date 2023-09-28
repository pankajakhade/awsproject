# Use the official Docker image as the base image
FROM docker:20.10-dind

# Expose the Docker daemon on an insecure port (2375)
CMD ["dockerd", "--tls=false", "--host=unix:///var/run/docker.sock", "--host=tcp://0.0.0.0:2375"]