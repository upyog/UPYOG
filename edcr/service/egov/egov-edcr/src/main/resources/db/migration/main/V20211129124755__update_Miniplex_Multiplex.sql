-- adding miniplex
INSERT INTO egbpa_sub_occupancy
(id, code, name, ordernumber, isactive, createdby, createddate,
 lastmodifieddate, lastmodifiedby, version, description,
 maxcoverage, minfar, maxfar, occupancy, colorcode, year, subyear)
VALUES
(nextval('seq_egbpa_sub_occupancy'), 'F-MIP',
 'Miniplex',
 (SELECT COALESCE(MAX(ordernumber),0)+1 FROM egbpa_sub_occupancy),
 TRUE, 1, NOW(), NOW(), 1, 0,
 'Miniplex',
 0, 0, 0, (SELECT id FROM egbpa_occupancy WHERE code='F'),
 16, '2018', '2018-01');

 -- update multiplex category
UPDATE egbpa_sub_occupancy
SET code='F-MTP' , name='Multiplex' , description='Multiplex'
WHERE code='F-MPMT';