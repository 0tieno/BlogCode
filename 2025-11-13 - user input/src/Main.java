import java.util.Scanner;

public class Main {
    public static void main(String [] args) {
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.print("Enter your name: ");
//        String name = scanner.nextLine();
//
//        System.out.println("Enter you age: ");
//        int age = scanner.nextInt();
//
//
//        System.out.println("Hi " + name + ", you are " + age + " years old.");
//
//        scanner.close();

//AREA OF RECTANGLE
        double width = 0;
        double height = 0;
        double area = 0;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter width: ");
        width = scanner.nextDouble();

        System.out.print("Enter height: ");
        height = scanner.nextDouble();

        area = width * height;

        System.out.println("The area of the rectangle is: " + area + "cm²");

        scanner.close();

    }
}