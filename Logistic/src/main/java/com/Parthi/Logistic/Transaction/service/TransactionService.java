package com.parthi.logistic.transaction.service;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parthi.logistic.customer.model.Customer;
import com.parthi.logistic.customer.repository.CustomerRepository;
import com.parthi.logistic.payment.model.Payment;
import com.parthi.logistic.product.Repository.ProductRepo;
import com.parthi.logistic.product.Service.ProductService;
import com.parthi.logistic.product.model.Product;
import com.parthi.logistic.transaction.model.Transaction;
import com.parthi.logistic.transaction.repository.TransactionRepository;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    TransactionRepository transactionRepository;
    CustomerRepository customerRepository;
    ProductRepo productRepo;

    /**
     * This method take a Transaction details and add it to the database.
     * 
     * @param Transaction that need to be added to the database
     * @return Returns the Response msg of the user
     * @throws Exception
     */

    public String addTransaction(Transaction transaction) throws Exception {
        String response = "";
        try {
            if (transaction.getTransactionDate().isAfter(LocalDate.now()))
                throw new Exception("Transaaction date can't be a future date");

            transactionRepository.save(transaction);
            response = "Transaction added successfully";
        } catch (Exception e) {
            response = e.getLocalizedMessage();
            logger.error("Exception occured while adding Transaction: " + e.getLocalizedMessage());
            throw e;
        }
        return response;

    }

    /**
     * This method take a a transaction id and retrieve the transaction from the database.
     * 
     * @param int id with which the transaction needs to be found
     * @return Returns a transaction
     * @throws RuntimeException if the transaction is unavailable in the database.
     */

    public Transaction getTransaction(int id) {
        try {
            Optional<Transaction> transaction = transactionRepository.findById(id);

            if (transaction.isPresent()) {
                logger.info("Transaction with id:  " + id + "found in the list");
            } else {
                logger.warn("Transaction with id: " + id + "not found");

            }
            return transaction.get();
        } catch (RuntimeException e) {
            logger.error("Exception occured while retrieveing transaction: " + e.getLocalizedMessage());
            return null;
        }
    }
    public String addSaleTransaction(Transaction transaction, Customer tmpCustomer, double sellingPrice) throws Exception {
		String response = "";
		Customer customer = null;
		try {
			if (transaction.getTransactionDate().isAfter(LocalDate.now()))
				throw new Exception("Transaction date cannot be a future date");


			if (transaction.getTxnCategory().equalsIgnoreCase(TransactionCategory.SALES.toString())) {

				// add customer if the customer is new or update the deposite if required.
				customer = customerRepository.getCustomer(tmpCustomer.getCustomerNumber());
				if (customer == null) {
					customerRepository.addCustomer(tmpCustomer);
					customer = tmpCustomer;
				} else {
					customer = tmpCustomer;

					// Enter a transaction with debit the deposite amount in transaction
					if (customer.getDepositeAmount() > 0) {
						Transaction withdraw_deposite = new Transaction();
						if (transaction.getAmount() > customer.getDepositeAmount()) {

							withdraw_deposite.setAmount(customer.getDepositeAmount());
							customer.setDepositeAmount(0);
						} else {
							withdraw_deposite.setAmount(transaction.getAmount());
							customer.setDepositeAmount(customer.getDepositeAmount() - transaction.getAmount());
						}
						withdraw_deposite.setTransactionDate(LocalDate.now());
						withdraw_deposite.setDescription("Withdraw amount from deposte for " + transaction.getParticular() + " by " + customer.getName());
						withdraw_deposite.setPaymentMode(PaymentMode.ONLINE.toString());
						withdraw_deposite.setTxnCategory(TransactionCategory.DEPOSITE.toString());
						withdraw_deposite.setTxnType(TransactionType.DEBIT.toString());
						addTransaction(withdraw_deposite);
					}
					customerRepo.updateCustomer(customer);
				}

				// Update the product status to sold, scustomerelling price, checkout date
				ProductService productService = new ProductService();
				productService.updateSoldProduct(transaction.getParticular(), transaction.getTransactionDate(), sellingPrice);



			} else {
				customer = tmpCustomer;
			}
			// make a entry in payment
			Payment payment = new Payment();
			payment.setPaymentDate(transaction.getTransactionDate());
			payment.setPaidAmount(transaction.getAmount());
			payment.setCustomerNumber(customer);
			Product product = productRepo.getProduct(transaction.getParticular());
			payment.setProductId(product);
			paymentService.addPayment(payment);


			response = transactionRepo.addTransaction(transaction);

		} catch (Exception e) {
			response = e.getLocalizedMessage();
			logger.error("Exception occured while adding Transaction: " + e.getLocalizedMessage());
			throw e;
		}
		return response;
	}
}