# Как исправить HTTP 401 на защищенном endpoint

## Что было исправлено:

1. ✅ **Исправлен realm** в `jwk_url`: `master` → `main_one`
2. ✅ **Исправлен backend host**: `localhost:8080` → `keycloak:8080` (для работы внутри Docker сети)
3. ✅ **Включен Direct Access Grants** для клиента "account" (для получения токенов через password grant)
4. ✅ **Добавлен audience** в валидатор: `["account"]`

## Что нужно сделать:

### 1. Перезапустите Keycloak для применения изменений:

```bash
docker restart keycloak
# или
docker compose restart keycloak
```

Дождитесь, пока Keycloak полностью запустится (проверьте логи).

### 2. Получите токен:

**Вариант А: Используйте скрипт**
```bash
./get-token.sh
```

**Вариант Б: Вручную через curl**
```bash
curl -X POST "http://localhost:8080/realms/main_one/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=account" \
  -d "username=ВАШ_USERNAME" \
  -d "password=ВАШ_PASSWORD" \
  -d "grant_type=password"
```

Скопируйте `access_token` из ответа.

### 3. Используйте токен для запроса:

```bash
curl -H "Authorization: Bearer ВАШ_ТОКЕН" http://localhost:8081/keycloak-protected
```

### 4. Или используйте тестовый скрипт:

```bash
./test-protected-endpoint.sh
```

## Ожидаемое поведение:

- **Без токена**: HTTP 401 Unauthorized
- **С валидным токеном**: HTTP 200/302 (успешный ответ от Keycloak)

## Если все еще получаете 401:

1. Проверьте, что токен не истек (обычно живет 5 минут)
2. Проверьте логи KrakenD: `docker logs seniory-pomidory-krakend-1`
3. Проверьте логи Keycloak: `docker logs keycloak`
4. Убедитесь, что Keycloak перезапустился после изменений в realm-config.json

## Примечание:

Для production рекомендуется:
- Создать отдельный клиент для KrakenD (не использовать "account")
- Использовать более длительное время жизни токена
- Настроить refresh tokens

