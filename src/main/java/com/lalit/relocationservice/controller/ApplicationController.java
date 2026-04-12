package com.lalit.relocationservice.controller;

import com.lalit.relocationservice.DTO.ApplicationResponse;
import com.lalit.relocationservice.DTO.CreateApplicationRequest;
import com.lalit.relocationservice.DTO.ReviewApplicationRequest;
import com.lalit.relocationservice.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relocation/applications")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     *  To Become provider user submit application
     * POST /api/relocation/applications
     * ok
     */
    @PostMapping
    public ResponseEntity<ApplicationResponse> submitApplication(
            @Valid @RequestBody CreateApplicationRequest request) {

        ApplicationResponse response = applicationService.submitApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Provider views their own applications.
     * GET /api/relocation/applications/my/{userId}
     * ok
     */
    @GetMapping("/my/{userId}")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(applicationService.getMyApplications(userId));
    }

    /**
     * Admin: get all applications.
     * GET /api/relocation/applications
     * ok
     */
    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getAllApplications(
            @RequestHeader("X-User-Role") String userRole) {

        if (!userRole.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    /**
     * Admin: get only pending applications.
     * GET /api/relocation/applications/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ApplicationResponse>> getPendingApplications(
            @RequestHeader("X-User-Role") String userRole) {

        if (!userRole.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(applicationService.getPendingApplications());
    }

    /**
     * Admin: approve an application (auto-creates the ServiceProvider).
     * PUT /api/relocation/applications/{id}/approve
     * ok
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApplicationResponse> approveApplication(
            @PathVariable Long id,
            @RequestBody(required = false) ReviewApplicationRequest request,
            @RequestHeader("X-User-Role") String userRole) {

        if (!userRole.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(applicationService.approveApplication(id, request));
    }

    /**
     * Admin: reject an application.
     * PUT /api/relocation/applications/{id}/reject
     * ok
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApplicationResponse> rejectApplication(
            @PathVariable Long id,
            @RequestBody(required = false) ReviewApplicationRequest request,
            @RequestHeader("X-User-Role") String userRole) {

        if (!userRole.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(applicationService.rejectApplication(id, request));
    }
}