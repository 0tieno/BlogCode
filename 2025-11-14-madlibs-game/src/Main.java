import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        areaOfRectangle();

    }

    public static void areaOfRectangle () {

        int width = 0;
        int height = 0;
        int area = width * height;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter width (cm): ");
        width = scanner.nextInt();

        System.out.print("Enter height (cm): ");
        height = scanner.nextInt();

        area = width * height;

        System.out.println("Area of the rectangle: " + area + " cm²");
    }
}