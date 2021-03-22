CREATE VIEW `c_city_with_population` AS
SELECT * FROM  v_city_full_data c
                   left join popul p on c.city_id = p.pop_city_id