package uni.S257123.storage.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import uni.S257123.models.CSV;
import uni.S257123.storage.interfaces.Storage;
import com.mongodb.client.FindIterable;

import java.text.SimpleDateFormat;
import java.util.*;

public class DatabaseStorage implements Storage {

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    public final Map<String, MongoCollection<Document>> csvDataSource;

    public DatabaseStorage() {
        String connectionString = "mongodb+srv://dbUserName:dbUserPassword@i2pdatabase.x8kre7j.mongodb.net/?retryWrites=true&w=majority";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        try {
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase("I2P");
            // Send a ping to confirm a successful connection
            database.runCommand(new Document("ping", 1));
            System.out.println("You successfully connected to MongoDB!");
            csvDataSource = new LinkedHashMap<>() {{
                put("items", database.getCollection("items"));
                put("transactions", database.getCollection("transactions"));
            }};
        } catch (MongoException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public List<String> getSources() {
        return new ArrayList<>(csvDataSource.keySet());
    }

    @Override
    public List<String> getHeaders(String target) {
        return readContents(target).get(0).definedFields.stream().toList();
    }

    @Override
    public boolean addRecord(List<String> parameters, String target, String transactionType) {
        List<String> parametersComplete = new ArrayList<>();
        if (target.equals("items")) {
            Map<String, Object> map = new LinkedHashMap<>() {{
                put("description", parameters.get(0));
                put("unitPrice", Double.parseDouble(parameters.get(1)));
                put("qtyInStock", Integer.parseInt(parameters.get(2)));
                put("totalPrice", Double.parseDouble(parameters.get(1)) * Integer.parseInt(parameters.get(2)));
            }};
            Document newDocument = new Document(map);
            csvDataSource.get(target).insertOne(newDocument);

            List<CSV> file = searchRecord("items", Pair.of("description",parameters.get(0)));
            parametersComplete.add((String) file.get(0).GetPropertyByName("id"));
            parametersComplete.addAll(parameters);
            addRecord(parametersComplete, "transactions", "added"); // adds a transaction record to the attempt
        } else {
            Map<String, Object> map = new LinkedHashMap<>() {{
                put("id", new ObjectId(parameters.get(0)));
                put("description", parameters.get(1));
                put("unitPrice", Double.parseDouble(parameters.get(2)));
                put("qtyInStock", Integer.parseInt(parameters.get(3)));
                put("totalPrice", Double.parseDouble(parameters.get(2)) * Integer.parseInt(parameters.get(3)));
                put("transactionType", transactionType);
                put("date", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            }};

            Document newDocument = new Document(map);
            csvDataSource.get(target).insertOne(newDocument);
        }
        return true;
    }

    @Override
    public boolean addRecord(List<String> parameters, String target) {
        return addRecord(parameters, target, "added");
    }

    @Override
    public void updateRecord(List<String> recordInfo) {
        Bson filter = Filters.eq("_id", new ObjectId(recordInfo.get(0)));
        FindIterable<Document> foundDocuments =  csvDataSource.get("items").find(filter);
        Document documentToUpdate= foundDocuments.first();
        if (recordInfo.get(1).equals("description")) {
            csvDataSource.get("items").updateOne(documentToUpdate, new Document("$set",
                    new Document(recordInfo.get(1), recordInfo.get(2))));
        } else if (recordInfo.get(1).equals("unitPrice")) {
            csvDataSource.get("items").updateOne(documentToUpdate, new Document("$set",
                    new Document(recordInfo.get(1), Double.parseDouble(recordInfo.get(2)))));

        } else {
            csvDataSource.get("items").updateOne(documentToUpdate, new Document("$set",
                    new Document(recordInfo.get(1), Integer.parseInt(recordInfo.get(2)))));
        }


        List<String> parameters = new ArrayList<>();
        foundDocuments =  csvDataSource.get("items").find(filter); //updated so new property shows up in transaction
        documentToUpdate= foundDocuments.first();

        if (recordInfo.get(1).equals("unitPrice") || recordInfo.get(1).equals("qtyInStock")) {
            csvDataSource.get("items").updateOne(documentToUpdate, new Document("$set",
                    new Document("totalPrice", (double) documentToUpdate.get("unitPrice") * (int) documentToUpdate.get("qtyInStock"))));
        }
        for (String key : documentToUpdate.keySet()) {
            Object value = documentToUpdate.get(key);
            parameters.add(value.toString());
        }
        addRecord(parameters,"transactions","updated");
    }

    @Override
    public void deleteRecord(String id) {
        Bson filter = Filters.eq("_id", new ObjectId(id));
        FindIterable<Document> foundDocuments =  csvDataSource.get("items").find(filter);
        Document documentToDelete = foundDocuments.first();
        csvDataSource.get("items").deleteOne(documentToDelete);

        List<String> parameters = new ArrayList<>();
        for (String key : documentToDelete.keySet()) {
            Object value = documentToDelete.get(key);
            parameters.add(value.toString());
        }
        addRecord(parameters,"transactions","deleted");
    }

    @Override
    public List<CSV> readContents(String target) {
        FindIterable<Document> foundDocuments =  csvDataSource.get(target).find();
        List<CSV> list = new ArrayList<>();
        for (Document doc : foundDocuments) {
            List<String> fieldNames = new ArrayList<>();
            List<String> fieldValues = new ArrayList<>();
            for (String key : doc.keySet()) {
                fieldNames.add(key);
                Object value = doc.get(key);
                fieldValues.add(value.toString());
            }
            list.add(new CSV(fieldValues, fieldNames));
        }
        return list;
    }

    @Override
    public List<CSV> searchRecord(String target, Pair<String, String> propertyNameValuePair) {
        Bson filter;
        if (propertyNameValuePair.getLeft().equals("id")) {
            if (propertyNameValuePair.getRight().isEmpty()) {
                return readContents(target);
            } else if (target.equals("transactions")) {
                filter = Filters.eq(propertyNameValuePair.getLeft(), new ObjectId(propertyNameValuePair.getRight()));
            }
            else {
                filter = Filters.eq("_" + propertyNameValuePair.getLeft(), new ObjectId(propertyNameValuePair.getRight()));
            }
        } else if (propertyNameValuePair.getLeft().equals("unitPrice") ||
                propertyNameValuePair.getLeft().equals("qtyInStock") ||
                propertyNameValuePair.getLeft().equals("totalPrice")) {
            String queryString = String.format(
                    "{ $regexMatch: { input: { $toString: '$%s' }, regex: '.*%s.*' } }",
                    propertyNameValuePair.getLeft(),
                    propertyNameValuePair.getRight()
            );
            filter = Aggregates.match(Filters.expr(Document.parse(queryString)));
            List<CSV> list = new ArrayList<>();
            csvDataSource.get(target).aggregate(List.of(filter)).forEach(doc -> {
                List<String> fieldNames = new ArrayList<>();
                List<String> fieldValues = new ArrayList<>();
                for (String key : doc.keySet()) {
                    fieldNames.add(key);
                    Object value = doc.get(key);
                    fieldValues.add(value.toString());
                }
                list.add(new CSV(fieldValues, fieldNames));
            });
            return list;
        }
        else {
                filter = Filters.regex(propertyNameValuePair.getLeft(), ".*" + propertyNameValuePair.getRight() + ".*");
            }
        FindIterable<Document> foundDocuments =  csvDataSource.get(target).find(filter);
        List<CSV> list = new ArrayList<>();
        for (Document doc : foundDocuments) {
            List<String> fieldNames = new ArrayList<>();
            List<String> fieldValues = new ArrayList<>();
            for (String key : doc.keySet()) {
                fieldNames.add(key);
                Object value = doc.get(key);
                fieldValues.add(value.toString());
            }
            list.add(new CSV(fieldValues, fieldNames));
        }
        return list;
    }

    @Override
    public String generateID() {
        return null;
    }

    @Override
    public List<String> getIDs() {
        List<String> list = new ArrayList<>();
        List<CSV> data = readContents("items");
        for (CSV csv : data) {
            list.add((String) csv.GetPropertyByName("id"));
        }
        return list;
    }
}
