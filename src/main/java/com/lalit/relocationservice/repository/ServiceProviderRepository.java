package com.lalit.relocationservice.repository;

import com.lalit.relocationservice.entity.ServiceProvider;
import com.lalit.relocationservice.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    List<ServiceProvider> findByCity(String city);
    List<ServiceProvider> findByServiceType(ServiceType serviceType);
    List<ServiceProvider> findByCityAndServiceType(String city, ServiceType serviceType);
    List<ServiceProvider> findByIsVerified(Boolean isVerified);

    @Query("SELECT p FROM ServiceProvider p WHERE p.rating >= :minRating ORDER BY p.rating DESC")
    List<ServiceProvider> findTopRatedProviders(@Param("minRating") Double minRating);

    @Query("SELECT p FROM ServiceProvider p WHERE " +
            "(:city IS NULL OR p.city = :city) AND " +
            "(:serviceType IS NULL OR p.serviceType = :serviceType) AND " +
            "(:isVerified IS NULL OR p.isVerified = :isVerified) AND " +
            "(:minRating IS NULL OR p.rating >= :minRating)")
    List<ServiceProvider> searchProviders(
            @Param("city") String city,
            @Param("serviceType") ServiceType serviceType,
            @Param("isVerified") Boolean isVerified,
            @Param("minRating") Double minRating);

    long countByCity(String city);
    long countByServiceType(ServiceType serviceType);

    /**
     * NEW: Find the provider profile owned by a specific user.
     * Used to:
     *  - Check if logged-in user is also a provider (show "My Bookings as Provider" tab)
     *  - Self-booking guard
     *  - Provider confirm ownership check
     */
    Optional<ServiceProvider> findByOwnerUserId(Long ownerUserId);
}

