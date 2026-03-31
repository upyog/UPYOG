DO
$$
DECLARE
    schema_list text[] := ARRAY[
        'abohar','adampur','ahmedgarh','ajnala','alawalpur','amargarh','amloh','amritsarregion',
        'anandpursahib','arniwalasheikhsubhan','bababakalasahib','badhnikalan','baghapurana','balachaur',
        'balianwala','banga','banur','bareta','bariwala','barnala','bassipathana','batala','bathinda',
        'bathindaregion','begowal','bhadaur','bhadson','bhagtabhaika','bhairoopa','bhawanigarh','bhikhi',
        'bhikhiwind','bhogpur','bhuchomandi','bhulath','bilga','boha','budhlada','chamkaursahib','chauke',
        'cheema','dasua','derababananak','derabassi','devigarh','dhanaula','dharamkot','dhariwal','dhilwan',
        'dhuri','dinanagar','dirba','doraha','faridkot','fatehgarhchurian','fatehgarhpanjtoor','fazilka',
        'firozpur','firozpurregion','gardhiwala','garhshankar','ghagga','ghanaur','gharuan','gidderbaha',
        'gobindgarh','goniana','goraya','gurdaspur','guruharsahai','handiaya','hariana','hoshiarpur',
        'jagraon','jaitu','jalalabad','jalandhar','jandiala','joga','kapurthala',
        'kartarpur','khamanon','khanauri','khanna','kharar','khemkaran','kiratpursahib','kotfatta',
        'kotissekhan','kotshamir','kothaguru','kotkapura','kurali','lalru','lehramohabbat','lehragaga',
        'lohiankhass','longowal','ludhiana','machhiwara','mahilpur','majitha','makhu',
        'malerkotla','mallanwalakhass','maloud','malout','maluka','mamdot','mandikalan','mansa','maur',
        'mehatpur','mehraj','moga','moonak','morinda','mudki','mukerian','mullanpurdakha','nabha','nadala',
        'nakodar','nangal','narotjaimalsingh','nathana','nawanshahr','nayagaon','nihalsinghwala','nurmahal',
        'pathankot','patiala','patialaregion','patran','patti','payal','phagwara','phillaur','qadian',
        'rahon','raikot','rajasansi','rajpura','raman','ramdas','rampura','rampuraphul','rayya','rupnagar',
        'mohali','sahnewal','samana','samrala','sanaur','sangat','sangrur','sardulgarh','shahkot',
        'shamchaurasi','sirhindfatehgarhsahib','srihargobindpur','srimuktsarsahib','sujanpur','sultanpur',
        'sunam','talwandibhai','talwandisabo','talwara','tapa','tarntaran','urmartanda','zira','zirakpur',
        'generic','pb','state'
    ];
    s text;
BEGIN
    FOREACH s IN ARRAY schema_list
    LOOP
        RAISE NOTICE 'Altering code column in schema: %', s;
        EXECUTE format('ALTER TABLE %I.eg_city ALTER COLUMN code TYPE VARCHAR(255);', s);
        RAISE NOTICE 'Schema % updated', s;
    END LOOP;
END
$$;
 
 