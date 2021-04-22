package com.boa.api.request;

import lombok.Data;

@Data
public class OAuthRequest {

    private String login;
    private String password;
    private String country;
    private String langue;
}
