public class Fibonacci {
    public static void fibonacci(int count) {
        int a = 0, b = 1, i = 0;
        while (i < count) {
            System.out.print(a + " ");
            int next = a + b;
            a = b;
            b = next;
            i++;
        }
        System.out.println();
    }

}
