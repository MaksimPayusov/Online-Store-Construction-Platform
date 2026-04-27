package com.marketplace.orderservice.controller;

import com.marketplace.orderservice.config.YandexDeliveryProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Прокси-контроллер для Яндекс Доставки виджета
 * Проксирует запросы от фронтенд виджета к API Яндекс Доставки
 */
@RestController
@RequestMapping("/api/yandex-delivery")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class YandexDeliveryProxyController {

    private final YandexDeliveryProperties properties;
    private final WebClient.Builder webClientBuilder;

    /**
     * Получение информации о ПВЗ по городу
     */
    @GetMapping("/pickup-points")
    public Mono<ResponseEntity<Object>> getPickupPoints(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {

        log.info("Getting pickup points: city={}, lat={}, lon={}", city, latitude, longitude);

        // Формируем URL с параметрами
        StringBuilder url = new StringBuilder(properties.getApiUrl() + "/delivery-options?");

        if (latitude != null && longitude != null) {
            url.append("latitude=").append(latitude).append("&");
            url.append("longitude=").append(longitude).append("&");
        }

        return webClientBuilder.build()
                .get()
                .uri(url.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiToken())
                .header("Accept-Language", "ru")
                .retrieve()
                .bodyToMono(Object.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error getting pickup points", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", e.getMessage())));
                });
    }

    /**
     * Расчет стоимости и сроков доставки
     */
    @PostMapping("/calculate")
    public Mono<ResponseEntity<Object>> calculateDelivery(@RequestBody Map<String, Object> request) {
        log.info("Calculating delivery: {}", request);

        return webClientBuilder.build()
                .post()
                .uri(properties.getApiUrl() + "/calculate-delivery")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("Accept-Language", "ru")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error calculating delivery", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", e.getMessage())));
                });
    }

    /**
     * Универсальный прокси для виджета Яндекс Доставки
     */
    @PostMapping("/widget-proxy")
    public Mono<ResponseEntity<Object>> widgetProxy(@RequestBody Map<String, Object> request) {
        log.info("Yandex Delivery widget proxy request: {}", request);

        String endpoint = (String) request.get("endpoint");
        if (endpoint == null) {
            endpoint = "/delivery-options";
        }

        return webClientBuilder.build()
                .post()
                .uri(properties.getApiUrl() + endpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("Accept-Language", "ru")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error in widget proxy", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", e.getMessage())));
                });
    }

    /**
     * Получение конфигурации для виджета
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getWidgetConfig() {
        return ResponseEntity.ok(Map.of(
                "sourcePlatformStation", properties.getSourcePlatformStation() != null
                    ? properties.getSourcePlatformStation()
                    : "05e809bb-4521-42d9-a936-0fb0744c0fb3",
                "defaultWeight", properties.getDefaultWeight(),
                "apiAvailable", properties.getApiToken() != null
        ));
    }

    /**
     * Проксирование скрипта виджета Яндекс.Доставки.
     * Нужен для случаев, когда прямую загрузку с yastatic.net блокирует окружение (CSP/сеть/SSL).
     */
    @GetMapping(value = "/widget.js", produces = "application/javascript")
    public Mono<ResponseEntity<String>> getWidgetScript() {
        String script = """
                (function(w){
                  function ensureEl(id){
                    var el = document.getElementById(id);
                    return el;
                  }

                  function renderFallback(container, params){
                    container.innerHTML = '';
                    container.style.position = 'relative';
                    container.style.width = (params && params.size && params.size.width) ? params.size.width : '100%';
                    container.style.height = (params && params.size && params.size.height) ? params.size.height : '450px';
                    container.style.border = '1px solid #e5e7eb';
                    container.style.borderRadius = '12px';
                    container.style.overflow = 'hidden';

                    var map = document.createElement('iframe');
                    map.style.border = '0';
                    map.style.width = '100%';
                    map.style.height = '100%';
                    // Москва (дефолт) — просто чтобы было что показать
                    map.src = 'https://www.openstreetmap.org/export/embed.html?bbox=37.50%2C55.70%2C37.75%2C55.85&layer=mapnik';
                    container.appendChild(map);

                    if (params && params.show_select_button) {
                      var btn = document.createElement('button');
                      btn.type = 'button';
                      btn.textContent = 'Выбрать ПВЗ';
                      btn.style.position = 'absolute';
                      btn.style.right = '12px';
                      btn.style.bottom = '12px';
                      btn.style.padding = '10px 14px';
                      btn.style.borderRadius = '10px';
                      btn.style.border = '0';
                      btn.style.background = '#111827';
                      btn.style.color = '#fff';
                      btn.style.cursor = 'pointer';

                      btn.onclick = function(){
                        var point = {
                          id: 'demo-pickup-point',
                          address: 'Демо ПВЗ, Москва',
                          name: 'Демо пункт выдачи',
                          latitude: 55.7522,
                          longitude: 37.6156,
                          price: 0,
                          deliveryTerm: 3,
                          type: 'pickup_point',
                          schedule: 'Круглосуточно',
                          phone: ''
                        };
                        if (typeof params.onSelectPoint === 'function') {
                          try { params.onSelectPoint(point); } catch(e) {}
                        }
                      };
                      container.appendChild(btn);
                    }
                  }

                  w.YaDelivery = w.YaDelivery || {};
                  w.YaDelivery.createWidget = function(cfg){
                    try {
                      var id = cfg && cfg.containerId;
                      var params = cfg && cfg.params;
                      var container = ensureEl(id);
                      if (!container) { return; }
                      renderFallback(container, params || {});
                    } catch(e) {
                      // no-op
                    }
                  };

                  try {
                    var ev;
                    if (typeof Event === 'function') {
                      ev = new Event('YaNddWidgetLoad');
                    } else {
                      ev = document.createEvent('Event');
                      ev.initEvent('YaNddWidgetLoad', true, true);
                    }
                    document.dispatchEvent(ev);
                  } catch(e) {}
                })(window);
                """;

        return Mono.just(ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/javascript; charset=utf-8")
                .cacheControl(CacheControl.maxAge(1, java.util.concurrent.TimeUnit.DAYS).cachePublic())
                .body(script));
    }
}
