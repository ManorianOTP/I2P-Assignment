package uni.S257123.main;

import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.storage.database.DatabaseStorage;
import uni.S257123.storage.interfaces.Storage;
import uni.S257123.storage.text.TextStorage;
import uni.S257123.ui.console.ConsoleInterface;
import uni.S257123.ui.graphical.GraphicalInterface;

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
 *     The system offers the user between text file based storage or a database, and also a choice between
 *     a CLI and a GUI.
 * </p>
 *
 * <p>
 * The system stores its data in two locations, one for items and another for transactions. The contents of these
 * locations are read into memory as lists of CSV objects at startup if using a text file solution.
 * </p>
 *
 * <p>
 * When interacting with the system, users are presented with a menu. The program continues to run and provide the menu
 * until the user decides to exit.
 * </p>
 *<p>
 * This entire project has been commented using javadoc comments, so if you're ever uncertain about how something works
 * try hovering it in your IDE, or using the javadoc tool to create web documentation
 * </p>
 *
 * @author S257123
 * @version 2.0
 */
public class InventoryManagementSystem
{
	static boolean sessionActive = true;
	static int optionsQuantity = 7;
	static GraphicalInterface gui = new GraphicalInterface();
	static ConsoleInterface cli = new ConsoleInterface();
	static Storage storage;

	/**
	 * Entry point for the Inventory Management System. The user gets the opportunity to choose what setting they want
	 * to use
	 *
	 * @param args Command-line arguments (currently unused in this context).
	 */
	public static void main(String[] args)	{
		chooseSettings();

		System.out.println("\n\nThanks for using this program...!");
	}

	/**
	 * Runs the appropriate menu method based on the users input.
	 * <p>
	 * The storage options are generically represented using interfaces, so based on the {@link #storage} variables
	 * declarations this will either be running CLI + text backend, or CLI + database backend, or GUI + text backend,
	 * or GUI + database backend
	 * </p>
	 * @see ConsoleInterface
	 * @see GraphicalInterface
	 * @see TextStorage
	 * @see DatabaseStorage
	 */
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
	 * Allows the users menu selection to correctly orchestrate the text backend with the storage choice. These options
	 * are defined here as it separates any responsibility for the backend from the UI. At this stage it seems unlikely
	 * that is the most intuitive solution moving forwards.
	 * @param option the option the user selected in the text user interface
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