import java.util.Scanner;

public class store
{
	static int userInput;
	static boolean sessionActive = true;
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
		userInput = input.nextInt();
		switch (userInput) {
			case 1 -> AddItem();
			case 2 -> UpdateItemQuantity();
			case 3 -> RemoveItem();
			case 4 -> ViewDailyTransactionReport();
			case 5 -> sessionActive = false;
			default -> System.out.println("Unexpected error occurred...!");
		}
	}

	private static void DisplayMenu() {
		System.out.println("I N V E N T O R Y    M A N A G E M E N T    S Y S T E M");
		System.out.println("-----------------------------------------------");
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
}