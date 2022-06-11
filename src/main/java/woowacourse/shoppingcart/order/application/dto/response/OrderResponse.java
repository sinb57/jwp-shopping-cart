package woowacourse.shoppingcart.order.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long id;
    private List<OrderDetailResponse> orderDetails;
    private Long totalPrice;
    private LocalDateTime orderDate;

    public OrderResponse() {
    }

    public OrderResponse(Long id, List<OrderDetailResponse> orderDetails, Long totalPrice, LocalDateTime orderDate) {
        this.id = id;
        this.orderDetails = orderDetails;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
    }

    public Long getId() {
        return id;
    }

    public List<OrderDetailResponse> getOrderDetails() {
        return orderDetails;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
}
