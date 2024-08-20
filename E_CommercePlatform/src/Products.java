import java.sql.*;
import java.util.Scanner;

public class Products {
	private int pid, qty;
	private String name, type;
	private float price;
	private static final Scanner sc = new Scanner(System.in);
	Connection con = Shop.con;
	PreparedStatement pst;

	public void ProductsPage() {
		Products ob = new Products();
		System.out.println("\nWELCOME TO PRODUCTS MANAGEMENT PAGE\n");
		int ch;
		do {
			System.out.println("*****************************************************\n");
			System.out.println("1 - ADD PRODUCTS");
			System.out.println("2 - REMOVE PRODUCTS");
			System.out.println("3 - ALTER PRODUCT INFO");
			System.out.println("4 - VIEW ALL PRODUCTS");
			System.out.println("5 - SEARCH A PARTICULAR PRODUCT");
			System.out.println("6 - EXIT PAGE");
			System.out.println("*****************************************************\n");
			System.out.print("Enter choice: ");
			ch = sc.nextInt();
			sc.nextLine(); // Consume newline

			switch (ch) {
				case 1 -> ob.addProducts();
				case 2 -> ob.removeProducts();
				case 3 -> ob.alterProduct();
				case 4 -> ob.viewProducts();
				case 5 -> ob.searchProduct();
				case 6 -> System.out.println("Thank you");
				default -> System.out.println("Wrong choice");
			}
		} while (ch != 6);
	}

