package uni.S257123.ui.console;

import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;
import uni.S257123.ui.interfaces.UserInterface;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ConsoleInterface implements UserInterface {
    @Override
    public void DisplayMenu() {
        System.out.println("I N V E N T O R Y    M A N A G E M E N T    S Y S T E M");
        System.out.println("--------------------------------------------------------");
        System.out.println("1. SEARCH");
        System.out.println("2. ADD NEW ITEM");
        System.out.println("3. UPDATE QUANTITY OF EXISTING ITEM");
        System.out.println("4. REMOVE ITEM");
        System.out.println("5. VIEW TRANSACTIONS");
        System.out.println("6. VIEW ITEMS IN INVENTORY");
        System.out.println("---------------------------------");
        System.out.println("7. Exit\n");
    }

    @Override
    public int MenuInputChoice(int optionsQuantity) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter a choice and Press ENTER to continue[1-7]: ");
        int userInput = -1;

       while (userInput < 1 || userInput > optionsQuantity) {
           try {
               userInput = input.nextInt();
           } catch (Exception e) {
               input.next(); // consume the invalid input
               System.out.println("Unexpected error occurred, please enter an integer!");
           }
       }
        return userInput;
    }

    @Override
    public List<String> AddRecordInput() {
        Scanner input = new Scanner(System.in);

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

//        List<String> parameters = Arrays.asList(String.valueOf(id),description,String.valueOf(unitPrice),String.valueOf(qtyInStock),String.valueOf(totalPrice));
        List<String> parameters = Arrays.asList(description,String.valueOf(unitPrice),String.valueOf(qtyInStock),String.valueOf(totalPrice));

        System.out.println("\nItem To Be Added:");
        System.out.println("Description: " + description);
        System.out.println("Unit Price: " + unitPrice);
        System.out.println("Quantity in Stock: " + qtyInStock);
        System.out.println("Total Price: " + totalPrice + "\n");

        // User Confirmation
        System.out.print("Do you want to add this item to the file? (yes/no): ");
        input.nextLine();  // Consume the remaining newline character from using input.nextInt() prior
        String confirmation = input.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes") || confirmation.equals("y")) {
            return parameters;
        } else {
            System.out.println("Item not added.\n");
            return null;
        }
    }

    @Override
    public List<String> UpdateRecordInput() {
        return null;
    }

    @Override
    public List<String> RemoveRecordInput() {
        return null;
    }

    @Override
    public String ViewTransactionsInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you want to view all the transactions from today, or all time? (today/all): ");
        String input = scanner.nextLine().trim().toLowerCase();

        while (!input.equals("today") && !input.equals("all")) {
            System.out.println("Invalid input. Please enter 'today' or 'all'.");
            input = scanner.nextLine().trim().toLowerCase();
        }

        if (input.equals("today")) {
            return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        } else {
            return "";
        }
    }

    @Override
    public Pair<String, String> PropertySearchInput(List<String> headers) {
        Scanner input = new Scanner(System.in);
        String propertyName = ChooseOption(headers);
		System.out.println("Please input a value");
		String value = input.nextLine();
		System.out.println(headers);
        return Pair.of(propertyName, value);
    }

    @Override
    public String ChooseOption(List<String> options) {
        Scanner input = new Scanner(System.in);
        String option = "";
        while (!options.contains(option)) {
            System.out.println("Please input a valid option from the following list");
            System.out.println(options);
            option = input.nextLine();
        }
        return option;
    }

    @Override
    public void DisplaySearch(List<CSV> csvs) {
        for (CSV csv: csvs) {
            System.out.println(csv);
        }
    }
}
