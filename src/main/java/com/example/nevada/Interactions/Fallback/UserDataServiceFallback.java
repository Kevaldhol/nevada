package com.example.nevada.Interactions.Fallback;

import com.example.nevada.Interactions.Client.UserDataServiceClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserDataServiceFallback implements FallbackFactory<UserDataServiceClient> {
    @Override
    public UserDataServiceClient create(Throwable cause) {
        return new UserDataServiceClient() {
            @Override
            public ResponseEntity<?> isValidUser(Long userId) {
                return null;
            }
        };
    }
}
