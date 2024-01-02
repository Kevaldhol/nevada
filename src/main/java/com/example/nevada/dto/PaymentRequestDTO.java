package com.example.nevada.dto;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {
    private Long transactionId;
    @NonNull
    private Long userId;
    @NonNull
    private Long merchantId;
    @NonNull
    private BigDecimal amount;
    @NonNull
    private String description;
}
