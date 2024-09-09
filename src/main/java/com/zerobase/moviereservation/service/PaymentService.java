package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.entity.Payment;
import com.zerobase.moviereservation.entity.Reservation;

public interface PaymentService {

  public Payment processPayment(Reservation reservation, Integer amount);
}
