package com.example.nevada.service;

import com.example.nevada.dto.PaymentRequestDTO;
import com.example.nevada.dto.PaymentResponseDTO;
import com.example.nevada.dto.Status;
import com.example.nevada.entity.Payment;
import com.example.nevada.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    //daily limit can be configured in property file
    private final BigDecimal maxDailySpent = BigDecimal.valueOf(1000);

    public ResponseEntity<PaymentResponseDTO> initiatePayment(PaymentRequestDTO request) {
        if (isInvalidPaymentRequest(request)) {
            return new ResponseEntity<>(PaymentResponseDTO.builder().message("InValid Request").build(), HttpStatus.UNAUTHORIZED);
        }

        if (isDailyLimitExceededForUser(request.getUserId(), request.getAmount())) {
            return new ResponseEntity<>(PaymentResponseDTO.builder().message("User daily limit exceeded").build(), HttpStatus.NOT_ACCEPTABLE);
        }

        String authCode = generateAuthorizationCode(request);
        Payment payment = Payment.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .merchantId(request.getMerchantId())
                .authorizationCode(authCode)
                .description(request.getDescription())
                .status(Status.UNAUTHORIZED)
                .build();

        paymentRepository.save(payment);

        return ResponseEntity.ok(PaymentResponseDTO.builder()
                .authorizationCode(authCode)
                .message("Payment authorized successfully")
                .build());
    }

    private boolean isDailyLimitExceededForUser(Long userId, BigDecimal amount) {
        Optional<BigDecimal> todaysSpent = paymentRepository.sumTodaysPaymentsAmountByUserId(userId);
        return todaysSpent
                .filter(bigDecimal -> bigDecimal.add(amount)
                                .compareTo(maxDailySpent) > 0)
                .isPresent();
    }

    private boolean isInvalidPaymentRequest(PaymentRequestDTO request) {
        //todo: call to user data service and merchat service to validate the request

        return false;
    }

    private String generateAuthorizationCode(PaymentRequestDTO request) {
        // todo: Need Implement the logic to generate a unique authorization code, for now use UUID
        return UUID.randomUUID().toString();
    }
}
