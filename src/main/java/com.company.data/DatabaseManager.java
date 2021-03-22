package com.company.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DatabaseManager {
    private static final String connectionUrl = "jdbc:mysql://localhost:3306/population?serverTimezone=UTC";

    public List <City> getCities(){
        List <City> items = new ArrayList<>();
        try {
            var connection = getConnection();
            var statement = connection.createStatement();
            var result = statement.executeQuery("select * from v_city_full_data");

            while (result.next()){
             items.add(City.create(result));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return items;
    }
    //==============================================
    public List<City> getCitiesWithPopulationData() {
        List<City> items = new ArrayList<>();

        Map<Integer, City> cities = new HashMap<>();

        try {
            var connection = getConnection();
            var statement = connection.createStatement();
            var result = statement.executeQuery("select * from v_city_with_population");

            while (result.next()) {

                var cityId = result.getInt("city_id");

                if(!cities.containsKey(cityId)) { // если в map нет объекта с таким ключом, то
                    var city = City.create(result); // создаем новым объект типа город и
                    cities.put(cityId, city); // добавляем его в колленцию map
                }

                var currentCity = cities.get(cityId); //взяли текущий город

                currentCity.getPopulation().add(Population.create(result, currentCity));
                // и добавили к нему список всего населения за все годы
            }

            return new ArrayList<>(cities.values());
            //альтернативная запись строчки выше: return cities.values().stream().collect(Collectors.toList());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return items;
    }

    //===============================================
    public City getCityById(int id) {
        try {
            var con = getConnection();
            var stmt = con.prepareStatement("select * from v_city_full_data where city_id = ?");

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                return City.create(rs);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    //===============================================
    public void getPopulationForCity (City city) {
        try {
            var con = getConnection();
            var stmt = con.prepareStatement("select * from popul where pop_city_id = ?");

            stmt.setInt(1, city.getId());

            ResultSet rs = stmt.executeQuery();

            city.getPopulation().clear();

            while (rs.next()) {
             city.getPopulation().add(Population.create(rs,city));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
      }


    //================================================

    public List <Region> getRegions(){
        List <Region> items = new ArrayList<>();
        try {
            var connection = getConnection();
            var statement = connection.createStatement();
            var result = statement.executeQuery("select * from region");

            while (result.next()){
                var region = new Region(result.getInt("region_id"),
                        result.getString("region_name"));

                items.add(region);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    return items;
    }

    public List <County> getCounties(){
        List <County> items = new ArrayList<>();
        try {
            var connection = getConnection();
            var statement = connection.createStatement();
            var result = statement.executeQuery("select * from county");

            while (result.next()){
                var county = new County(result.getInt("county_id"),
                        result.getString("county_name"),
                        new Region(result.getInt("county_region_id"), ""));

                items.add(county);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return items;
    }

    public void addCounties(List <County> counties){
        Connection con = null;
        try {
            con = getConnection();
            for (County county: counties
            ) {
                PreparedStatement insertCounties = con.prepareStatement(
                        "insert into county(county_name, county_region_id) values (?,?)");
                insertCounties.setString(1, county.getName());
                insertCounties.setInt(2, county.getRegion().getId());
                insertCounties.executeUpdate(); // вызов этого метода возвращает количество добавленных строк
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Integer addCity(City city){
        Connection con = null;
        try {
            con = getConnection();
                PreparedStatement insertCity = con.prepareStatement(
                        "insert into city (city_name,city_founded, city_region_id, city_county_id) values (?,?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                insertCity.setString(1, city.getName());

                if (city.getFounded() !=null){
                    insertCity.setInt(2, city.getFounded());
                } else {
                    insertCity.setNull(2, java.sql.Types.INTEGER);
                }

            if (city.getRegion() !=null){
                insertCity.setInt(3, city.getRegion().getId());
            } else {
                insertCity.setNull(3, java.sql.Types.INTEGER);
            }

            if (city.getCounty() !=null){
                insertCity.setInt(4, city.getCounty().getId());
            } else {
                insertCity.setNull(4, java.sql.Types.INTEGER);
            }

            insertCity.executeUpdate(); // вызов этого метода возвращает количество добавленных строк

            Integer id = 0;

            try (ResultSet keys = insertCity.getGeneratedKeys()){
             keys.next();
             id = keys.getInt(1);
             city.setId(id);
            }
            con.close();

            for (var population:city.getPopulation()
            ) {
                population.setCity(city);
                addPopulation(population);
            }
            return id;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public Integer addPopulation(Population population){
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement insertStatement = connection.prepareStatement(
                    "insert into popul (pop_year, pop_number, pop_city_id) values (?,?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, population.getYear());
            insertStatement.setInt(2, population.getPopulation());
            insertStatement.setInt(3, population.getCity().getId());

            insertStatement.executeUpdate();

            Integer id = 0;

            try (ResultSet keys = insertStatement.getGeneratedKeys()){
            keys.next();
            id = keys.getInt(1);
            population.setId(id);
            }
            connection.close();
            return id;

        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl, "test","test123");
    }
}
