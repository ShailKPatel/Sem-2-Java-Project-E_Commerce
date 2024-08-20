import java.util.*;
import java.io.IOException;
import java.sql.*;

public class Customer extends Shop {
	private int customerID;
	private String customerPass;
	private Cart customerCart = new Cart();
	private int cartFlag = 0;
	private int billPaidFlag = 0;
	private int checkFlag = -1;
	Scanner sc = new Scanner(System.in);

	private ArrayList<Integer> pid = new ArrayList<>();
	private ArrayList<String> name = new ArrayList<>();
	private ArrayList<String> type = new ArrayList<>();
	private ArrayList<Integer> qty = new ArrayList<>();
	private ArrayList<Float> price = new ArrayList<>();

	private int products_Check;

	Customer(int custID, String passw) {
		customerID = custID;
		customerPass = passw;
		customerCart = new Cart();
		billPaidFlag = 0;
		cartFlag = 0;
	}

	public void CustomerPage() throws IOException {
		products_Check = this.initializeProducts();
		System.out.println("WELCOME TO CUSTOMER SECTION\n");
		int ch;
		do {
			System.out.println("*****************************************************\n");
			System.out.println("1 - VIEW PRODUCTS LIST");
			System.out.println("2 - SEARCH A PRODUCT NAMEWISE");
			System.out.println("3 - SEARCH PRODUCTS TYPEWISE");
			System.out.println("4 - ADD PRODUCT TO CART");
			System.out.println("5 - REMOVE PRODUCT FROM CART");
			System.out.println("6 - VIEW CART");
			System.out.println("7 - PROCEED TO PAYMENT");
			System.out.println("8 - EDIT PROFILE");
			System.out.println("9 - LOGOUT FROM SYSTEM");
			System.out.println("*****************************************************\n");
			System.out.print("Enter choice: ");
			ch = sc.nextInt();
			sc.nextLine(); // consume newline

			switch (ch) {
				case 1 -> this.viewProducts();
				case 2 -> this.searchNameWise();
				case 3 -> this.searchTypeWise();
				case 4 -> this.addProducts();
				case 5 -> {
					System.out.print("ENTER PRODUCT ID TO REMOVE FROM CART: ");
					int rem = sc.nextInt();
					sc.nextLine(); // consume newline
					customerCart.removeFromCart(rem);
					this.updateArrayList();
				}
				case 6 -> customerCart.viewCart();
				case 7 -> this.proceedPayment(customerCart);
				case 8 -> editProfile(customerID);
				case 9 -> ch = this.checkExit();
				default -> System.out.println("Wrong choice");
			}
		} while (ch != 9);
	}

	private int checkExit() throws IOException {
		if (cartFlag == 1) {
			System.out.println("YOU HAVE A PENDING CART!");
			System.out.print("DO YOU WANT TO MAKE PAYMENT (PRESS Y) ELSE CANCEL THE CART (PRESS N): ");
			String chc = sc.nextLine();
			if (chc.equalsIgnoreCase("Y")) {
				proceedPayment(customerCart);
				if (billPaidFlag != 1 && checkFlag == -1)
					return -1;
				else
					return 0;
			} else {
				customerCart.cancelCart();
				customerCart = new Cart();
				cartFlag = 0;
				billPaidFlag = 0;
			}
		}
		System.out.println("THANK YOU!");
		return 9;
	}

