package com.example.nevada.Interactions.Fallback;

import com.example.nevada.Interactions.Client.MerchantOnboardingClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class MerchantOnboardingFallback implements FallbackFactory<MerchantOnboardingClient> {
    @Override
    public MerchantOnboardingClient create(Throwable cause) {
        return new MerchantOnboardingClient() {
            @Override
            public ResponseEntity<?> isValidMerchant(Long merchantId) {
                return null;
            }
        };
    }
}
