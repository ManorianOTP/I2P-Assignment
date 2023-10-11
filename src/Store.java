import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Store
{
	static boolean sessionActive = true;
	// https://www.w3schools.com/java/java_files_read.asp
	static File itemsFile = new File("TextFiles/items.txt");
	static File transactionsFile = new File("TextFiles/transactions.txt");

	public static void main(String[] args)
	{
		List<CSV> items = InventoryRead(itemsFile, false);
		List<CSV> transactions = InventoryRead(transactionsFile, true);
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
			case 5 -> sessionActive = false;
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
		System.out.println("---------------------------------");
		System.out.println("5. Exit\n");
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
		System.out.println("Report printed\n");
	}

	private static List<CSV> InventoryRead(File file, Boolean transaction) {
		List<CSV> result = new ArrayList<>();

		try {
			Scanner myReader = new Scanner(file);

			while (myReader.hasNextLine()) {
				String row = myReader.nextLine();
				// https://stackoverflow.com/questions/10631715/how-to-split-a-comma-separated-string
				List<String> parameterFileRow = Arrays.asList(row.split(","));
				result.add(new CSV(parameterFileRow, transaction));
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
}