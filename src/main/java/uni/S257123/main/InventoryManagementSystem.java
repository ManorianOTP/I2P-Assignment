package uni.S257123.main;

import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.storage.database.DatabaseStorage;
import uni.S257123.storage.interfaces.Storage;
import uni.S257123.storage.text.TextStorage;
import uni.S257123.ui.console.ConsoleInterface;
import uni.S257123.ui.graphical.GraphicalInterface;
import uni.S257123.ui.interfaces.UserInterface;

import java.util.ArrayList;
import java.util.Arrays;

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
	static GraphicalInterface gui = new GraphicalInterface();
	static ConsoleInterface cli = new ConsoleInterface();
	static Storage storage;

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
		chooseSettings();

		System.out.println("\n\nThanks for using this program...!");
	}

	private static void chooseSettings() {
		String choice = cli.chooseOption(new ArrayList<>(Arrays.asList("text", "database")));
		if (choice.equals("text")) {
			choice = cli.chooseOption(new ArrayList<>(Arrays.asList("gui", "cli")));
			if (choice.equals("gui")) {
				gui.setStorage(new TextStorage());
				gui.displayMenu();
			} else if (choice.equals("cli")) {
				storage = new TextStorage();
				cli.displayMenu();
				while (sessionActive) {
					menuOptions(cli.menuInputChoice(optionsQuantity));
				}
			}
		} else if (choice.equals("database")) {
			choice = cli.chooseOption(new ArrayList<>(Arrays.asList("gui", "cli")));
			if (choice.equals("gui")) {
				gui.setStorage(new DatabaseStorage());
				gui.displayMenu();
			} else if (choice.equals("cli")) {
				storage = new DatabaseStorage();
				cli.displayMenu();
				while (sessionActive) {
					menuOptions(cli.menuInputChoice(optionsQuantity));
				}
			}
		}
	}

	/**
	 * Runs the appropriate menu method based on the users input.
	 * <p>
	 * These options are generically represented using interfaces, so based on the {@link #storage} variables
	 * declarations this will either be running CLI + text backend, or CLI + database backend
	 * </p>
	 * @param option an integer that represents what method the user wants to run
	 * @see UserInterface#menuInputChoice
	 */
	public static void menuOptions(int option) {
		switch (option) {
			case 1 -> {
				String selectedSource = cli.chooseOption(storage.getSources());
				cli.displayRecords(
						storage.searchRecord(
								selectedSource,
								cli.propertySearchInput(storage.getHeaders(selectedSource))
						)
				);
			}
			case 2 -> storage.addRecord(cli.addRecordInput(),"items");
			case 3 -> storage.updateRecord(cli.updateRecordInput(storage.searchRecord(), storage.getHeaders("items")));
			case 4 -> storage.deleteRecord(cli.deleteRecordInput(storage.searchRecord()));
			case 5 -> cli.displayRecords(
					storage.searchRecord(
							"transactions",
							Pair.of("date",cli.viewTransactionsInput())));
			case 6 -> cli.displayRecords(storage.searchRecord());
			case 7 -> sessionActive = false;
			default -> System.out.println("Unexpected error occurred, please enter an integer!");
		}
	}
}