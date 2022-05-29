package woowacourse.shoppingcart.infrastructure.jdbc.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import woowacourse.shoppingcart.domain.Customer;

@JdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class CustomerDaoTest {

    private static final String CUSTOMER_EMAIL = "guest@woowa.com";
    private static final String CUSTOMER_NAME = "guest";
    private static final String CUSTOMER_PASSWORD = "qwe123!@#";

    private final CustomerDao customerDao;

    CustomerDaoTest(final DataSource dataSource) {
        customerDao = new CustomerDao(dataSource);
    }

    @DisplayName("회원을 저장한다.")
    @Test
    void save() {
        final Customer customer = new Customer(CUSTOMER_EMAIL, CUSTOMER_NAME, CUSTOMER_PASSWORD);
        final Long customerId = customerDao.save(customer);

        assertThat(customerId).isGreaterThan(0);
    }

    @DisplayName("회원을 조회한다.")
    @Test
    void findByEmail() {
        final Customer expected = new Customer(CUSTOMER_EMAIL, CUSTOMER_NAME, CUSTOMER_PASSWORD);
        customerDao.save(expected);
        final Optional<Customer> actual = customerDao.findByEmail(CUSTOMER_EMAIL);

        assertThat(actual).isPresent();
        assertThat(actual.get()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @DisplayName("회원을 삭제한다.")
    @Test
    void deleteById() {
        final Customer customer = new Customer(CUSTOMER_EMAIL, CUSTOMER_NAME, CUSTOMER_PASSWORD);
        final Long customerId = customerDao.save(customer);

        customerDao.deleteById(customerId);

        assertThat(customerDao.findByEmail(customer.getEmail())).isEmpty();
    }
}
