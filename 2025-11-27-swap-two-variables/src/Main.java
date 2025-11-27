public class Main {
    public static void main (String[] args){
//        String firstVariable = "world!";
//        String secondVariable = "Hello, ";
//
//        String swappedFirstVariable = secondVariable;
//        String swappedSecondVariable = firstVariable;
//
//        String swappedVariable = swappedFirstVariable + swappedSecondVariable;
//        System.out.println(swappedVariable);

//        int a= 10;
//        int b= 5;
//
//        int temp = a;
//        a = b;
//        b = temp;
//
//        System.out.println("a = " + a + ", b = " + b);

        String a = "world!";
        String b = "Hello";

        String temp = a;
        a = b;
        b = temp;

        System.out.println("a = " + a + ", b = " + b);
    }
}
//    Explanation: We save a in a temporary variable, move b into a, and then move the saved value into b.
