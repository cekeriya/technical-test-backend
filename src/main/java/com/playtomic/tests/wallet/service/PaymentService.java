package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.model.Payment;
import com.playtomic.tests.wallet.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
	private final PaymentRepository paymentRepository;

	public PaymentService(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}

	public Payment save(Payment payment){
		return paymentRepository.save(payment);
	}

	public List<Payment> findAll(){
		return paymentRepository.findAll();
	}

	public Optional<Payment> findByUuid(UUID uuid){
		return paymentRepository.findById(uuid);
	}

}
