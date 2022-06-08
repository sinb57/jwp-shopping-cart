package woowacourse.shoppingcart.customer.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import woowacourse.shoppingcart.customer.support.exception.CustomerException;
import woowacourse.shoppingcart.customer.support.exception.CustomerExceptionCode;

class NicknameTest {

    @DisplayName("닉네임은 최소 2자에서 최대 10자 이내여야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "", " ", "  ", "   ",
            "a", "1234567890a"
    })
    void validateNickname(final String nickname) {
        assertThatThrownBy(() -> new Nickname(nickname))
                .isInstanceOf(CustomerException.class)
                .extracting("exceptionCode")
                .isEqualTo(CustomerExceptionCode.INVALID_FORMAT_NICKNAME);
    }
}
