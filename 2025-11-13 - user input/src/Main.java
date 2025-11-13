import java.util.Scanner;

public class Main {
    public static void main(String [] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.println("Enter you age: ");
        int age = scanner.nextInt();


        System.out.println("Hi " + name + ", you are " + age + " years old.");
    }
}