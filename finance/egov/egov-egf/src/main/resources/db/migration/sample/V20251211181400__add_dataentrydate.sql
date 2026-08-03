UPDATE eg_appconfig_values
    SET value = '31-Mar-2099'
    WHERE key_id = (SELECT id from eg_appconfig WHERE key_name = 'DataEntryCutOffDate');