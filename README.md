# Платформа-конструктор интернет-магазинов

> **Учебный проект**: Платформа-конструктор для создания и управления интернет-магазинами с микросервисной архитектурой.

## Технологический стек

### Backend (Java Spring Boot)
- **Java 17** - основной язык программирования
- **Spring Boot** - фреймворк для микросервисов
- **Spring Data JPA** - работа с базами данных
- **Spring AMQP** - интеграция с RabbitMQ
- **PostgreSQL** - основная СУБД
- **Flyway** - миграции баз данных
- **Maven** - система сборки

### Frontend
**Admin панель (fashion):**
- **React** - UI библиотека
- **Vite** - сборщик
- **TypeScript** - типизация
- **TailwindCSS** - стилизация
- **Redux Toolkit** - управление состоянием

**Клиентский фронтенд (client-storefront):**
- **Next.js** - React фреймворк
- **React** - UI библиотека
- **TypeScript** - типизация
- **TailwindCSS** - стилизация
- **Zustand** - управление состоянием

### Инфраструктура
- **Docker & Docker Compose** - контейнеризация
- **Keycloak** - аутентификация и авторизация
- **KrakenD** - API Gateway
- **Redis** - кеширование и сессии
- **RabbitMQ** - message broker
- **MinIO** - объектное хранилище
- **PostgreSQL** - базы данных

### Мониторинг
- **Prometheus** - сбор метрик
- **Grafana** - визуализация и дашборды
- **Loki** - агрегация логов
- **Tempo** - распределенная трассировка
- **Alloy** - агент для сбора данных

## Микросервисная архитектура

### Core сервисы:
- **ProductService (8083)** - управление товарами, категориями, брендами
- **OrderService (8087)** - обработка заказов, доставки, оплаты
- **CartService (8084)** - управление корзиной (Redis)
- **NewsService (8085)** - управление новостями
- **FileService (8082)** - загрузка/скачивание файлов (MinIO)
- **UserService (8088)** - управление пользователями (Keycloak)
- **Shop (8089)** - управление магазинами

### Инфраструктурные сервисы:
- **KrakenD (8081)** - API Gateway с аутентификацией
- **Keycloak (8080)** - аутентификация/авторизация
- **PostgreSQL (5432)** - базы данных для всех сервисов
- **Redis (6379)** - кеширование и сессии
- **RabbitMQ (5672, 15672)** - асинхронный обмен сообщениями
- **MinIO (9000, 9001)** - объектное хранилище

## Доступ ко всем сервисам:

### Frontend

**Админский фронтенд (fashion):**
- http://localhost:3000
- Панель администрирования для управления магазинами

**Клиентский фронтенд (client-storefront):**
- http://localhost:3300
- Пользовательский интерфейс для покупателей

### API и микросервисы

**API Gateway (KrakenD):**
- http://localhost:8081
- Основная точка входа для всех API запросов

**Keycloak (аутентификация):**
- http://localhost:8080
- Admin Console: admin/admin
- Realm: main_one

### Swagger/OpenAPI документация

Каждый микросервис имеет документацию по своим портам:

- **ProductService:** http://localhost:8083/swagger-ui.html
- **CartService:** http://localhost:8084/swagger-ui.html  
- **NewsService:** http://localhost:8085/swagger-ui.html
- **FileService:** http://localhost:8082/swagger-ui.html
- **OrderService:** http://localhost:8087/swagger-ui.html
- **UserService:** http://localhost:8088/swagger-ui.html
- **Shop:** http://localhost:8089/swagger-ui.html

### Инфраструктурные сервисы

**RabbitMQ Management:**
- http://localhost:15672
- Логин: admin/admin

**MinIO Console:**
- http://localhost:9001
- Логин: admin/admin1234

**Grafana (мониторинг):**
- http://localhost:3001
- Логин: admin/admin

**Prometheus (метрики):**
- http://localhost:9090

**Loki (логи):**
- http://localhost:3100

**Tempo (трассировка):**
- http://localhost:3200

**Alloy (агент):**
- http://localhost:9080

## База данных

**PostgreSQL:**
- localhost:5432
- Логин: postgres/postgres

**Redis:**
- localhost:6379

### Замечания

1. **Все API запросы** должны идти через KrakenD (8081), а не напрямую к микросервисам
2. **Swagger документация** доступна на каждом микросервисе индивидуально
3. **Frontend приложения** настроены работать с API Gateway, а не напрямую с сервисами
4. **Для доступа к защищенным эндпоинтам** требуется JWT токен от Keycloak

Для быстрой проверки работоспособности можно использовать health checks:
- `curl http://localhost:8081/__health` - KrakenD
- `curl http://localhost:8080/realms/main_one/.well-known/openid-configuration` - Keycloak
