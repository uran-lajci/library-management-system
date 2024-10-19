package org.kodelabs.response;

import javax.ws.rs.core.Response;

public class CustomResponse {
    private String message;
    private int code;

    public CustomResponse() {
    }

    public CustomResponse(Response.StatusType statusInfo, String message) {
        this.message = message;
        this.code = statusInfo.getStatusCode();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
