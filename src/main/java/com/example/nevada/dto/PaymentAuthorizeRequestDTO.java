package com.example.nevada.dto;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class PaymentAuthorizeRequestDTO {
    @NonNull
    private String authorizationCode;
    @NonNull
    private String otp;
}
