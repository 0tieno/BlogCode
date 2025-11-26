//public class Main {
//    public static void main(String[] args){
//        double length = 5.0;
//        double width = 3.0;
//        double area = length * width;
//        System.out.println("The area of the rectangle is " + area + "cm²");
//    }
//}

// This program calculates the area of a rectangle given its length and width.
// It then prints the area to the console.

//..........Accepting user inputs..........

import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the length of the rectangle in cm: ");
        double length = scanner.nextDouble();

        System.out.print("Enter the width of the rectangle in cm: ");
        double width = scanner.nextDouble();

        System.out.println("The area of the rectangle is " + (  length * width ) + "cm²");
    }
}
