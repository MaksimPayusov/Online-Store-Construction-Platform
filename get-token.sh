#!/bin/bash

# Скрипт для получения токена из Keycloak

REALM="main_one"
CLIENT_ID="account"
KEYCLOAK_URL="http://localhost:8080"

echo "=========================================="
echo "Получение токена из Keycloak"
echo "=========================================="
echo ""
echo "Введите username:"
read USERNAME
echo "Введите password:"
read -s PASSWORD
echo ""

# Получаем токен
TOKEN_RESPONSE=$(curl -s -X POST "${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=${CLIENT_ID}" \
  -d "username=${USERNAME}" \
  -d "password=${PASSWORD}" \
  -d "grant_type=password")

# Проверяем ответ
if echo "$TOKEN_RESPONSE" | grep -q "access_token"; then
    ACCESS_TOKEN=$(echo "$TOKEN_RESPONSE" | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)
    echo "✓ Токен получен успешно!"
    echo ""
    echo "Используйте этот токен для запроса к защищенному endpoint:"
    echo ""
    echo "curl -H \"Authorization: Bearer ${ACCESS_TOKEN}\" http://localhost:8081/keycloak-protected"
    echo ""
    echo "Или сохраните токен в переменную:"
    echo "export TOKEN=\"${ACCESS_TOKEN}\""
    echo "curl -H \"Authorization: Bearer \$TOKEN\" http://localhost:8081/keycloak-protected"
    echo ""
else
    echo "✗ Ошибка получения токена:"
    echo "$TOKEN_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$TOKEN_RESPONSE"
    exit 1
fi

