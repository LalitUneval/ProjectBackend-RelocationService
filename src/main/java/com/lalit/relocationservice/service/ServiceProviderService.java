package com.lalit.relocationservice.service;

import com.lalit.relocationservice.DTO.CreateServiceProviderRequest;
import com.lalit.relocationservice.DTO.ServiceProviderResponse;
import com.lalit.relocationservice.DTO.ServiceProviderSearchRequest;
import com.lalit.relocationservice.DTO.UpdateServiceProviderRequest;
import com.lalit.relocationservice.entity.ServiceProvider;
import com.lalit.relocationservice.entity.ServiceType;
import com.lalit.relocationservice.exception.ProviderNotFoundException;
import com.lalit.relocationservice.exception.ResourceNotFoundException;
import com.lalit.relocationservice.repository.ServiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ServiceProviderService {

    private final ServiceProviderRepository  serviceProviderRepository;


    /**
     * Create service provider (Admin only)
     */
    @Caching(
            evict = {
                    @CacheEvict(value = "relocation_providers_all",allEntries = true),
                    @CacheEvict(value = "relocation_providers_city",allEntries = true),
                    @CacheEvict(value = "relocation_providers_type",allEntries = true),
                    @CacheEvict(value = "relocation_providers_city_type",allEntries = true),
                    @CacheEvict(value = "relocation_providers_verified",allEntries = true),
                    @CacheEvict(value = "relocation_providers_toprated",allEntries = true),
                    @CacheEvict(value = "relocation_providers_search",allEntries = true)

            }
    )
    public ServiceProviderResponse createProvider(CreateServiceProviderRequest request) {
        ServiceProvider provider = ServiceProvider.builder()
                .name(request.getName())
                .serviceType(request.getServiceType())
                .city(request.getCity())
                .contactNumber(request.getContactNumber())
                .email(request.getEmail())
                .description(request.getDescription())
                .rating(0.0)
                .totalReviews(0)
                .isVerified(request.getIsVerified() != null ? request.getIsVerified() : false)
                .build();

        ServiceProvider savedProvider = serviceProviderRepository.save(provider);

        return mapToResponse(savedProvider);
    }

//    /**
//     * Get provider by ID
//     */
//    public ServiceProviderResponse getProvider(Long providerId) {
//        ServiceProvider provider = serviceProviderRepository.findById(providerId)
//                .orElseThrow(() -> new ProviderNotFoundException("Provider not found: " + providerId));
//
//        return mapToResponse(provider);
//    }

    /**
     * Get all providers
     */
    @Cacheable(value = "relocation_providers_all",key = "'all'")
    public List<ServiceProviderResponse> getAllProviders() {
        List<ServiceProvider> providers = serviceProviderRepository.findAll();
        return providers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get providers by city
     */
    @Cacheable(value = "relocation_providers_city",key = "#city")
    public List<ServiceProviderResponse> getProvidersByCity(String city) {
        List<ServiceProvider> providers = serviceProviderRepository.findByCity(city);

        if(providers.isEmpty()){
         throw new ResourceNotFoundException("Resource not find with: "+city);
        }

        return providers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get providers by service type
     */
    @Cacheable(value = "relocation_providers_type", key = "#serviceType")
    public List<ServiceProviderResponse> getProvidersByServiceType(ServiceType serviceType) {
        List<ServiceProvider> providers = serviceProviderRepository.findByServiceType(serviceType);

        if(providers.isEmpty()){
            throw new ResourceNotFoundException("Resource not find with: "+serviceType);
        }

        return providers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get providers by city and service type
     */
    @Cacheable(value = "relocation_providers_city_type", key = "#city + '-' + #serviceType")
    public List<ServiceProviderResponse> getProvidersByCityAndType(String city, ServiceType serviceType) {
        List<ServiceProvider> providers = serviceProviderRepository.findByCityAndServiceType(city, serviceType);

        if(providers.isEmpty()){
            throw new ResourceNotFoundException("Resource not find with: "+serviceType+"&"+city);
        }

        return providers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get verified providers
     */
    @Cacheable(value = "relocation_providers_verified", key = "'verified'")
    public List<ServiceProviderResponse> getVerifiedProviders() {
        List<ServiceProvider> providers = serviceProviderRepository.findByIsVerified(true);
        return providers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get top-rated providers
     */
    @Cacheable(value = "relocation_providers_toprated", key = "#minRating")
    public List<ServiceProviderResponse> getTopRatedProviders(Double minRating) {
        List<ServiceProvider> providers = serviceProviderRepository.findTopRatedProviders(minRating);
        if(providers.isEmpty()){
            throw new ResourceNotFoundException("Your Requirements are not match.");
        }
        return providers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search providers
     */
    @Cacheable(value = "relocation_providers_search", key = "#request.city + '-' + #request.serviceType + '-' + #request.minRating")
    public List<ServiceProviderResponse> searchProviders(ServiceProviderSearchRequest request) {
        List<ServiceProvider> providers = serviceProviderRepository.searchProviders(
                request.getCity(),
                request.getServiceType(),
                request.getIsVerified(),
                request.getMinRating()
        );
        if(providers.isEmpty()){
            throw new ResourceNotFoundException("Try with another resource.");
        }
        return providers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update provider (Admin only)
     */
    @Caching(evict = {
            @CacheEvict(value = "relocation_providers_all", allEntries = true),
            @CacheEvict(value = "relocation_providers_city", allEntries = true),
            @CacheEvict(value = "relocation_providers_type", allEntries = true),
            @CacheEvict(value = "relocation_providers_verified", allEntries = true),
            @CacheEvict(value = "relocation_providers_toprated", allEntries = true),
            @CacheEvict(value = "relocation_providers_search", allEntries = true)
    })
    public ServiceProviderResponse updateProvider(Long providerId, UpdateServiceProviderRequest request) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found: " + providerId));

        if (request.getContactNumber() != null) {
            provider.setContactNumber(request.getContactNumber());
        }
        if (request.getEmail() != null) {
            provider.setEmail(request.getEmail());
        }
        if (request.getDescription() != null) {
            provider.setDescription(request.getDescription());
        }
        if (request.getIsVerified() != null) {
            provider.setIsVerified(request.getIsVerified());
        }

        ServiceProvider updatedProvider = serviceProviderRepository.save(provider);

        return mapToResponse(updatedProvider);
    }

    /**
     * Update rating (called after booking completion)
     */
    public void updateRating(Long providerId, Double newRating) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found: " + providerId));

        // Calculate new average rating
        int totalReviews = provider.getTotalReviews();
        double currentRating = provider.getRating();
        double updatedRating = ((currentRating * totalReviews) + newRating) / (totalReviews + 1);

        provider.setRating(updatedRating);
        provider.setTotalReviews(totalReviews + 1);

        serviceProviderRepository.save(provider);
    }

    /**
     * Delete provider (Admin only)
     */
    public void deleteProvider(Long providerId) {
        if (!serviceProviderRepository.existsById(providerId)) {
            throw new ProviderNotFoundException("Provider not found: " + providerId);
        }
        serviceProviderRepository.deleteById(providerId);
    }

    /**
     * Count providers by city
     */
    public long countProvidersByCity(String city) {
        return serviceProviderRepository.countByCity(city);
    }

    /**
     * Count providers by service type
     */
    public long countProvidersByType(ServiceType serviceType) {
        return serviceProviderRepository.countByServiceType(serviceType);
    }

    // Helper method
    private ServiceProviderResponse mapToResponse(ServiceProvider provider) {
        return ServiceProviderResponse.builder()
                .id(provider.getId())
                .name(provider.getName())
                .serviceType(provider.getServiceType())
                .city(provider.getCity())
                .contactNumber(provider.getContactNumber())
                .email(provider.getEmail())
                .description(provider.getDescription())
                .rating(provider.getRating())
                .totalReviews(provider.getTotalReviews())
                .isVerified(provider.getIsVerified())
                .build();
    }

}
