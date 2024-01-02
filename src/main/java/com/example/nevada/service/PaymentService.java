package com.example.nevada.service;

import com.example.nevada.Interactions.Client.MerchantOnboardingClient;
import com.example.nevada.Interactions.Client.NotificationServiceClient;
import com.example.nevada.Interactions.Client.UserDataServiceClient;
import com.example.nevada.dto.PaymentAuthorizeRequestDTO;
import com.example.nevada.dto.PaymentRequestDTO;
import com.example.nevada.dto.PaymentResponseDTO;
import com.example.nevada.dto.Status;
import com.example.nevada.entity.Payment;
import com.example.nevada.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MerchantOnboardingClient merchantOnboardingClient;
    private final UserDataServiceClient userDataServiceClient;
    private final NotificationServiceClient notificationServiceClient;
    //daily limit can be configured in property file
    private final BigDecimal maxDailySpent = BigDecimal.valueOf(1000);

    @Transactional
    public ResponseEntity<PaymentResponseDTO> initiatePayment(PaymentRequestDTO request) {
        if (Objects.nonNull(request.getTransactionId()))
            return new ResponseEntity<>(PaymentResponseDTO.builder().message("payment amendment is supported currently").build(), HttpStatus.BAD_REQUEST);

        if (!validatedUserAndMerhcant(request))
            return new ResponseEntity<>(PaymentResponseDTO.builder().message("InValid User/Merchant").build(), HttpStatus.UNAUTHORIZED);

        if (isDailyLimitExceededForUser(request.getUserId(), request.getAmount()))
            return new ResponseEntity<>(PaymentResponseDTO.builder().message("User daily limit exceeded").build(), HttpStatus.NOT_ACCEPTABLE);

        String authCode = generateAuthorizationCode(request);
        Payment paymentObj = Payment.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .merchantId(request.getMerchantId())
                .authorizationCode(authCode)
                .description(request.getDescription())
                .status(Status.UNAUTHORIZED)
                .build();

        Payment payment = paymentRepository.save(paymentObj);
        sendOtpForAutorization(payment.getUserId(), payment.getTransactionId());

        return ResponseEntity.ok(PaymentResponseDTO.builder()
                .authorizationCode(authCode)
                .message("Payment Initiate successfully")
                .build());
    }

    public ResponseEntity<?> authorizePayment(PaymentAuthorizeRequestDTO paymentAuthorizeRequestDTO) {
        Optional<Payment> payment = paymentRepository.findByAuthorizationCode(paymentAuthorizeRequestDTO.getAuthorizationCode());
        if (payment.isEmpty()) {
            return new ResponseEntity<>(PaymentResponseDTO.builder().message("InValid Request").build(), HttpStatus.UNAUTHORIZED);
        }

        Payment paymentObj = payment.get();
        if(Objects.equals(paymentObj.getStatus(), Status.AUTHORIZED))
            return new ResponseEntity<>(PaymentResponseDTO.builder().message("this payment is already authorized by user").build(), HttpStatus.OK);

        ResponseEntity<?> response = notificationServiceClient.validateOtp(paymentAuthorizeRequestDTO.getOtp(), paymentObj.getTransactionId(), paymentObj.getUserId());
        if (!(response.getStatusCode() == HttpStatus.OK))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        paymentObj.setStatus(Status.AUTHORIZED);
        paymentRepository.save(paymentObj);
        sendNotificationToMerchant(paymentObj);
        return ResponseEntity.ok(PaymentResponseDTO.builder()
                .message("Payment authorized successfully")
                .build());
    }

    private void sendNotificationToMerchant(Payment payment) {
        //async call to send notification to merhcant
        CompletableFuture.supplyAsync(() -> notificationServiceClient.sendNotificationToMarchant(payment));
    }

    private void sendOtpForAutorization(Long userId, Long transactionId) {
        //async call to send otp to client
        CompletableFuture.supplyAsync(() -> notificationServiceClient.sendOtp(userId, transactionId));
    }

    private boolean isDailyLimitExceededForUser(Long userId, BigDecimal amount) {
        Optional<BigDecimal> todaysSpent = paymentRepository.sumTodaysPaymentsAmountByUserId(userId);
        return todaysSpent
                .filter(bigDecimal -> bigDecimal.add(amount).compareTo(maxDailySpent) > 0)
                .isPresent();
    }

    private boolean validatedUserAndMerhcant(PaymentRequestDTO request) {
        ResponseEntity<?> merchantValidationResponse = merchantOnboardingClient.isValidMerchant(request.getMerchantId());
        ResponseEntity<?> userValidationResponse = userDataServiceClient.isValidUser(request.getUserId());
        return merchantValidationResponse.getStatusCode() == HttpStatus.OK
                && userValidationResponse.getStatusCode() == HttpStatus.OK;
    }

    private String generateAuthorizationCode(PaymentRequestDTO request) {
        // Need Implement the logic to generate a unique authorization code, for now using UUID
        return UUID.randomUUID().toString();
    }
}
