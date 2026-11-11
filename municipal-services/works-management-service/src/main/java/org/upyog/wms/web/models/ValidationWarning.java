package org.upyog.wms.web.models;

/** Non-blocking observation returned with a successful save (e.g. similar component names). */
public record ValidationWarning(String code, String field, String message) {
}
