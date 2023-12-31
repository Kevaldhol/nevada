package com.example.nevada.repository;

import com.example.nevada.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT sum(p.amount) FROM Payment p WHERE p.createdAt > CURRENT_DATE and p.userId=:userId")
    Optional<BigDecimal> sumTodaysPaymentsAmountByUserId(@Param("userId") Long userId);

    Optional<Payment> findByAuthorizationCode(String authorizationCode);
}
