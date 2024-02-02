package com.mcouture.customer;

import com.mcouture.exception.DuplicateResourceException;
import com.mcouture.exception.RequestValidationException;
import com.mcouture.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    private CustomerService underTest;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao,
                passwordEncoder,
                customerDTOMapper);
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        // Given
        long id = 10;
        Customer customer = new Customer(
                id,
                "Alex",
                "alex@gmail.com",
                "password", 19,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        // When
        CustomerDTO actual = underTest.getCustomer(id);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        // Given
        long id = 10;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "customer with [%s] not found".formatted(id)
                );
    }

    @Test
    void addCustomer() {
        // Given
        String email = "alex@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex",
                email,
                "password",
                19,
                Gender.MALE
        );

        String passwordHash = "$5554ml;f;lsd";

        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

        // When
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        // Given
        String email = "alex@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex",
                email,
                "password", 19,
                Gender.MALE
        );

        // When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        // Then
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void canDeleteCustomerById() {
        // Given
        long id = 10;

        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenIdExistsWhileDeletingACustomer() {
        // Given
        long id = 10;

        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with [%s] not found".formatted(id));

        // Then
        verify(customerDao, never()).deleteCustomerById(any());
    }

    @Test
    void canUpdateAllCustomerProperties() {
        // Given
        long customerId = 10;
        Customer customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                "password", 18,
                Gender.MALE);

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@yahoo.com";
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                "Alexandro",
                newEmail,
                20
        );

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(customerId, customerUpdateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNotNull();
        assertThat(capturedCustomer.getName()).isEqualTo(customerUpdateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerUpdateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customerUpdateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        long customerId = 10;
        Customer customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                "password", 18,
                Gender.MALE);

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                "Alexandro",
                null,
                null
        );

        // When
        underTest.updateCustomer(customerId, customerUpdateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customerUpdateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        long customerId = 10;
        String newEmail = "foo@mail.com";
        Customer customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                "password", 18,
                Gender.MALE);

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                null,
                newEmail,
                null
        );

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(customerId, customerUpdateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerUpdateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        long customerId = 10;
        Customer customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                "password", 18,
                Gender.MALE);

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                null,
                null,
                22
        );

        // When
        underTest.updateCustomer(customerId, customerUpdateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customerUpdateRequest.age());
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyWhileUpdatingCustomer() {
        // Given
        long customerId = 10;
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                "Alex",
                "alex@yahoo.com",
                20
        );

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, customerUpdateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with [%s] not found".formatted(customerId));

        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        // Given
        long customerId = 10;
        String duplicateEmail = "marc@mail.com";
        Customer customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                "password", 18,
                Gender.MALE);
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                "Alex",
                duplicateEmail,
                18
        );

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));
        when(customerDao.existsCustomerWithEmail(duplicateEmail)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, customerUpdateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        // Given
        long customerId = 10;
        Customer customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                "password", 18,
                Gender.MALE);
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, customerUpdateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        // Then
        verify(customerDao, never()).updateCustomer(any());
    }
}