package com.lalit.relocationservice.client;

import com.lalit.relocationservice.DTO.UserProfileDTO;
import com.lalit.relocationservice.fallback.UserProfileFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

//in this we write the url temp for the testing the individual service
@FeignClient(name = "user-service",fallback = UserProfileFallback.class)
public interface UserProfileClient {

    @GetMapping("/api/users/profile/{userId}")
    UserProfileDTO getUserById(@PathVariable ("userId") Long userId);

    @PostMapping("/api/users/batch")
    List<UserProfileDTO> getUsersByIds(@RequestBody List<Long> userIds);
}
