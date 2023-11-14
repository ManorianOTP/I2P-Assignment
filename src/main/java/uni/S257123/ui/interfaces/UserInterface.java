package uni.S257123.ui.interfaces;

import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;
import uni.S257123.storage.interfaces.Storage;

import java.util.List;

/**
 * Defines the user interaction layer for an Inventory Management System.
 * <p>
 * This interface abstracts the methods necessary to interact with the user, regardless
 * of the specific mechanism of interaction (e.g., command line, graphical user interface).
 * It encapsulates all user input and output operations such as displaying menus, capturing
 * user choices, and showing results of operations such as searching or updating records.
 * </p>
 * <p>At no point should any implementation for a method in this interface interact directly with a storage system
 * that should all be handled by the {@link Storage} interface</p>
 */
public interface UserInterface {

    /**
     * Displays a menu for the Inventory Management System.
     * <p>
     * The menu lists out all the following actions:
     * <ol>
     *     <li>Search for an item</li>
     *     <li>Add a new item to storage</li>
     *     <li>Update an items quantity in storage</li>
     *     <li>Remove an item from storage</li>
     *     <li>View transactions that have occurred</li>
     *     <li>View the current inventory</li>
     *     <li>Exit the system</li>
     * </ol>
     * </p>
     */
    void DisplayMenu();

    /**
     * Prompts the user for a menu choice and retrieves the input.
     * <p>
     * Capture's a user's choice as an integer that corresponds to a menu option. The valid range of choices is between
     * 1 and the specified optionsQuantity. Implementers should ensure that if the provided input is not within the
     * valid range, the user is prompted to try again.
     * </p>
     *
     * @param optionsQuantity the number of menu options available; this sets the valid range of choices
     * @return the user's validated menu choice as an integer
     */
    int MenuInputChoice(int optionsQuantity);

    /**
     * Prompts for and collects input necessary to add a new record to the inventory.
     * <p>
     * Captures and validates user input for the following fields:
     * <ol>
     *    <li>An item description</li>
     *     <li>The unit price</li>
     *     <li>The quantity in stock.</li>
     * </ol>
     *
     * The total price should be calculated (unit price x quantity in stock) and a summary of the item is to be
     * displayed for user confirmation before finalizing the addition.
     * </p>
     * @return a list of strings containing item details, where each element represents:
     *         <ol>
     *           <li>Item description</li>
     *           <li>Unit price</li>
     *           <li>Quantity in stock</li>
     *           <li>Total price (calculated)</li>
     *         </ol>
     */
    List<String> AddRecordInput();
    /**
     * Takes in a list of csv formatted rows, and returns the user's chosen details to update from a given row.
     * @param csvs the rows of the file, stored as a list of {@link CSV} objects
     * @param headers the headers of the associated rows ideally from {@link Storage#GetHeaders(String)}
     * @return the id of the chosen row, along with the property and value to be changed as a list of strings
     */
    List<String> UpdateRecordInput(List<CSV> csvs, List<String> headers);
    /**
     * Takes in a list of csv formatted rows, and returns the user's chosen row id to delete
     * @param csvs the rows of the file, stored as a list of {@link CSV} objects
     * @return the id of the chosen row
     */
    String DeleteRecordInput(List<CSV> csvs);

    /**
     * Prompts the user to view transactions either from today or from all time, validates the choice, and then returns
     * their selection.
     * @return A string of either today's date in the format "dd/MM/yyyy" or an empty string "" to represent all time
     */
    String ViewTransactionsInput();

    /**
     * Prompts the user to input a property name from a list of properties, and what value they want to search that
     * property for.
     * <p>
     * Uses {@link #ChooseOption(List)} to validate that the input is one from the list of headers.
     * </p>
     *
     * @param headers A list of strings containing all possible parameters that can be searched
     * @return A pair with the left holding the propertyName, and the right holding the value to search for
     */
    Pair<String, String> PropertySearchInput(List<String> headers);

    /**
     * Takes in a list of options and continuously asks for an input until a valid option is chosen.
     * @param options a string list of options to be selected from
     * @return a string that matches a string in the options list
     */
    String ChooseOption(List<String> options);

    /**
     * Takes in a list of {@link CSV}'s, and displays all of them to the UI
     * @param csvs a list of CSVs that you want to display to the user
     */
    void DisplayRecords(List<CSV> csvs);
}
