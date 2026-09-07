export const BUSINESS_SERVICES = {
  EST: "est-services",
  GC: "garbage-service",
  NDC: "NDC",
  CHB: "chb-services",
  ADS: "adv-services",
  SV: "sv-services",
  PTR: "pet-services",
  WATER_TANKER: "request-service.water_tanker",
  MOBILE_TOILET: "request-service.mobile_toilet",
  TREE_PRUNING: "request-service.tree_pruning",
  PT: "PT",
  TL: "TL",
  WS: "WS",
  SW: "SW",
  FSM: "FSM",
  BPA: "BPA",
};

export const RECEIPT_KEYS = {
  EST: "est-service-receipt",
  GC: "garbage-service-receipt",
  NDC: "ndc-receipt",
  CHB: "chbservice-receipt",
  ADS: "advservice-receipt",
  SV: "svcertificate",
  PTR: "petservice-receipt",
  WATER_TANKER: "request-service.water_tanker-receipt",
  MOBILE_TOILET: "request-service.mobile_toilet-receipt",
  TREE_PRUNING: "request-service.tree_pruning-receipt",
};

export const EST_BUSINESS_SERVICE = "est-services";
export const EST_RECEIPT_KEY = "est-service-receipt";

/**
 * List of business services that handle custom on-demand receipt generation
 * and are excluded from default background receipt generation / default print buttons.
 */
export const CUSTOM_BUSINESS_SERVICES = [
  BUSINESS_SERVICES.CHB,
  BUSINESS_SERVICES.ADS,
  BUSINESS_SERVICES.SV,
  BUSINESS_SERVICES.PTR,
  BUSINESS_SERVICES.WATER_TANKER,
  BUSINESS_SERVICES.MOBILE_TOILET,
  BUSINESS_SERVICES.TREE_PRUNING,
  BUSINESS_SERVICES.NDC,
  BUSINESS_SERVICES.EST,
  BUSINESS_SERVICES.GC,
];
