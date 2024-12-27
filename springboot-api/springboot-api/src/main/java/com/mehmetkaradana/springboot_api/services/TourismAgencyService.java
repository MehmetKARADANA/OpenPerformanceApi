package com.mehmetkaradana.springboot_api.services;

import  com.mehmetkaradana.springboot_api.config.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class TourismAgencyService {

    private final ServerProperties serverProperties;
    private final RestTemplate restTemplate;

    public TourismAgencyService(ServerProperties serverProperties, RestTemplate restTemplate) {
        this.serverProperties = serverProperties;
        this.restTemplate = restTemplate;
    }


    public List<String> getRoomsFromAgencies() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) { // Virtual thread executor
            List<Future<String>> futures = serverProperties.getServers().stream()
                    .map(server -> executor.submit(() -> getRoomsFromAgency(server.getUrl())))
                    .toList();
            return futures.stream()
                    .map(future -> {
                        try {
                            return future.get(); // Her future'dan sonucu al
                        } catch (Exception e) {
                            System.err.println("Error: " + e.getMessage());
                            return null; // Hata durumunda null döndür
                        }
                    })
                    .filter(Objects::nonNull) // Null sonuçları filtrele
                    .collect(Collectors.toList());
        }
    }

    private String getRoomsFromAgency(String url) {
        try {
           // return "succes";
            return restTemplate.getForObject(url + "/rooms", String.class); // RestTemplate ile istek yap
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }
}
