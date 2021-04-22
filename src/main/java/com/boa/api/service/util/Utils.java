package com.boa.api.service.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class Utils {

    private final Logger log = LoggerFactory.getLogger(Utils.class);

    /*
     * private final Random RANDOM = new SecureRandom(); public String getRandomId
     * (){ return RandomStringUtils.randomAlphanumeric(16); }
     */

    public HttpURLConnection doConnexion(String endPoint, String params, String appType, String appRetour, String token)
        throws IOException {
        OutputStream os = null;
        HttpURLConnection conn = null;
        try {
            log.info("end point wso2 calling== [{}]", endPoint);
            URL url = new URL(endPoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", appType);
            if (!StringUtils.isEmpty(appRetour)) conn.setRequestProperty("Accept", appRetour);

            if (!StringUtils.isEmpty(token)) conn.setRequestProperty("Authorization", token);

            // tracking.setRequestTr(jsonString);
            os = conn.getOutputStream();
            byte[] postDataBytes = params.getBytes();
            os.write(postDataBytes);
            os.flush();
        } catch (Exception e) {
            log.error("Error in doConn endpoint[{}], params [{}] & trace [{}]", e);
            return conn;
        }
        os.close();
        return conn;
    }
    /*public Map<String, Object> sendPost(String endPoint, String params, String appType, String appRetour) throws Exception {
        HttpPost post = new HttpPost(endPoint);
        // add request parameter, form parameters
        /*List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("login", "admin"));
        urlParameters.add(new BasicNameValuePair("password", "admin"));

        post.setEntity(new StringEntity(params));
        post.setHeader("Content-type", appType);
        if(!StringUtils.isEmpty(appRetour)) post.setHeader("Accept", appRetour);

        Map<String, Object> data = new HashMap<>();
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(post)) {
            //log.info("resp ===> [ {} ]", EntityUtils.toString(response.getEntity()));
            
            data.put("code", response.getStatusLine().getStatusCode());
            data.put("data", EntityUtils.toString(response.getEntity()));
            return data;
        }catch(Exception e){
            data.put("code", "404");
            data.put("data", "Exception occur.");
            return data;
        }
    }*/

    /*public ResponseEntity<String> doRestTemplate(String request, String endPoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        GenericResponse gResponse = new GenericResponse();
        gResponse.setCode("200");
        HttpEntity<GenericResponse> entity = new HttpEntity<GenericResponse>(gResponse, headers);

        return new RestTemplate().exchange(endPoint, HttpMethod.POST, entity, String.class);
    }

    class ResponseHttpGen {
        Integer status;
        Map<String, Object> data;        
    }*/
}
