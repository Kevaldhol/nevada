package com.example.nevada.Interactions.Client;

import com.example.nevada.Interactions.Fallback.MerchantOnboardingFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "merchant-onboarding-service", url = "http://localhost:8081",fallback = MerchantOnboardingFallback.class)
public interface MerchantOnboardingClient {

    @GetMapping("/merchant/validation")
    ResponseEntity<?> isValidMerchant(@RequestParam("merchantId") Long merchantId);

}
