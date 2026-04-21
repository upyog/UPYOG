package org.egov.commons.mdms;

public enum LayerErrorType {

    INVALID_LAYER("INVALID_LAYER_%s",
            "Layer '%s' is not defined in the standard. Please rename the layer according to guidelines."),

    INVALID_COLOR("INVALID_COLOR_%s",
            "Color mismatch on layer '%s'. The standard color code is %d, but found %d.");

    private final String keyFormat;
    private final String messageFormat;

    LayerErrorType(String keyFormat, String messageFormat) {
        this.keyFormat = keyFormat;
        this.messageFormat = messageFormat;
    }

    public String getKey(String layerName) {
        return String.format(keyFormat, layerName);
    }

    public String getMessage(Object... args) {
        return String.format(messageFormat, args);
    }
}
