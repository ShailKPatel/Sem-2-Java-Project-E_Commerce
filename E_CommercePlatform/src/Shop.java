import java.util.*;
import java.sql.*;

public class Shop {
	static int UID;
	static int CUID;
	public static Connection con;
	static Scanner sc = new Scanner(System.in);
	static PreparedStatement pst;

	public static void main(String args[]) throws Exception {
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/E_Commerce", "root", "");
		System.out.println("WELCOME TO ONLINE SHOPPING SYSTEM\n");
		int ch;
		do {
			System.out.print("*****************************************************\n");
			System.out.println("1 - REGISTER AS ADMIN");
			System.out.println("2 - REGISTER AS CUSTOMER");
			System.out.println("3 - LOGIN TO SYSTEM");
			System.out.println("4 - EXIT");
			System.out.println("*****************************************************\n");
			System.out.print("Enter choice : ");
			ch = sc.nextInt();
			sc.nextLine();

			switch (ch) {
				case 1:
					registerAdmin();
					break;
				case 2:
					registerCustomer();
					break;
				case 3:
					loginSystem();
					break;
				case 4:
					System.out.println("Thank you for Visiting");
					break;
				default:
					System.out.println("Invalid Option");
					break;
			}
		} while (ch != 4);

	}

	static void registerAdmin() {
		String pass, name, num, addr, email;
		int age;
		System.out.println("\nWELCOME TO ADMIN REGISTRATION PAGE\n");
		System.out.println("*****************************************************\n");
		setUID();
		System.out.println("ADMIN ID = " + UID);
		System.out.print("Enter Name = ");
		name = sc.nextLine();
		System.out.print("Enter password = ");
		pass = sc.nextLine();
		System.out.print("Enter age = ");
		age = sc.nextInt();
		sc.nextLine();
		System.out.print("Enter contact number = ");
		num = sc.nextLine();
		System.out.print("Enter address = ");
		addr = sc.nextLine();
		System.out.print("Enter email = ");
		email = sc.nextLine();

		// inserting data into database
		try {
			pst = con.prepareStatement(
					"insert into adminInfo(AdminID,Name,Age,Email,Address,ContactNumber) values(?,?,?,?,?,?)");
			PreparedStatement ps1 = con
					.prepareStatement("insert into loginInfo(UserId,password,userType) values(?,?,?)");
			pst.setString(1, Integer.toString(UID));
			pst.setString(2, name);
			pst.setString(3, Integer.toString(age));
			pst.setString(4, email);
			pst.setString(5, addr);
			pst.setString(6, num);
			ps1.setString(1, Integer.toString(UID));
			ps1.setString(2, pass);
			ps1.setString(3, "Admin");
			if (pst.executeUpdate() > 0 && ps1.executeUpdate() > 0)
				System.out.println("REGISTRATION DONE SUCCESSFULLY !\n");
			else
				System.out.println("REGISTRATION FAILED !\n");
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	static void registerCustomer() {
		String pass, name, num, addr, email;
		int age;
		System.out.println("\nWELCOME TO CUSTOMER REGISTRATION PAGE\n");
		System.out.println("*****************************************************\n");
		setCUID();
		System.out.println("CUSTOMER ID = " + CUID);
		System.out.print("Enter Name = ");
		name = sc.nextLine();
		System.out.print("Enter password = ");
		pass = sc.nextLine();
		System.out.print("Enter age = ");
		age = sc.nextInt();
		sc.nextLine();
		System.out.print("Enter contact number = ");
		num = sc.nextLine();
		System.out.print("Enter address = ");
		addr = sc.nextLine();
		System.out.print("Enter email = ");
		email = sc.nextLine();

		// inserting data into database
		try {
			pst = con.prepareStatement(
					"insert into custInfo(CustID,Name,Age,Email,Address,ContactNumber) values(?,?,?,?,?,?)");
			PreparedStatement ps1 = con
					.prepareStatement("insert into loginInfo(userID,password,userType) values(?,?,?)");
			pst.setString(1, Integer.toString(CUID));
			pst.setString(2, name);
			pst.setString(3, Integer.toString(age));
			pst.setString(4, email);
			pst.setString(5, addr);
			pst.setString(6, num);
			ps1.setString(1, Integer.toString(CUID));
			ps1.setString(2, pass);
			ps1.setString(3, "Customer");

			if (pst.executeUpdate() > 0 && ps1.executeUpdate() > 0)
				System.out.println("REGISTRATION DONE SUCCESSFULLY !\n");
			else
				System.out.println("REGISTRATION FAILED !\n");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	static void loginSystem() throws Exception {
		System.out.println("\nWELCOME TO LOGIN PAGE\n");
		System.out.println("*****************************************************\n");

		// Initialize lists to store user data from the database
		List<Integer> idList = new ArrayList<>();
		List<String> passwordList = new ArrayList<>();
		List<String> userTypeList = new ArrayList<>();

		// Retrieve login info from the database
		pst = con.prepareStatement("SELECT userID, password, userType FROM logininfo");
		ResultSet rs = pst.executeQuery();

		// Store data into lists
		while (rs.next()) {
			idList.add(rs.getInt("userID"));
			passwordList.add(rs.getString("password"));
			userTypeList.add(rs.getString("userType"));
		}

		// Variables to store user input and validation status
		int uid;
		String password;
		String userType = "";
		boolean isValid = false;

		do {
			// Prompt user for ID and password
			System.out.print("Enter USER ID: ");
			uid = sc.nextInt();
			sc.nextLine(); // Consume newline character

			System.out.print("Enter PASSWORD: ");
			password = sc.nextLine();

			// Validate credentials
			int idIndex = idList.indexOf(uid);
			int passwordIndex = passwordList.indexOf(password);

			if (idIndex == passwordIndex && idIndex != -1) {
				isValid = true;
				userType = userTypeList.get(idIndex);
			} else {
				// Invalid credentials message
				System.out.println("INVALID CREDENTIALS, PLEASE TRY AGAIN!");
				// Ask if the user wants to retry
				System.out.print("Do you want to continue (Y for Yes, N for No): ");
				String choice = sc.nextLine();
				if (choice.equalsIgnoreCase("N")) {
					return; // Exit the method if the user chooses not to retry
				}
			}
		} while (!isValid);

		// Navigate to the appropriate page based on user type
		switch (userType) {
			case "Admin":
				Admin admin = new Admin(uid, password);
				admin.AdminPage();
				break;
			case "Customer":
				Customer customer = new Customer(uid, password);
				customer.CustomerPage();
				break;
			default:
				System.out.println("Unknown user type.");
				break;
		}
	}

	static void setCUID() {
		try {
			pst = con.prepareStatement("select CustID from custinfo");
			ResultSet rs = pst.executeQuery();
			int x = 199;
			while (rs.next()) {
				x = Integer.parseInt(rs.getString("CustID"));
			}
			CUID = x + 1;
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(e);
		}
	}

	static void setUID() {
		try {
			pst = con.prepareStatement("select AdminID from admininfo");
			ResultSet rs = pst.executeQuery();
			int x = 99;
			while (rs.next()) {
				x = Integer.parseInt(rs.getString("AdminID"));
			}
			UID = x + 1;
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(e);
		}
	}
}
