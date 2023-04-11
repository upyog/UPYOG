const envVariables = {
  MAX_NUMBER_PAGES: process.env.MAX_NUMBER_PAGES || 80,
  EGOV_LOCALISATION_HOST:
    process.env.EGOV_LOCALISATION_HOST || "https://ksmart-dev.lsgkerala.gov.in",
  EGOV_LOCALISATION_SEARCH:
    process.env.EGOV_LOCALISATION_SEARCH || "/localization/messages/v2/_search",
  EGOV_FILESTORE_SERVICE_HOST:
    process.env.EGOV_FILESTORE_SERVICE_HOST || "https://ksmart-dev.lsgkerala.gov.in",
  SERVER_PORT: process.env.SERVER_PORT || 8078,

  KAFKA_BROKER_HOST: process.env.KAFKA_BROKER_HOST || "localhost:9092",
  KAFKA_CREATE_JOB_TOPIC:
    process.env.KAFKA_CREATE_JOB_TOPIC || "PDF_GEN_CREATE",
  KAFKA_RECEIVE_CREATE_JOB_TOPIC:
    process.env.KAFKA_RECEIVE_CREATE_JOB_TOPIC || "PDF_GEN_RECEIVE",
  DATE_TIMEZONE: process.env.DATE_TIMEZONE || "Asia/Kolkata",
  DB_USER: process.env.DB_USER || "postgres",
  DB_PASSWORD: process.env.DB_PASSWORD || "ikm",
  DB_HOST: process.env.DB_HOST || "localhost",
  DB_NAME: process.env.DB_NAME || "birthdb2",
  DB_PORT: process.env.DB_PORT || 5432,
  EGOV_EXTERNAL_HOST: process.env.EGOV_EXTERNAL_HOST || "https://ksmart-dev.lsgkerala.gov.in/" ,
  DEFAULT_LOCALISATION_LOCALE:
    process.env.DEFAULT_LOCALISATION_LOCALE || "en_IN",
    DEFAULT_LOCALISATION_TENANT:
    process.env.DEFAULT_LOCALISATION_TENANT || "kl",
    DATA_CONFIG_URLS: process.env.DATA_CONFIG_URLS || "file:///home/lekshmy/Documents/BIRTH-KSMART/KSMART_BIRTH_NEW/KSMART/municipal-services/birth-services/pdf/data-config/birth-certificate-pdf.json",
    FORMAT_CONFIG_URLS: process.env.FORMAT_CONFIG_URLS || "file:///home/lekshmy/Documents/BIRTH-KSMART/KSMART_BIRTH_NEW/KSMART/municipal-services/birth-services/pdf/format-config/birth-certificate.json"
};
export default envVariables;
