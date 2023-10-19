import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents an Inventory Management System that allows users to manage items and view transactions.
 * The system provides functionalities to:
 * <ul>
 *   <li>Search for items based on various properties.</li>
 *   <li>Add new items to the inventory.</li>
 *   <li>Update the quantity of existing items.</li>
 *   <li>Remove items from the inventory.</li>
 *   <li>View past transactions either for the current day or all-time.</li>
 *   <li>View all items currently in the inventory.</li>
 * </ul>
 *
 * <p>
 * The system stores its data in two text files, one for items and another for transactions. The contents of these
 * files are read into memory as lists of CSV objects at startup.
 * </p>
 *
 * <p>
 * When interacting with the system, users are presented with a text-based menu. They can choose the desired
 * functionality by entering the corresponding number. The program continues to run and provide the menu until
 * the user decides to exit.
 * </p>
 *
 * @author S257123
 * @version 1.0
 */
public class InventoryManagementSystem
{
	static boolean sessionActive = true;
	static File itemsFile = new File("TextFiles/items.txt");
	static File transactionsFile = new File("TextFiles/transactions.txt");
	static List<CSV> items = CSVRead(itemsFile);
	static List<CSV> transactions = CSVRead(transactionsFile);

	/**
	 * Entry point for the Inventory Management System.
	 *
	 * <p>
	 * Upon starting, the system displays a menu of options. The user can then interact with this menu
	 * by choosing various functionalities. The program continues to prompt the user until they decide to
	 * exit the session. On exit, a farewell message is displayed.
	 * </p>
	 *
	 * @param args Command-line arguments (currently unused in this context).
	 *
	 * @see #DisplayMenu()
	 * @see #MenuInputChoice()
	 */
	public static void main(String[] args)	{
		DisplayMenu();

		while (sessionActive) {
			MenuInputChoice();
		}
		System.out.println("\n\nThanks for using this program...!");
	}

	/**
	 * Interprets and executes the user's menu choice.
	 *
	 * <p>
	 * The user is prompted to enter a choice corresponding to the menu options. If the input is a valid integer
	 * between 1 and 7, the corresponding function for that menu option is executed. Otherwise, an error message
	 * is displayed prompting the user to enter a valid integer.
	 * The available options are:
	 * <ol>
	 *   <li>Search for an item</li>
	 *   <li>Add a new item</li>
	 *   <li>Update quantity of an existing item</li>
	 *   <li>Remove an item</li>
	 *   <li>View transactions</li>
	 *   <li>View items in inventory</li>
	 *   <li>Exit the session</li>
	 * </ol>
	 * </p>
	 *
	 * @see #SearchPropertyUserInput(List)
	 * @see #AddItem()
	 * @see #UpdateItemQuantity()
	 * @see #RemoveItem()
	 * @see #ViewTransactions()
	 * @see #ViewItems()
	 */
	private static void MenuInputChoice() {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter a choice and Press ENTER to continue[1-7]: ");
		int userInput = -1;
		try {
			userInput = input.nextInt();
		} catch (Exception e) {
			input.next(); // consume the invalid input
		}
		switch (userInput) {
			case 1 -> SearchPropertyUserInput(items);
			case 2 -> AddItem();
			case 3 -> UpdateItemQuantity();
			case 4 -> RemoveItem();
			case 5 -> ViewTransactions();
			case 6 -> ViewItems();
			case 7 -> sessionActive = false;
			default -> System.out.println("Unexpected error occurred, please enter an integer!");
		}
	}

	/**
	 * Displays a menu for the Inventory Management System.
	 *
	 * <p>
	 * The menu provides the user with the following options:
	 * <ol>
	 *   <li>SEARCH FOR ITEM</li>
	 *   <li>ADD NEW ITEM</li>
	 *   <li>UPDATE QUANTITY OF EXISTING ITEM</li>
	 *   <li>REMOVE ITEM</li>
	 *   <li>VIEW TRANSACTIONS</li>
	 *   <li>VIEW ITEMS IN INVENTORY</li>
	 *   <li>Exit</li>
	 * </ol>
	 * </p>
	 */
	private static void DisplayMenu() {
		System.out.println("I N V E N T O R Y    M A N A G E M E N T    S Y S T E M");
		System.out.println("--------------------------------------------------------");
		System.out.println("1. SEARCH FOR ITEM");
		System.out.println("2. ADD NEW ITEM");
		System.out.println("3. UPDATE QUANTITY OF EXISTING ITEM");
		System.out.println("4. REMOVE ITEM");
		System.out.println("5. VIEW TRANSACTIONS");
		System.out.println("6. VIEW ITEMS IN INVENTORY");
		System.out.println("---------------------------------");
		System.out.println("7. Exit\n");
	}

