import java.time.LocalDateTime;
import java.util.Date;

public class Test {

    static void main(String[] args) {
        String name = "backend engineer";
        char grade = 'A';
        Date date = new Date();
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(date);
        System.out.println(grade);
        System.out.println(name.toUpperCase());
        System.out.println(dateTime.toLocalDate());
    }
}
