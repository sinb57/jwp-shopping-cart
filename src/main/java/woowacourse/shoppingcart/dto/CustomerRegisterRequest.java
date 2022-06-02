package woowacourse.shoppingcart.dto;

public class CustomerRegisterRequest {

    private String email;
    private String nickname;
    private String password;

    public CustomerRegisterRequest() {
    }

    public CustomerRegisterRequest(final String email, final String nickname, final String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }
}
