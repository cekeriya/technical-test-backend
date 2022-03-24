package com.playtomic.tests.wallet.mapper;

import com.playtomic.tests.wallet.dto.PaymentGetResponse;
import com.playtomic.tests.wallet.dto.PaymentGetResponse.PaymentGetResponseBuilder;
import com.playtomic.tests.wallet.model.Payment;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-03-24T01:02:40+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_251 (Oracle Corporation)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public List<PaymentGetResponse> toPaymentGetResponseList(List<Payment> paymentList) {
        if ( paymentList == null ) {
            return null;
        }

        List<PaymentGetResponse> list = new ArrayList<PaymentGetResponse>( paymentList.size() );
        for ( Payment payment : paymentList ) {
            list.add( paymentToPaymentGetResponse( payment ) );
        }

        return list;
    }

    protected PaymentGetResponse paymentToPaymentGetResponse(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        PaymentGetResponseBuilder paymentGetResponse = PaymentGetResponse.builder();

        paymentGetResponse.uuid( payment.getUuid() );
        paymentGetResponse.amount( payment.getAmount() );
        paymentGetResponse.refund( payment.getRefund() );
        paymentGetResponse.createdDate( payment.getCreatedDate() );
        paymentGetResponse.lastModifiedDate( payment.getLastModifiedDate() );
        paymentGetResponse.paymentId( payment.getPaymentId() );

        return paymentGetResponse.build();
    }
}
