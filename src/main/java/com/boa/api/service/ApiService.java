package com.boa.api.service;

import com.boa.api.domain.ParamEndPoint;
import com.boa.api.domain.Tracking;
import com.boa.api.request.CreditIgorRequest;
import com.boa.api.request.LoanRequest;
import com.boa.api.request.OAuthRequest;
import com.boa.api.request.SearchClientRequest;
import com.boa.api.response.CreditIgorResponse;
import com.boa.api.response.LoanResponse;
import com.boa.api.response.OAuthResponse;
import com.boa.api.response.SearchClientResponse;
import com.boa.api.service.util.ICodeDescResponse;
import com.boa.api.service.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ApiService {

    private final TrackingService trackingService;
    private final UserService userService;
    private final Utils utils;
    private final ParamEndPointService endPointService;
    private final MessageSource messageSource;

    public OAuthResponse oAuth(OAuthRequest authRequest, HttpServletRequest request) {
        log.info("Enter in oAuth=== [{}]", authRequest);
        Locale locale = defineLocale(authRequest.getLangue());

        OAuthResponse genericResp = new OAuthResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("oAuth");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "oAuth",
                    genericResp.toString(),
                    authRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject()
                .put("username", authRequest.getLogin())
                .put("password", authRequest.getPassword())
                .put("pays", authRequest.getCountry())
                .toString();
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code envoi [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                // result = IOUtils.toString(conn.getInputStream(), "UTF-8");
                log.info("oAuth result ===== [{}]", result);
                obj = new JSONObject(result);
                obj = obj.getJSONObject("Rauthenticate").getJSONObject("response");
                log.info("ob to str =[{}]", obj.toString());
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);
                genericResp.setDataOauth(map);
                if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0100")) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("auth.success", null, locale));
                    genericResp.setDateResponse(Instant.now());
                    map.put("p_message", messageSource.getMessage("auth.success", null, locale));
                    genericResp.setDataOauth(map);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            authRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else {
                    String ret = getMsgEchecAuth(obj, locale);
                    map.put("p_message", ret);
                    genericResp.setDataOauth(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(ret);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            authRequest.toString(),
                            genericResp.getResponseReference()
                        );
                }
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("resp envoi error ===== [{}]", result);
                obj = new JSONObject(result);
                /*
                 * ObjectMapper mapper = new ObjectMapper(); Map<String, Object> map =
                 * mapper.readValue(result, Map.class);
                 */
                obj = new JSONObject(result);
                // genericResp.setData(map);
                genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                genericResp.setDateResponse(Instant.now());
                genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale));
                tracking =
                    createTracking(
                        tracking,
                        ICodeDescResponse.ECHEC_CODE,
                        request.getRequestURI(),
                        genericResp.toString(),
                        authRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in oAuth [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            // genericResp.setDescription(ICodeDescResponse.ECHEC_DESCRIPTION + " " +
            // e.getMessage());
            genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    authRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    public SearchClientResponse getClients(SearchClientRequest clientRequest, HttpServletRequest request) {
        log.info("Enter in getClients=== [{}]", clientRequest);
        Locale locale = defineLocale(clientRequest.getLangue());

        SearchClientResponse genericResp = new SearchClientResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("getClients");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "getClients",
                    genericResp.toString(),
                    clientRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject()
                .put("client", clientRequest.getClient())
                .put("pays", clientRequest.getCountry())
                .put("agence", clientRequest.getAgence())
                .toString();
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code envoi [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("getClients result ===== [{}]", result);
                obj = new JSONObject(result);
                obj = obj.getJSONObject("customers");

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);

                if (
                    obj.toString() != null &&
                    !obj.isNull("customer") &&
                    !obj.getJSONObject("customer").isNull("rcod") &&
                    obj.getJSONObject("customer").get("rcod").equals("0100")
                ) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("client.success", null, locale));
                    genericResp.setDateResponse(Instant.now());
                    obj = obj.getJSONObject("customer");
                    map = mapper.readValue(obj.toString(), Map.class);
                    map.put("rmessage", messageSource.getMessage("client.success", null, locale));
                    genericResp.setDataGetClient(map);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            clientRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else {
                    // obj = obj.getJSONObject("customer");
                    map.put("rmessage", messageSource.getMessage("client.error", null, locale));
                    genericResp.setDataGetClient(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(messageSource.getMessage("client.error", null, locale));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            clientRequest.toString(),
                            genericResp.getResponseReference()
                        );
                }
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("resp envoi error ===== [{}]", result);
                obj = new JSONObject(result);

                obj = new JSONObject(result);
                genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                genericResp.setDateResponse(Instant.now());
                genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale));
                tracking =
                    createTracking(
                        tracking,
                        ICodeDescResponse.ECHEC_CODE,
                        request.getRequestURI(),
                        genericResp.toString(),
                        clientRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in getclients [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    clientRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    private String getMsgEchecAuth(JSONObject obj, Locale locale) {
        log.info("in getMsgEchecAuth [{}]", obj.toString());
        try {
            if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0202")) {
                return messageSource.getMessage("auth.error.0202", null, locale);
            } else if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0203")) {
                return messageSource.getMessage("auth.error.0203", null, locale);
            } else if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0204")) {
                String msg = obj.getString("rmessage");
                final String[] params = new String[] { msg };
                return messageSource.getMessage("auth.error.0204", params, locale);
            } else if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0205")) {
                return messageSource.getMessage("auth.error.0205", null, locale);
            } else if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0206")) {
                return messageSource.getMessage("auth.error.0206", null, locale);
            } else if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0207")) {
                return messageSource.getMessage("auth.error.0207", null, locale);
            } else if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0208")) {
                return messageSource.getMessage("auth.error.0208", null, locale);
            } else if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0209")) {
                return messageSource.getMessage("auth.error.0209", null, locale);
            } else if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0210")) {
                return messageSource.getMessage("auth.error.0210", null, locale);
            } else if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0211")) {
                return messageSource.getMessage("auth.error.0211", null, locale);
            } else if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0101")) {
                return messageSource.getMessage("auth.error.0101", null, locale);
            } else if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0201")) {
                return messageSource.getMessage("auth.error.0201", null, locale);
            } else if (obj.toString() != null && !obj.isNull("p_code") && obj.get("p_code").equals("0200")) {
                return messageSource.getMessage("auth.error.0200", null, locale);
            }
        } catch (Exception e) {
            return messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage();
        }
        return messageSource.getMessage("auth.error.exep", null, locale);
    }

    public LoanResponse createLoan(LoanRequest loanRequest, HttpServletRequest request) {
        log.info("Enter in createLoan=== [{}]", loanRequest);
        Locale locale = defineLocale(loanRequest.getLangue());

        LoanResponse genericResp = new LoanResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("createLoan");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "createLoan",
                    genericResp.toString(),
                    loanRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject()
                .put("loanType", loanRequest.getLoantype())
                .put("client", loanRequest.getClient())
                .put("currentAccount", loanRequest.getCurrentaccount())
                .put("currency", loanRequest.getCurrency())
                .put("loanAmount", loanRequest.getLoanamount())
                .put("firstpaymentday", loanRequest.getFirstpaymentday())
                .put("contractdate", loanRequest.getContractdate())
                .put("duration", loanRequest.getDuration())
                .put("deferredmode", loanRequest.getDeferredmode())
                .put("endmonth_anniversary", loanRequest.getEndmonthAnniversary())
                .put("calculationtype", loanRequest.getCalculationtype())
                .put("reimbursementmode", loanRequest.getReimbursementmode())
                .put("interestperiodicity", loanRequest.getInterestperiodicity())
                .put("reimbursementperiodicity", loanRequest.getReimbursementperiodicity())
                .put("ratetype", loanRequest.getRatetype())
                .put("interesttaxrate", loanRequest.getInteresttaxrate())
                .put("advance_maturity", loanRequest.getAdvancematurity())
                .put("interetsrecovery", loanRequest.getInteretsrecovery())
                .put("insurancecalculationmode", loanRequest.getInsurancecalculationmode())
                .put("interestsrecalculation", loanRequest.getInterestsrecalculation())
                .put("deferredrate", loanRequest.getDeferredrate())
                .put("margin", loanRequest.getMargin())
                .put("filefeesamount", loanRequest.getFilefeesamount())
                .put("filefeestaxflag", loanRequest.getFilefeestaxflag())
                .put("stampfeesamount", loanRequest.getStampfeesamount())
                .put("variousfeesamount", loanRequest.getVariousfeesamount())
                .put("variousfeestaxflag", loanRequest.getVariousfeestaxflag())
                .put("insurancefeesamount", loanRequest.getInsurancefeesamount())
                .put("insurancefeestaxflag", loanRequest.getInsurancefeestaxflag())
                .put("loanobjectif", loanRequest.getLoanobjectif())
                .put("loanbiendesc1", loanRequest.getLoanbiendesc1())
                .put("loanbiendesc2", loanRequest.getLoanbiendesc2())
                .put("loansimulation", loanRequest.getLoansimulation())
                .put("usercode", loanRequest.getUsercode())
                .put("language", loanRequest.getLangue())
                .put("referenceIn", loanRequest.getReferenceIn())
                .put("sequence", loanRequest.getSequence())
                .put("pays", loanRequest.getCountry())
                .toString();
            log.info("to send [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code envoi [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("create loan result ===== [{}]", result);
                obj = new JSONObject(result);
                obj = obj.getJSONObject("customers"); //TODO

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);

                if (
                    obj.toString() != null &&
                    !obj.isNull("customer") &&
                    !obj.getJSONObject("customer").isNull("rcod") &&
                    obj.getJSONObject("customer").get("rcod").equals("0100")
                ) { //TODO
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("client.success", null, locale));
                    genericResp.setDateResponse(Instant.now());
                    obj = obj.getJSONObject("customer");
                    map = mapper.readValue(obj.toString(), Map.class);
                    map.put("rmessage", messageSource.getMessage("client.success", null, locale));
                    genericResp.setDataCreateLoan(map);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            loanRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else {
                    // obj = obj.getJSONObject("customer");
                    map.put("rmessage", messageSource.getMessage("client.error", null, locale));
                    genericResp.setDataCreateLoan(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(messageSource.getMessage("client.error", null, locale));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            loanRequest.toString(),
                            genericResp.getResponseReference()
                        );
                }
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("resp envoi error ===== [{}]", result);
                obj = new JSONObject(result);

                obj = new JSONObject(result);
                genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                genericResp.setDateResponse(Instant.now());
                genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale));
                tracking =
                    createTracking(
                        tracking,
                        ICodeDescResponse.ECHEC_CODE,
                        request.getRequestURI(),
                        genericResp.toString(),
                        loanRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in getclients [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    loanRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    public CreditIgorResponse createCreditIg(CreditIgorRequest creditRequest, HttpServletRequest request) {
        log.info("Enter in createCreditIg=== [{}]", creditRequest);
        Locale locale = defineLocale(creditRequest.getLangue());

        CreditIgorResponse genericResp = new CreditIgorResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("createCreditIg");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "getClients",
                    genericResp.toString(),
                    creditRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject()
                .put("p_pays", creditRequest.getCountry())
                .put("p_nooperingec", creditRequest.getNooperIngec())
                .put("p_njandb", creditRequest.getNjandb())
                .put("p_devise", creditRequest.getDevise())
                .put("p_datoper", creditRequest.getDateOper())
                .put("p_datdep", creditRequest.getDateDep())
                .put("p_daterpremremb", creditRequest.getDatePremRemb())
                .put("p_mtpret", creditRequest.getMontantPret())
                .put("p_valide", creditRequest.getValide())
                .put("p_txtax", creditRequest.getTxtax())
                .put("p_moddif", creditRequest.getModdif())
                .put("p_ncgprt", creditRequest.getNcgprt())
                .put("p_client", creditRequest.getClient())
                .put("p_compte", creditRequest.getCompte())
                .put("p_cptdeb", creditRequest.getCompteDeb())
                .put("p_perint", creditRequest.getPerint())
                .put("p_typrmb", creditRequest.getTyprmb())
                .put("p_intdif", creditRequest.getIntdif())
                .put("p_perrmb", creditRequest.getPerrmb())
                .put("p_xrecalc", creditRequest.getXrecalc())
                .put("p_xcomdec", creditRequest.getXcomdec())
                .put("p_typcalc", creditRequest.getTypcalc())
                .put("p_modrmb", creditRequest.getModrmb())
                .put("p_typtaux", creditRequest.getTypetaux())
                .put("p_xrecalcd", creditRequest.getXrecalcd())
                .put("p_modass", creditRequest.getModass())
                .put("p_xdos", creditRequest.getXdos())
                .put("p_fraisdoss", creditRequest.getFraisDossier())
                .put("p_txt1", creditRequest.getTxt1())
                .put("p_txavg", creditRequest.getTxavg())
                .put("p_xtaxdos", creditRequest.getXtaxdos())
                .put("p_mnttaxdos", creditRequest.getMnttaxdos())
                .put("p_txfongar", creditRequest.getTxfongar())
                .put("p_mntfongar", creditRequest.getMntfongar())
                .put("p_txfonass", creditRequest.getTxfonass())
                .put("p_mntfonass", creditRequest.getMntfonass())
                .put("p_txassini", creditRequest.getTxassini())
                .put("p_mntnetcli", creditRequest.getMntnetcli())
                .put("p_mtassur", creditRequest.getMtassur())
                .put("p_datrmbfm", creditRequest.getDatrmbfm())
                .put("p_cvmnttaxdos", creditRequest.getCvmnttaxdos())
                .put("p_cptass", creditRequest.getCptass())
                .put("p_duree", creditRequest.getDuree())
                .put("p_txannuel", creditRequest.getTxannuel())
                .put("p_datcontr", creditRequest.getDatcontr())
                .toString();
            log.info("request create credit ig [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code envoi [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("createCreditIg result ===== [{}]", result);
                obj = new JSONObject(result);
                obj = obj.getJSONObject("wf-credit");

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);

                if (
                    obj.toString() != null &&
                    !obj.isNull("response") &&
                    !obj.getJSONObject("response").isNull("p_code_retour") &&
                    obj.getJSONObject("response").get("p_code_retour").equals("0100")
                ) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("credit.ig.success", null, locale));
                    genericResp.setDateResponse(Instant.now());
                    obj = obj.getJSONObject("response");
                    map = mapper.readValue(obj.toString(), Map.class);
                    map.put("p_message_retour", messageSource.getMessage("credit.ig.success", null, locale));
                    genericResp.setDataCredit(map);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            creditRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else {
                    // obj = obj.getJSONObject("customer");
                    map.put("p_message_retour", messageSource.getMessage("credit.ig.error", null, locale));
                    genericResp.setDataCredit(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(messageSource.getMessage("credit.ig.error", null, locale));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            creditRequest.toString(),
                            genericResp.getResponseReference()
                        );
                }
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("resp credit ig error ===== [{}]", result);
                obj = new JSONObject(result);

                obj = new JSONObject(result);
                genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                genericResp.setDateResponse(Instant.now());
                genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale));
                tracking =
                    createTracking(
                        tracking,
                        ICodeDescResponse.ECHEC_CODE,
                        request.getRequestURI(),
                        genericResp.toString(),
                        creditRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in creditIg [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    creditRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    public Tracking createTracking(Tracking tracking, String code, String endPoint, String result, String req, String reqId) {
        tracking.setRequestId(reqId);
        tracking.setCodeResponse(code);
        tracking.setDateResponse(Instant.now());
        tracking.setEndPoint(endPoint);
        tracking.setLoginActeur(userService.getUserWithAuthorities().get().getLogin());
        tracking.setResponseTr(result);
        tracking.setRequestTr(req);
        return tracking;
    }

    private Locale defineLocale(String lang) {
        Locale locale = null;
        if (lang.equalsIgnoreCase("en")) locale = Locale.ENGLISH; else locale = Locale.FRANCE;
        return locale;
    }
}
