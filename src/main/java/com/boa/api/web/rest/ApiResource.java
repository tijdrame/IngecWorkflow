package com.boa.api.web.rest;

import com.boa.api.request.OAuthRequest;
import com.boa.api.response.OAuthResponse;
import com.boa.api.service.ApiService;
import com.boa.api.service.util.ICodeDescResponse;
import java.time.Instant;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class ApiResource {

    private final ApiService apiService;
    private final MessageSource messageSource;

    @PostMapping("/oAuth")
    public ResponseEntity<OAuthResponse> oAuth(@RequestBody OAuthRequest authRequest, HttpServletRequest request) {
        log.debug("REST request to oAuth : [{}]", authRequest);
        OAuthResponse response = new OAuthResponse();
        if (
            controleParam(authRequest.getCountry()) ||
            controleParam(authRequest.getLogin()) ||
            controleParam(authRequest.getPassword()) ||
            controleParam(authRequest.getLangue())
        ) {
            Locale locale = defineLocale(authRequest.getLangue());
            response.setCode(ICodeDescResponse.PARAM_ABSENT_CODE);
            response.setDateResponse(Instant.now());
            response.setDescription(messageSource.getMessage("param.oblig", null, locale));
            return ResponseEntity.badRequest().header("Authorization", request.getHeader("Authorization")).body(response);
        }
        response = apiService.oAuth(authRequest, request);
        return ResponseEntity.ok().header("Authorization", request.getHeader("Authorization")).body(response);
    }

    private Boolean controleParam(Object param) {
        Boolean flag = false;
        if (StringUtils.isEmpty(param)) flag = true;
        return flag;
    }

    private Locale defineLocale(String lang) {
        Locale locale = null;
        if (StringUtils.isEmpty(lang)) lang = "en";
        if (lang.equalsIgnoreCase("en")) locale = Locale.ENGLISH; else locale = Locale.FRANCE;
        return locale;
    }
}
