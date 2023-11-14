package uni.S257123.ui.graphical;

import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;
import uni.S257123.ui.interfaces.UserInterface;

import java.util.List;

public class GraphicalInterface implements UserInterface {
    @Override
    public void DisplayMenu() {

    }

    @Override
    public int MenuInputChoice(int optionsQuantity) {
        return 0;
    }

    @Override
    public List<String> AddRecordInput() {
        return null;
    }

    @Override
    public List<String> UpdateRecordInput(List<CSV> csvs, List<String> headers) {
        return null;
    }

    @Override
    public String DeleteRecordInput(List<CSV> csvs) {
        return null;
    }

    @Override
    public String ViewTransactionsInput() {
        return null;
    }

    @Override
    public Pair<String, String> PropertySearchInput(List<String> headers) {
        return null;
    }

    @Override
    public String ChooseOption(List<String> options) {
        return null;
    }

    @Override
    public void DisplayRecords(List<CSV> csvs) {

    }
}
