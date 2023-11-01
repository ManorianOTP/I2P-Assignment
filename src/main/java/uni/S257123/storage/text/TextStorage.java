package uni.S257123.storage.text;

import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;
import uni.S257123.storage.interfaces.Storage;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class TextStorage implements Storage {
    private final String itemsFilePath = "src/main/resources/items.txt";
    private final String transactionsFilePath = "src/main/resources/transactions.txt";
    private final Map<String, String> csvDataSource = Map.of(
            "items", itemsFilePath,
            "transactions", transactionsFilePath
    );
    private HashMap<String, List<CSV>> csvDataMap = new HashMap<>(Map.of(
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

    @Override
    public void UpdateRecord() {
        System.out.println("Item quantity updated\n");
    }

    @Override
    public void DeleteRecord() {
        System.out.println("Item deleted updated\n");
    }

    @Override
    public List<CSV> ReadContents(String target) {
        List<CSV> result = new ArrayList<>();

        try {
            Scanner myReader = new Scanner(new File(target));
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
