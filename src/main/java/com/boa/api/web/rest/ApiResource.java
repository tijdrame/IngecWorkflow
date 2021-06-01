package com.boa.api.web.rest;

import com.boa.api.request.AmtIgorRequest;
import com.boa.api.request.AmtIngecRequest;
import com.boa.api.request.CreditIgorRequest;
import com.boa.api.request.InfosProfilRequest;
import com.boa.api.request.LoanRequest;
import com.boa.api.request.OAuthRequest;
import com.boa.api.request.SearchClientRequest;
import com.boa.api.request.ValiderCreditIgRequest;
import com.boa.api.response.CreditIgorResponse;
import com.boa.api.response.InfosProfilResponse;
import com.boa.api.response.IngecResponse;
import com.boa.api.response.LoanResponse;
import com.boa.api.response.OAuthResponse;
import com.boa.api.response.SearchClientResponse;
import com.boa.api.response.ValiderCreditIgResponse;
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
        log.info("REST request to oAuth : [{}]", authRequest);
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

    @PostMapping("/getClients")
    public ResponseEntity<SearchClientResponse> getClients(@RequestBody SearchClientRequest clientRequest, HttpServletRequest request) {
        log.debug("REST request to getClients : [{}]", clientRequest);
        SearchClientResponse response = new SearchClientResponse();
        if (
            controleParam(clientRequest.getCountry()) ||
            controleParam(clientRequest.getClient()) ||
            controleParam(clientRequest.getAgence()) ||
            controleParam(clientRequest.getLangue())
        ) {
            Locale locale = defineLocale(clientRequest.getLangue());
            response.setCode(ICodeDescResponse.PARAM_ABSENT_CODE);
            response.setDateResponse(Instant.now());
            response.setDescription(messageSource.getMessage("param.oblig", null, locale));
            return ResponseEntity.badRequest().header("Authorization", request.getHeader("Authorization")).body(response);
        }
        response = apiService.getClients(clientRequest, request);
        return ResponseEntity.ok().header("Authorization", request.getHeader("Authorization")).body(response);
    }

    @PostMapping("/createLoan")
    public ResponseEntity<LoanResponse> createLoan(@RequestBody LoanRequest loanRequest, HttpServletRequest request) {
        log.info("REST request to createLoan : [{}]", loanRequest);
        LoanResponse response = new LoanResponse();
        if (
            controleParam(loanRequest.getCountry()) ||
            controleParam(loanRequest.getLoantype()) ||
            controleParam(loanRequest.getClient()) ||
            controleParam(loanRequest.getCurrentaccount()) ||
            controleParam(loanRequest.getCurrency()) ||
            controleParam(loanRequest.getLoanamount()) ||
            controleParam(loanRequest.getFirstpaymentday()) ||
            controleParam(loanRequest.getContractdate()) ||
            controleParam(loanRequest.getDuration()) ||
            controleParam(loanRequest.getDeferredmode()) ||
            controleParam(loanRequest.getCalculationtype()) ||
            controleParam(loanRequest.getReimbursementmode()) ||
            controleParam(loanRequest.getInterestperiodicity()) ||
            controleParam(loanRequest.getRatetype()) ||
            controleParam(loanRequest.getInteresttaxrate()) ||
            controleParam(loanRequest.getInteretsrecovery()) ||
            controleParam(loanRequest.getInsurancecalculationmode()) ||
            controleParam(loanRequest.getInterestsrecalculation()) ||
            controleParam(loanRequest.getDeferredrate()) ||
            controleParam(loanRequest.getMargin()) ||
            controleParam(loanRequest.getFilefeesamount()) ||
            controleParam(loanRequest.getFilefeestaxflag()) ||
            controleParam(loanRequest.getStampfeesamount()) ||
            controleParam(loanRequest.getVariousfeesamount()) ||
            controleParam(loanRequest.getVariousfeestaxflag()) ||
            controleParam(loanRequest.getLoanobjectif()) ||
            controleParam(loanRequest.getLoanbiendesc1()) ||
            controleParam(loanRequest.getLoanbiendesc2()) ||
            controleParam(loanRequest.getLoansimulation()) ||
            controleParam(loanRequest.getUsercode()) ||
            controleParam(loanRequest.getLangue())
        ) {
            Locale locale = defineLocale(loanRequest.getLangue());
            response.setCode(ICodeDescResponse.PARAM_ABSENT_CODE);
            response.setDateResponse(Instant.now());
            response.setDescription(messageSource.getMessage("param.oblig", null, locale));
            return ResponseEntity.badRequest().header("Authorization", request.getHeader("Authorization")).body(response);
        }
        response = apiService.createLoan(loanRequest, request);
        return ResponseEntity.ok().header("Authorization", request.getHeader("Authorization")).body(response);
    }

    @PostMapping("/createCreditIg")
    public ResponseEntity<CreditIgorResponse> createCreditIg(@RequestBody CreditIgorRequest creditRequest, HttpServletRequest request) {
        log.debug("REST request to createCreditIg : [{}]", creditRequest);
        CreditIgorResponse response = new CreditIgorResponse();
        if (
            controleParam(creditRequest.getCountry()) ||
            //controleParam(clientRequest.getClient()) ||
            //controleParam(clientRequest.getAgence()) ||
            controleParam(creditRequest.getLangue())
        ) {
            Locale locale = defineLocale(creditRequest.getLangue());
            response.setCode(ICodeDescResponse.PARAM_ABSENT_CODE);
            response.setDateResponse(Instant.now());
            response.setDescription(messageSource.getMessage("param.oblig", null, locale));
            return ResponseEntity.badRequest().header("Authorization", request.getHeader("Authorization")).body(response);
        }
        response = apiService.createCreditIg(creditRequest, request);
        return ResponseEntity.ok().header("Authorization", request.getHeader("Authorization")).body(response);
    }

    @PostMapping("/validerCreditIg")
    public ResponseEntity<ValiderCreditIgResponse> validerCreditIg(
        @RequestBody ValiderCreditIgRequest creditRequest,
        HttpServletRequest request
    ) {
        log.debug("REST request to validerCreditIg : [{}]", creditRequest);
        ValiderCreditIgResponse response = new ValiderCreditIgResponse();
        if (
            controleParam(creditRequest.getCountry()) ||
            //controleParam(clientRequest.getClient()) ||
            //controleParam(clientRequest.getAgence()) ||
            controleParam(creditRequest.getLangue())
        ) {
            Locale locale = defineLocale(creditRequest.getLangue());
            response.setCode(ICodeDescResponse.PARAM_ABSENT_CODE);
            response.setDateResponse(Instant.now());
            response.setDescription(messageSource.getMessage("param.oblig", null, locale));
            return ResponseEntity.badRequest().header("Authorization", request.getHeader("Authorization")).body(response);
        }
        response = apiService.validerCreditIg(creditRequest, request);
        return ResponseEntity.ok().header("Authorization", request.getHeader("Authorization")).body(response);
    }

    @PostMapping("/infosProfil")
    public ResponseEntity<InfosProfilResponse> infosProfil(@RequestBody InfosProfilRequest infosProfilRequest, HttpServletRequest request) {
        log.debug("REST request to infosProfil : [{}]", infosProfilRequest);
        InfosProfilResponse response = new InfosProfilResponse();
        if (
            controleParam(infosProfilRequest.getCountry()) ||
            controleParam(infosProfilRequest.getCodeProfil()) ||
            controleParam(infosProfilRequest.getLangue())
        ) {
            Locale locale = defineLocale(infosProfilRequest.getLangue());
            response.setCode(ICodeDescResponse.PARAM_ABSENT_CODE);
            response.setDateResponse(Instant.now());
            response.setDescription(messageSource.getMessage("param.oblig", null, locale));
            return ResponseEntity.badRequest().header("Authorization", request.getHeader("Authorization")).body(response);
        }
        response = apiService.infosProfil(infosProfilRequest, request);
        return ResponseEntity.ok().header("Authorization", request.getHeader("Authorization")).body(response);
    }

    @PostMapping("/amortissementIgor")
    public ResponseEntity<IngecResponse> amortissementIgor(@RequestBody AmtIgorRequest amtRequest, HttpServletRequest request) {
        log.debug("REST request to amortissementIgor : [{}]", amtRequest);
        IngecResponse response = new IngecResponse();
        if (controleParam(amtRequest.getCountry()) || controleParam(amtRequest.getLangue()) || controleParam(amtRequest.getNooper())) {
            Locale locale = defineLocale(amtRequest.getLangue());
            response.setCode(ICodeDescResponse.PARAM_ABSENT_CODE);
            response.setDateResponse(Instant.now());
            response.setDescription(messageSource.getMessage("param.oblig", null, locale));
            return ResponseEntity.badRequest().header("Authorization", request.getHeader("Authorization")).body(response);
        }
        response = apiService.amortissementIgor(amtRequest, request);
        return ResponseEntity.ok().header("Authorization", request.getHeader("Authorization")).body(response);
    }

    @PostMapping("/amortissementIngec")
    public ResponseEntity<IngecResponse> amortissementIngec(@RequestBody AmtIngecRequest amtRequest, HttpServletRequest request) {
        log.debug("REST request to amortissementIngec : [{}]", amtRequest);
        IngecResponse response = new IngecResponse();
        if (controleParam(amtRequest.getCountry()) || controleParam(amtRequest.getLangue()) || controleParam(amtRequest.getNooper())) {
            Locale locale = defineLocale(amtRequest.getLangue());
            response.setCode(ICodeDescResponse.PARAM_ABSENT_CODE);
            response.setDateResponse(Instant.now());
            response.setDescription(messageSource.getMessage("param.oblig", null, locale));
            return ResponseEntity.badRequest().header("Authorization", request.getHeader("Authorization")).body(response);
        }
        response = apiService.amortissementIngec(amtRequest, request);
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
