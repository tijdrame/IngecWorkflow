package com.boa.api.request;

import lombok.Data;

@Data
public class ListSansAutorisatioRequest {

    private String langue;
    private String country;
    private String listncg;
    private String agence;
}
