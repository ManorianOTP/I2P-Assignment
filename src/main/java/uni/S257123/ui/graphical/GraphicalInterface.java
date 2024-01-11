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
import uni.S257123.storage.text.TextStorage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A javafx-based graphical user interface for the Inventory Management System. This class
 * implements methods that interact with the user via a graphical window.
 *
 * <p>All methods depend upon being able to load the GUI for input and output. Invalid inputs
 * are handled within each method, ensuring that the user is always prompted to enter
 * correct data as necessary (primarily by dynamically providing the user dropdown options).</p>
 *
 * @see uni.S257123.ui.console.ConsoleInterface
 */
public class GraphicalInterface extends Application {
    static Storage storage = new TextStorage();

    /**
     * Sets the storage that powers the backend of the GUI. If this method doesn't get called,
     * the default will be the {@link TextStorage}.
     * @param storage the backend to be used for the GUI
     */
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

    /**
     * Sets up each tab in the GUI. This is the first method to resolve, so any code prior to the application starting
     * should be run from here. This method sometimes gets re-called as an easy way to ensure all tables have up-to-date
     * data, and all dropdowns have the correct available options.
     *
     * <p>To avoid too many repetitive comments, most of the methods that appear within this have inline comments
     * rather than javadoc style comments</p>
     */
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
        // When option chosen, make button visible
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
        // When option chosen, make option visible
        UpdateItemIDSelection.valueProperty().addListener((observable, oldValue, newValue) -> UpdateItemPropertySelection.setVisible(true));
        // Add all headers except id and total price, as you shouldn't be able to edit those
        UpdateItemPropertySelection.getItems().setAll(storage.getHeaders("items").stream()
                .filter(header -> !header.equals("id"))
                .filter(header -> !header.equals("totalPrice"))
                .collect(Collectors.toList()));
        // When option chosen, make option visible
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
        // When option chosen, make option visible
        SearchSourcesSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            SearchHeadersSelector.setValue(null);
            SearchSubmitButton.setVisible(false);
            SearchHeadersSelector.getItems().setAll(storage.getHeaders(newValue));
            SearchHeadersSelector.setVisible(true);
        });
        // When option chosen, make option visible
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

    /**
     * Used to set up a tables' columns with both how they accept data, and provide them with the ability to be copied
     * to clipboard from. Given the custom CSV classed being used, this method overrides the traditional javabeans
     * style of getting properties from objects with a custom CellValueFactory that uses the
     * {@link CSV#GetPropertyByName(String)}.
     * @param headers the list of headers that should be turned into column headers
     * @param table the table that these columns should be added to once created
     */
    private void setupTableColumns(ObservableList<String> headers, TableView<CSV> table) {
        table.getItems().clear();
        table.getColumns().clear();
        for (String propertyName : headers) {
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
        //Ensures that the string can be turned into a number
        if (!NumberUtils.isCreatable(AddItemUnitPrice.getText())) {
            showAlert("Invalid Input", "Unit Price must be a valid number", Alert.AlertType.ERROR);
            return false;
        }
        //Ensures that the string can be turned into a number
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

    /**
     * Displays a popup indicating that some kind of event has occurred.
     * @param title the heading of the popup
     * @param content the description to the user of what the popup is about
     * @param alertType the icon indicating what the tone of the message is
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(content);
        alert.showAndWait();
    }

    public void displayMenu() {
        launch();
    }
}