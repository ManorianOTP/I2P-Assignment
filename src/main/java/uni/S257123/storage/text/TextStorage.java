package uni.S257123.storage.text;

import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;
import uni.S257123.storage.interfaces.Storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A text file based storage system for the Inventory Management System. This class
 * implements methods that interact with data stored within text files.
 */
public class TextStorage implements Storage {
    public final String itemsFilePath = "src/main/resources/items.txt";
    public final String transactionsFilePath = "src/main/resources/transactions.txt";
    /**
     * Maps the file names to their associated file path
     */
    public final Map<String, String> csvDataSource = new LinkedHashMap<>() {{
        put("items", itemsFilePath);
        put("transactions", transactionsFilePath);
    }};

    /**
     * Maps the file names to the data read into memory from their associated file
     */
    public final HashMap<String, List<CSV>> csvDataMap = new HashMap<>(Map.of(
            "items", readContents(itemsFilePath),
            "transactions", readContents(transactionsFilePath))
    );

    @Override
    public List<String> getSources()  {
        return new ArrayList<>(csvDataSource.keySet());
    }

    @Override
    public List<String> getHeaders(String target)  {
        return csvDataMap.get(target).getFirst().definedFields.stream().toList();
    }

    /**
	 * {@inheritDoc}
	 * <p>
	 * The method creates a new {@link CSV} object using the given parameters, and getting the headers
	 * It then appends this record to the file. Upon successful writing, the file's
	 * contents are read and stored into the {@link #csvDataMap}.
	 * </p>
     * @param parameters At a minimum, should provide:
     *                   <ol>
     *                   <li>Item Description</li>
     *                   <li>Unit Price</li>
     *                   <li>Quantity in Stock</li>
     *                   </ol>
     *                   It is up to the implementation whether providing the total price (calculated) is necessary
     * @param target the file name for the record to be added, without its file extension
     * @return true or false, based off whether the add succeeded
	 * @throws RuntimeException if an IOException occurs while writing to or reading from the file.
     * @see #addRecord(List, String)
	 */
    @Override
    public boolean addRecord(List<String> parameters, String target, String transactionType) {
        if (Objects.nonNull(parameters)) {

            List<String> headers = getHeaders(target);
            List<String> parametersComplete = new ArrayList<>();
            if (target.equals("items")) {
                parametersComplete.add(generateID());
                parametersComplete.addAll(parameters);
                addRecord(parametersComplete, "transactions", "added"); // adds a transaction record to the attempt
            } else {
                parametersComplete.addAll(parameters); // ID, description, unitPrice, totalPrice, Stock remaining
                parametersComplete.add(transactionType); // transaction type
                parametersComplete.add(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            }
            CSV newRecord = new CSV(parametersComplete, headers);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvDataSource.get(target), true))) {
                bw.newLine(); // Add a new line for the new row
                bw.write(newRecord.toCSVFileOutput()); // Write the new row content
                bw.flush();
                csvDataMap.put(target, readContents(csvDataSource.get(target))); //updates the in-memory store of csv records
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    /**
     * Defaults transaction type to "added" for {@link #addRecord(List, String, String)}
     * @param parameters At a minimum, should provide:
     *                   <ol>
     *                   <li>Item Description</li>
     *                   <li>Unit Price</li>
     *                   <li>Quantity in Stock</li>
     *                    </ol>
     *                   It is up to the implementation whether providing the total price (calculated) is necessary
     * @param target the file name for the record to be added, without its file extension
     * @return true or false, based off whether the add succeeded
     */
    @Override
    public boolean addRecord(List<String> parameters, String target) {
        return addRecord(parameters, target, "added");
    }

    /**
     * {@inheritDoc}
     * @param recordInfo index 0: row ID, index 1: property wanted to be edited, index 2: what the value to be edited to
     *                  is
     * @throws RuntimeException If either the temp file fails to write, fails to overwrite the main file, or the main
     * file initially fails to get read
     */
    @Override
    public void updateRecord(List<String> recordInfo) {
        List<String> headers = getHeaders("items");
        CSV changedRow = null;

        int columnToUpdateIndex = -1;
        for (int i = 0; i < headers.size(); i++) {
            if (recordInfo.get(1).equals(headers.get(i))) {
                columnToUpdateIndex = i;
            }
        }
        try (Scanner myReader = new Scanner(new BufferedReader(new FileReader(csvDataSource.get("items"))))) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(csvDataSource.get("items") + ".tmp"));

            boolean isFirstLine = true;
            while (myReader.hasNextLine()) {
                if (!isFirstLine) {
                    writer.newLine();
                } else {
                    isFirstLine = false;
                }
                String currentLine = myReader.nextLine();
                String[] columns = currentLine.split(",");

                if (columns[0].equals(recordInfo.get(0))) {
                    columns[columnToUpdateIndex] = recordInfo.get(2);
                    changedRow = new CSV(List.of(columns), headers);
                }
                writer.write(String.join(",", columns));

            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Files.move(
                    Paths.get(csvDataSource.get("items") + ".tmp"),
                    Paths.get(csvDataSource.get("items")),
                    StandardCopyOption.ATOMIC_MOVE);
            if (changedRow != null) {
                addRecord(List.of(changedRow.toCSVFileOutput().split(",")),"transactions","updated");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        csvDataMap.put("items", readContents(csvDataSource.get("items"))); //updates the in-memory store of csv records
    }

    @Override
    public void deleteRecord(String id) {
        try (Scanner myReader = new Scanner(new BufferedReader(new FileReader(csvDataSource.get("items"))))) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(csvDataSource.get("items") + ".tmp"));

            boolean isFirstLine = true;
            CSV deletedRow = null;
            while (myReader.hasNextLine()) {
                String currentLine = myReader.nextLine();
                String[] columns = currentLine.split(",");
                if (columns[0].equals(id)) {
                    List<String> columnslist = new ArrayList<>(List.of(columns));
                    columnslist.set(3,"0");
                    deletedRow = new CSV(columnslist, getHeaders("items"));
                    continue;
                }
                if (!isFirstLine) {
                    writer.newLine();
                } else {
                    isFirstLine = false;
                }

                writer.write(String.join(",", columns));
            }
            writer.close();
            if (deletedRow != null) {
                addRecord(List.of(deletedRow.toCSVFileOutput().split(",")),"transactions","deleted");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Files.move(
                    Paths.get(csvDataSource.get("items") + ".tmp"),
                    Paths.get(csvDataSource.get("items")),
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        csvDataMap.put("items", readContents(csvDataSource.get("items"))); //updates the in-memory store of csv records
    }


    /**
	 * {@inheritDoc}
     * <p>
 	 * The method assumes the first row of the file to be the headers. Each header is separated
 	 * by a comma. Subsequent rows are interpreted as data, where each value is associated with
 	 * a header based on its position. Each row is transformed into a CSV object and
 	 * added to the resulting list.
 	 * </p>
     *
     * @param target the file to be read from, minus any file extensions
     * @return a list of CSV objects, each representing one row of the file
	 * @throws RuntimeException if the provided file is not found or other IO issues occur.
	 */
    @Override
    public List<CSV> readContents(String target) {
        List<CSV> result = new ArrayList<>();

        try (Scanner myReader = new Scanner(new BufferedReader(new FileReader(target)))) {
            List<String> headers = Arrays.asList(myReader.nextLine().split(","));

            while (myReader.hasNextLine()) {
                String row = myReader.nextLine();
                List<String> parameterFileRow = Arrays.asList(row.split(","));
                result.add(new CSV(parameterFileRow, headers));
            }
            return result;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CSV> searchRecord(String target, Pair<String, String> propertyNameValuePair) {
        List<CSV> csvs = csvDataMap.get(target);
        List<CSV> output = new ArrayList<>();
        for (CSV csv: csvs) {
            // Pattern.quote to treat the user input as not regex, with 0+ wildcards preceding and following
            if (csv.GetPropertyByName(propertyNameValuePair.getLeft()).toString().matches(
                    (".*" + Pattern.quote(propertyNameValuePair.getRight()) + ".*"))) {
                output.add(csv);
            }
        }
        return output;
    }

    /**
     * Generates an ID by looking at the last ID in the items.txt file, and adding one to it.
     * @return The ID formatted as a 5 character string filled with preceding zeros
     * @throws RuntimeException If the ID surpasses 5 characters
     * @implNote The RuntimeException thrown by this method is just to fit the strict definition set by the assigment
     * as a proof of concept and can be freely removed if desired
     */
    public String generateID() {
        List<CSV> itemsList = csvDataMap.get("items");
        CSV lastRow = itemsList.getLast();

        if (Integer.parseInt(lastRow.id) + 1 >= 100_000) {
            throw new RuntimeException("ID exceeds the maximum allowed value");
        }

        return String.format("%05d", Integer.parseInt(lastRow.id) + 1);
    }

    @Override
    public List<String> getIDs() {
        return csvDataMap.get("items").stream()
                .map(csv -> (String) csv.GetPropertyByName("id"))
                .toList();
    }
}
