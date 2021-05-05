package com.boa.api.response;

import java.util.Map;
import lombok.Data;

@Data
public class SearchClientResponse extends GenericResponse {

    private Map<String, Object> dataGetClient;
}
