insert into state.egdcr_layername(id,key,value,createdby,createddate,lastmodifiedby,lastmodifieddate,version) 
select nextval('state.seq_egdcr_layername'),'LAYER_NAME_HEIGHT_OF_BUILDING_EXCLUDING_MP','HT_OF_BLDG_EXCLUDING_MP',1,now(),1,now(),0 where not exists(select key from state.egdcr_layername where key='LAYER_NAME_HEIGHT_OF_BUILDING_EXCLUDING_MP');

ALTER TABLE state.edcr_application ADD COLUMN coreArea VARCHAR(255);
