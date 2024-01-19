package org.egov.pg.service.gateways.nttdata;

public class HeadDetails {
	private String version;
	private String	payMode;
	private String channel;
	private String api;
	private String stage;
	private String platform;
	private String source;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getPayMode() {
		return payMode;
	}
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getApi() {
		return api;
	}
	public void setApi(String api) {
		this.api = api;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	@Override
	public String toString() {
		return "HeadDetails [version=" + version + ", payMode=" + payMode + ", channel=" + channel + ", api=" + api
				+ ", stage=" + stage + ", platform=" + platform + ", source=" + source + ", getSource()=" + getSource()
				+ ", getVersion()=" + getVersion() + ", getPayMode()=" + getPayMode() + ", getChannel()=" + getChannel()
				+ ", getApi()=" + getApi() + ", getStage()=" + getStage() + ", getPlatform()=" + getPlatform()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}

}
