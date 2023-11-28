package uni.S257123.ui.graphical;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;
import uni.S257123.ui.interfaces.UserInterface;

import java.util.List;

public class GraphicalInterface extends Application implements UserInterface {
    public static void main() {
        launch();
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(new Scene(new VBox(10), 800, 600));
        primaryStage.show();
    }

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
