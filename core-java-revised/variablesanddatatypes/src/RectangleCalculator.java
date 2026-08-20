public class RectangleCalculator {
        public static void main(String[] args) {
            // Declare and initialize variables
            double length = 10.0;
            double width = 5.0;

            // Calculate area and perimeter
            double area = length * width;
            double perimeter = 2 * (length + width);

            // Display results
            System.out.println("Rectangle Calculator");
            System.out.println("Length: " + length);
            System.out.println("Width: " + width);
            System.out.println("Area: " + area);
            System.out.println("Perimeter: " + perimeter);

            //typecasting

            int age = 25;
            double myAge = (double) age;
            System.out.println("My Age: " + myAge);
        }

}
