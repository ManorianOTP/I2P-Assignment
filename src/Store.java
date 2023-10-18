import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Store
{
	static boolean sessionActive = true;
	static File itemsFile = new File("TextFiles/items.txt");
	static File transactionsFile = new File("TextFiles/transactions.txt");
	static List<CSV> items = CSVRead(itemsFile);
	static List<CSV> transactions = CSVRead(transactionsFile);

	// open the menu and start taking user input until they put a choice that cancels the session
	public static void main(String[] args)
	{
		DisplayMenu();

		while (sessionActive) {
			MenuInputChoice();
		}
		System.out.println("\n\nThanks for using this program...!");
	}

	// Keep taking user input from the console, as long as it's an integer do the appropriate menu function,
	// otherwise consume the invalid input and provide an error output
	private static void MenuInputChoice() {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter a choice and Press ENTER to continue[1-6]: ");
		int userInput = -1;
		try {
			userInput = input.nextInt();
		} catch (Exception e) {
			// consume the invalid input
			input.next();
		}
		switch (userInput) {
			case 1 -> AddItem();
			case 2 -> UpdateItemQuantity();
			case 3 -> RemoveItem();
			case 4 -> ViewDailyTransactionReport();
			case 5 -> ViewItems();
			case 6 -> sessionActive = false;
			default -> System.out.println("Unexpected error occurred, please enter an integer!");
		}
	}

	// The string's that get outputted to the menu to explain what your options are
	private static void DisplayMenu() {
		System.out.println("I N V E N T O R Y    M A N A G E M E N T    S Y S T E M");
		System.out.println("--------------------------------------------------------");
		System.out.println("1. ADD NEW ITEM");
		System.out.println("2. UPDATE QUANTITY OF EXISTING ITEM");
		System.out.println("3. REMOVE ITEM");
		System.out.println("4. VIEW DAILY TRANSACTION REPORT");
		System.out.println("5. VIEW ITEMS IN INVENTORY");
		System.out.println("---------------------------------");
		System.out.println("6. Exit\n");
	}

	// Generates an ID, requests an input for the appropriate fields, shows the user what they input, and asks for
	// confirmation if they want it added. If yes, create a CSV object using the headers and the provided fields
	// and then use it's to CSV function along with a buffered writer to write the fields into the file
	private static void AddItem() {
		Scanner input = new Scanner(System.in);

		String id = GenerateID();

		System.out.print("Enter item description: ");
		String description = input.nextLine();

		System.out.print("Enter unit price: ");
		double unitPrice = input.nextDouble();

		System.out.print("Enter quantity in stock: ");
		int qtyInStock = input.nextInt();

		double totalPrice = unitPrice * qtyInStock;

		List<String> headers = Arrays.asList("id", "description", "unitPrice", "qtyInStock", "totalPrice");
		List<String> parameters = Arrays.asList(String.valueOf(id),description,String.valueOf(unitPrice),String.valueOf(qtyInStock),String.valueOf(totalPrice));

		System.out.println("\nItem To Be Added:");
		System.out.println("ID: " + id);
		System.out.println("Description: " + description);
		System.out.println("Unit Price: " + unitPrice);
		System.out.println("Quantity in Stock: " + qtyInStock);
		System.out.println("Total Price: " + totalPrice + "\n");

		// User Confirmation
		System.out.print("Do you want to add this item to the file? (yes/no): ");
		input.nextLine();  // consume the remaining newline character from using input.nextInt() prior
		String confirmation = input.nextLine().trim().toLowerCase();

		if (confirmation.equals("yes") || confirmation.equals("y")) {
			CSV newItem = new CSV(parameters,headers);
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(itemsFile, true))) {  // 'true' means we're appending to the file
				bw.newLine();  // Add a new line for the new row
				bw.write(newItem.toCSVFileOutput());  // Write the new row content
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			items = CSVRead(itemsFile);
			System.out.println("New Item Added\n");
		} else {
			System.out.println("Item not added.\n");
		}
	}

	// Will update an item quantity, currently has placeholder text
	private static void UpdateItemQuantity() {
		System.out.println("Item quantity updated\n");
	}

	// Will remove an item, currently has placeholder text
	private static void RemoveItem() {
		System.out.println("Item Removed\n");
	}

	// Outputs the transactions list by reading the transactions file, then printing each row to the console
	private static void ViewDailyTransactionReport() {
		transactions = CSVRead(transactionsFile);
		for (CSV csv : transactions) {
			System.out.println(csv);
		}
	}

	// Outputs the items list by reading the items file, then printing each row to the console
	// maybe make output all out of stock separately
	private static void ViewItems() {
		items = CSVRead(itemsFile);
		for (CSV csv : items) {
			System.out.println(csv);
		}
	}

	// generates the next unused 5-digit ID, filling in the preceding digits with 0 if it's not a naturally 5-digit
	// number
	private static String GenerateID() {
		CSV lastRow = items.get(items.size() - 1);

		if (Integer.parseInt(lastRow.id) >= 100_000) {
			throw new RuntimeException("ID exceeds the maximum allowed value");
		}

		return String.format("%05d", Integer.parseInt(lastRow.id) + 1);
	}


	// Tries to read the contents of the provided file, if it can it takes the first row to be the headers, split's them
	// by comma, and stores them. Then for every following row it splits them appropriately, and creates a CSV object
	// based off the header row and the values found in the current row, which it adds to a list
	// could be made more efficient by only reading changed/unread rows
	private static List<CSV> CSVRead(File file) {
		List<CSV> result = new ArrayList<>();

		try {
			Scanner myReader = new Scanner(file);
			List<String> headers = Arrays.asList(myReader.nextLine().split(","));

			while (myReader.hasNextLine()) {
				String row = myReader.nextLine();
				List<String> parameterFileRow = Arrays.asList(row.split(","));
				result.add(new CSV(parameterFileRow, headers));
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
}