	/**
	 * Facilitates the process of adding a new item to the items file.
	 * <p>
	 * This method performs the following steps:
	 * <ol>
	 *   <li>Generates a unique ID for the new item.</li>
	 *   <li>Prompts the user for item description, unit price, and quantity in stock.</li>
	 *   <li>Calculates the total price based on unit price and quantity.</li>
	 *   <li>Displays a summary of the entered data for the user to review.</li>
	 *   <li>Asks for user confirmation before adding the item to the file.</li>
	 *   <li>If confirmed, writes the new item to the items file.</li>
	 * </ol>
	 * Invalid input for unit price or quantity will prompt the user to re-enter the correct data.
	 * </p>
	 *
	 * @see #GenerateID()
	 * @see #WriteToFile(List, List, File)
	 */
	private static void AddItem() {
		Scanner input = new Scanner(System.in);

		String id = GenerateID();

		System.out.print("Enter item description: ");
		String description = input.nextLine();

		System.out.print("Enter unit price: ");
		while (!input.hasNextDouble()) {
			System.out.println("That's not a valid unit price. Please enter a number.");
			input.next(); // Consume the invalid input
		}
		double unitPrice = input.nextDouble();

		System.out.print("Enter quantity in stock: ");
		while (!input.hasNextInt()) {
			System.out.println("That's not a valid quantity. Please enter an integer.");
			input.next(); // Consume the invalid input
		}
		int qtyInStock = input.nextInt();

		double totalPrice = unitPrice * qtyInStock;

		List<String> headers = items.get(0).definedFields.stream().toList();
		List<String> parameters = Arrays.asList(String.valueOf(id),description,String.valueOf(unitPrice),String.valueOf(qtyInStock),String.valueOf(totalPrice));

		System.out.println("\nItem To Be Added:");
		System.out.println("ID: " + id);
		System.out.println("Description: " + description);
		System.out.println("Unit Price: " + unitPrice);
		System.out.println("Quantity in Stock: " + qtyInStock);
		System.out.println("Total Price: " + totalPrice + "\n");

		// User Confirmation
		System.out.print("Do you want to add this item to the file? (yes/no): ");
		input.nextLine();  // Consume the remaining newline character from using input.nextInt() prior
		String confirmation = input.nextLine().trim().toLowerCase();

		if (confirmation.equals("yes") || confirmation.equals("y")) {
			WriteToFile(headers, parameters, itemsFile);
			System.out.println("New Item Added\n");
		} else {
			System.out.println("Item not added.\n");
		}
	}

