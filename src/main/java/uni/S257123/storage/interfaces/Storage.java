package uni.S257123.storage.interfaces;
import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;

import java.util.List;

public interface Storage {
    List<String> GetSources();

    List<String> GetHeaders(String target);

    boolean AddRecord(List<String> parameters, String target);
    void UpdateRecord();
    void DeleteRecord();

    List<CSV> ReadContents(String target);

    List<CSV> SearchRecord(String target, Pair<String, String> propertyNameValuePair);
    String GenerateID();
}
