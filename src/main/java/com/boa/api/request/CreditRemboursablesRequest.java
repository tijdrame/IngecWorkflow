package com.boa.api.request;

import lombok.Data;

@Data
public class CreditRemboursablesRequest {

    private String langue;
    private String country;
    private String client;
    private String ncg;
}
