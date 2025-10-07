/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */
package org.egov.finance.voucher.util;

public class ApplicationThreadLocals {
    private static ThreadLocal<String> domainName = new ThreadLocal<>();
    private static ThreadLocal<Long> userId = new ThreadLocal<>();
    private static ThreadLocal<String> tenantID = new ThreadLocal<>();
    private static ThreadLocal<String> cityCode = new ThreadLocal<>();
    private static ThreadLocal<String> cityName = new ThreadLocal<>();
    private static ThreadLocal<String> municipalityName = new ThreadLocal<>();
    private static ThreadLocal<String> domainURL = new ThreadLocal<>();
    private static ThreadLocal<String> ipAddress = new ThreadLocal<>();
    private static ThreadLocal<String> userTenantId = new ThreadLocal<>();
    private static ThreadLocal<String> userToken = new ThreadLocal<>();
    private static ThreadLocal<String> collectionVersion = new ThreadLocal<>();
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();
    
    private ApplicationThreadLocals() {
        //Not to be initialized
    }

    public static String getCityName() {
        return cityName.get();
    }

    public static void setCityName(String citiName) {
        cityName.set(citiName);
    }

    public static String getCityCode() {
        return cityCode.get();
    }

    public static void setCityCode(String citiCode) {
        cityCode.set(citiCode);
    }

    public static String getTenantID() {
        return tenantID.get();
    }

    public static void setTenantID(String tenantJNDI) {
        tenantID.set(tenantJNDI);
    }

    public static String getDomainName() {
        return domainName.get();
    }

    public static void setDomainName(String domName) {
        domainName.set(domName);
    }

    public static Long getUserId() {
        return userId.get();
    }

    public static void setUserId(Long userid) {
        userId.set(userid);
    }

    public static String getMunicipalityName() {
        return municipalityName.get();
    }

    public static void setMunicipalityName(String cityMunicipalityName) {
        municipalityName.set(cityMunicipalityName);
    }

    public static String getDomainURL() {
        return domainURL.get();
    }

    public static void setDomainURL(String domURL) {
        domainURL.set(domURL);
    }

    public static String getIPAddress() {
        return ipAddress.get();
    }

    public static void setIPAddress(String ipAddr) {
        ipAddress.set(ipAddr);
    }

    public static String getUserTenantId(){
    	return userTenantId.get();
    }
    
    public static void setUserTenantId(String tenantId){
    	userTenantId.set(tenantId);
    }
    
    public static String getUserToken(){
    	
    	return userToken.get();
    }
    
    public static void setUserToken(String token){
    	userToken.set(token);
    }
    
    public static String getCollectionVersion(){
        return collectionVersion.get();
    }
    
    public static void setCollectionVersion(String colVersion){
        collectionVersion.set(colVersion);
    }
    
    public static void setCurrentUserId(Long userId) {
        currentUserId.set(userId);
    }

    public static Long getCurrentUserId() {
        return currentUserId.get();
    }
    
    public static void clearValues() {
        domainName.remove();
        userId.remove();
        tenantID.remove();
        cityCode.remove();
        cityName.remove();
        municipalityName.remove();
        domainURL.remove();
        ipAddress.remove();
        userTenantId.remove();
        userToken.remove();
        collectionVersion.remove();
        currentUserId.remove();
    }
    

}

