public class Main {
    public static void main(String[] args) {

        int n = 501;
        int sum = 0;

        while (n > 0) {
            int lastDigit = n % 10; // get last digit
            sum += lastDigit;       // add to sum
            n = n / 10;             // remove last digit
        }

        System.out.println(sum); // prints 6
    }
}
