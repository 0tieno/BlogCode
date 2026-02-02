public class Palindrome {

    static void main(String[] args) {
        //Reversing the string
//        String str = "Iconsoul";
//        String reversed = "";
//
//        for(int i = str.length() - 1; i >= 0; i--){
//            reversed += str.charAt(i);
//        }
//        System.out.println("Reversed string is: " + reversed);

        String str = "Iconsoul";
        String reversed = new StringBuilder(str).reverse().toString();
        System.out.println(reversed);
    }
}
