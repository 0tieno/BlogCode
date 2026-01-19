import java.util.*;

public class NumberAnalyzer {

    public static int sum(List<Integer> nums) {
        int s = 0;
        for (int n : nums) s += n;
        return s;
    }

    public static int max(List<Integer> nums) {
        int m = nums.getFirst();
        for (int n : nums) if (n > m) m = n;
        return m;
    }

    public static int min(List<Integer> nums) {
        int m = nums.getFirst();
        for (int n : nums) if (n < m) m = n;
        return m;
    }

    public static double average(List<Integer> nums) {
        return (double) sum(nums) / nums.size();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("How many numbers? ");
        int count = sc.nextInt();

        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            System.out.print("Enter number " + (i + 1) + ": ");
            numbers.add(sc.nextInt());
        }

        System.out.println("\nRESULTS:");
        System.out.println("Sum = " + sum(numbers));
        System.out.println("Max = " + max(numbers));
        System.out.println("Min = " + min(numbers));
        System.out.println("Average = " + average(numbers));
    }
}

