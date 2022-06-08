package woowacourse.shoppingcart.order.application.dto.request;

import java.util.List;

public class OrderRequest {

    private List<Long> productIds;

    public OrderRequest() {
    }

    public OrderRequest(List<Long> productIds) {
        this.productIds = productIds;
    }

    public List<Long> getProductIds() {
        return productIds;
    }
}
