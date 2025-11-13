public class Main {
    public static void main(String[] args) {

        boolean isRaining = true;
        boolean haveUmbrella = false;

        if (isRaining && !haveUmbrella) {
            System.out.println("Stay indoors");
        } else {
            System.out.println("You can go outside");
        }
    }
}
