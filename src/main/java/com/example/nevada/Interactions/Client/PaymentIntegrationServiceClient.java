package com.example.nevada.Interactions.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Payment-Integration-service", url = "http://localhost:8081")
public interface PaymentIntegrationServiceClient {
}
