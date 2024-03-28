alter table  eg_user 
  ALTER COLUMN IF NOT EXISTS name TYPE varchar (250),
  ALTER COLUMN IF NOT EXISTS mobilenumber TYPE varchar (150),
  ALTER COLUMN IF NOT EXISTS emailid TYPE varchar (300),
  ALTER COLUMN IF NOT EXISTS username TYPE varchar (180),
  ALTER COLUMN IF NOT EXISTS altcontactnumber TYPE varchar (150),
  ALTER COLUMN IF NOT EXISTS pan TYPE varchar (65),
  ALTER COLUMN IF NOT EXISTS aadhaarnumber TYPE varchar (85),
  ALTER COLUMN IF NOT EXISTS guardian TYPE varchar (250);

alter table  eg_user_address
  ALTER COLUMN IF NOT EXISTS address TYPE varchar (440);