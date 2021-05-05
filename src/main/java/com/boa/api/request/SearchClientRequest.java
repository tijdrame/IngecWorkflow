package com.boa.api.request;

import lombok.Data;

@Data
public class SearchClientRequest {

    private String client;
    private String country;
    private String agence;
    private String langue;
}
