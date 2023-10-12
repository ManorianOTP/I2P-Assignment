import java.io.File;
import java.io.FileNotFoundException;
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

	public static void main(String[] args)
	{
		Scanner input = new Scanner(System.in);
		DisplayMenu();

		while(sessionActive) {
			MenuInputChoice(input);
		}
		System.out.println("\n\nThanks for using this program...!");
	}

	private static void MenuInputChoice(Scanner input) {
		System.out.print("Enter a choice and Press ENTER to continue[1-5]: ");
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

	private static void AddItem() {
		System.out.println("New Item Added\n");
	}

	private static void UpdateItemQuantity() {
		System.out.println("Item quantity updated\n");
	}

	private static void RemoveItem() {
		System.out.println("Item Removed\n");
	}

	private static void ViewDailyTransactionReport() {
		transactions = CSVRead(transactionsFile);
		for (CSV csv : transactions) {
			System.out.println(csv);
		}
	}

	private static void ViewItems() {
		items = CSVRead(itemsFile);
		for (CSV csv : items) {
			System.out.println(csv);
		}
	}

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