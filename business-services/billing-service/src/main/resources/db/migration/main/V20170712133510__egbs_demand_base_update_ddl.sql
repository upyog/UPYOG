ALTER TABLE  egbs_demand drop CONSTRAINT IF  EXISTS uk_egbs_demand_consumercode_businessservice ;

ALTER TABLE  egbs_demand add CONSTRAINT uk_egbs_demand_consumercode_businessservice UNIQUE (consumercode,businessservice);
