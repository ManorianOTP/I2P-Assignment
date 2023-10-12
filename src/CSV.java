import java.util.List;

public class CSV {
    int id;
    String description;
    int qtySold;
    int amount;
    int stockRemaining;
    String transactionType;
    double unitPrice;
    int qtyInStock;
    double totalPrice;

    public CSV(List<String> parameterFileRow, List<String> headers) {
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            String value = parameterFileRow.get(i);

            switch (header) {
                case "id" -> this.id = Integer.parseInt(value);
                case "description" -> this.description = value;
                case "qtySold" -> this.qtySold = Integer.parseInt(value);
                case "amount" -> this.amount = Integer.parseInt(value);
                case "stockRemaining" -> this.stockRemaining = Integer.parseInt(value);
                case "transactionType" -> this.transactionType = value;
                case "unitPrice" -> this.unitPrice = Double.parseDouble(value);
                case "qtyInStock" -> this.qtyInStock = Integer.parseInt(value);
                case "totalPrice" -> this.totalPrice = Double.parseDouble(value);
                default -> throw new IllegalArgumentException("Unexpected header: " + header);
            }
        }
    }

    @Override
    public String toString() {
        return "id=%d, description='%s', qtySold=%d, amount=%d, stockRemaining=%d, transactionType='%s', unitPrice=%s, qtyInStock=%d, totalPrice=%s".formatted(
                id,
                description,
                qtySold,
                amount,
                stockRemaining,
                transactionType,
                unitPrice,
                qtyInStock,
                totalPrice);
    }

}