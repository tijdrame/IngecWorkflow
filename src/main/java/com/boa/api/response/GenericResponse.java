package com.boa.api.response;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;

public class GenericResponse {

    protected String code;
    protected String description;
    protected Instant dateResponse;
    protected String responseReference;

    //private Map<String, Object> data = new HashMap<>();
    public GenericResponse() {
        this.responseReference = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDateResponse() {
        return this.dateResponse;
    }

    public void setDateResponse(Instant dateResponse) {
        this.dateResponse = dateResponse;
    }

    public GenericResponse(String code, String description, Instant dateResponse, String responseReference) {
        this.code = code;
        this.description = description;
        this.dateResponse = dateResponse;
        this.responseReference = responseReference;
    }

    public String getResponseReference() {
        return this.responseReference;
    }

    public void setResponseReference(String responseReference) {
        this.responseReference = responseReference;
    }

    public GenericResponse code(String code) {
        this.code = code;
        return this;
    }

    public GenericResponse description(String description) {
        this.description = description;
        return this;
    }

    public GenericResponse dateResponse(Instant dateResponse) {
        this.dateResponse = dateResponse;
        return this;
    }

    public GenericResponse responseReference(String responseReference) {
        this.responseReference = responseReference;
        return this;
    }

    /**
     * @return Map<String, Object> return the data
     */
    /*public Map<String, Object> getData() {
        return data;
    }*/

    /**
     * @param data the data to set
     */
    /*public void setData(Map<String, Object> data) {
        this.data = data;
    }*/

    @Override
    public String toString() {
        return (
            "{" +
            " code='" +
            code +
            "'" +
            ", description='" +
            description +
            "'" +
            ", dateResponse='" +
            dateResponse +
            "'" +
            ", responseReference='" +
            responseReference +
            "'" +
            //", data='" + data + "'" +
            "}"
        );
    }
}
