package com.company.data;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.sql.ResultSet;

@Data
@AllArgsConstructor
public class Population {

    private Integer id;
    private Integer year;
    private Integer population;
    private City city;

    @SneakyThrows
    public static Population create(ResultSet rs){
       var population = new Population(rs.getInt("pop_id"),
                rs.getInt("pop_year"),
                rs.getInt("pop_number"),
                City.create(rs));
        return population;
    }

    @SneakyThrows
    public static Population create(ResultSet rs, City city){
        var population = new Population(rs.getInt("pop_id"),
                rs.getInt("pop_year"),
                rs.getInt("pop_number"),
                city);
        return population;
    }
}