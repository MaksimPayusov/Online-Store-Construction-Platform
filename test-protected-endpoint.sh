#!/bin/bash

# Скрипт для тестирования защищенного endpoint

echo "=========================================="
echo "Тестирование защищенного endpoint"
echo "=========================================="
echo ""

# Проверка без токена
echo "1. Запрос БЕЗ токена (должен вернуть 401):"
echo "----------------------------------------"
curl -i http://localhost:8081/keycloak-protected 2>&1 | head -15
echo ""
echo ""

# Получение токена
echo "2. Получение токена..."
echo "----------------------------------------"
echo "Введите username:"
read USERNAME
echo "Введите password:"
read -s PASSWORD
echo ""

TOKEN_RESPONSE=$(curl -s -X POST "http://localhost:8080/realms/main_one/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=account" \
  -d "username=${USERNAME}" \
  -d "password=${PASSWORD}" \
  -d "grant_type=password")

if echo "$TOKEN_RESPONSE" | grep -q "access_token"; then
    ACCESS_TOKEN=$(echo "$TOKEN_RESPONSE" | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)
    echo "✓ Токен получен!"
    echo ""
    echo "3. Запрос С токеном (должен вернуть 200 или 302):"
    echo "----------------------------------------"
    curl -i -H "Authorization: Bearer ${ACCESS_TOKEN}" http://localhost:8081/keycloak-protected 2>&1 | head -20
    echo ""
else
    echo "✗ Ошибка получения токена:"
    echo "$TOKEN_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$TOKEN_RESPONSE"
    exit 1
fi

