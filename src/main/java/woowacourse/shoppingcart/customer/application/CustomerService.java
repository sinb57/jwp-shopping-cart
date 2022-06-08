package woowacourse.shoppingcart.customer.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import woowacourse.shoppingcart.auth.support.exception.AuthException;
import woowacourse.shoppingcart.auth.support.exception.AuthExceptionCode;
import woowacourse.shoppingcart.customer.application.dto.request.CustomerPasswordUpdateRequest;
import woowacourse.shoppingcart.customer.application.dto.request.CustomerProfileUpdateRequest;
import woowacourse.shoppingcart.customer.application.dto.request.CustomerRegisterRequest;
import woowacourse.shoppingcart.customer.application.dto.request.CustomerRemoveRequest;
import woowacourse.shoppingcart.customer.application.dto.response.CustomerResponse;
import woowacourse.shoppingcart.customer.application.dto.response.CustomerUpdateResponse;
import woowacourse.shoppingcart.customer.domain.Customer;
import woowacourse.shoppingcart.customer.domain.Password;
import woowacourse.shoppingcart.customer.support.exception.CustomerException;
import woowacourse.shoppingcart.customer.support.exception.CustomerExceptionCode;
import woowacourse.shoppingcart.customer.support.jdbc.dao.CustomerDao;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(final CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Transactional
    public Long registerCustomer(final CustomerRegisterRequest customerRegisterRequest) {
        if (customerDao.existsByEmail(customerRegisterRequest.getEmail())) {
            throw new CustomerException(CustomerExceptionCode.ALREADY_EMAIL_EXIST);
        }

        final Customer customer = new Customer(customerRegisterRequest.getEmail(),
                customerRegisterRequest.getNickname(),
                customerRegisterRequest.getPassword());
        return customerDao.save(customer);
    }

    public CustomerResponse findById(final Long customerId) {
        final Customer customer = getById(customerId);
        return new CustomerResponse(customer.getEmail(), customer.getNickname());
    }

    private Customer getById(final Long customerId) {
        return customerDao.findById(customerId)
                .orElseThrow(() -> new AuthException(AuthExceptionCode.REQUIRED_AUTHORIZATION));
    }

    @Transactional
    public CustomerUpdateResponse updateCustomerProfile(final Long customerId,
                                                        final CustomerProfileUpdateRequest customerUpdateRequest) {
        final Customer customer = getById(customerId);
        customer.updateProfile(customerUpdateRequest.getNickname());
        customerDao.update(customer);
        return new CustomerUpdateResponse(customer.getNickname());
    }

    @Transactional
    public void updateCustomerPassword(final Long customerId,
                                       final CustomerPasswordUpdateRequest customerPasswordUpdateRequest) {
        final Customer customer = getById(customerId);
        validatePassword(customer, new Password(customerPasswordUpdateRequest.getPassword()));
        customer.updatePassword(customerPasswordUpdateRequest.getNewPassword());
        customerDao.update(customer);
    }

    @Transactional
    public void removeCustomer(final Long customerId,
                               final CustomerRemoveRequest customerRemoveRequest) {
        final Customer customer = getById(customerId);
        validatePassword(customer, customerRemoveRequest.getPassword());
        customerDao.deleteById(customerId);
    }

    private void validatePassword(final Customer customer, final Password password) {
        if (!customer.equalsPassword(password)) {
            throw new CustomerException(CustomerExceptionCode.MISMATCH_PASSWORD);
        }
    }

    private void validatePassword(final Customer customer, final String password) {
        if (!customer.equalsPassword(password)) {
            throw new CustomerException(CustomerExceptionCode.MISMATCH_PASSWORD);
        }
    }
}
