
package com.mehmetkaradana.springboot_api.controllers;

import com.mehmetkaradana.springboot_api.services.TourismAgencyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api")
public class TourismController {

    private final TourismAgencyService tourismAgencyService;

    public TourismController(TourismAgencyService tourismAgencyService) {
        this.tourismAgencyService = tourismAgencyService;
    }

    @GetMapping("/getRooms")
    public ResponseEntity<?> getRooms(HttpServletRequest request) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<String> rooms = executor.submit(() -> tourismAgencyService.getRoomsFromAgencies()).get();
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Hata oluştu: " + e.getMessage());
        }

    }

}