	private void alterProduct() {
		int x = 0;
		String choice;
		try {
			pst = con.prepareStatement("select * from products");
			ResultSet rs = pst.executeQuery();

			if (rs.last()) {
				x = rs.getRow();
				rs.beforeFirst();
			}

			if (x == 0) {
				System.out.println("NO PRODUCTS AVAILABLE");
			} else {
				do {
					System.out.print("Enter product ID to update info: ");
					pid = sc.nextInt();
					sc.nextLine(); // Consume newline
					boolean productFound = false;

					while (rs.next()) {
						if (rs.getInt("productID") == pid) {
							productFound = true;
							name = rs.getString("Name");
							type = rs.getString("Type");
							qty = rs.getInt("Quantity");
							price = rs.getFloat("Price");

							do {
								System.out.println("FETCHED PRODUCT INFO:\n");
								System.out.printf("Product ID   = %-5d\n", pid);
								System.out.printf("Product Name = %-20s\n", name);
								System.out.printf("Product Type = %-20s\n", type);
								System.out.printf("Quantity     = %-5d\n", qty);
								System.out.printf("Price        = %-10.2f\n", price);
								System.out.println("\n1 - UPDATE PRODUCT NAME");
								System.out.println("\n2 - UPDATE PRODUCT TYPE");
								System.out.println("\n3 - UPDATE PRODUCT QUANTITY");
								System.out.println("\n4 - UPDATE PRICE");
								System.out.print("\nEnter choice: ");
								int updateChoice = sc.nextInt();
								sc.nextLine(); // Consume newline

								switch (updateChoice) {
									case 1 -> {
										System.out.print("ENTER NEW NAME: ");
										name = sc.nextLine();
									}
									case 2 -> {
										System.out.print("ENTER NEW TYPE: ");
										type = sc.nextLine();
									}
									case 3 -> {
										System.out.print("ENTER NEW QUANTITY: ");
										qty = sc.nextInt();
										sc.nextLine(); // Consume newline
									}
									case 4 -> {
										System.out.print("ENTER NEW PRICE: ");
										price = sc.nextFloat();
										sc.nextLine(); // Consume newline
									}
									default -> System.out.println("Invalid choice.");
								}

								System.out.print("DO YOU WANT TO CONTINUE (Y for yes, N for No): ");
								choice = sc.nextLine();
							} while (choice.equalsIgnoreCase("Y"));

							updateProductInfo(con, pid, name, type, qty, price);
						}
					}

					if (!productFound)
						System.out.println("PRODUCT NOT FOUND!");

					System.out.print("DO YOU WANT TO CONTINUE (Y for yes, N for no): ");
					choice = sc.nextLine();
					rs.beforeFirst();
				} while (choice.equalsIgnoreCase("Y"));
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void updateProductInfo(Connection con, int pid, String name, String type, int qty, float price)
			throws SQLException {
		PreparedStatement ps1 = con.prepareStatement("update products set Name = ? where productID=?");
		PreparedStatement ps2 = con.prepareStatement("update products set Type = ? where productID=?");
		PreparedStatement ps3 = con.prepareStatement("update products set Quantity = ? where productID=?");
		PreparedStatement ps4 = con.prepareStatement("update products set Price = ? where productID=?");

		ps1.setString(1, name);
		ps2.setString(1, type);
		ps3.setInt(1, qty);
		ps4.setFloat(1, price);

		ps1.setInt(2, pid);
		ps2.setInt(2, pid);
		ps3.setInt(2, pid);
		ps4.setInt(2, pid);

		int x1 = ps1.executeUpdate();
		int x2 = ps2.executeUpdate();
		int x3 = ps3.executeUpdate();
		int x4 = ps4.executeUpdate();

		if (x1 > 0 && x2 > 0 && x3 > 0 && x4 > 0)
			System.out.println("PRODUCT INFO UPDATED SUCCESSFULLY!");
	}

	private void searchProduct() {
		int flag = 0;
		String choice;
		try {
			pst = con.prepareStatement("select * from products");
			ResultSet rs = pst.executeQuery();

			if (rs.last()) {
				rs.beforeFirst();
			} else {
				System.out.println("NO PRODUCTS AVAILABLE");
				return;
			}

			do {
				System.out.print("Enter product ID to search: ");
				pid = sc.nextInt();
				sc.nextLine(); // Consume newline
				PreparedStatement ps1 = con.prepareStatement("select * from products where productID=?");
				ps1.setInt(1, pid);
				ResultSet rs1 = ps1.executeQuery();
				flag = 0;

				while (rs1.next()) {
					System.out.printf("Product ID   =  %-5d\n", rs1.getInt("productID"));
					System.out.printf("Product Name =  %-20s\n", rs1.getString("Name"));
					System.out.printf("Product Type =  %-20s\n", rs1.getString("Type"));
					System.out.printf("Quantity     =  %-5d\n", rs1.getInt("Quantity"));
					System.out.printf("Price        =  %-10.2f\n", rs1.getFloat("Price"));
					flag = 1;
				}

				if (flag == 0)
					System.out.println("PRODUCT NOT FOUND!");

				System.out.print("Do you want to continue, press Y for 'yes' N for 'no': ");
				choice = sc.nextLine();
			} while (choice.equalsIgnoreCase("Y"));
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println();
	}

	private void removeProducts() {
		String choice;
		try {
			pst = con.prepareStatement("delete from products where productID=?");

			do {
				System.out.print("Enter product ID to delete: ");
				pid = sc.nextInt();
				sc.nextLine(); // Consume newline
				pst.setInt(1, pid);
				int x = pst.executeUpdate();

				if (x == 0)
					System.out.println("PRODUCT NOT FOUND!");
				else
					System.out.println("PRODUCT DELETED SUCCESSFULLY!");

				System.out.print("Do you want to continue (Y for YES, N for NO): ");
				choice = sc.nextLine();
			} while (choice.equalsIgnoreCase("Y"));
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void addProducts() {
		String choice;
		System.out.println("*****************************************************\n");
		try {
			pst = con
					.prepareStatement("insert into products(productID,Name,Type,Quantity,Price) values(?,?,?,?,?)");
			do {
				pid = setPid();
				System.out.println("Product ID = " + pid);
				System.out.print("Enter Name: ");
				name = sc.nextLine();
				System.out.print("Enter Type: ");
				type = sc.nextLine();
				System.out.print("Enter Quantity: ");
				qty = sc.nextInt();
				System.out.print("Enter Price: ");
				price = sc.nextFloat();
				sc.nextLine(); // Consume newline

				pst.setInt(1, pid);
				pst.setString(2, name);
				pst.setString(3, type);
				pst.setInt(4, qty);
				pst.setFloat(5, price);

				int x = pst.executeUpdate();
				if (x > 0)
					System.out.println("Product Added!");

				System.out.print("Do you want to continue, press Y for 'yes' N for 'no': ");
				choice = sc.nextLine();
			} while (choice.equalsIgnoreCase("Y"));
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println();
	}

	private void viewProducts() {
		int x = 0;
		try {
			pst = con.prepareStatement("select * from products");
			ResultSet rs = pst.executeQuery();

			if (rs.last()) {
				x = rs.getRow();
				rs.beforeFirst();
			}

			if (x == 0) {
				System.out.println("NO PRODUCTS AVAILABLE");
			} else {
				System.out.println("AVAILABLE PRODUCTS ARE:\n");
				System.out
						.println("PRODUCT ID   PRODUCT NAME             PRODUCT TYPE           QUANTITY        PRICE");
				while (rs.next()) {
					System.out.printf("%-12d%-25s%-25s%-10d%-10.2f\n", rs.getInt("productID"), rs.getString("Name"),
							rs.getString("Type"),
							rs.getInt("Quantity"), rs.getFloat("Price"));
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println();
	}

	private int setPid() {
		int maxPid = 0;
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select max(productID) from products");

			if (rs.next())
				maxPid = rs.getInt(1);
		} catch (Exception e) {
			System.out.println(e);
		}
		return maxPid + 1;
	}
}
