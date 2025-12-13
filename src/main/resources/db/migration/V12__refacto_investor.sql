Alter table investment
    add column user_email varchar;


update investment i
set user_email = (select i2.user_email from investor i2 where i2.user_id = i.investor_id);

alter table simulation
    add column user_email varchar;


update simulation s
set user_email = (select i2.user_email from investor i2 where i2.user_id = s.user_id);


ALTER TABLE investment
    DROP COLUMN investor_id CASCADE;


ALTER TABLE simulation
    DROP COLUMN user_id CASCADE;

DROP table investor;


ALTER table profiles
add column email varchar