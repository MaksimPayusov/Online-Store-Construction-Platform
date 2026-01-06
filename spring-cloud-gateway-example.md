# Пример конфигурации Spring Cloud Gateway для JWT

## build.gradle
```gradle
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-gateway'
    implementation 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
    implementation 'org.springframework.boot:spring-boot-starter-security'
}
```

## application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: userservice
          uri: http://userservice:8080
          predicates:
            - Path=/api/users/**
          filters:
            - name: JwtHeaderFilter
              args:
                headerName: X-User-Id
                claimName: sub
            - name: JwtRoleFilter
              args:
                headerName: X-User-Role
                rolePath: realm_access.roles
          filters:
            - StripPrefix=2
        - id: basket
          uri: http://basket:8080
          predicates:
            - Path=/api/basket/**
          filters:
            - name: JwtHeaderFilter
            - name: JwtRoleFilter
```

## Кастомный фильтр для извлечения роли
```java
@Component
public class JwtRoleFilter implements GatewayFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        String token = extractToken(request);
        if (token != null) {
            try {
                DecodedJWT jwt = JWT.decode(token);
                Map<String, Claim> claims = jwt.getClaims();
                
                // Извлекаем роль из realm_access.roles
                Claim realmAccess = claims.get("realm_access");
                if (realmAccess != null) {
                    Map<String, Object> realmAccessMap = realmAccess.asMap();
                    List<String> roles = (List<String>) realmAccessMap.get("roles");
                    
                    String role = determineRole(roles); // admin > owner > user
                    
                    // Добавляем заголовок
                    ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Role", role)
                        .build();
                    
                    return chain.filter(exchange.mutate()
                        .request(modifiedRequest)
                        .build());
                }
            } catch (Exception e) {
                // Обработка ошибки
            }
        }
        
        return chain.filter(exchange);
    }
    
    private String determineRole(List<String> roles) {
        if (roles.contains("admin")) return "admin";
        if (roles.contains("owner")) return "owner";
        if (roles.contains("user")) return "user";
        return "user"; // по умолчанию
    }
    
    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
```

## Или через ReactiveJwtDecoder (рекомендуется)
```java
@Configuration
public class GatewayConfig {
    
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(
            "http://keycloak:8080/realms/main_one/protocol/openid-connect/certs"
        ).build();
    }
    
    @Bean
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> {
            return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .flatMap(token -> {
                    Jwt jwt = token.getToken();
                    
                    // Извлекаем роль
                    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
                    List<String> roles = (List<String>) realmAccess.get("roles");
                    String role = determineRole(roles);
                    
                    // Добавляем заголовки
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", jwt.getSubject())
                        .header("X-User-Role", role)
                        .build();
                    
                    return chain.filter(exchange.mutate()
                        .request(modifiedRequest)
                        .build());
                })
                .switchIfEmpty(chain.filter(exchange));
        };
    }
}
```

## Преимущества этого подхода:
1. ✅ **Проще** - Java код вместо Lua
2. ✅ **Типобезопасность** - компилятор проверит ошибки
3. ✅ **Легче тестировать** - можно писать unit-тесты
4. ✅ **Лучшая интеграция** - с Spring Security, Keycloak
5. ✅ **Готовые библиотеки** - spring-security-oauth2-resource-server

## Недостатки:
1. ❌ Больше памяти (JVM vs Go)
2. ❌ Нужно поддерживать еще один Spring Boot сервис
3. ❌ Медленнее старт приложения
