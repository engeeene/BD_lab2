import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;

import java.io.Closeable;
import java.util.Date;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

public class MongoDB implements Closeable{
    private static final String T_IP = "this." + LogModel.IP;
    private static final String T_URL = "this." + LogModel.URL;
    private static final String T_TIME_STAMP = "this." + LogModel.TIME_STAMP;
    private static final String T_TIME_SPENT = "this." + LogModel.TIME_SPENT;

    private MongoCollection<Document> collection;
    private MongoDatabase dataBase;
    private MongoClient mongoClient;

    public  MongoDB(String databaseName) {
        mongoClient = new MongoClient("localhost", 27017);
        dataBase = mongoClient.getDatabase(databaseName);
        collection = dataBase.getCollection("logs");
    }

    public boolean insert(Document document) {
        try {
            collection.insertOne(document);
            return true;
        }
        catch (MongoException mongoException) {
            System.out.println("Can not insert object");
            return false;
        }
    }

    public MongoIterable<Document> getAllLogs() {
        return collection.find();
    }
    public MongoIterable<Document> getVisitorsIpsOfUrl(String url) {
        return collection
                .find(eq(LogModel.URL, url))
                .sort(ascending(LogModel.IP))
                .projection(fields(include(LogModel.IP), excludeId()));
    }
    public MongoIterable<Document> getVisitedUrlsInPeriod(Date fromDate, Date toDate) {
        return collection.
                find(and(gte(LogModel.TIME_STAMP, fromDate), lte(LogModel.TIME_STAMP, toDate))).
                projection(fields(include(LogModel.URL), excludeId())).sort(ascending(LogModel.URL));

    }
    public MongoIterable<Document> getVisitedUrlsByIp(String ip) {
        return collection.find(eq(LogModel.IP, ip))
                .sort(ascending(LogModel.URL))
                .projection(fields(include(LogModel.URL), excludeId()));
    }

    public MongoIterable<Document> getTotalVisitTimeOfUrls() {
        String destinationName = "totalVisitTimeOfUrls";
        String mapFunction = String.format("function () { emit(%s, %s); }", T_URL, T_TIME_SPENT);
        String reduceFunction = "function(key, values) { return Array.sum(values) / 1000; }";

        collection.mapReduce(mapFunction, reduceFunction).collectionName(destinationName).toCollection();
        return dataBase.getCollection(destinationName).find().sort(descending("value"));
    }
    public MongoIterable<Document> getTotalVisitCountOfUrls() {
        String destinationName = "totalVisitCountOfUrls";
        String mapFunction = String.format("function () { emit(%s, 1); }", T_URL);
        String reduceFunction = "function(key, values) { return Array.sum(values); }";

        collection.mapReduce(mapFunction, reduceFunction).collectionName(destinationName).toCollection();
        return dataBase.getCollection(destinationName).find().sort(descending("value"));
    }
    public MongoIterable<Document> getVisitsCountOfUrlsInPeriod(Date fromDate, Date toDate) {
        String destinationName = "visitsCountOfUrlsInPeriod";
        String mapFunction = String.format("function () {" +
                "if (%s >= %s && %s <= %s)" +
                " emit(%s, 1); }", T_TIME_STAMP, fromDate.getTime(), T_TIME_STAMP, toDate.getTime(), T_URL);
        String reduceFunction = "function(key, values) { return Array.sum(values); }";

        collection.mapReduce(mapFunction, reduceFunction).collectionName(destinationName).toCollection();
        return dataBase.getCollection(destinationName).find().sort(descending("value"));
    }
    public MongoIterable<Document> getTotalVisitsCountAndTimeOfIps() {
        String destinationName = "totalVisitsCountAndTimeOfIps";
        String mapFunction = String.format("function (){ emit({ip: %s, url: %s}, {totalCount: 1, totalDuration: %s}); }", T_IP, T_URL, T_TIME_SPENT);
        String reduceFunction = "function(key, values) {" +
                "var totalCount = 0; " +
                "var totalDuration = 0; " +
                "for (var i in values) {" +
                "totalCount += values[i].totalCount;" +
                "totalDuration += values[i].totalDuration;}" +
                "return {totalCount: totalCount, totalDuration: totalDuration}; }";

        collection.mapReduce(mapFunction, reduceFunction).collectionName(destinationName).toCollection();
        return dataBase.getCollection(destinationName).find().sort(descending("url", "totalCount", "totalDuration"));
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
