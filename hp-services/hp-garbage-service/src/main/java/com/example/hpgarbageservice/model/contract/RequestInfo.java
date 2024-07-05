package com.example.hpgarbageservice.model.contract;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RequestInfo {

    private String apiId;

    private String ver;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "IST")
    private Date ts;

    private String action;

    private String did;

    private String key;

    private String msgId;

    private String authToken;
    
    private String requesterId;

    private String correlationId;

    private UserInfo userInfo;

//    public String getApiId() {
//        return apiId;
//    }
//
//    public void setApiId(final String apiId) {
//        this.apiId = apiId;
//    }
//
//    public String getVer() {
//        return ver;
//    }
//
//    public void setVer(final String ver) {
//        this.ver = ver;
//    }
//
//    public Date getTs() {
//        return ts;
//    }
//
//    public void setTs(final Date ts) {
//        this.ts = ts;
//    }
//
//    public String getAction() {
//        return action;
//    }
//
//    public void setAction(final String action) {
//        this.action = action;
//    }
//
//    public String getDid() {
//        return did;
//    }
//
//    public void setDid(final String did) {
//        this.did = did;
//    }
//	
//    public String getKey() {
//        return key;
//    }
//
//    public void setKey(final String key) {
//        this.key = key;
//    }
//
//    public String getMsgId() {
//        return msgId;
//    }
//
//    public void setMsgId(final String msgId) {
//        this.msgId = msgId;
//    }
//
//    public String getAuthToken() {
//        return authToken;
//    }
//
//    public void setAuthToken(final String authToken) {
//        this.authToken = authToken;
//    }
//
//    public String getRequesterId() {
//		return requesterId;
//	}
//
//	public void setRequesterId(String requesterId) {
//		this.requesterId = requesterId;
//	}
//
//	public String getCorrelationId() {
//        return correlationId;
//    }
//
//    public void setCorrelationId(final String correlationId) {
//        this.correlationId = correlationId;
//    }
//
//    public UserInfo getUserInfo() {
//        return userInfo;
//    }
//
//    public void setUserInfo(final UserInfo userInfo) {
//        this.userInfo = userInfo;
//    }

}
