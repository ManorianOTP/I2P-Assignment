package uni.S257123.ui.interfaces;

import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;

import java.util.List;

public interface UserInterface {
    void DisplayMenu();
    int MenuInputChoice(int optionsQuantity);
    List<String> AddRecordInput();
    List<String> UpdateRecordInput();
    List<String> RemoveRecordInput();
    String ViewTransactionsInput();

    Pair<String, String> PropertySearchInput(List<String> headers);
    String ChooseOption(List<String> options);

    void DisplaySearch(List<CSV> csvs);
}
