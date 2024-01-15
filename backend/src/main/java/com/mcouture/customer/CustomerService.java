package com.mcouture.customer;

import com.mcouture.exception.DuplicateResourceException;
import com.mcouture.exception.RequestValidationException;
import com.mcouture.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers(){
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Long id){
        return customerDao
                .selectCustomerById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "customer with [%s] not found".formatted(id)
                        )
                );
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest){
        // check if email exists
        String email = customerRegistrationRequest.email();
        if(customerDao.existsCustomerWithEmail(email)){
            throw new DuplicateResourceException("email already taken");
        }

        // add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                email,
                customerRegistrationRequest.age()
        );
        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Long customerId) {
        if(!customerDao.existsCustomerWithId(customerId)){
            throw new ResourceNotFoundException(
                    "customer with [%s] not found".formatted(customerId)
            );
        }

        customerDao.deleteCustomerById(customerId);
    }

    public void updateCustomer(
            Long customerId,
            CustomerUpdateRequest customerUpdateRequest){
        Customer customer = getCustomer(customerId);

        boolean changes = false;

        if(customerUpdateRequest.name() != null
                && !customerUpdateRequest.name().equals(customer.getName())){
            customer.setName(customerUpdateRequest.name());
            changes = true;
        }

        if(customerUpdateRequest.email() != null
                && !customerUpdateRequest.email().equals(customer.getEmail())){
            if(customerDao.existsCustomerWithEmail(customerUpdateRequest.email())){
                throw new DuplicateResourceException(
                        "email already taken"
                );
            }
            customer.setEmail(customerUpdateRequest.email());
            changes = true;
        }

        if(customerUpdateRequest.age() != null
                && !customerUpdateRequest.age().equals(customer.getAge())){
            customer.setAge(customerUpdateRequest.age());
            changes = true;
        }

        if(!changes){
            throw new RequestValidationException("no data changes found");
        }

        customerDao.updateCustomer(customer);
    }

}
