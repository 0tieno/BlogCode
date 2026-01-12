import java.util.Scanner;

public class ConsoleInput {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
//        System.out.print("Please enter your name:");
//
//        String name = scanner.nextLine();
//        System.out.println("Hello, " + name + "! Welcome to the Java world.");

        System.out.println("enter number:");
        int num = scanner.nextInt();

        if (num % 2 == 0){
            System.out.println(num + " is even number");
        }else{
            System.out.println(num + " is odd number");
        }

        scanner.close();
    }
}
