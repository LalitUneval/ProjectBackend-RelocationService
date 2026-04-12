package com.lalit.relocationservice.controller;

import com.lalit.relocationservice.DTO.CreateServiceProviderRequest;
import com.lalit.relocationservice.DTO.ServiceProviderResponse;
import com.lalit.relocationservice.DTO.ServiceProviderSearchRequest;
import com.lalit.relocationservice.DTO.UpdateServiceProviderRequest;
import com.lalit.relocationservice.entity.ServiceType;
import com.lalit.relocationservice.service.ServiceProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/relocation/services")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;

    /**
     * Create service provider (Admin only)
     * POST /api/relocation/services
     * ok
     */
    @PostMapping
    public ResponseEntity<ServiceProviderResponse> createProvider(
            @Valid @RequestBody CreateServiceProviderRequest request,
            @RequestHeader("X-User-Role") String userRole) {

        if (!userRole.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ServiceProviderResponse response = serviceProviderService.createProvider(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    /**
//     * Get provider by ID
//     * GET /api/relocation/services/{providerId}
//     */
//    @GetMapping("/{providerId}")
//    public ResponseEntity<ServiceProviderResponse> getProvider(@PathVariable Long providerId) {
//        ServiceProviderResponse response = serviceProviderService.getProvider(providerId);
//        return ResponseEntity.ok(response);
//    }

    /**
     * Get all providers
     * GET /api/relocation/services
     * ok
     */
    @GetMapping
    public ResponseEntity<List<ServiceProviderResponse>> getAllProviders() {
        List<ServiceProviderResponse> providers = serviceProviderService.getAllProviders();
        log.info(providers.toString());
        return ResponseEntity.ok(providers);
    }

    /**
     * Get providers by city
     * GET /api/relocation/services/city/{city}
     * ok
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<ServiceProviderResponse>> getProvidersByCity(@PathVariable String city) {
        List<ServiceProviderResponse> providers = serviceProviderService.getProvidersByCity(city);
        return ResponseEntity.ok(providers);
    }

    /**
     * Get providers by service type
     * GET /api/relocation/services/type/{serviceType}
     * ok
     */
    @GetMapping("/type/{serviceType}")
    public ResponseEntity<List<ServiceProviderResponse>> getProvidersByServiceType(@PathVariable ServiceType serviceType) {
        List<ServiceProviderResponse> providers = serviceProviderService.getProvidersByServiceType(serviceType);
        return ResponseEntity.ok(providers);
    }

    /**
     * Get verified providers
     * GET /api/relocation/services/verified
     * ok
     */
    @GetMapping("/verified")
    public ResponseEntity<List<ServiceProviderResponse>> getVerifiedProviders() {
        List<ServiceProviderResponse> providers = serviceProviderService.getVerifiedProviders();
        return ResponseEntity.ok(providers);
    }

    /**
     * Get top-rated providers
     * GET /api/relocation/services/top-rated?minRating=4.5
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<ServiceProviderResponse>> getTopRatedProviders(
            @RequestParam(defaultValue = "4.0") Double minRating) {

        List<ServiceProviderResponse> providers = serviceProviderService.getTopRatedProviders(minRating);
        return ResponseEntity.ok(providers);
    }

    /**
     * Search providers
     * GET /api/relocation/services/search?city=SF&type=AIRPORT_PICKUP
     * ok
     */
    @GetMapping("/search")
    public ResponseEntity<List<ServiceProviderResponse>> searchProviders(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) ServiceType serviceType,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(required = false) Double minRating) {

        ServiceProviderSearchRequest searchRequest = ServiceProviderSearchRequest.builder()
                .city(city)
                .serviceType(serviceType)
                .isVerified(isVerified)
                .minRating(minRating)
                .build();

        List<ServiceProviderResponse> providers = serviceProviderService.searchProviders(searchRequest);
        return ResponseEntity.ok(providers);
    }

    /**
     * Update provider (Admin only)
     * PUT /api/relocation/services/{providerId}
     * ok
     */
    @PutMapping("/{providerId}")
    public ResponseEntity<ServiceProviderResponse> updateProvider(
            @PathVariable Long providerId,
            @Valid @RequestBody UpdateServiceProviderRequest request,
            @RequestHeader("X-User-Role") String userRole) {

        if (!userRole.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ServiceProviderResponse response = serviceProviderService.updateProvider(providerId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete provider (Admin only)
     * DELETE /api/relocation/services/{providerId}
     * ok
     */
    @DeleteMapping("/{providerId}")
    public ResponseEntity<Void> deleteProvider(
            @PathVariable Long providerId,
            @RequestHeader("X-User-Role") String userRole) {

        if (!userRole.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        serviceProviderService.deleteProvider(providerId);
        return ResponseEntity.noContent().build();
    }
}


