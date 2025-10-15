package org.egov.dx.gis.models;

public enum GeometryType {
    POINT("point"),
    POLYGON("polygon");

    private final String value;

    GeometryType(String value) {
        this.value = value;
    }

}

