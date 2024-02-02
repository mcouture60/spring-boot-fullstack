package com.mcouture.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Long id);
    void insertCustomer(Customer customer);
    boolean existsCustomerWithEmail(String email);
    void deleteCustomerById(Long customerId);
    boolean existsCustomerWithId(Long id);
    void updateCustomer(Customer update);
    Optional<Customer> selectUserByEmail(String email);
}
