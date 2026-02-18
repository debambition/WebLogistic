package com.parthi.logistic.payment.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.parthi.logistic.payment.model.Payment;

@Repository

public interface PaymentRepository extends CrudRepository<Payment, Integer>{
    Optional<Payment> findById(Integer id);
}
//public class PaymentRepository {
    // private static final Logger logger = LoggerFactory.getLogger(PaymentRepository.class);
    // private List<Payment> paymentList = new ArrayList<>();
    
    // public double getPaidAmount(String paymentId) {
    //     logger.info("Retriving paid amount for a product/s" + paymentId);
    //     Payment payment = paymentList.stream().filter(p -> p.getPaymentId().equals(paymentId)).findFirst().orElse(null);

    //     return payment != null ? payment.getPaidAmount() : 0.0;
    // }
//}
