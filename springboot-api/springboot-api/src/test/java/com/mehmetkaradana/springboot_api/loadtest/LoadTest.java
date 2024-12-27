package com.mehmetkaradana.springboot_api.loadtest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LoadTest {

    private static final int TOTAL_REQUESTS = 1000; // Toplam istek sayısı
    private static final int THREAD_COUNT = 100; // Aynı anda gönderilecek istek sayısı
    private static final String URL = "http://localhost:8081/api/getRooms"; // Test etmek istediğiniz endpoint
    private static final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<Long>> responseTimes = new ArrayList<>();

        // Toplam istek sayısı kadar işlem oluştur
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            Future<Long> responseTime = executor.submit(() -> sendRequestAndMeasureTime());
            responseTimes.add(responseTime);
        }

        executor.shutdown();

        analyzeResults(responseTimes);
    }

    private static Long sendRequestAndMeasureTime() {
        try {
            long start = System.currentTimeMillis();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY3MWEyOTFiNzY0YThlMTEwNzlkMGYyZSIsImlhdCI6MTcyOTc2NzcwNywiZXhwIjoxNzMyMzU5NzA3fQ.ijMcqGw4OUOWG79jrXhmkAwGolVJUjNXp6Y15y-OY7Y"); // Token'i buraya ekleyin
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, entity, String.class);

            long end = System.currentTimeMillis();

            if (response.getStatusCode().is2xxSuccessful()) {
                return end - start;
            } else {
                System.out.println("Error: " + response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    private static void analyzeResults(List<Future<Long>> responseTimes) {
        List<Long> times = new ArrayList<>();
        int errorCount = 0;

        for (Future<Long> future : responseTimes) {
            try {
                Long time = future.get();
                if (time != null) {
                    times.add(time);
                } else {
                    errorCount++;
                }
            } catch (Exception e) {
                errorCount++;
            }
        }

        long total = times.stream().mapToLong(Long::longValue).sum();
        long maxTime = times.stream().mapToLong(Long::longValue).max().orElse(0);
        long minTime = times.stream().mapToLong(Long::longValue).min().orElse(0);

        System.out.println("Total Requests: " + (times.size() + errorCount));
        System.out.println("Successful Requests: " + times.size());
        System.out.println("Failed Requests: " + errorCount);
        System.out.println("Average Response Time: " + (times.size() > 0 ? (total / times.size()) : 0) + " ms");
        System.out.println("Maximum Response Time: " + maxTime + " ms");
        System.out.println("Minimum Response Time: " + minTime + " ms");

    }
}
