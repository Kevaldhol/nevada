package com.example.nevada.controller;

import com.example.nevada.dto.PaymentAuthorizeRequestDTO;
import com.example.nevada.dto.PaymentRequestDTO;
import com.example.nevada.dto.PaymentResponseDTO;
import com.example.nevada.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@Valid @RequestBody PaymentRequestDTO request) {
        return paymentService.initiatePayment(request);
    }

    @PostMapping("/authorize")
    public ResponseEntity<?> authorizePayment(@Valid @RequestBody PaymentAuthorizeRequestDTO PaymentAuthorizeRequestDTO) {
        return paymentService.authorizePayment(PaymentAuthorizeRequestDTO);
    }
}