	/**
	 * Writes a new record to the specified file based on provided headers and parameters
	 * and then updates the {@code items} list with the latest file contents.
	 * <p>
	 * The method creates a new {@code CSV} object using the given headers and parameters.
	 * It then appends this record to the file. Upon successful writing, the file's
	 * contents are read and stored into the {@code items} list.
	 * </p>
	 *
	 * @param headers   The list of headers for the CSV record.
	 * @param parameters The list of values corresponding to the headers.
	 * @param file      The file to which the new record should be appended.
	 * @throws RuntimeException if an IOException occurs while writing to or reading from the file.
	 * @see CSV#CSV(List, List)
	 * @see #CSVRead(File)
	 */
	private static void WriteToFile(List<String> headers, List<String> parameters, File file) {
		CSV newItem = new CSV(parameters, headers);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
			bw.newLine();  // Add a new line for the new row
			bw.write(newItem.toCSVFileOutput());  // Write the new row content
			bw.flush(); // Close the writer to make it available for CSVRead due to CSVRead being in the try block
			items = CSVRead(file); // read the files only if the prior write has succeeded
		} catch (IOException e) {
			throw new RuntimeException(e);
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

	/**
	 * Prompts the user to view transactions either from today or from all time,
	 * and then displays the selected transactions to the console.
	 * <p>
	 * The user is asked to input either 'today' or 'all'. If the input is 'today',
	 * the method will display transactions from the current day.
	 * If the input is 'all', all transactions will be displayed.
	 * </p>
	 *
	 * @see #SearchPropertyByNameValue(List, String, String)
	 */
	private static void ViewTransactions() {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Do you want to view all the transactions from today, or all time? (today/all): ");
		String input = scanner.nextLine().trim().toLowerCase();

		while (!input.equals("today") && !input.equals("all")) {
			System.out.println("Invalid input. Please enter 'today' or 'all'.");
			input = scanner.nextLine().trim().toLowerCase();
		}

		if (input.equals("today")) {
			String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
			SearchPropertyByNameValue(transactions, "date", today);
		} else {
			SearchPropertyByNameValue(transactions,"date", "");
		}
	}

	/**
	 * Prompts the user for a property name and a value, and then searches the specified
	 * CSV list for records that match the given property-value pair.
	 * <p>
	 * The method continuously prompts the user for a valid property name until one is provided
	 * that exists in the {@code definedFields} of the first CSV object in the list.
	 * </p>
	 *
	 * @param csvs A list of CSV objects. All CSVs in this list should have the same set of defined fields,
	 *              as only the fields from the first CSV are displayed to the user.
	 * @see CSV#definedFields
	 * @see #SearchPropertyByNameValue(List, String, String)
	 */
	private static void SearchPropertyUserInput(List<CSV> csvs) {
		Scanner input = new Scanner(System.in);

		System.out.println("Please input a property name from the following list");
		System.out.println(csvs.get(0).definedFields);
		String propertyName = input.nextLine();
		while (!csvs.get(0).definedFields.contains(propertyName)) {
			System.out.println("Please input a valid property name from the following list");
			System.out.println(csvs.get(0).definedFields);
			propertyName = input.nextLine();
		}
		System.out.println("Please input a value");
		String value = input.nextLine();
		SearchPropertyByNameValue(csvs, propertyName, value);
	}

	/**
	 * Searches and prints CSV rows where the given property matches or contains the specified value.
	 *
	 * <p>
	 * Iterates through the list of provided CSV objects, checking if the property specified by
	 * {@code propertyName} matches (or contains) the provided {@code value}. Matching is done using a
	 * regular expression, but the value is treated as a literal string, not as a regex pattern to avoid issues with "."
	 * in Regex. All matching CSV rows are printed to the console.
	 * </p>
	 *
	 * @param csvs         List of CSV objects to be searched.
	 * @param propertyName The name of the property to be checked.
	 * @param value        The value to be searched for in the specified property.
	 *
	 * @see CSV#GetPropertyByName(String)
	 */
	private static void SearchPropertyByNameValue(List<CSV> csvs, String propertyName, String value) {
		for (CSV csv: csvs) {
			// Pattern.quote to treat the user input as not regex, with 0+ wildcards preceding and following
			if (csv.GetPropertyByName(propertyName).toString().matches((".*" + Pattern.quote(value) + ".*"))) {
				System.out.println(csv);
			}
		}
	}

	/**
	 * Displays the items from the items file to the console.
	 *
	 * <p>
	 * The method reads the items from the {@code itemsFile} using the {@link #CSVRead(File)} function
	 * and then iterates through the list, printing each CSV object to the console.
	 * <p>
	 *  Note: A potential enhancement might be to display out-of-stock items separately at the bottom
	 * </p>
	 * </p>
	 *
	 * @see #CSVRead(File)
	 */
	private static void ViewItems() {
		items = CSVRead(itemsFile);
		for (CSV csv : items) {
			System.out.println(csv);
		}
	}

	/**
	 * Generates the next unused 5-digit ID, based on the last ID in the {@code items} list.
	 * <p>
	 * If the last ID in the list is less than 5 digits, the returned ID will be padded
	 * with leading zeros to ensure a 5-digit length. If the last ID exceeds or equals
	 * 100,000, a runtime exception will be thrown since this is beyond the allowed range for 5-digit IDs.
	 * </p>
	 *
	 * @return A 5-digit string representing the next unused ID.
	 * @throws RuntimeException if the last ID in the {@code items} list exceeds or equals 100,000.
	 * @see CSV#id
	 */
	private static String GenerateID() {
		CSV lastRow = items.get(items.size() - 1);

		if (Integer.parseInt(lastRow.id) >= 100_000) {
			throw new RuntimeException("ID exceeds the maximum allowed value");
		}

		return String.format("%05d", Integer.parseInt(lastRow.id) + 1);
	}


	/**
	 * Reads the contents of the provided CSV file and returns a list of {@code CSV} objects.
	 * <p>
	 * The method assumes the first row of the file to be the headers. Each header is separated
	 * by a comma. Subsequent rows are interpreted as data, where each value is associated with
	 * a header based on its position. Each row is transformed into a {@code CSV} object and
	 * added to the resulting list.
	 * </p>
	 * <p>
	 * Note: This method reads the entire file every time it's called. Potential performance
	 * improvements could be achieved by reading only changed or unread rows.
	 * </p>
	 *
	 * @param file The CSV file to be read.
	 * @return A list of {@code CSV} objects constructed from the contents of the file.
	 * @throws RuntimeException if the provided file is not found or other IO issues occur.
	 * @see CSV#CSV(List, List)
	 */
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