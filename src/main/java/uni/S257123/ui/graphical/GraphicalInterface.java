package uni.S257123.ui.graphical;

import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;
import uni.S257123.ui.interfaces.UserInterface;

import java.util.List;

public class GraphicalInterface implements UserInterface {
    @Override
    public void displayMenu() {

    }

    @Override
    public int menuInputChoice(int optionsQuantity) {
        return 0;
    }

    @Override
    public List<String> addRecordInput() {
        return null;
    }

    @Override
    public List<String> updateRecordInput(List<CSV> csvs, List<String> headers) {
        return null;
    }

    @Override
    public String deleteRecordInput(List<CSV> csvs) {
        return null;
    }

    @Override
    public String viewTransactionsInput() {
        return null;
    }

    @Override
    public Pair<String, String> propertySearchInput(List<String> headers) {
        return null;
    }

    @Override
    public String chooseOption(List<String> options) {
        return null;
    }

    @Override
    public void displayRecords(List<CSV> csvs) {

    }
}
