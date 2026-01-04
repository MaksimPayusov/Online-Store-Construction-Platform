DOCKER_COMPOSE = docker compose
INFRA_SEVICES?= keycloak prometheus grafana tempo loki alloy goodscategory basket krakend

.PHONY: all up build start stop clean logs rebuild obs down downvolumes

obs:
	$(DOCKER_COMPOSE) up prometheus grafana tempo loki alloy -d

core:
	$(DOCKER_COMPOSE) up goodscategory basket keycloak krakend fileservice basket-db goodscategory-db keycloak-postgres minio rabbitmq -d

obsdb:obs db

all:
	$(DOCKER_COMPOSE) up -d

build:
	$(DOCKER_COMPOSE) up -d --build

stop:
	$(DOCKER_COMPOSE) stop

down:
	$(DOCKER_COMPOSE) down
	COMPOSE_PROFILES=obs $(DOCKER_COMPOSE) down


downvolumes: 
	$(DOCKER_COMPOSE) down -v
	$(DOCKER_COMPOSE) --profile obs down
clean:
	docker builder prune
	$(DOCKER_COMPOSE) down --rmi local