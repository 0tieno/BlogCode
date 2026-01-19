import java.util.Scanner;

public class InputValidation {
    public static int readPositive(Scanner sc) {
        int num;
        do {
            System.out.print("Enter a positive number: ");
            num = sc.nextInt();
        } while (num <= 0);
        return num;
    }

}
