import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CSV {
    // using the object classes rather than primitives like int, as otherwise I can't store all the differently typed
    // properties into one Map
    String id;
    String description;
    Integer qtySold;
    Integer amount;
    Integer stockRemaining;
    String transactionType;
    Double unitPrice;
    Integer qtyInStock;
    Double totalPrice;
    String date;

    // LinkedHashSet is highly performant but maintains its output order for printing
    Set<String> definedFields = new LinkedHashSet<>();

    // A Map that links the string's of potential header values to a supplier object, which acts like a Getter in this
    // context without requiring the boilerplate normally associated with a Getter
    Map<String, Supplier<Object>> fieldSuppliers = Map.of(
            "id", () -> id,
            "description", () -> description,
            "qtySold", () -> qtySold,
            "amount", () -> amount,
            "stockRemaining", () -> stockRemaining,
            "transactionType", () -> transactionType,
            "unitPrice", () -> unitPrice,
            "qtyInStock", () -> qtyInStock,
            "totalPrice", () -> totalPrice,
            "date", () -> date
    );

    // Constructor that takes in an input of the list of parameters found in the file, and the header row associated
    public CSV(List<String> parameterFileRow, List<String> headers) {
        // for each header, get the headers string and the associated value
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            String value = parameterFileRow.get(i);

            // then assign the associated value to the relevant property, and store that it's been defined into
            // definedFields
            switch (header) {
                case "id" -> { id = value; definedFields.add("id"); }
                case "description" -> { description = value; definedFields.add("description"); }
                case "qtySold" -> { qtySold = Integer.parseInt(value); definedFields.add("qtySold"); }
                case "amount" -> { amount = Integer.parseInt(value); definedFields.add("amount"); }
                case "stockRemaining" -> { stockRemaining = Integer.parseInt(value); definedFields.add("stockRemaining"); }
                case "transactionType" -> { transactionType = value; definedFields.add("transactionType"); }
                case "unitPrice" -> { unitPrice = Double.parseDouble(value); definedFields.add("unitPrice"); }
                case "qtyInStock" -> { qtyInStock = Integer.parseInt(value); definedFields.add("qtyInStock"); }
                case "totalPrice" -> { totalPrice = Double.parseDouble(value); definedFields.add("totalPrice"); }
                case "date" -> { date = value; definedFields.add("date"); }
                default -> throw new IllegalArgumentException("Unexpected header: " + header);
            }
        }
    }

    // When called should take in a property name, and make use of the fieldSuppliers map to get the value out
    public Object GetPropertyByName(String propertyName) {
        return fieldSuppliers.get(propertyName).get();
    }

    @Override
    public String toString() {
        // Override the default toString() method for an object, so rather than returning its memory address, it returns
        // a string representation of its fields from the list of all variables that were defined in the setup of the
        // instance of CSV. Map in sequence the list of field names to a string in the format "String1=String2"
        // where String1 is the current field name being operated on, and String2 is the associated value got from the
        // fieldSuppliers map. Once the Stream has been fully consumed, each string collected gets joined with a ", "
        return definedFields.stream()
                .map(fieldName -> String.format("%s=%s", fieldName, fieldSuppliers.get(fieldName).get()))
                .collect(Collectors.joining(", "));
    }

    public String toCSVFileOutput() {
        // Returns a string representation of its fields
        // from the list of all variables that were defined in the setup of the instance of CSV
        // map in sequence the list of field names to a string in the format "Value1,"
        // where Value1 is the current value got from the fieldSuppliers map,
        // each value collected gets joined with a ","
        return definedFields.stream()
                .map(fieldName -> String.format("%s", fieldSuppliers.get(fieldName).get()))
                .collect(Collectors.joining(","));
    }
}
