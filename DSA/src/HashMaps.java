import java.util.HashMap;

public class HashMaps {
    public static void main() {

        /*
         * put();
         * get();
         * clear();
         * remove();
         * size();
         * ......LOOPs.........
         * keySet();
         * values();
         *
         * A HashMap stores items in key/value pairs, where each key maps to a specific value.
         * It is part of the java.util package and implements the Map interface.
         * Instead of accessing elements by an index (like with ArrayList), you use a key to retrieve its associated value.
         * A HashMap can store many different combinations, such as:
         * String keys and Integer values
         * String keys and String values
         */
      HashMap<String, String> capitalCities = new HashMap<>();

        capitalCities.put("Kenya", "Nairobi");
        capitalCities.put("England", "London");
        capitalCities.put("India", "New Dehli");
        capitalCities.put("Austria", "Wien");
        capitalCities.put("Norway", "Oslo");
        capitalCities.put("Norway", "Oslo"); // Duplicate
        capitalCities.put("USA", "Washington DC");

//        System.out.println(capitalCities.get("Kenya"));
//        System.out.println(capitalCities.remove("Australia"));

//        for (String city: capitalCities.keySet()){
//            System.out.println(city);
//        }

//        for (String city: capitalCities.values()){
//            System.out.println(city);
//        }

//        System.out.println(capitalCities.size());

//        System.out.println(capitalCities);

        HashMap<String, Integer> People = new HashMap<>();

        People.put("ron", 23);
        People.put("steve", 23);
        People.put("brian", 23);

        System.out.println(People);
        System.out.println(People.size());


    }
}
