-- Lua скрипт для парсинга JWT и извлечения роли и ID пользователя
-- Используется в KrakenD для добавления заголовков X-User-Role и X-User-Id

local base64 = require("base64")
local json = require("json")

function request(ctx, params)
  local auth = ctx.request.headers['Authorization']
  if not auth then
    return ctx
  end
  
  local token = string.match(auth, "Bearer%s+(.+)")
  if not token then
    return ctx
  end
  
  -- Разбиваем JWT на части
  local parts = {}
  for part in string.gmatch(token, "[^.]+") do
    table.insert(parts, part)
  end
  
  if #parts < 2 then
    return ctx
  end
  
  -- Декодируем payload (вторая часть)
  local payload = parts[2]
  
  -- Добавляем padding если нужно
  local padding = 4 - (string.len(payload) % 4)
  if padding ~= 4 then
    payload = payload .. string.rep("=", padding)
  end
  
  -- Заменяем URL-safe символы
  payload = payload:gsub("-", "+"):gsub("_", "/")
  
  -- Декодируем base64
  local decoded_str = base64.decode(payload)
  if not decoded_str then
    return ctx
  end
  
  -- Парсим JSON
  local decoded = json.decode(decoded_str)
  if not decoded then
    return ctx
  end
  
  -- Извлекаем ID пользователя (sub)
  if decoded.sub then
    ctx.request.headers['X-User-Id'] = decoded.sub
  end
  
  -- Извлекаем роль из realm_access.roles
  if decoded.realm_access and decoded.realm_access.roles then
    local roles = decoded.realm_access.roles
    local role = "user" -- По умолчанию
    
    -- Приоритет: admin > owner > user
    for i, r in ipairs(roles) do
      if r == "admin" then
        role = "admin"
        break
      elseif r == "owner" and role ~= "admin" then
        role = "owner"
      end
    end
    
    ctx.request.headers['X-User-Role'] = role
  end
  
  return ctx
end

