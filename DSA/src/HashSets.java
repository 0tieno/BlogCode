import java.util.HashSet;
import java.util.Set;

public class HashSets {

//    A HashSet is a collection of elements where every element is unique.

    static void main() {
        Set<String> cars = new HashSet<>();

        cars.add("range");
        cars.add("bmw");
        cars.add("volvo");
        cars.add("audi");

//        System.out.println(cars.size());

//        System.out.println(cars);

        for (String car:cars){
            System.out.println(car);
        }
    }
}
