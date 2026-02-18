package com.parthi.logistic.customer.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.parthi.logistic.customer.model.Customer;


public interface CustomerRepository extends CrudRepository<Customer, String> {
    
    Optional<Customer> findById(String id);

    
}


