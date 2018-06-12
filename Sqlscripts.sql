
-- My Sql Scripts.
-- 1.Write MySQL query to find IPs that mode more than a certain number of requests for a given time period.
-- I developed in two ways with and without using stored procedure.


--   1.Without stored procedure.

select Ip,count(Ip) as GreaterThanThreshold from LogFil where Dat  between   '2017-01-01.00:00:00' and  '2017-01-01.23:59:59' group by Ip having count(Ip)>500;

--    2.With stored procedure.

DELIMITER $$
DROP PROCEDURE IF EXISTS`IP_made_more_than_request`$$
CREATE PROCEDURE `IP_made_more_than_request`(IN Dt timestamp(3),In Dt1 timestamp(3),In threshold int)
BEGIN
select Ip,count(Ip) as GreaterThanThreshold from LogFil where Dat  between  Dt and  dt1  group by Ip having count(Ip)>threshold;
END$$
DELIMITER ;

-- ------------------------------------------------------------------------------------------------------------

-- Write MySQL query to find requests made by a given IP.

select IP,count(Ip) as Requests from LogFil where IP='192.168.0.182';


-- ----------------------------------------------------------------------------------------------------

--  Stored procedure to load the data into mysql

DELIMITER $$
DROP PROCEDURE IF EXISTS `insert_into_mysql`$$

CREATE  PROCEDURE `insert_into_mysql`(In Dt timestamp(3),In Ip varchar(20),IN Request varchar(20),In Status int,In Agent varchar(200))
BEGIN

    insert into LogFil(Dat,Ip,Request,Status,Agent) values(Dt,Ip,Request,Status,Agent);

END$$
DELIMITER ;




