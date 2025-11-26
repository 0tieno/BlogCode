//public class Main {
//    public static void main(String[] args){
//        //program to add two numbers
//
//        int x = 5;
//        int y = 10;
//        int sum = x + y;
//
//        System.out.println("The sum of " + x + " and " + y + " is: " + sum);
//    }
//}

//...........using Scanner to take user input...........
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first number: ");
        int x = scanner.nextInt();

        System.out.print("Enter second number: ");
        int y = scanner.nextInt();

        int sum = x + y;
        System.out.println("The sum of " + x + " and " + y + " is: " + sum);
    }
}
