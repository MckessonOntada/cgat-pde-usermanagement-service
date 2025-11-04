alter table catalog.material drop IF EXISTS cgat_identifier;
alter table catalog.material drop IF EXISTS cgat_type;
alter table catalog.material drop IF EXISTS cgat_therapy_type;
alter table catalog.material add IF NOT EXISTS cgat_therapy_type varchar(8);
alter table catalog.material alter selling_unit_code  drop not null;