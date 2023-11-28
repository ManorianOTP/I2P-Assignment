package uni.S257123.ui.console;

import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.main.InventoryManagementSystem;
import uni.S257123.models.CSV;
import uni.S257123.ui.interfaces.UserInterface;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A console-based user interface for the Inventory Management System. This class
 * implements the {@code UserInterface} with methods that interact with the user
 * through the command line.
 *
 * <p>All methods assume a console environment for input and output. Invalid inputs
 * are handled within each method, ensuring that the user is always prompted to enter
 * correct data as necessary.</p>
 *
 * @see UserInterface
 */
public class ConsoleInterface implements UserInterface {
    @Override
    public void displayMenu() {
        System.out.print("""
            ╔══════════════════════════════════════════════════════════╗
            ║ I N V E N T O R Y    M A N A G E M E N T    S Y S T E M  ║
            ╟──────────────────────────────────────────────────────────╢
            ║ 1. SEARCH                                                ║
            ║ 2. ADD NEW ITEM                                          ║
            ║ 3. UPDATE QUANTITY OF EXISTING ITEM                      ║
            ║ 4. REMOVE ITEM                                           ║
            ║ 5. VIEW TRANSACTIONS                                     ║
            ║ 6. VIEW ITEMS IN INVENTORY                               ║
            ╠══════════════════════════════════════════════════════════╣
            ║ 7. Exit                                                  ║
            ╠══════════════════════════════════════════════════════════╣
            """);

    }

    /**
     * {@inheritDoc}
     * <p>
     * The available options are defined in {@code InventoryManagementSystem} class.
     * </p>
     * @param optionsQuantity the total number of menu options
     * @return the integer value corresponding to the user's valid menu choice
     * @throws InputMismatchException if the input provided is not an integer
     * @see InventoryManagementSystem#menuOptions(int)
     */
    @Override
    public int menuInputChoice(int optionsQuantity) {
        Scanner input = new Scanner(System.in);
        System.out.print("║ Enter a choice and Press ENTER to continue [1-7]:        ║");
        int userInput = -1;
        try {
            userInput = input.nextInt();
        } catch (InputMismatchException e) {
            input.next(); // consume the invalid input
            System.out.println("║ Unexpected error occurred, please enter an integer!      ║");
        }

       while (userInput < 1 || userInput > optionsQuantity) {
           try {
               System.out.println("║ Please enter a choice between 1 and 7:                   ║");
               userInput = input.nextInt();
           } catch (InputMismatchException e) {
               input.next(); // consume the invalid input
               System.out.println("║ Unexpected error occurred, please enter an integer!      ║");
           }
       }
        return userInput;
    }

