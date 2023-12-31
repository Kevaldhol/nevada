package com.example.nevada.Interactions.Client;

import com.example.nevada.Interactions.Fallback.UserDataServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-data-service", url = "http://localhost:8081",fallback = UserDataServiceFallback.class)
public interface UserDataServiceClient {

    @GetMapping("/user/validation")
    ResponseEntity<?> isValidUser(@RequestParam("userId") Long userId);

}
