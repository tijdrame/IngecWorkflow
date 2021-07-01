package com.boa.api.request;

import lombok.Data;

@Data
public class ListAutorisatioRequest {

    private String langue;
    private String country;
    private String agence;
}
