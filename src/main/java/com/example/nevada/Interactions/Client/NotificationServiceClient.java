package com.example.nevada.Interactions.Client;

import com.example.nevada.entity.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "notification-service", url = "http://localhost:8081")
public interface NotificationServiceClient {

    @GetMapping("/notification/otp/{userId}/{transactionId}")
    ResponseEntity<?> sendOtp(@PathVariable("userId") Long userId,@PathVariable("transactionId") Long transactionId);

    @GetMapping("/notification/otp/{otp}/{userId}/{transactionId}")
    ResponseEntity<?> validateOtp(@PathVariable("otp") String otp,@PathVariable("transactionId") Long transactionId,@PathVariable("userId") Long userId);

    @PostMapping("/notification/marchant/notify")
    ResponseEntity<?> sendNotificationToMarchant(Payment payment);
}
