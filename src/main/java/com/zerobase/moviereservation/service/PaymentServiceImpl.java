package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.PAYMENT_FAILED;

import com.zerobase.moviereservation.entity.Payment;
import com.zerobase.moviereservation.entity.Reservation;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.type.PaymentType;
import com.zerobase.moviereservation.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;

  public Payment processPayment(Reservation reservation, Integer amount) {
    try {
      // 결제에 소요되는 delayTime 임의로 설정
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new CustomException(PAYMENT_FAILED);
    }

    return paymentRepository.save(Payment.builder()
        .reservation(reservation)
        .amount(amount)
        .status(PaymentType.S)
        .build());
  }
}
