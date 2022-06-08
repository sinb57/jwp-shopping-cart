package woowacourse.shoppingcart.order.support.jdbc.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class OrderDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OrderDao(final DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Long addOrders(final Long customerId, final LocalDateTime orderDateTime) {
        final String sql = "INSERT INTO orders (customer_id, order_date) VALUES (:customerId, :orderDateTime)";
        final SqlParameterSource parameters = new MapSqlParameterSource("customerId", customerId)
                .addValue("orderDateTime", Timestamp.valueOf(orderDateTime));
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, parameters, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public List<Long> findOrderIdsByCustomerId(final Long customerId) {
        final String sql = "SELECT id FROM orders WHERE customer_id = (:customerId) ";
        final SqlParameterSource parameters = new MapSqlParameterSource("customerId", customerId);
        return jdbcTemplate.query(sql, parameters, (rs, rowNum) -> rs.getLong("id"));
    }

    public LocalDateTime getOrderDateById(final Long id) {
        try {
            final String sql = "SELECT order_date FROM orders WHERE id = (:id) ";
            final SqlParameterSource parameters = new MapSqlParameterSource("id", id);
            return jdbcTemplate.queryForObject(sql, parameters, (rs, rowNum)
                            -> rs.getTimestamp("order_date")).toLocalDateTime();
        }
        catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException();
        }
    }

    public boolean existsById(final Long id) {
        final String query = "SELECT EXISTS(SELECT id FROM orders WHERE id=(:id)) as existable";
        final SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, parameters,
                (resultSet, rowNum) -> resultSet.getBoolean("existable")));
    }

    public boolean existsByIdAndCustomerId(final Long id, final Long customerId) {
        final String query = "SELECT EXISTS(SELECT id FROM orders WHERE id=(:id) and customer_id=(:customerId)) as existable";
        final SqlParameterSource parameters = new MapSqlParameterSource("id", id)
                .addValue("customerId", customerId);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, parameters,
                (resultSet, rowNum) -> resultSet.getBoolean("existable")));
    }
}