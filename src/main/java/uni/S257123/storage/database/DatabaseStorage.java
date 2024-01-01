package uni.S257123.storage.database;

import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;
import uni.S257123.storage.interfaces.Storage;

import java.util.List;

public class DatabaseStorage implements Storage {
    @Override
    public List<String> getSources() {
        return null;
    }

    @Override
    public List<String> getHeaders(String target) {
        return null;
    }

    @Override
    public boolean addRecord(List<String> parameters, String target, String transactionType) {
        return false;
    }
    @Override
    public boolean addRecord(List<String> parameters, String target) {
        return addRecord(parameters, target, "added");
    }

    @Override
    public void updateRecord(List<String> recordInfo) {

    }

    @Override
    public void deleteRecord(String id) {

    }

    @Override
    public List<CSV> readContents(String target) {
        return null;
    }

    @Override
    public List<CSV> searchRecord(String target, Pair<String, String> propertyNameValuePair) {
        return null;
    }

    @Override
    public String generateID() {
        return null;
    }
}
