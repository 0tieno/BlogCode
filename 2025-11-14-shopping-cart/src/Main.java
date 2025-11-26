import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        printReceipt(args);
    }

    static void printReceipt(String[] args){
        String itemName;
        double itemQuantity;
        double itemPrice;
        double totalPrice;

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter item name: ");
        itemName = scanner.nextLine();
        System.out.print("Enter item quantity: ");
        itemQuantity = scanner.nextDouble();
        System.out.print("Enter item price: ");
        itemPrice = scanner.nextDouble();

        totalPrice = itemPrice * itemQuantity;

        System.out.println("You have bought " + itemQuantity + " " + itemName + "(s) at a price of $" + itemPrice + " each.");
        System.out.println("Total price: $" + totalPrice);

        scanner.close();
    }
}
