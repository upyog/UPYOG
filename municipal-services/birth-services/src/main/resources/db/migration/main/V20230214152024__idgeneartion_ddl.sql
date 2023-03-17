CREATE TABLE eg_birth_idgeneration(
                                idtype character varying(10),
                                modulecode character varying(10),
                                tenantid character varying(64),
                                yearid int,
                                lastid int
);

CREATE OR REPLACE FUNCTION fn_next_birth_id(
  _idtype character varying(10),
  _modulecode character varying(10),
  _tenantid character varying(64),
  _yearid integer)
    RETURNS bigint
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE  genid bigint;
BEGIN
genid := (Select lastid From eg_birth_idgeneration where tenantid = _tenantid AND yearid = _yearid AND idtype=_idtype AND modulecode = _modulecode) ;
IF ( genid IS NOT NULL ) THEN
genid :=  genid  + 1 ;
Update eg_birth_idgeneration SET lastid = genid
WHERE tenantid = _tenantid AND yearid = _yearid AND idtype=_idtype AND modulecode = _modulecode;
ELSE
genid := 1  ;
INSERT INTO eg_birth_idgeneration(idtype, modulecode, tenantid, yearid, lastid)
VALUES (_idtype, _modulecode, _tenantid, _yearid, genid);
END IF ;
RETURN  genid ;
END

$BODY$;