INSERT INTO financialyear (id, financialyear, startingdate, endingdate, isactive, createddate, lastmodifieddate,lastmodifiedby,createdby,version, isactiveforposting, isclosed, transferclosingbalance) 
SELECT nextval('seq_financialyear'), '2020-21', '01-Apr-2020', '31-Mar-2021', true, current_date, current_date, 1,1,0, true, false, false 
WHERE NOT EXISTS (SELECT 1 FROM financialyear WHERE financialyear='2020-21');

INSERT INTO fiscalperiod (id,name, startingdate, endingdate,isactiveforposting, isactive, createddate, lastmodifieddate,lastmodifiedby,createdby,version, financialyearid) 
SELECT nextval('seq_fiscalperiod'),'202021', '01-Apr-2020', '31-Mar-2021',false, true, current_date, current_date,1,1,0, (select id from financialyear where financialyear='2020-21') 
WHERE NOT EXISTS (SELECT 1 FROM fiscalperiod WHERE name='202021');
