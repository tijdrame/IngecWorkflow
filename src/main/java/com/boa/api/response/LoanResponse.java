package com.boa.api.response;

import java.util.Map;
import lombok.Data;

/**
 * LoanResponse
 */
@Data
public class LoanResponse extends GenericResponse {

    private Map<String, Object> dataCreateLoan;
}
