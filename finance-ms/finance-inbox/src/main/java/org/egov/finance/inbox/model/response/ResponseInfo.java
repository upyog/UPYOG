
package org.egov.finance.inbox.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class ResponseInfo {

    private String apiId;

    private String ver;

    private String ts;

    private String resMsgId;

    private String msgId;

    private String status;

    public String getApiId() {
        return apiId;
    }

    public String getVer() {
        return ver;
    }

    public String getTs() {
        return ts;
    }

    public String getResMsgId() {
        return resMsgId;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getStatus() {
        return status;
    }

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public void setResMsgId(String resMsgId) {
		this.resMsgId = resMsgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "ResponseInfo [apiId=" + apiId + ", ver=" + ver + ", ts=" + ts + ", resMsgId=" + resMsgId + ", msgId="
				+ msgId + ", status=" + status + "]";
	}
	 public enum StatusEnum {
	        SUCCESSFUL("SUCCESSFUL"),
	        
	        FAILED("FAILED");

	        private String value;

	        StatusEnum(String value) {
	          this.value = value;
	        }

	        @Override
	        @JsonValue
	        public String toString() {
	          return String.valueOf(value);
	        }

	        @JsonCreator
	        public static StatusEnum fromValue(String text) {
	          for (StatusEnum b : StatusEnum.values()) {
	            if (String.valueOf(b.value).equals(text)) {
	              return b;
	            }
	          }
	          return null;
	        }
	      }
    
}