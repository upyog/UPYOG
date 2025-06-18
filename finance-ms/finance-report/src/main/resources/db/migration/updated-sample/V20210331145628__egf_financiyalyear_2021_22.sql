INSERT INTO financialyear (id, financialyear, startingdate, endingdate, isactive, createddate, lastmodifieddate,lastmodifiedby,createdby,version, isactiveforposting, isclosed, transferclosingbalance) 
SELECT nextval('seq_financialyear'), '2021-22', '01-Apr-2021', '31-Mar-2022', true, current_date, current_date, 1,1,0, true, false, false 
WHERE NOT EXISTS (SELECT 1 FROM financialyear WHERE financialyear='2021-22');

INSERT INTO fiscalperiod (id,name, startingdate, endingdate,isactiveforposting, isactive, createddate, lastmodifieddate,lastmodifiedby,createdby,version, financialyearid) 
SELECT nextval('seq_fiscalperiod'),'202122', '01-Apr-2021', '31-Mar-2022',false, true, current_date, current_date,1,1,0, (select id from financialyear where financialyear='2021-22') 
WHERE NOT EXISTS (SELECT 1 FROM fiscalperiod WHERE name='202122');
