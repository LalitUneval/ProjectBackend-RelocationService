package com.lalit.relocationservice.fallback;

import com.lalit.relocationservice.DTO.UserProfileDTO;
import com.lalit.relocationservice.client.UserProfileClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserProfileFallback  implements UserProfileClient {
    @Override
    public UserProfileDTO getUserById(Long userId) {

        log.error("USER-SERVICE is down. Fallback triggered for getUserById: {}", userId);

        return UserProfileDTO.builder()
                .id(userId)
                .fullName("User Info Unavailable")
                .phoneNumber(null)
                .build();
    }

    @Override
    public List<UserProfileDTO> getUsersByIds(List<Long> userIds) {

        log.error("USER-SERVICE is down. Fallback triggered for batch user fetch");

        return userIds.stream()
                .map(id -> UserProfileDTO.builder()
                        .id(id)
                        .fullName("User Info Unavailable")
                        .phoneNumber(null)
                        .build())
                .collect(Collectors.toList());
    }
}
