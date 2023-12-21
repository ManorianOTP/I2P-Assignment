package uni.S257123.ui.graphical;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;
import uni.S257123.storage.text.TextStorage;
import uni.S257123.ui.interfaces.UserInterface;
import java.io.IOException;
import java.util.*;

public class GraphicalInterface extends Application implements UserInterface{
    static TextStorage storage = new TextStorage();

    public TabPane ManageItemsPane;
    public TextField AddItemDescription;
    public TextField AddItemUnitPrice;
    public Button AddItemSubmit;
    public TextField AddItemQuantity;
    public ChoiceBox<String> SearchSourcesSelector;
    public ChoiceBox<String> SearchHeadersSelector;
    public TextField SearchPropertyText;
    public Button SearchSubmitButton;
    public Pane SearchResultPane;
    public TableView<CSV> SearchResultTable;
    public Button SearchResultFinishButton;
    public TableView<CSV> ViewTransactionsTable;
    public TableView<CSV> ViewItemsTable;
    public Button UpdateItemSubmit;
    public ComboBox<String> UpdateItemIDSelection;
    public TextField UpdateItemNewValue;
    public ComboBox<String> UpdateItemPropertySelection;
    public ComboBox<String> DeleteIDItemSelection;
    public Button DeleteItemButton;

    public void entry() {
        launch();
    }

    @FXML
    private void initialize() {
        setupSearch();
        setupAddItem();
        setupUpdateItem();
        setupDeleteItem();
        setupTable("items", ViewItemsTable);
        setupTable("transactions",  ViewTransactionsTable);
    }

    private void setupDeleteItem() {
        List<String> ids = storage.csvDataMap.get("items").stream()
                .map(csv -> (String) csv.GetPropertyByName("id"))
                .toList();
        DeleteIDItemSelection.getItems().setAll(ids);
        DeleteIDItemSelection.valueProperty().addListener((observable, oldValue, newValue) -> DeleteItemButton.setVisible(true));
        DeleteItemButton.setOnAction(actionEvent -> deleteItem());
    }

    private void deleteItem() {
        storage.deleteRecord(DeleteIDItemSelection.getValue());
        initialize();
    }

    private void setupUpdateItem() {
        List<String> ids = storage.csvDataMap.get("items").stream()
                .map(csv -> (String) csv.GetPropertyByName("id"))
                .toList();
        UpdateItemIDSelection.getItems().setAll(ids);
        UpdateItemIDSelection.valueProperty().addListener((observable, oldValue, newValue) -> UpdateItemPropertySelection.setVisible(true));
        UpdateItemPropertySelection.getItems().setAll(storage.getHeaders("items"));
        UpdateItemPropertySelection.valueProperty().addListener((observable, oldValue, newValue) -> {
            UpdateItemNewValue.setVisible(true);
            UpdateItemSubmit.setVisible(true);
        });
        UpdateItemSubmit.setOnAction(actionEvent -> updateItem());
    }

    private void updateItem() {
        List<TextField> textFields = Collections.singletonList(UpdateItemNewValue);
        if (validateTextInputNotEmpty(textFields)) {
            storage.updateRecord(
                    List.of(UpdateItemIDSelection.getValue(),
                    UpdateItemPropertySelection.getValue(),
                    UpdateItemNewValue.getText()));
        }
        initialize();
    }

    private void setupTable(String target, TableView<CSV> table) {
        List<CSV> items = storage.searchRecord(target, Pair.of("id", ""));
        setupTableColumns(FXCollections.observableArrayList(storage.getHeaders(target)), table);
        table.setItems(FXCollections.observableArrayList(items));
    }

    private void setupAddItem() {
        AddItemSubmit.setOnAction(actionEvent -> handleAddItemSubmitButton());
    }

    private void setupSearch() {
        SearchSourcesSelector.getItems().setAll(storage.getSources());
        SearchSourcesSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            SearchHeadersSelector.setValue(null);
            SearchSubmitButton.setVisible(false);
            SearchHeadersSelector.getItems().setAll(storage.getHeaders(newValue));
            SearchHeadersSelector.setVisible(true);
        });
        SearchHeadersSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            SearchPropertyText.setVisible(true);
            SearchSubmitButton.setVisible(true);
        });
        SearchSubmitButton.setOnAction(actionEvent -> searchRecord());
        SearchResultFinishButton.setOnAction(actionEvent -> SearchResultPane.setVisible(false));
    }

    private void searchRecord() {
        List<CSV> matchingRows = storage.searchRecord(SearchSourcesSelector.getValue(),
                Pair.of(SearchHeadersSelector.getValue(), SearchPropertyText.getText()));

        setupTableColumns(SearchHeadersSelector.getItems(), SearchResultTable);

        SearchResultPane.setVisible(true);
        SearchResultTable.setItems(FXCollections.observableArrayList(matchingRows));
    }

    private void setupTableColumns(ObservableList<String> items, TableView<CSV> table) {
        table.getItems().clear();
        table.getColumns().clear();
        for (String propertyName : items) {
            TableColumn<CSV, String> column = new TableColumn<>(propertyName);
            column.setCellValueFactory(cellData -> {
                CSV csv = cellData.getValue();
                Object value = csv.GetPropertyByName(propertyName);
                return new ReadOnlyObjectWrapper<>(value != null ? value.toString() : "");
            });
            table.getColumns().add(column);
        }
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
        initialize();
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
