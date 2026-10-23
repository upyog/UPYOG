INSERT INTO eg_appconfig ( ID, KEY_NAME, DESCRIPTION, VERSION, MODULE )
VALUES (nextval('SEQ_EG_APPCONFIG'), 'DXF_TO_PDF_ENGINE', 'Aspose or Kabeja use for Dxf to pdf conversion',0,
        (select id from eg_module where name='Digit DCR'));

INSERT INTO eg_appconfig_values ( ID, KEY_ID, EFFECTIVE_FROM, VALUE, VERSION )
VALUES (nextval('SEQ_EG_APPCONFIG_VALUES'), (
    SELECT id FROM EG_APPCONFIG WHERE KEY_NAME='DXF_TO_PDF_ENGINE' and module= (select id from eg_module where name='Digit DCR')),
            current_date, 'ASPOSE' ,0);

INSERT INTO eg_appconfig ( ID, KEY_NAME, DESCRIPTION, VERSION, MODULE )
VALUES (nextval('SEQ_EG_APPCONFIG'), 'DXF_TO_PDF_USE_LEGACY_LAYER_SHEETS', 'Use of legacy multi-layer conversion or single page conversion',0,
        (select id from eg_module where name='Digit DCR'));

INSERT INTO eg_appconfig_values ( ID, KEY_ID, EFFECTIVE_FROM, VALUE, VERSION )
VALUES (nextval('SEQ_EG_APPCONFIG_VALUES'), (
    SELECT id FROM EG_APPCONFIG WHERE KEY_NAME='DXF_TO_PDF_USE_LEGACY_LAYER_SHEETS' and module= (select id from eg_module where name='Digit DCR')),
            current_date, 'NO' ,0);