    @Override
    public List<String> addRecordInput() {
        Scanner input = new Scanner(System.in);

        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.print("║ " + String.format("%-57s","Enter item description: ") + "║");
        String description = input.nextLine();

        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.print("║ " + String.format("%-57s","Enter unit price: ") + "║");
        while (!input.hasNextDouble()) {
            System.out.print("║ " + String.format("%-57s","That's not a valid unit price. Please enter a number.") + "║");
            input.next(); // Consume the invalid input
        }
        double unitPrice = input.nextDouble();

        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.print("║ " + String.format("%-57s","Enter quantity in stock: ") + "║");
        while (!input.hasNextInt()) {
            System.out.print("║ " + String.format("%-57s","That's not a valid quantity. Please enter an integer.") + "║");
            input.next(); // Consume the invalid input
        }
        int qtyInStock = input.nextInt();

        double totalPrice = unitPrice * qtyInStock;

        List<String> parameters = Arrays.asList(
                description,
                String.valueOf(unitPrice),
                String.valueOf(qtyInStock),
                String.valueOf(totalPrice)
        );

        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║ " + String.format("%-57s","Item To Be Added:") + "║");
        System.out.println("║ " + String.format("%-57s","Description: " + description) + "║");
        System.out.println("║ " + String.format("%-57s","Unit Price:" + unitPrice) + "║");
        System.out.println("║ " + String.format("%-57s","Quantity in Stock: " + qtyInStock) + "║");
        System.out.println("║ " + String.format("%-57s","Total Price: " + totalPrice) + "║");

        // User Confirmation
        System.out.print("║ " + String.format("%-57s","Do you want to add this item to the file? (yes/no): ") + "║");
        input.nextLine();  // Consume the remaining newline character from using input.nextInt() prior
        String confirmation = input.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes") || confirmation.equals("y")) {
            System.out.println("╠══════════════════════════════════════════════════════════╣");
            return parameters;
        } else {
            System.out.println("║ " + String.format("%-57s","Item not added.") + "║");
            System.out.println("╠══════════════════════════════════════════════════════════╣");
            return null;
        }
    }

    @Override
    public List<String> updateRecordInput(List<CSV> csvs, List<String> headers) {
        Scanner scanner = new Scanner(System.in);
        List<String> output = new ArrayList<>();

        String id = GetUserChosenRecord(csvs, scanner);
        output.add(id);

        Pair<String, String> propertyValue = propertySearchInput(headers);
        output.add(propertyValue.getLeft());
        output.add(propertyValue.getRight());

        return output;
    }

    /**
     * Takes in a list of {@link CSV}s, and validates that the user has chosen an id from amongst them.
     * @param csvs the list of records to be chosen from
     * @param scanner the scanner object to take user input
     * @return the index of the record chosen
     */
    private String GetUserChosenRecord(List<CSV> csvs, Scanner scanner) {
        displayRecords(csvs);
        System.out.print("║ " + String.format("%-57s","Which record do you want to update? (input an ID)") + "║");
        boolean validID = false;
        String id = "";
        while (!validID){
            String input = scanner.nextLine();
            for (CSV csv : csvs) {
                // if the current csv has an id that matches the input
                if (csv.id.equals(String.format("%05d", Integer.parseInt(input)))) {
                    validID = true;
                    id = String.format("%05d", Integer.parseInt(input));
                    break;
                }
            }
            if (!validID) {
                System.out.println("Please input a valid ID: ");
            }
        }
        return id;
    }

    @Override
    public String deleteRecordInput(List<CSV> csvs) {
        Scanner scanner = new Scanner(System.in);
        return GetUserChosenRecord(csvs, scanner);
    }

    @Override
    public String viewTransactionsInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("║ " + String.format("%-57s","View transactions from today, or all time? (today/all): ") + "║");
        String input = scanner.nextLine().trim().toLowerCase();

        while (!input.equals("today") && !input.equals("all")) {
            System.out.print("║ " + String.format("%-57s","Invalid input. Please enter 'today' or 'all'.") + "║");
            input = scanner.nextLine().trim().toLowerCase();
        }

        if (input.equals("today")) {
            return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        } else {
            return "";
        }
    }

    @Override
    public Pair<String, String> propertySearchInput(List<String> headers) {
        Scanner input = new Scanner(System.in);
        String propertyName = chooseOption(headers);
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.print("║ " + String.format("%-57s","Please input a value:") + "║");
		String value = input.nextLine();
        return Pair.of(propertyName, value);
    }

    @Override
    public String chooseOption(List<String> options) {
        Scanner input = new Scanner(System.in);
        String option = "";
        while (!options.contains(option)) {
            System.out.println("╠══════════════════════════════════════════════════════════╣");
            System.out.println("║ Please input a valid option from the following list:     ║");
            System.out.print("║ " + String.format("%-57s",options) + "║");
            option = input.nextLine();
        }
        return option;
    }

    @Override
    public void displayRecords(List<CSV> csvs) {
        for (CSV csv: csvs) {
            System.out.println("╠" + String.format("%-158s","").replace(" ", "═") + "╣");
            System.out.println("║ " + String.format("%-157s",csv) + "║");
        }
        System.out.println("╠" + String.format("%-58s","").replace(" ", "═") + "╦"
                + String.format("%-99s","").replace(" ", "═") + "╝");
    }
}
