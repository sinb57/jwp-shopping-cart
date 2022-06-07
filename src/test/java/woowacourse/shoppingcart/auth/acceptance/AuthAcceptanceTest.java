package woowacourse.shoppingcart.auth.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import woowacourse.shoppingcart.auth.dto.ExceptionResponse;
import woowacourse.shoppingcart.auth.dto.TokenRequest;
import woowacourse.shoppingcart.auth.dto.TokenResponse;
import woowacourse.shoppingcart.customer.dto.CustomerRegisterRequest;
import woowacourse.support.acceptance.AcceptanceTest;
import woowacourse.support.acceptance.RequestHandler;

@DisplayName("인증 관련 기능")
public class AuthAcceptanceTest extends AcceptanceTest {

    private static final String CUSTOMER_EMAIL = "guest@woowa.com";
    private static final String CUSTOMER_NAME = "guest";
    private static final String CUSTOMER_PASSWORD = "qwer1234!@#$";

    @DisplayName("Bearer Auth 로그인 성공")
    @Test
    void loginSuccess() {
        // given
        RequestHandler.postRequest("/customers", new CustomerRegisterRequest(
                CUSTOMER_EMAIL, CUSTOMER_NAME, CUSTOMER_PASSWORD));

        // when
        final ExtractableResponse<Response> response = RequestHandler.postRequest(
                "/auth/login", new TokenRequest(CUSTOMER_EMAIL, CUSTOMER_PASSWORD));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final TokenResponse tokenResponse = response.jsonPath().getObject(".", TokenResponse.class);
        assertThat(tokenResponse.getNickname()).isEqualTo(CUSTOMER_NAME);
    }

    @DisplayName("로그인 실패 - 이메일이 존재하지 않는 경우")
    @Test
    void loginFailedWithNoSuchEmail() {
        // given
        RequestHandler.postRequest("/customers", new CustomerRegisterRequest(
                CUSTOMER_EMAIL, CUSTOMER_NAME, CUSTOMER_PASSWORD));

        // when
        final ExtractableResponse<Response> response = RequestHandler.postRequest(
                "/auth/login", new TokenRequest("noGuest@woowa.com", CUSTOMER_PASSWORD));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        final ExceptionResponse exceptionResponse = response.jsonPath().getObject(".", ExceptionResponse.class);
        assertThat(exceptionResponse.getMessage()).isEqualTo("이메일에 해당하는 회원이 존재하지 않거나 비밀번호가 일치하지 않습니다.");
    }

    @DisplayName("로그인 실패 - 비밀번호가 일치하지 않는 경우")
    @Test
    void loginFailedWithWrongPassword() {
        // given
        RequestHandler.postRequest("/customers", new CustomerRegisterRequest(
                CUSTOMER_EMAIL, CUSTOMER_NAME, CUSTOMER_PASSWORD));

        // when
        final ExtractableResponse<Response> response = RequestHandler.postRequest(
                "/auth/login", new TokenRequest(CUSTOMER_EMAIL, "wrongqwe123!@#"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        final ExceptionResponse exceptionResponse = response.jsonPath().getObject(".", ExceptionResponse.class);
        assertThat(exceptionResponse.getMessage()).isEqualTo("이메일에 해당하는 회원이 존재하지 않거나 비밀번호가 일치하지 않습니다.");
    }

    @DisplayName("인증 실패 - 토큰의 기간이 만료된 경우")
    @Test
    void authFailWhenTokenExpired() {
        // given
        final String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjUzOTkzMzM3LCJleHAiOjE2NTM5OTMzMzd9."
                + "rlmlgHw_zjq7eY4FAgBU3Fx2Pq9rUgSdE9le9kpwd4w";

        RequestHandler.postRequest("/customers", new CustomerRegisterRequest(
                CUSTOMER_EMAIL, CUSTOMER_NAME, CUSTOMER_PASSWORD));

        // when
        RequestHandler.postRequest("/auth/login", new TokenRequest(CUSTOMER_EMAIL, CUSTOMER_PASSWORD));
        final ExtractableResponse<Response> wrongResponse = RequestHandler.postRequest("/auth/logout", expiredToken);

        // then
        assertThat(wrongResponse.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("인증 실패 - 토큰의 무결성이 깨진 경우")
    @Test
    void authFailWhenTokenTampered() {
        // given
        final String tamperedToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiaWF0IjoxNjUzOTkzMzM3LCJleHAiOjE2NTM5OTMzMzd9."
                + "rlmlgHw_zjq7eY4FAgBU3Fx2Pq9rUgSdE9le9kpwd4w";

        RequestHandler.postRequest("/customers", new CustomerRegisterRequest(
                CUSTOMER_EMAIL, CUSTOMER_NAME, CUSTOMER_PASSWORD));

        // when
        RequestHandler.postRequest("/auth/login", new TokenRequest(CUSTOMER_EMAIL, CUSTOMER_PASSWORD));
        final ExtractableResponse<Response> wrongResponse = RequestHandler.postRequest("/auth/logout", tamperedToken);

        // then
        assertThat(wrongResponse.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
