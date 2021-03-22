import com.company.data.DataLoader;
import com.company.data.DatabaseManager;


public class Main {
    public static void main(String[] args) {
      var dm = new DatabaseManager();


//      var cities = dm.getCitiesWithPopulationData();
//        for (var city:cities
//             ) {
//            System.out.println(city.getName());
//            for (var popul: city.getPopulation()
//                 ) {
//                System.out.println(popul.getYear() + " " + popul.getPopulation());
//            }
//        }


      var city = dm.getCityById(199);
        System.out.println(city.getName());

       dm.getPopulationForCity(city);
        for (var pop: city.getPopulation()
             ) {
            System.out.println(pop.getYear() + " " + pop.getPopulation());
        }


//      var cities = dm.getCities();
//        for (var city: cities
//             ) {
//            System.out.println(city.getName() + " " + city.getRegion().getName());
//        }
 //     var dl = new DataLoader(dm);
//    var allCities = dl.loadCities();
//
//        for (var region: dm.getRegions()
//             ) {
//            System.out.println(region.getName());
//        }
//
//
//        var counties = dl.load();
//
//        dm.addCounties(counties);
    }
}
