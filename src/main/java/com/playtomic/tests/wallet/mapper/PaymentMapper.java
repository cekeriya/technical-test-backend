package com.playtomic.tests.wallet.mapper;

import com.playtomic.tests.wallet.dto.PaymentGetResponse;
import com.playtomic.tests.wallet.model.Payment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
	List<PaymentGetResponse> toPaymentGetResponseList(List<Payment> paymentList);
}
