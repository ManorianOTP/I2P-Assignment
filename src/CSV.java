import java.util.List;

public class CSV {
    // id,description, qtySold, amount, stockRemaining, transactionType
    int id;
    String description;
    int qtySold;
    int amount;
    int  stockRemaining;
    String transactionType;
    double unitPrice;
    int qtyInStock;
    double totalPrice;

    CSV(List<String> parameterFileRow, Boolean transaction) {
        if (transaction) {
            this.id = Integer.parseInt(parameterFileRow.get(0));
            this.description = parameterFileRow.get(1);
            this.qtySold = Integer.parseInt(parameterFileRow.get(2));
            this.amount = Integer.parseInt(parameterFileRow.get(3));
            this.stockRemaining = Integer.parseInt(parameterFileRow.get(4));
            this.transactionType = parameterFileRow.get(5);
        } else {
            this.id = Integer.parseInt(parameterFileRow.get(0));
            this.description = parameterFileRow.get(1);
            this.unitPrice = Double.parseDouble(parameterFileRow.get(2));
            this.qtyInStock = Integer.parseInt(parameterFileRow.get(3));
            this.totalPrice = Double.parseDouble(parameterFileRow.get(4));
        }

    }
}
