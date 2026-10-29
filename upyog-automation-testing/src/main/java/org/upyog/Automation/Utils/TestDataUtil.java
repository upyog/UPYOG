package org.upyog.Automation.Utils;

public class TestDataUtil {

    public static String getMobileNo() {
        return ConfigReader.get("mobile.number");
    }

    public static String getApplicantName() {
        return ConfigReader.get("applicant.name");
    }

    public static String getApplicantEmail() {
        return ConfigReader.get("applicant.email");
    }

    public static String getPincode() {
        return ConfigReader.get("address.pincode");
    }

    public static String getStreet() {
        return ConfigReader.get("address.street");
    }

    public static String getDoorNo() {
        return ConfigReader.get("address.doorNo");
    }

    public static String getBuilding() {
        return ConfigReader.get("address.building");
    }

    public static String getAddressLine1() {
        return ConfigReader.get("address.line1");
    }

    public static String getAddressLine2() {
        return ConfigReader.get("address.line2");
    }

    public static String getLandmark() {
        return ConfigReader.get("address.landmark");
    }

    public static String getProductQuantity() {
        return ConfigReader.get("product.quantity");
    }
}