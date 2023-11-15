package uni.S257123.main;

import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.storage.text.TextStorage;
import uni.S257123.ui.console.ConsoleInterface;
import uni.S257123.ui.interfaces.UserInterface;

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
 *<p>
 * This entire project has been commented using javadoc comments, so if you're ever uncertain about how something works
 * try hovering it in your IDE, or using the javadoc tool to create web documentation
 * </p>
 * <p>Note: there are plans to both change the CLI to a GUI, and add database support</p>
 *
 * @author S257123
 * @version 1.0
 */
public class InventoryManagementSystem
{
	static boolean sessionActive = true;
	static int optionsQuantity = 7;
	static ConsoleInterface ui = new ConsoleInterface();
	static TextStorage storage = new TextStorage();

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
	 */
	public static void main(String[] args)	{

		ui.displayMenu();

		while (sessionActive) {
			menuOptions(ui.menuInputChoice(optionsQuantity));
		}
		System.out.println("\n\nThanks for using this program...!");
	}

	/**
	 * Runs the appropriate menu method based on the users input.
	 * <p>
	 * These options are generically represented using interfaces, so based on the {@link #ui} and {@link #storage} variables
	 * declarations this will either be running cli + text backend, or GUI + database backend, or a mixture of the two.
	 * </p>
	 * @param option an integer that represents what method the user wants to run
	 * @see UserInterface#menuInputChoice
	 */
	public static void menuOptions(int option) {
		switch (option) {
			case 1 -> {
				String selectedSource = ui.chooseOption(storage.getSources());
				ui.displayRecords(
						storage.searchRecord(
								selectedSource,
								ui.propertySearchInput(storage.getHeaders(selectedSource))
						)
				);
			}
			case 2 -> storage.addRecord(ui.addRecordInput(),"items");
			case 3 -> storage.updateRecord(ui.updateRecordInput(storage.searchRecord(), storage.getHeaders("items")));
			case 4 -> storage.deleteRecord(ui.deleteRecordInput(storage.searchRecord()));
			case 5 -> ui.displayRecords(
					storage.searchRecord(
							"transactions",
							Pair.of("date",ui.viewTransactionsInput())));
			case 6 -> ui.displayRecords(storage.searchRecord());
			case 7 -> sessionActive = false;
			default -> System.out.println("Unexpected error occurred, please enter an integer!");
		}
	}
}