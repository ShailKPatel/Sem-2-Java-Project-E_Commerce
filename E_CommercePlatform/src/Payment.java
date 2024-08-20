import java.io.*;
import java.util.*;

public class Payment {
	Bill customerBill;
	public int billPaidFlag = 0;
	private Cart customerCart;
	public static Scanner sc = new Scanner(System.in);

	Payment(Cart cart, String customerName, String billingAddress, String customerPhone) throws IOException {
		customerCart = cart;
		customerBill = new Bill(customerName, billingAddress, customerPhone,
				customerCart.getpid(), customerCart.getpname(),
				customerCart.getpqty(), customerCart.getprice());
		billPaidFlag = 0;
	}

	public void paymentPage() throws IOException {
		System.out.println("WELCOME TO PAYMENTS PAGE\n");
		int choice;
		do {
			System.out.println("*****************************************\n");
			System.out.println("1 - PAY BILL");
			System.out.println("2 - DISPLAY BILL");
			System.out.println("3 - EXIT");
			System.out.println("*****************************************\n");
			System.out.print("Enter choice: ");
			choice = sc.nextInt();

			switch (choice) {
				case 1:
					customerBill.displayBill();
					System.out.print("\nENTER AMOUNT TO PAY: ");
					float amount = sc.nextFloat();
					while (amount != customerBill.total_amount) {
						System.out.println("Invalid amount entered!");
						System.out.print("Enter again: ");
						amount = sc.nextFloat();
					}
					customerBill.addToDatabase();
					System.out.println("BILL PAID SUCCESSFULLY!");
					billPaidFlag = 1;
					choice = 3; // Automatically exit after payment
					break;
				case 2:
					customerBill.displayBill();
					break;
				case 3:
					System.out.println("Thank you");
					break;
				default:
					System.out.println("Wrong choice");
					break;
			}
		} while (choice != 3);
	}
}
