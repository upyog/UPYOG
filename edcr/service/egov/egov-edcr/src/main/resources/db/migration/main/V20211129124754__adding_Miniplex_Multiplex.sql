

-- adding miniplex multiplex 
-- Compressed Natural Gas Station
INSERT INTO egbpa_sub_occupancy
(id, code, name, ordernumber, isactive, createdby, createddate,
 lastmodifieddate, lastmodifiedby, version, description,
 maxcoverage, minfar, maxfar, occupancy, colorcode, year, subyear)
VALUES
(nextval('seq_egbpa_sub_occupancy'), 'F-MPMT',
 'Miniplex/Multiplex',
 (SELECT COALESCE(MAX(ordernumber),0)+1 FROM egbpa_sub_occupancy),
 TRUE, 1, NOW(), NOW(), 1, 0,
 'Miniplex/Multiplex',
 0, 0, 0, (SELECT id FROM egbpa_occupancy WHERE code='F'),
 17, '2018', '2018-01');