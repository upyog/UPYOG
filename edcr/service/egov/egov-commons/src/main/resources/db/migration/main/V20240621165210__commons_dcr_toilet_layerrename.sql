insert into state.egdcr_layername(id,key,value,createdby,createddate,lastmodifiedby,lastmodifieddate,version) 
select nextval('state.seq_egdcr_layername'),'LAYER_NAME_BLK_FLR_TOILET','BLK_%s_FLR_%s_TOILET_%s',1,now(),1,now(),0 where not exists(select key from state.egdcr_layername where key='LAYER_NAME_BLK_FLR_TOILET');

insert into state.egdcr_layername(id,key,value,createdby,createddate,lastmodifiedby,lastmodifieddate,version) 
select nextval('state.seq_egdcr_layername'),'LAYER_NAME_BLK_FLR_TOILET_HT','BLK_%s_FLR_%s_TOILET_%s_HT',1,now(),1,now(),0 where not exists(select key from state.egdcr_layername where key='LAYER_NAME_BLK_FLR_TOILET_HT');

insert into state.egdcr_layername(id,key,value,createdby,createddate,lastmodifiedby,lastmodifieddate,version) 
select nextval('state.seq_egdcr_layername'),'LAYER_NAME_BLK_FLR_TOILET_VENTILATION','BLK_%s_FLR_%s_TOILET_%s_VENTILATION',1,now(),1,now(),0 where not exists(select key from state.egdcr_layername where key='LAYER_NAME_BLK_FLR_TOILET_VENTILATION');

