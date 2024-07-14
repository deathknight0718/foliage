## DOCKER COMPONSE EXAMPLE

```
# @author: deathknight0718@qq.com
version: "3.8"

networks:
  storage:
    external: true

services:
  ###############################################################################
  # DEVELOPMENT
  ###############################################################################
  minio:
    image: "${FOLIAGE_HOST}:5000/minio:latest"
    networks:
      - "storage"
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: 10
        mode: "non-blocking"
    ports:
      - "9201:9000"
      - "9202:9001"
    volumes:
      # Volumes of Certificates
      - type: "bind"
        source: "${FOLIAGE_MNT_HOME}/certs"
        target: "/root/.minio/certs"
      # Volumes of Data
      - type: "bind"
        source: "${FOLIAGE_MNT_HOME}/minio/data"
        target: "/data"
    command: ["server", "/data", "--console-address", ":9001"]
    environment:
      - "TZ=Asia/Shanghai"
      - "MINIO_VOLUMES=https"
      - "MINIO_ROOT_USER=admin"
      - "MINIO_ROOT_PASSWORD=changeit"
    deploy:
      mode: "replicated"
      replicas: 1
      placement:
        constraints:
          - "node.labels.area == app"
```