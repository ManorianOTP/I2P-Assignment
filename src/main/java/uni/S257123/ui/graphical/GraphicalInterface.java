package uni.S257123.ui.graphical;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;
import uni.S257123.storage.text.TextStorage;
import uni.S257123.ui.interfaces.UserInterface;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class GraphicalInterface extends Application implements UserInterface {
    static TextStorage storage = new TextStorage();

    public TabPane ManageItemsPane;
    public TextField AddItemDescription;
    public TextField AddItemUnitPrice;
    public Button AddItemSubmit;
    public TextField AddItemQuantity;

    public void entry() {
        launch();
    }

    @FXML
    private void initialize() {
        AddItemSubmit.setOnAction(event -> handleAddItemSubmitButton());
    }

    private void handleAddItemSubmitButton() {
        List<TextField> textFields = Arrays.asList(AddItemDescription, AddItemUnitPrice, AddItemQuantity);
        if (validateTextInputNotEmpty(textFields) && validateAddItemInput()) {
            processAddItemInput();
            showAlert("Submission Successful", "The item has been added successfully.", Alert.AlertType.INFORMATION);
        }
    }

    private boolean validateAddItemInput() {
        if (!NumberUtils.isCreatable(AddItemUnitPrice.getText())) {
            showAlert("Invalid Input", "Unit Price must be a valid number", Alert.AlertType.ERROR);
            return false;
        }
        if (!NumberUtils.isCreatable(AddItemQuantity.getText())) {
            showAlert("Invalid Input", "Quantity in stock must be a valid number", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private boolean validateTextInputNotEmpty(List<TextField> fieldsToValidate) {
        for (TextField field : fieldsToValidate) {
            if (field.getText().trim().isEmpty()) {
                showAlert("Validation Error", "Please fill in all fields", Alert.AlertType.ERROR);
                return false;
            }
        }
        return true;
    }

    private void processAddItemInput() {
        List<String> textFields = new ArrayList<>(Arrays.asList(
                AddItemDescription.getText(),
                AddItemUnitPrice.getText(),
                AddItemQuantity.getText()
        ));
        String totalPrice = String.valueOf(Double.parseDouble(AddItemUnitPrice.getText()) * Double.parseDouble(AddItemQuantity.getText()));
        textFields.add(totalPrice);
        storage.addRecord(textFields,"items");
    }

    public void start(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/GUI.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(content);
        alert.showAndWait();
    }

    @Override
    public void displayMenu() { }

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
