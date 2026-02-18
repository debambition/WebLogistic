package com.parthi.logistic.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.parthi.logistic.payment.model.Payment;
import com.parthi.logistic.payment.service.PaymentService;


import jakarta.validation.Valid;

@RestController
public class PaymentController {
    @Autowired
    PaymentService paymentService;

/**
 * This API shows the details of a payment whose payment id is entered
 * @param pathVariable is used to add id to  the path
 * @return Returns a response entity ok with payment details if found
 * @throws Exception 
 */
    @GetMapping("/getpayment/paymentid/{productId}")
    public ResponseEntity<?> getPaidAmount(@PathVariable Integer productId) throws Exception {
        return ResponseEntity.ok(paymentService.getPaidAmount(productId));
    }


    /**
     * @param payment
     * @return
     * @throws Exception 
     */
    @PostMapping("/addpayment")
        public ResponseEntity<?> submitPaymentDeatil(@Valid @RequestBody Payment payment, BindingResult result) throws Exception{
            if(result.hasErrors()){
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }
            return ResponseEntity.ok(paymentService.addPayment(payment));
        }

}

