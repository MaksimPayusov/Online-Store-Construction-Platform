DOCKER_COMPOSE = docker compose
INFRA_SEVICES?= keycloak prometheus grafana tempo loki

.PHONY: all up build start stop clean logs rebuild obs down downvolumes

obs:
	$(DOCKER_COMPOSE) --profile obs up -d

db:
	$(DOCKER_COMPOSE) --profile db up -d

obsdb:obs db

all:
	$(DOCKER_COMPOSE) up -dß

rebuild:
	$(DOCKER_COMPOSE) up -d --rebuild

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