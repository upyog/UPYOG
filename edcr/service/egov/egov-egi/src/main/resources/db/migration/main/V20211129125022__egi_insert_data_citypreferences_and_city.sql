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
        'jagraon','jaitu','jalalabad','jalandhar','jalandharregion','jandiala','joga','kapurthala',
        'kartarpur','khamanon','khanauri','khanna','kharar','khemkaran','kiratpursahib','kotfatta',
        'kotissekhan','kotshamir','kothaguru','kotkapura','kurali','lalru','lehramohabbat','lehragaga',
        'lohiankhass','longowal','ludhiana','ludhianaregion','machhiwara','mahilpur','majitha','makhu',
        'malerkotla','mallanwalakhass','maloud','malout','maluka','mamdot','mandikalan','mansa','maur',
        'mehatpur','mehraj','moga','moonak','morinda','mudki','mukerian','mullanpurdakha','nabha','nadala',
        'nakodar','nangal','narotjaimalsingh','nathana','nawanshahr','nayagaon','nihalsinghwala','nurmahal',
        'pathankot','patiala','patialaregion','patran','patti','payal','phagwara','phillaur','qadian',
        'rahon','raikot','rajasansi','rajpura','raman','ramdas','rampura','rampuraphul','rayya','rupnagar',
        'sasmohali','sahnewal','samana','samrala','sanaur','sangat','sangrur','sardulgarh','shahkot',
        'shamchaurasi','sirhindfatehgarhsahib','srihargobindpur','srimuktsarsahib','sujanpur','sultanpur',
        'sunam','talwandibhai','talwandisabo','talwara','tapa','tarntaran','urmartanda','zira','zirakpur',
        'generic','pb','public','state'
    ];
    s text;
BEGIN
    FOREACH s IN ARRAY schema_list
    LOOP
        -- Insert into eg_citypreferences if not exists
        EXECUTE format($sql$
            INSERT INTO %I.eg_citypreferences(
                id, municipalitylogo, createdby, createddate, lastmodifiedby, lastmodifieddate, version, municipalityname, municipalitycontactno, municipalityaddress, municipalitycontactemail, municipalitygislocation, municipalitycallcenterno, municipalityfacebooklink, municipalitytwitterlink, googleapikey, recaptchapk, recaptchapub
            )
            SELECT 1, null, 1, '0001-01-01 21:32:00 BC', 1, '0001-01-01 21:32:00 BC', 0, 'Digit Municipal Corporation', 
	null, null, null, null, null, null, null, 'AIzaSyA1otT5_xEGe0qMrh2lemKKYH7Vo-pGOlA', 
	'6LfidggTAAAAANDSoCgfkNdvYm3Ugnl9HC8_68o0', '6LfidggTAAAAADwfl4uOq1CSLhCkH8OE7QFinbVs'
            WHERE NOT EXISTS (SELECT 1 FROM %I.eg_citypreferences);
        $sql$, s, s);

        -- Insert into eg_city if not exists
        EXECUTE format($sql$
            INSERT INTO %I.eg_city(
                domainurl, name, localname, id, active, version, createdby, lastmodifiedby,
                createddate, lastmodifieddate, code, districtcode, districtname,
                longitude, latitude, preferences, regionname, grade
            )
            SELECT
                'localhost', 'Digit Municipal Corporation', 'ulb',
                nextval('%I.seq_eg_city'), TRUE, 0, 1, 1,
                now(), now(),
                'cg.' || %L,  -- dynamically set code as pb.<schema_name>
                1, '%I', NULL, NULL, 1, NULL, 'Corp'
            WHERE NOT EXISTS (SELECT 1 FROM %I.eg_city);
        $sql$, s, s, s, s, s);

    END LOOP;
END
$$;