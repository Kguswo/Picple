package com.ssafy.picple.domain.booth.controller;


import com.ssafy.picple.domain.booth.dto.BoothCreateRequest;
import com.ssafy.picple.domain.booth.service.BoothService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booths")
public class BoothController {
    private final BoothService boothService;

    public BoothController(BoothService boothService) {
        this.boothService = boothService;
    }

    @PostMapping
    public ResponseEntity<String> createBooth(@RequestBody BoothCreateRequest request) {
        try {
            String boothId = boothService.createBooth(request.getName(), request.getMaxParticipants());
            return ResponseEntity.ok(boothId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create booth: " + e.getMessage());
        }
    }

    @PostMapping("/{boothId}/join")
    public ResponseEntity<String> joinBooth(@PathVariable String boothId) {
        try {
            boolean joined = boothService.joinBooth(boothId);
            if (joined) {
                return ResponseEntity.ok("Joined the booth successfully");
            } else {
                return ResponseEntity.badRequest().body("Booth is full");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to join booth: " + e.getMessage());
        }
    }

    @PostMapping("/{boothId}/leave")
    public ResponseEntity<String> leaveBooth(@PathVariable String boothId, @RequestParam String participantId) {
        try {
            boothService.leaveBooth(boothId, participantId);
            return ResponseEntity.ok("Left the booth successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to leave booth: " + e.getMessage());
        }
    }
}