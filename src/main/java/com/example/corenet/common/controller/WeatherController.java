package com.example.corenet.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey = "9d6611138c9593c0cab49e18b335aaa7";

    @GetMapping("/current")
    public ResponseEntity<String> getWeather(@RequestParam("lat") double lat, @RequestParam("lon") double lon) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat +
                "&lon=" + lon +
                "&appid=" + apiKey +
                "&units=metric&lang=kr";

        try {
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그에 에러 출력
            return ResponseEntity
                    .status(500)
                    .body("{\"error\":\"서버에서 날씨 정보를 불러오지 못했습니다.\"}");
        }
    }
}
