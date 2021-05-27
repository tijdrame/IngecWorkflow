package com.boa.api.response;

import java.util.Map;
import lombok.Data;

@Data
public class ValiderCreditIgResponse extends GenericResponse {

    private Map<String, Object> dataCredit;
}
