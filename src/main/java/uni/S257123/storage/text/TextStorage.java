package uni.S257123.storage.text;

import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;
import uni.S257123.storage.interfaces.Storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Pattern;

public class TextStorage implements Storage {
    private final String itemsFilePath = "src/main/resources/items.txt";
    private final String transactionsFilePath = "src/main/resources/transactions.txt";
    private final Map<String, String> csvDataSource = Map.of(
            "items", itemsFilePath,
            "transactions", transactionsFilePath
    );
    private final HashMap<String, List<CSV>> csvDataMap = new HashMap<>(Map.of(
            "items", ReadContents(itemsFilePath),
            "transactions", ReadContents(transactionsFilePath))
    );

    @Override
    public List<String> GetSources()  {
        return new ArrayList<>(csvDataSource.keySet());
    }

    @Override
    public List<String> GetHeaders(String target)  {
        return csvDataMap.get(target).get(0).definedFields.stream().toList();
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
	 * @throws RuntimeException if an IOException occurs while writing to or reading from the file.
	 */
    @Override
    public boolean AddRecord(List<String> parameters, String target) {
        List<String> headers = csvDataMap.get(target).get(0).definedFields.stream().toList();
        List<String> parametersComplete = new ArrayList<>();
        parametersComplete.add(GenerateID());
        parametersComplete.addAll(parameters);
        CSV newRecord = new CSV(parametersComplete, headers);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvDataSource.get(target), true))) {
            bw.newLine();  // Add a new line for the new row
            bw.write(newRecord.toCSVFileOutput());  // Write the new row content
            bw.flush();
            csvDataMap.put(target, ReadContents(csvDataSource.get(target))); //updates the in-memory store of csv records
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     * @param recordInfo index 0: row ID, index 1: property wanted to be edited, index 2: what the value to be edited to
     *                  is
     * @throws RuntimeException If either the temp file fails to write, fails to overwrite the main file, or the main
     * file initially fails to get read
     */
    @Override
    public void UpdateRecord(List<String> recordInfo) {
        List<String> headers = GetHeaders("items");

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        csvDataMap.put("items", ReadContents(csvDataSource.get("items"))); //updates the in-memory store of csv records
    }

    @Override
    public void DeleteRecord(String id) {
        try (Scanner myReader = new Scanner(new BufferedReader(new FileReader(csvDataSource.get("items"))))) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(csvDataSource.get("items") + ".tmp"));

            boolean isFirstLine = true;
            while (myReader.hasNextLine()) {
                String currentLine = myReader.nextLine();
                String[] columns = currentLine.split(",");
                if (columns[0].equals(id)) {
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
        csvDataMap.put("items", ReadContents(csvDataSource.get("items"))); //updates the in-memory store of csv records
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
    public List<CSV> ReadContents(String target) {
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
    public List<CSV> SearchRecord(String target, Pair<String, String> propertyNameValuePair) {
        List<CSV> csvs = csvDataMap.get(target);
        List<CSV> output = new ArrayList<>();
        for (CSV csv: csvs) {
            // Pattern.quote to treat the user input as not regex, with 0+ wildcards preceding and following
            if (csv.GetPropertyByName(propertyNameValuePair.getLeft()).toString().matches((".*" + Pattern.quote(propertyNameValuePair.getRight()) + ".*"))) {
                output.add(csv);
            }
        }
        return output;
    }

    @Override
    public String GenerateID() {
        List<CSV> itemsList = csvDataMap.get("items");
        CSV lastRow = itemsList.get(itemsList.size() - 1);

        if (Integer.parseInt(lastRow.id) >= 100_000) {
            throw new RuntimeException("ID exceeds the maximum allowed value");
        }

        return String.format("%05d", Integer.parseInt(lastRow.id) + 1);

    }
}