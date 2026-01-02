DOCKER_COMPOSE = docker compose
INFRA_SEVICES?= keycloak prometheus grafana tempo loki

.PHONY: all up build start stop clean logs rebuild observ down downvolumes

observ:
	COMPOSE_PROFILES=obs $(DOCKER_COMPOSE) up -d

all:
	$(DOCKER_COMPOSE) up -d

rebuild:
	$(DOCKER_COMPOSE) up -d --rebuild

stop:
	$(DOCKER_COMPOSE) stop

down:
	$(DOCKER_COMPOSE) down


downvolumes: 
	$(DOCKER_COMPOSE) down -v
	COMPOSE_PROFILES=obs $(DOCKER_COMPOSE) down
clean:
	docker builder prune
	$(DOCKER_COMPOSE) down --rmi local