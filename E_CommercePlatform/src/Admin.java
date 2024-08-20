import java.util.*;
import java.sql.*;

public class Admin extends Shop {
	private int adminID;
	static PreparedStatement pst;
	static Scanner sc = new Scanner(System.in);

	Admin(int adminID, String password) {
		this.adminID = adminID;
	}

	public void AdminPage() throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("WELCOME TO ADMIN SECTION\n");
		int ch;
		do {
			System.out.println("*****************************************************\n");
			System.out.println("1 - MANAGE PRODUCTS");
			System.out.println("2 - ADD CUSTOMERS");
			System.out.println("3 - REMOVE CUSTOMERS");
			System.out.println("4 - EDIT PROFILE");
			System.out.println("5 - VIEW REGISTERED CUSTOMERS");
			System.out.println("6 - LOGOUT FROM SYSTEM");
			System.out.println("*****************************************************\n");
			System.out.print("Enter choice: ");
			ch = sc.nextInt();
			sc.nextLine(); // To consume newline

			switch (ch) {
				case 1:
					Products ob = new Products();
					ob.ProductsPage();
					break;
				case 2:
					addCustomer();
					break;
				case 3:
					removeCustomer();
					break;
				case 4:
					editProfile(adminID);
					break;
				case 5:
					viewCustomers();
					break;
				case 6:
					System.out.println("Thank you");
					break;
				default:
					System.out.println("Wrong choice");
			}
		} while (ch != 6);
		sc.close();
	}

	private static void editProfile(int adminID) {
		try {
			String chc;
			String s = "";
			int fc = -1;
			String name = "", email = "", addr = "", contact = "", passw = "";
			int age = 0;
			int ch;

			do {
				System.out.println("************************************************************");
				System.out.println("1 - EDIT NAME");
				System.out.println("2 - EDIT AGE");
				System.out.println("3 - EDIT EMAIL ID");
				System.out.println("4 - EDIT ADDRESS");
				System.out.println("5 - EDIT CONTACT NUMBER");
				System.out.println("6 - CHANGE PASSWORD");
				System.out.println("7 - EXIT");
				System.out.println("************************************************************");
				System.out.print("Enter choice: ");
				ch = sc.nextInt();
				sc.nextLine(); // To consume newline

				switch (ch) {
					case 1:
						System.out.print("ENTER NEW NAME: ");
						name = sc.nextLine();
						s = "Name";
						fc = 1;
						break;
					case 2:
						System.out.print("ENTER AGE: ");
						age = sc.nextInt();
						sc.nextLine(); // To consume newline
						s = "Age";
						fc = 1;
						break;
					case 3:
						System.out.print("ENTER NEW EMAIL ID: ");
						email = sc.nextLine();
						s = "Email";
						fc = 1;
						break;
					case 4:
						System.out.print("ENTER ADDRESS: ");
						addr = sc.nextLine();
						s = "Address";
						fc = 1;
						break;
					case 5:
						System.out.print("ENTER NEW CONTACT NUMBER: ");
						contact = sc.nextLine();
						s = "ContactNumber";
						fc = 1;
						break;
					case 6:
						System.out.print("ENTER NEW PASSWORD: ");
						passw = sc.nextLine();
						s = "password";
						fc = 0;
						break;
					case 7:
						System.out.println("Thank you");
						return;
					default:
						System.out.println("Wrong choice");
						break;
				}

				if (fc == 1) {
					String query = "update admininfo set " + s + " = ? where AdminID=?";
					pst = con.prepareStatement(query);

					if (s.equalsIgnoreCase("Name")) {
						pst.setString(1, name);
					} else if (s.equalsIgnoreCase("Age")) {
						pst.setInt(1, age);
					} else if (s.equalsIgnoreCase("Email")) {
						pst.setString(1, email);
					} else if (s.equalsIgnoreCase("Address")) {
						pst.setString(1, addr);
					} else if (s.equalsIgnoreCase("ContactNumber")) {
						pst.setString(1, contact);
					}

					pst.setInt(2, adminID);

					if (pst.executeUpdate() != 0)
						System.out.println("INFORMATION UPDATED SUCCESSFULLY!");
				} else if (fc == 0) {
					pst = con.prepareStatement("update logininfo set password=? where userID=?");
					pst.setString(1, passw);
					pst.setInt(2, adminID);

					if (pst.executeUpdate() != 0)
						System.out.println("PASSWORD CHANGED SUCCESSFULLY!");
				}

				System.out.print("Do you want to continue ( Y for yes, N for No ): ");
				chc = sc.nextLine();

			} while (chc.equalsIgnoreCase("Y"));
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static void viewCustomers() {

		try {
			pst = con.prepareStatement("select * from custinfo");
			ResultSet rs = pst.executeQuery();

			if (!rs.isBeforeFirst()) {
				System.out.println("NO CUSTOMERS AVAILABLE");
			} else {
				System.out.print(
						"**********************************************************************************************************************************************************************\n");
				System.out.printf("%-20s \t %-20s \t %-10s \t %-20s \t %-30s \t %-20s\n", "CUSTOMER_ID", "NAME", "AGE",
						"EMAIL", "ADDRESS", "CONTACT_NUMBER");
				System.out.println(
						"**********************************************************************************************************************************************************************\n");

				while (rs.next()) {
					int cid = rs.getInt("custID");
					String name = rs.getString("Name");
					int age = rs.getInt("Age");
					String email = rs.getString("Email");
					String addr = rs.getString("Address");
					String contact = rs.getString("ContactNumber");

					System.out.printf("%-20d \t %-20s \t %-10d \t %-20s \t %-30s \t %-20s\n", cid, name, age, email,
							addr, contact);
				}

				System.out.println(
						"*********************************************************************************************************************************************************************\n");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static void addCustomer() {
		Shop.registerCustomer();
	}

	private static void removeCustomer() {
		try {
			pst = con.prepareStatement("select * from custinfo");
			ResultSet rs = pst.executeQuery();

			if (!rs.isBeforeFirst()) {
				System.out.println("NO CUSTOMERS AVAILABLE");
			} else {
				System.out.print("Enter customer ID to delete: ");
				int cid = sc.nextInt();
				sc.nextLine(); // To consume newline

				pst = con.prepareStatement("delete from custinfo where custID=?");

				pst.setInt(1, cid);

				if (pst.executeUpdate() != 0)
					System.out.println("CUSTOMER INFO DELETED SUCCESSFULLY!");
				else
					System.out.println("CUSTOMER INFO NOT FOUND!");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
