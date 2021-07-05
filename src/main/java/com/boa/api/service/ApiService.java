package com.boa.api.service;

import com.boa.api.domain.ParamEndPoint;
import com.boa.api.domain.Tracking;
import com.boa.api.request.AmtIgorRequest;
import com.boa.api.request.AmtIngecRequest;
import com.boa.api.request.AutorisationRequest;
import com.boa.api.request.ComptaTAERequest;
import com.boa.api.request.CreditIgorRequest;
import com.boa.api.request.CreditRemboursablesRequest;
import com.boa.api.request.InfosProfilRequest;
import com.boa.api.request.ListAutorisatioRequest;
import com.boa.api.request.ListSansAutorisatioRequest;
import com.boa.api.request.LoanRequest;
import com.boa.api.request.OAuthRequest;
import com.boa.api.request.RemboursementTAERequest;
import com.boa.api.request.SearchClientRequest;
import com.boa.api.request.ValiderCreditIgRequest;
import com.boa.api.response.CreditIgorResponse;
import com.boa.api.response.InfosProfilResponse;
import com.boa.api.response.IngecResponse;
import com.boa.api.response.LoanResponse;
import com.boa.api.response.OAuthResponse;
import com.boa.api.response.SearchClientResponse;
import com.boa.api.response.ValiderCreditIgResponse;
import com.boa.api.service.util.ICodeDescResponse;
import com.boa.api.service.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
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
                .put("loantype", loanRequest.getLoantype())
                .put("client", loanRequest.getClient())
                .put("currentaccount", loanRequest.getCurrentaccount())
                .put("currency", loanRequest.getCurrency())
                .put("loanamount", loanRequest.getLoanamount())
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
                    "createCreditIg",
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
                obj = obj.getJSONObject("wfcredit");

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

    public ValiderCreditIgResponse validerCreditIg(ValiderCreditIgRequest creditRequest, HttpServletRequest request) {
        log.info("Enter in validerCreditIg=== [{}]", creditRequest);
        Locale locale = defineLocale(creditRequest.getLangue());

        ValiderCreditIgResponse genericResp = new ValiderCreditIgResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("validerCreditIg");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "ValiderCreditIg",
                    genericResp.toString(),
                    creditRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject()
                .put("pays", creditRequest.getCountry())
                .put("pnoop", creditRequest.getNooper())
                .put("expl", creditRequest.getExpl())
                .toString();
            log.info("request validate credit ig [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code validate [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("ValiderCreditIg result ===== [{}]", result);
                obj = new JSONObject(result);
                obj = obj.getJSONObject("wfcredit");

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);

                if (
                    obj.toString() != null &&
                    !obj.isNull("response") &&
                    !obj.getJSONObject("response").isNull("p_code_retour") &&
                    obj.getJSONObject("response").get("p_code_retour").equals("0100")
                ) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("validate.ig.success", null, locale));
                    genericResp.setDateResponse(Instant.now());
                    obj = obj.getJSONObject("response");
                    map = mapper.readValue(obj.toString(), Map.class);
                    map.put("p_message_retour", messageSource.getMessage("validate.ig.success", null, locale));
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
                    map.put("p_message_retour", messageSource.getMessage("validate.ig.error", null, locale));
                    genericResp.setDataCredit(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(messageSource.getMessage("validate.ig.error", null, locale));
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
                log.info("resp validate ig error ===== [{}]", result);
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
            log.error("Exception in validateCreditIg [{}]", e);
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

    public InfosProfilResponse infosProfil(InfosProfilRequest infosProfilRequest, HttpServletRequest request) {
        log.info("Enter in infosProfil=== [{}]", infosProfilRequest);
        Locale locale = defineLocale(infosProfilRequest.getLangue());

        InfosProfilResponse genericResp = new InfosProfilResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("infosProfil");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "infosProfil",
                    genericResp.toString(),
                    infosProfilRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject()
                .put("pays", infosProfilRequest.getCountry())
                .put("codeprofil", infosProfilRequest.getCodeProfil())
                .toString();
            log.info("request infosProfil  [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code infosProfil [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("infosProfil result ===== [{}]", result);
                obj = new JSONObject(result);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);

                if (
                    obj.toString() != null && !obj.isNull("infosProfil")
                    /* &&
                    !obj.getJSONObject("response").isNull("p_code_retour") &&
                    obj.getJSONObject("response").get("p_code_retour").equals("0100")*/
                ) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.success", null, locale));
                    genericResp.setDateResponse(Instant.now());
                    obj = obj.getJSONObject("infosProfil");
                    map = mapper.readValue(obj.toString(), Map.class);
                    genericResp.setDataCredit(map);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            infosProfilRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else {
                    genericResp.setDataCredit(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.error", null, locale));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            infosProfilRequest.toString(),
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
                log.info("resp infosProfilRequest error ===== [{}]", result);
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
                        infosProfilRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in validateCreditIg [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    infosProfilRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    public IngecResponse amortissementIgor(AmtIgorRequest amtRequest, HttpServletRequest request) {
        log.info("Enter in amortissementIgor=== [{}]", amtRequest);
        Locale locale = defineLocale(amtRequest.getLangue());

        IngecResponse genericResp = new IngecResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("amortissementIgor");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "amortissementIgor",
                    genericResp.toString(),
                    amtRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject().put("pays", amtRequest.getCountry()).put("nooper", amtRequest.getNooper()).toString();
            log.info("request amortissementIgor  [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code amortissementIgor [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("amortissementIgor result ===== [{}]", result);
                obj = new JSONObject(result);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);

                if (
                    obj.toString() != null && !obj.isNull("wf-credit")
                    /* &&
                    !obj.getJSONObject("response").isNull("p_code_retour") &&
                    obj.getJSONObject("response").get("p_code_retour").equals("0100")*/
                ) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.success", null, locale));
                    genericResp.setDateResponse(Instant.now());
                    obj = obj.getJSONObject("wf-credit");
                    map = mapper.readValue(obj.toString(), Map.class);
                    genericResp.setData(map);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            amtRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else {
                    genericResp.setData(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.error", null, locale));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            amtRequest.toString(),
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
                log.info("resp amortissementIgor error ===== [{}]", result);
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
                        amtRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in amtortissementIgor [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    amtRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    public IngecResponse amortissementIngec(AmtIngecRequest amtRequest, HttpServletRequest request) {
        log.info("Enter in amortissementIngec=== [{}]", amtRequest);
        Locale locale = defineLocale(amtRequest.getLangue());

        IngecResponse genericResp = new IngecResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("amortissementIngec");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "amortissementIngec",
                    genericResp.toString(),
                    amtRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject().put("pays", amtRequest.getCountry()).put("nooper", amtRequest.getNooper()).toString();
            log.info("request amortissementIngec  [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code amortissementIngec [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("amortissementIngec result ===== [{}]", result);
                obj = new JSONObject(result);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);

                if (
                    obj.toString() != null && !obj.isNull("wf-credit")
                    /* &&
                    !obj.getJSONObject("response").isNull("p_code_retour") &&
                    obj.getJSONObject("response").get("p_code_retour").equals("0100")*/
                ) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.success", null, locale));
                    genericResp.setDateResponse(Instant.now());
                    obj = obj.getJSONObject("wf-credit");
                    map = mapper.readValue(obj.toString(), Map.class);
                    genericResp.setData(map);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            amtRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else {
                    genericResp.setData(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.error", null, locale));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            amtRequest.toString(),
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
                log.info("resp amortissementIngec error ===== [{}]", result);
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
                        amtRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in amtortissementIngec [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    amtRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    public IngecResponse autorisation(AutorisationRequest autRequest, HttpServletRequest request) {
        log.info("Enter in autorisation=== [{}]", autRequest);
        Locale locale = defineLocale(autRequest.getLangue());

        IngecResponse genericResp = new IngecResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("autorisation");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "autorisation",
                    genericResp.toString(),
                    autRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject()
                .put("pays", autRequest.getCountry())
                .put("iddossier", autRequest.getIdDossier())
                .put("coddossier", autRequest.getCodDossier())
                .put("expl", autRequest.getExpl())
                .put("client", autRequest.getClient())
                .put("ncg", autRequest.getNcg())
                .put("dev", autRequest.getDevise())
                .put("montant", autRequest.getMontant())
                .put("date_debut", autRequest.getDateDebut())
                .put("date_ech", autRequest.getDateEch())
                .put("cpt", autRequest.getCompte())
                .put("valide", autRequest.getValide())
                .toString();
            log.info("request autorisation  [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code autorisation [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("autorisation result ===== [{}]", result);
                obj = new JSONObject(result);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);

                if (
                    obj.toString() != null &&
                    !obj.isNull("wfcredit") &&
                    !obj.getJSONObject("wfcredit").isNull("response") &&
                    !obj.getJSONObject("wfcredit").getJSONObject("response").isNull("code_retour") &&
                    obj.getJSONObject("wfcredit").getJSONObject("response").get("code_retour").equals("0100")
                ) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("auto.decouv.ig.success", null, locale));
                    genericResp.setDateResponse(Instant.now());
                    obj = obj.getJSONObject("wfcredit").getJSONObject("response");
                    map = mapper.readValue(obj.toString(), Map.class);
                    map.put("message_retour", messageSource.getMessage("auto.decouv.ig.success", null, locale));
                    genericResp.setData(map);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            autRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else if (
                    obj.toString() != null &&
                    !obj.isNull("wfcredit") &&
                    !obj.getJSONObject("wfcredit").isNull("response") &&
                    !obj.getJSONObject("wfcredit").getJSONObject("response").isNull("code_retour") &&
                    !obj.getJSONObject("wfcredit").getJSONObject("response").get("code_retour").equals("0100")
                ) {
                    obj = obj.getJSONObject("wfcredit").getJSONObject("response");
                    map = mapper.readValue(obj.toString(), Map.class);
                    genericResp.setData(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.error", null, locale));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            autRequest.toString(),
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
                log.info("resp autorisation error ===== [{}]", result);
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
                        autRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in autorisation [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    autRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    public IngecResponse comptaTAE(ComptaTAERequest cRequest, HttpServletRequest request) {
        log.info("Enter in comptaTAE=== [{}]", cRequest);
        Locale locale = defineLocale(cRequest.getLangue());

        IngecResponse genericResp = new IngecResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("comptaTAE");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "comptaTAE",
                    genericResp.toString(),
                    cRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject()
                .put("pays", cRequest.getCountry())
                .put("code_assur", cRequest.getCodeAssur())
                .put("nooper", cRequest.getNoOper())
                .put("code_oper", cRequest.getCodeOper())
                .put("refrel", cRequest.getRefrel())
                .put("ncg_cli", cRequest.getNcgClient())
                .put("cpt_cli", cRequest.getCompteClient())
                .put("ncg_assur", cRequest.getNcgAssur())
                .put("ncg_fiscal", cRequest.getNcgFiscal())
                .put("cpt_assur", cRequest.getCptAssur())
                .put("ncg_assur_prdt", cRequest.getNcgAssurPrdt())
                .put("cpt_assur_prdt", cRequest.getCptAssurPrdt())
                .put("cpt_fiscal", cRequest.getCptFiscal())
                .put("mnt_assur", cRequest.getMntAssur())
                .put("mnt_fiscal", cRequest.getMntFiscal())
                .put("taux_cions", cRequest.getTauxCions())
                .put("mnt_pret", cRequest.getMntPret())
                .put("nom_cli", cRequest.getNomClient())
                .put("client", cRequest.getClient())
                .put("id_dossier", cRequest.getIdDossier())
                .toString();
            log.info("request comptaTAE  [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code comptaTAE [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("comptaTAE result ===== [{}]", result);
                obj = new JSONObject(result);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);

                if (
                    obj.toString() != null &&
                    !obj.isNull("wfcredit") &&
                    !obj.getJSONObject("wfcredit").isNull("comptatae") &&
                    !obj.getJSONObject("wfcredit").getJSONObject("comptatae").isNull("p_code_retour") &&
                    obj.getJSONObject("wfcredit").getJSONObject("comptatae").get("p_code_retour").equals("0100")
                ) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("comptaTAE.success", null, locale));
                    genericResp.setDateResponse(Instant.now());
                    obj = obj.getJSONObject("wfcredit").getJSONObject("comptatae");
                    map = mapper.readValue(obj.toString(), Map.class);
                    map.put("p_message_retour", messageSource.getMessage("comptaTAE.success", null, locale));
                    genericResp.setData(map);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            cRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else if (
                    obj.toString() != null &&
                    !obj.isNull("wfcredit") &&
                    !obj.getJSONObject("wfcredit").isNull("comptatae") &&
                    !obj.getJSONObject("wfcredit").getJSONObject("comptatae").isNull("p_code_retour") &&
                    !obj.getJSONObject("wfcredit").getJSONObject("comptatae").get("p_code_retour").equals("0100")
                ) {
                    obj = obj.getJSONObject("wfcredit").getJSONObject("comptatae");
                    map = mapper.readValue(obj.toString(), Map.class);
                    genericResp.setData(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.error", null, locale));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            cRequest.toString(),
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
                log.info("resp comptaTAE error ===== [{}]", result);
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
                        cRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in comptaTAE [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    cRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    public IngecResponse remboursementTAE(RemboursementTAERequest rRequest, HttpServletRequest request) {
        log.info("Enter in remboursementTAE=== [{}]", rRequest);
        Locale locale = defineLocale(rRequest.getLangue());

        IngecResponse genericResp = new IngecResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("remboursementTAE");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "autorisation",
                    genericResp.toString(),
                    rRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject()
                .put("pays", rRequest.getCountry())
                .put("dat_remb_ant", rRequest.getDateRembAnt())
                .put("nooper", rRequest.getNooper())
                .put("num_seq", rRequest.getNumSeq())
                .put("mnt_rest_du", rRequest.getMntRestDu())
                .put("mnt_ramb_ant", rRequest.getMntRembAnt())
                .put("mnt_penalite", rRequest.getMntPenalite())
                .put("mnt_taxe_pen", rRequest.getMntTaxePen())
                .put("taux_penalite", rRequest.getTauxPenalite())
                .toString();
            log.info("request remboursementTAE  [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code remboursementTAE [{}]", conn.getResponseCode());
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("remboursementTAE result ===== [{}]", result);
                obj = new JSONObject(result);

                if (
                    obj.toString() != null &&
                    !obj.isNull("wfcredit") &&
                    !obj.getJSONObject("wfcredit").isNull("rembantic") &&
                    !obj.getJSONObject("wfcredit").getJSONObject("rembantic").isNull("p_code_retour") &&
                    obj.getJSONObject("wfcredit").getJSONObject("rembantic").get("p_code_retour").equals("0100")
                ) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("remb.success", null, locale));
                    genericResp.setDateResponse(Instant.now());
                    obj = obj.getJSONObject("wfcredit").getJSONObject("rembantic");
                    map = mapper.readValue(obj.toString(), Map.class);
                    map.put("message_retour", messageSource.getMessage("remb.success", null, locale));
                    genericResp.setData(map);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            rRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else if (
                    obj.toString() != null &&
                    !obj.isNull("wfcredit") &&
                    !obj.getJSONObject("wfcredit").isNull("rembantic") &&
                    !obj.getJSONObject("wfcredit").getJSONObject("rembantic").isNull("p_code_retour") &&
                    !obj.getJSONObject("wfcredit").getJSONObject("rembantic").get("p_code_retour").equals("0100")
                ) {
                    obj = obj.getJSONObject("wfcredit").getJSONObject("rembantic");
                    //TODO construct resp basé sur le p_code_retour
                    map = mapper.readValue(obj.toString(), Map.class);
                    genericResp.setData(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.error", null, locale));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            rRequest.toString(),
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
                log.info("resp remboursementTAE error ===== [{}]", result);
                obj = new JSONObject(result);
                genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                genericResp.setDateResponse(Instant.now());
                //TODO call getMsgEchecRembousementTAE & param all msg
                /*String ret = getMsgEchecRembousementTAE(obj, locale);
                map.put("p_message", ret);
                genericResp.setData(map);
                genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                genericResp.setDateResponse(Instant.now());
                genericResp.setDescription(ret);*/
                genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale));

                tracking =
                    createTracking(
                        tracking,
                        ICodeDescResponse.ECHEC_CODE,
                        request.getRequestURI(),
                        genericResp.toString(),
                        rRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in remboursementTAE [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    rRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    private String getMsgEchecRembousementTAE(JSONObject obj, Locale locale) {
        log.info("in getMsgEchecRembousementTAE [{}]", obj.toString());
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

    public IngecResponse listAutorisation(ListAutorisatioRequest rRequest, HttpServletRequest request) {
        log.info("Enter in listAutorisation=== [{}]", rRequest);
        Locale locale = defineLocale(rRequest.getLangue());

        IngecResponse genericResp = new IngecResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("listAutorisation");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "listAutorisation",
                    genericResp.toString(),
                    rRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject().put("pays", rRequest.getCountry()).put("agence", rRequest.getAgence()).toString();
            log.info("request listAutorisation  [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code listAutorisation [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("listAutorisation result ===== [{}]", result);
                obj = new JSONObject(result);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);

                JSONObject jsObject = null;
                JSONArray jsArray = null;
                if (!obj.isNull("wfcredit")) {
                    if (obj.getJSONObject("wfcredit").get("listautorisation") instanceof JSONObject) jsObject =
                        obj.getJSONObject("wfcredit").getJSONObject("listautorisation"); else if (
                        obj.getJSONObject("wfcredit").get("listautorisation") instanceof JSONArray
                    ) jsArray = obj.getJSONObject("wfcredit").getJSONArray("listautorisation");
                }

                if (
                    obj.toString() != null &&
                    !obj.isNull("wfcredit") &&
                    ((jsArray != null && !jsArray.isEmpty()) || (jsObject != null && !jsObject.isEmpty()))
                    /* &&
                    !obj.getJSONObject("response").isNull("p_code_retour") &&
                    obj.getJSONObject("response").get("p_code_retour").equals("0100")*/
                ) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.success", null, locale));
                    genericResp.setDateResponse(Instant.now());

                    /*if(jsArray != null) {
                        //String str = jsArray.toString();
                    }  else if(jsObject != null) map = mapper.readValue(jsObject.toString(), Map.class);*/
                    map = mapper.readValue(obj.toString(), Map.class);

                    genericResp.setData(map);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            rRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else {
                    genericResp.setData(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.error", null, locale));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            rRequest.toString(),
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
                log.info("resp listAutorisation error ===== [{}]", result);
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
                        rRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in listAutorisation [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    rRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    public IngecResponse listSansAutorisation(ListSansAutorisatioRequest rRequest, HttpServletRequest request) {
        log.info("Enter in listSansAutorisation=== [{}]", rRequest);
        Locale locale = defineLocale(rRequest.getLangue());

        IngecResponse genericResp = new IngecResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("listSansAutorisation");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "listSansAutorisation",
                    genericResp.toString(),
                    rRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject()
                .put("pays", rRequest.getCountry())
                .put("agence", rRequest.getAgence())
                .put("listncg", rRequest.getListncg())
                .toString();
            log.info("request listSansAutorisation  [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code listSansAutorisation [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("listSansAutorisation result ===== [{}]", result);
                obj = new JSONObject(result);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);

                JSONObject jsObject = null;
                JSONArray jsArray = null;
                if (!obj.isNull("wfcredit")) {
                    if (obj.getJSONObject("wfcredit").get("listeSansAutorisation") instanceof JSONObject) jsObject =
                        obj.getJSONObject("wfcredit").getJSONObject("listeSansAutorisation"); else if (
                        obj.getJSONObject("wfcredit").get("listeSansAutorisation") instanceof JSONArray
                    ) jsArray = obj.getJSONObject("wfcredit").getJSONArray("listeSansAutorisation");
                }

                if (
                    obj.toString() != null &&
                    !obj.isNull("wfcredit") &&
                    ((jsArray != null && !jsArray.isEmpty()) || (jsObject != null && !jsObject.isEmpty()))
                    /* &&
                    !obj.getJSONObject("response").isNull("p_code_retour") &&
                    obj.getJSONObject("response").get("p_code_retour").equals("0100")*/
                ) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.success", null, locale));
                    genericResp.setDateResponse(Instant.now());

                    /*if(jsArray != null) {
                        //String str = jsArray.toString();
                    }  else if(jsObject != null) map = mapper.readValue(jsObject.toString(), Map.class);*/
                    map = mapper.readValue(obj.toString(), Map.class);

                    genericResp.setData(map);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            rRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else {
                    genericResp.setData(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.error", null, locale));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            rRequest.toString(),
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
                log.info("resp listSansAutorisation error ===== [{}]", result);
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
                        rRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in listSansAutorisation [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    rRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    public IngecResponse getCreditsRemboursables(CreditRemboursablesRequest rRequest, HttpServletRequest request) {
        log.info("Enter in getCreditsRemboursables=== [{}]", rRequest);
        Locale locale = defineLocale(rRequest.getLangue());

        IngecResponse genericResp = new IngecResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        Optional<ParamEndPoint> endPoint = endPointService.findByCodeParam("getCreditsRemboursables");
        if (!endPoint.isPresent()) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(messageSource.getMessage("service.absent", null, locale));
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "getCreditsRemboursables",
                    genericResp.toString(),
                    rRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject()
                .put("pays", rRequest.getCountry())
                .put("client", rRequest.getClient())
                .put("ncg", rRequest.getNcg())
                .toString();
            log.info("request getCreditsRemboursables  [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.get().getEndPoints(), jsonStr, "application/json", null, null);
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code getCreditsRemboursables [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("getCreditsRemboursables result ===== [{}]", result);
                obj = new JSONObject(result);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(obj.toString(), Map.class);

                JSONObject jsObject = null;
                JSONArray jsArray = null;
                if (!obj.isNull("wfcredit")) {
                    if (obj.getJSONObject("wfcredit").get("response") instanceof JSONObject) jsObject =
                        obj.getJSONObject("wfcredit").getJSONObject("response"); else if (
                        obj.getJSONObject("wfcredit").get("response") instanceof JSONArray
                    ) jsArray = obj.getJSONObject("wfcredit").getJSONArray("response");
                }

                if (
                    obj.toString() != null &&
                    !obj.isNull("wfcredit") &&
                    ((jsArray != null && !jsArray.isEmpty()) || (jsObject != null && !jsObject.isEmpty()))
                    /* &&
                    !obj.getJSONObject("response").isNull("p_code_retour") &&
                    obj.getJSONObject("response").get("p_code_retour").equals("0100")*/
                ) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.success", null, locale));
                    genericResp.setDateResponse(Instant.now());

                    /*if(jsArray != null) {
                        //String str = jsArray.toString();
                    }  else if(jsObject != null) map = mapper.readValue(jsObject.toString(), Map.class);*/
                    map = mapper.readValue(obj.toString(), Map.class);

                    genericResp.setData(map);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            rRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else {
                    genericResp.setData(map);
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(messageSource.getMessage("infosProfil.ig.error", null, locale));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            genericResp.toString(),
                            rRequest.toString(),
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
                log.info("resp getCreditsRemboursables error ===== [{}]", result);
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
                        rRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in getCreditsRemboursables [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(messageSource.getMessage("auth.error.exep", null, locale) + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    rRequest.toString(),
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
