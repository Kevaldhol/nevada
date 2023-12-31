package com.example.nevada.entity;

import com.example.nevada.dto.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "payment")
@Data
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transactionId;
    private Long userId;
    private Long merchantId;
    private BigDecimal amount;
    private Long billId;
    private Status status;
    private String description;
    private String authorizationCode;
    @CreationTimestamp
    private Date createdAt;
}