import java.util.Scanner;

public class PersonalInfoCollector {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your age: ");
        int age = scanner.nextInt();

        if (age <= 0) {
            System.out.println("Invalid age. Age must be greater than 0.");
        } else {
            System.out.println("================================");
            System.out.println("Hello " + name + " 👋");
            System.out.println("You are " + age + " years old.");
            System.out.println("Welcome to Java programming!");
            System.out.println("================================");
        }

        scanner.close();
    }
}
