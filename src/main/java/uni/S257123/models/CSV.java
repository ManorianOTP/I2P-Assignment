package uni.S257123.models;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A structured representation of an entry in a CSV (Comma-Separated Values) file.
 * This class captures various data attributes typically found in CSV files.
 * Each attribute corresponds to a potential column in the CSV file.
 *
 * <p>This class also provides functionality to convert a row in a CSV file
 * to an object instance and vice versa. It uses dynamic mapping to achieve
 * this by associating field names with their respective values using a map
 * of suppliers. This makes the class flexible in handling various CSV formats
 * and structures.</p>
 *
 * <p>Note: The class is designed to handle CSV rows with string representations of
 * numerical values and does not validate the format or type of CSV data.
 * For instance, if a field expected to be an integer receives a non-numeric string,
 * a {@code NumberFormatException} will be thrown.</p>
 */
public class CSV {
    /**
     * The list of potential properties that can be declared in an instance of the CSV class.
     * Using the object classes rather than primitives like int, as otherwise I can't store all the differently typed
     * properties into one Map
     */
    public String id;
    public String description;
    public Integer stockRemaining;
    public String transactionType;
    public Double unitPrice;
    public Integer qtyInStock;
    public Double totalPrice;
    public String date;

    /**
     * A set of fields that were defined for this CSV entry.
     * Using a LinkedHashSet to maintain the order of insertion for predictable iteration.
     */
    public Set<String> definedFields = new LinkedHashSet<>();

    /**
     * A map associating each field name with a supplier.
     * The supplier effectively acts as a getter for the field, allowing dynamic retrieval
     * of field values based on the field's name.
     */
    Map<String, Supplier<Object>> fieldSuppliers = Map.of(
            "id", () -> id,
            "description", () -> description,
            "stockRemaining", () -> stockRemaining,
            "transactionType", () -> transactionType,
            "unitPrice", () -> unitPrice,
            "qtyInStock", () -> qtyInStock,
            "totalPrice", () -> totalPrice,
            "date", () -> date
    );

    /**
     * Constructs a CSV object using provided row parameters and associated headers.
     *
     * <p>This constructor iterates over each header, identifies the corresponding value from the
     * input row parameters, assigns this value to the relevant property of the object, and marks
     * the property as defined in {@code definedFields}. If the header is not recognized, it throws
     * an {@code IllegalArgumentException}.</p>
     *
     * @param parameterFileRow The list of values associated with a particular row in a CSV file.
     * @param headers          The list of headers corresponding to the order of values in
     *                         {@code parameterFileRow}.
     *
     * @throws IllegalArgumentException If an unexpected header is encountered.
     */
    public CSV(List<String> parameterFileRow, List<String> headers) {
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            String value = parameterFileRow.get(i);

            switch (header) {
                case "id", "_id" -> { id = value; definedFields.add("id"); }
                case "description" -> { description = value; definedFields.add("description"); }
                case "stockRemaining" -> { stockRemaining = Integer.parseInt(value); definedFields.add("stockRemaining"); }
                case "transactionType" -> { transactionType = value; definedFields.add("transactionType"); }
                case "unitPrice" -> { unitPrice = Double.parseDouble(value); definedFields.add("unitPrice"); }
                case "qtyInStock" -> { qtyInStock = Integer.parseInt(value); definedFields.add("qtyInStock"); }
                case "totalPrice" -> { totalPrice = Double.parseDouble(value); definedFields.add("totalPrice"); }
                case "date" -> { date = value; definedFields.add("date"); }
                default -> throw new IllegalArgumentException("Unexpected header: " + header);
            }
        }
        if (definedFields.contains("qtyInStock") && definedFields.contains("unitPrice")) {
            definedFields.add("totalPrice");
            totalPrice = qtyInStock * unitPrice;
        }
    }

    /**
     * Retrieves the value of a specified property using the {@code fieldSuppliers} map.
     *
     * <p> Fetches the corresponding value to the given property name through the map of field suppliers. If the
     * property name does not exist in the map, it will return {@code null}.</p>
     *
     * @param propertyName The name of the property whose value needs to be retrieved.
     * @return The value of the specified property, or {@code null} if the property does not exist.
     */
    public Object GetPropertyByName(String propertyName) {
        return fieldSuppliers.get(propertyName).get();
    }

    /**
     * Provides a custom string representation of the CSV object.
     * <p>
     * For each field that was defined during the CSV object instantiation,
     * constructs a string in the format "fieldName=fieldValue".
     * These individual strings are then concatenated using a ", " delimiter.
     * </p>
     *
     * @return A string representation of the CSV object.
     *         For instance, if "id" and "description" were defined fields
     *         with values "12345" and "item1" respectively,
     *         this method would return "id=12345, description=item1".
     */
    @Override
    public String toString() {
        return definedFields.stream()
                .map(fieldName -> String.format("%s=%s", fieldName, fieldSuppliers.get(fieldName).get()))
                .collect(Collectors.joining(", "));
    }

    /**
     * Produces a CSV-formatted string representation of the object's defined fields.
     * <p>
     * Iterates over each field that was defined during the CSV object instantiation and
     * maps it to its corresponding value. These values are then concatenated using a ","
     * delimiter, constructing a string suitable for CSV file output.
     * </p>
     *
     * @return A CSV-formatted string representation of the CSV object's defined fields.
     *         For instance, if "id" and "description" were defined fields
     *         with values "12345" and "item1" respectively,
     *         this method would return "12345,item1".
     */
    public String toCSVFileOutput() {
        return definedFields.stream()
                .map(fieldName -> String.format("%s", fieldSuppliers.get(fieldName).get()))
                .collect(Collectors.joining(","));
    }
}