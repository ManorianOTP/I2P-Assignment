package uni.S257123.storage.interfaces;
import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;

import java.util.List;

/**
 *
 */
public interface Storage {
    /**
     * Outputs a list of what datasource's data is being stored in.
     * @return string list of the available datasource's data is being stored in (without file extensions)
     */
    List<String> getSources();

    /**
     * Outputs the headers of a given file as a list of strings.
     * @param target the datasource you want to get headers from, without its file extension
     * @return all the headers the target file contains
     */
    List<String> getHeaders(String target);

    /**
     * Adds a new record to the specified location based on the provided parameters. It is the additional responsibility
     * of this method to ensure that the new ID from {@link #generateID()} is added to the provided parameters if being
     * used to add to items. It also should ensure that future operations are aware that this file has been added.
     * <p>Note: When transaction functionality is added, need to ensure a transaction gets added when this is run</p>
     * @param parameters At a minimum, should provide:
     *                   <ol>
     *                   <li>Item Description</li>
     *                   <li>Unit Price</li>
     *                   <li>Quantity in Stock</li>
     *                   </ol>
     *                   It is up to the implementation whether providing the total price (calculated) is necessary
     * @param target the datasource for the record to be added to, without its file extension
     * @param transactionType either "added", "updated", or "deleted", based on what is being done to items
     * @return true if the record was successfully added
     */
    boolean addRecord(List<String> parameters, String target, String transactionType);

    boolean addRecord(List<String> parameters, String target);

    /**
     * Takes in an ID along with a property and value to update, and the items data-store to change the associated
     * values.
     * @param recordInfo index 0: record ID, index 1: property wanted to be edited, index 2: what the value to be edited
     *                  to is
     */
    void updateRecord(List<String> recordInfo);

    /**
     * Takes in an id, and deletes the record that matches that row.
     * @param id the id of the row to be removed
     */
    void deleteRecord(String id);

    /**
     * Reads the contents of the provided datasource, and returns a list of {@link CSV} objects, with each object representing
     * one row of the data.
 	 * <p>
 	 * Note: This method reads the entire file every time it's called. Potential performance
 	 * improvements could be achieved by reading only changed or unread rows.
 	 * </p>
     * @param target the datasource to be read from, missing any file extensions if applicable
     * @return a list of CSV objects, each representing one row of the file
     */
    List<CSV> readContents(String target);

    /**
     * Searches a selected datasource to see if a certain value matches any pre-existing data for the specified property.
     * <p>
     * Iterates through the datasource at target, checking if the property specified by
     * propertyNameValuePair matches (or contains) the provided value within the same Pair. Matching is done using a
     * regular expression, but the value is treated as a literal string, not as a regex pattern to avoid issues with "."
     * in Regex. All matching CSV rows are returned
     * </p>
     * @param target the name of datasource that you want to search (minus any extensions if applicable)
     * @param propertyNameValuePair the property you want to search (left), and the value to search for (right)
     * @return a list of {@link CSV} objects that match the chosen search
     * @see #searchRecord()
     */
    List<CSV> searchRecord(String target, Pair<String, String> propertyNameValuePair);
    /**
     * Searches for a record using default search parameters.
     * This is a convenience method that uses "items" as the table name
     * and an empty string for the ID search criteria by default.
     * For custom search parameters, use {@link #searchRecord(String, Pair)}.
     */
    default List<CSV> searchRecord() {
        return searchRecord("items", Pair.of("id", ""));
    }

    /**
     * Generates the next unused 5-digit ID, based on the last ID in the items source.
     * <p>
     * If the last ID in the list is less than 5 digits, the returned ID will be padded
     * with leading zeros to ensure a 5-digit length. If the last ID exceeds or equals
     * 100,000, a runtime exception will be thrown since this is beyond the allowed range for 5-digit IDs.
     * </p>
     * @return A 5-digit string representing the next unused ID.
     * @throws RuntimeException if the last ID in the {@code items} list exceeds or equals 100,000.
     */
    String generateID();
}
