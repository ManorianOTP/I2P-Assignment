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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import uni.S257123.models.CSV;
import uni.S257123.storage.interfaces.Storage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GraphicalInterface extends Application {
    Storage storage;

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

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
        List<String> ids = storage.getIDs();
        DeleteIDItemSelection.getItems().setAll(ids);
        DeleteIDItemSelection.valueProperty().addListener((observable, oldValue, newValue) -> DeleteItemButton.setVisible(true));
        DeleteItemButton.setOnAction(actionEvent -> deleteItem());
    }

    private void deleteItem() {
        storage.deleteRecord(DeleteIDItemSelection.getValue());
        showAlert("Delete Successful", "The item has been deleted successfully.", Alert.AlertType.INFORMATION);
        initialize();
    }

    private void setupUpdateItem() {
        List<String> ids = storage.getIDs();
        UpdateItemIDSelection.getItems().setAll(ids);
        UpdateItemIDSelection.valueProperty().addListener((observable, oldValue, newValue) -> UpdateItemPropertySelection.setVisible(true));
        UpdateItemPropertySelection.getItems().setAll(storage.getHeaders("items").stream()
                .filter(header -> !header.equals("id"))
                .filter(header -> !header.equals("totalPrice"))
                .collect(Collectors.toList()));
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
            showAlert("Update Successful", "The item has been updated successfully.", Alert.AlertType.INFORMATION);
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
            column.setCellFactory(tc -> {
                TableCell<CSV, String> cell = new TableCell<>();
                ContextMenu contextMenu = new ContextMenu();
                MenuItem copyMenuItem = new MenuItem("Copy");
                contextMenu.getItems().add(copyMenuItem);

                copyMenuItem.setOnAction(e -> {
                    if (!cell.isEmpty()) {
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(cell.getItem());
                        clipboard.setContent(content);
                    }
                });

                cell.textProperty().bind(cell.itemProperty());
                cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                    if (isNowEmpty) {
                        cell.setContextMenu(null);
                    } else {
                        cell.setContextMenu(contextMenu);
                    }
                });
                return cell;
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
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(content);
        alert.showAndWait();
        alert.showAndWait();
    }

    public void displayMenu() {
        launch();
    }
}