	private void proceedPayment(Cart cart1) throws IOException {
		if (cartFlag == 1) {
			String c_name = "";
			String b_add = "";
			String c_phn = "";
			try {
				PreparedStatement ps = con.prepareStatement("SELECT * FROM custinfo WHERE custID = ?");
				ps.setInt(1, customerID);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					c_name = rs.getString("Name");
					b_add = rs.getString("Address");
					c_phn = rs.getString("ContactNumber");
				}
			} catch (Exception e) {
				System.out.println(e);
			}
			Payment p = new Payment(cart1, c_name, b_add, c_phn);
			p.paymentPage();
			if (p.billPaidFlag == 1)
				billPaidFlag = 1;
			if (billPaidFlag == 1) {
				customerCart = new Cart();
				cartFlag = 0;
				billPaidFlag = 0;
				checkFlag = -2;
			}
		} else {
			System.out.println("CART IS EMPTY!");
		}
	}

	private void updateArrayList() {
		pid.clear();
		name.clear();
		type.clear();
		qty.clear();
		price.clear();
		initializeProducts();
	}

	private int searchProd(int x) {
		int res = pid.indexOf(x);
		return res != -1 ? qty.get(res) : -1;
	}

	private void updateQty(int sub, int x) {
		try {
			int res = pid.indexOf(x);
			int min = qty.get(res);
			qty.set(res, Math.max(min - sub, 0));

			PreparedStatement ps = con.prepareStatement("UPDATE products SET Quantity = ? WHERE productID = ?");
			ps.setInt(1, qty.get(res));
			ps.setInt(2, x);
			int m = ps.executeUpdate();
			if (m == 0)
				System.out.println("PRODUCT UPDATION FAILED!");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void addProducts() throws IOException {
		String chc;
		do {
			System.out.print("ENTER PRODUCT ID TO ADD TO CART: ");
			int p_id = sc.nextInt();
			sc.nextLine(); // consume newline

			int q_avail = searchProd(p_id);
			if (q_avail == -1) {
				System.out.println("PRODUCT NOT FOUND!");
			} else {
				System.out.println("QUANTITY AVAILABLE = " + q_avail);
				System.out.print("ENTER QUANTITY TO PURCHASE: ");
				int q_pur = sc.nextInt();
				sc.nextLine(); // consume newline

				if (q_pur > q_avail) {
					System.out.println("STOCK NOT AVAILABLE");
				} else {
					updateQty(q_pur, p_id);

					String p_name = name.get(pid.indexOf(p_id));
					String p_type = type.get(pid.indexOf(p_id));
					float p_price = q_pur * price.get(pid.indexOf(p_id));

					customerCart.addToCart(p_id, p_name, p_type, q_pur, p_price);
					cartFlag = 1;
				}
			}
			System.out.print("DO YOU WANT TO CONTINUE (Y for yes, N for no): ");
			chc = sc.nextLine();
		} while (chc.equalsIgnoreCase("Y"));
	}

	private int initializeProducts() {
		int x = 0;
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM products");
			ResultSet rs = ps.executeQuery();

			if (rs.last()) {
				x = rs.getRow();
				rs.beforeFirst();
			}
			if (x == 0) {
				return 0;
			} else {
				while (rs.next()) {
					pid.add(rs.getInt("productID"));
					name.add(rs.getString("productName"));
					type.add(rs.getString("productType"));
					qty.add(rs.getInt("Quantity"));
					price.add(rs.getFloat("Price"));
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return x;
	}

	private void viewProducts() {
		if (products_Check == 0) {
			System.out.println("PRODUCTS NOT AVAILABLE!");
		} else {
			System.out.println(
					"***********************************************************************************************************************\n");
			System.out.printf("%-20s \t %-20s \t %-20s \t %-20s \t %-20s\n", "Product_ID", "Product_Name",
					"Product_Type", "Product_Quantity", "Product_Price");
			System.out.println(
					"***********************************************************************************************************************\n");
			for (int i = 0; i < pid.size(); i++) {
				if (qty.get(i) != 0)
					System.out.printf("%-20d \t %-20s \t %-20s \t %-20d \t %-20f\n", pid.get(i), name.get(i),
							type.get(i), qty.get(i), price.get(i));
				else
					System.out.printf("%-20d \t %-20s \t %-20s \t %-20s \t %-20f\n", pid.get(i), name.get(i),
							type.get(i), "NOT IN STOCK", price.get(i));
			}
			System.out.println(
					"***********************************************************************************************************************\n");
		}
	}

	private void searchNameWise() {
		if (products_Check == 0) {
			System.out.println("PRODUCTS NOT AVAILABLE!");
		} else {
			String sr;
			String chc;
			do {
				System.out.print("ENTER PRODUCT NAME TO SEARCH: ");
				sr = sc.nextLine();
				int res = name.indexOf(sr);
				if (res == -1) {
					System.out.println("PRODUCT NOT FOUND!");
				} else {
					System.out.println("PRODUCT DETAILS ARE:\n");
					System.out.printf("PRODUCT ID         = %-5d\n", pid.get(res));
					System.out.printf("PRODUCT NAME       = %-5s\n", name.get(res));
					System.out.printf("PRODUCT TYPE       = %-5s\n", type.get(res));
					if (qty.get(res) != 0)
						System.out.printf("PRODUCT QUANTITY   = %-5d\n", qty.get(res));
					else
						System.out.printf("PRODUCT QUANTITY   = NOT IN STOCK\n");
					System.out.printf("PRODUCT PRICE      = %-5f\n", price.get(res));
				}
				System.out.print("DO YOU WANT TO SEARCH AGAIN (Y for yes, N for no): ");
				chc = sc.nextLine();
			} while (chc.equalsIgnoreCase("Y"));
		}
	}

	private void searchTypeWise() {
		if (products_Check == 0) {
			System.out.println("PRODUCTS NOT AVAILABLE!");
		} else {
			String sr;
			String chc;
			do {
				System.out.print("ENTER PRODUCT TYPE TO SEARCH: ");
				sr = sc.nextLine();
				System.out.println(
						"***********************************************************************************************************************\n");
				System.out.printf("%-20s \t %-20s \t %-20s \t %-20s \t %-20s\n", "Product_ID", "Product_Name",
						"Product_Type", "Product_Quantity", "Product_Price");
				System.out.println(
						"***********************************************************************************************************************\n");
				int flag = 0;
				for (int i = 0; i < type.size(); i++) {
					if (type.get(i).equalsIgnoreCase(sr)) {
						flag = 1;
						if (qty.get(i) != 0)
							System.out.printf("%-20d \t %-20s \t %-20s \t %-20d \t %-20f\n", pid.get(i), name.get(i),
									type.get(i), qty.get(i), price.get(i));
						else
							System.out.printf("%-20d \t %-20s \t %-20s \t %-20s \t %-20f\n", pid.get(i), name.get(i),
									type.get(i), "NOT IN STOCK", price.get(i));
					}
				}
				if (flag == 0)
					System.out.println("PRODUCT NOT FOUND!");
				System.out.print("DO YOU WANT TO SEARCH AGAIN (Y for yes, N for no): ");
				chc = sc.nextLine();
			} while (chc.equalsIgnoreCase("Y"));
		}
	}

	private void editProfile(int customerId) {
		System.out.println("EDIT PROFILE SECTION");
		System.out.println("1 - CHANGE PASSWORD");
		System.out.println("2 - CHANGE ADDRESS");
		System.out.println("3 - CHANGE CONTACT NUMBER");
		System.out.print("Enter your choice: ");
		int choice = sc.nextInt();
		sc.nextLine(); // consume newline

		try {
			PreparedStatement ps = null;

			switch (choice) {
				case 1 -> {
					System.out.print("Enter new password: ");
					String newPassword = sc.nextLine();
					ps = con.prepareStatement("UPDATE custinfo SET Password = ? WHERE custID = ?");
					ps.setString(1, newPassword);
					ps.setInt(2, customerId);
				}
				case 2 -> {
					System.out.print("Enter new address: ");
					String newAddress = sc.nextLine();
					ps = con.prepareStatement("UPDATE custinfo SET Address = ? WHERE custID = ?");
					ps.setString(1, newAddress);
					ps.setInt(2, customerId);
				}
				case 3 -> {
					System.out.print("Enter new contact number: ");
					String newContact = sc.nextLine();
					ps = con.prepareStatement("UPDATE custinfo SET ContactNumber = ? WHERE custID = ?");
					ps.setString(1, newContact);
					ps.setInt(2, customerId);
				}
				default -> System.out.println("Invalid choice");
			}

			if (ps != null) {
				int rowsUpdated = ps.executeUpdate();
				if (rowsUpdated > 0) {
					System.out.println("Profile updated successfully!");
				} else {
					System.out.println("Profile update failed!");
				}
			}
		} catch (Exception e) {
			System.out.println("Error while updating profile: " + e.getMessage());
		}
	}

}
