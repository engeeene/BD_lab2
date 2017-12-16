import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Main {

    private static String[] ips, urls;
    private static Date[] dates;

    public static void main(String[] args) {
        List<LogModel> logModels = generateLogs();

        try (MongoDB mongoDB = new MongoDB("logsDb")) {

            logModels.forEach(log -> mongoDB.insert(log.toDocument()));
            printIterable(mongoDB.getAllLogs());

            printIterable(mongoDB.getVisitedUrlsByIp(ips[3]));
            printIterable(mongoDB.getVisitorsIpsOfUrl(urls[3]));
            printIterable(mongoDB.getVisitedUrlsInPeriod(dates[2], dates[0]));
            printIterable(mongoDB.getTotalVisitTimeOfUrls());
            printIterable(mongoDB.getTotalVisitCountOfUrls());
            printIterable(mongoDB.getVisitsCountOfUrlsInPeriod(dates[2], dates[0]));
            printIterable(mongoDB.getTotalVisitsCountAndTimeOfIps());
        }
    }

    public static List<LogModel> generateLogs() {
        List<LogModel> documents = new ArrayList<>();

        ips = new String[]{
                "109.108.22.11",
                "105.132.12.15",
                "219.108.32.12",
                "109.101.72.13",
                "179.180.22.17",
        };

        urls = new String[]{
                "google.com",
                "facebook.com",
                "github.com",
                "aliexpress.com",
                "rozetka.ua"
        };

        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH, -1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_MONTH, -2);
        Calendar calendar3 = Calendar.getInstance();
        calendar3.add(Calendar.DAY_OF_MONTH, -3);
        Calendar calendar4 = Calendar.getInstance();
        calendar4.add(Calendar.DAY_OF_MONTH, -4);
        Calendar calendar5 = Calendar.getInstance();
        calendar5.add(Calendar.DAY_OF_MONTH, -5);

        dates = new Date[]{
                calendar1.getTime(),
                calendar2.getTime(),
                calendar3.getTime(),
                calendar4.getTime(),
                calendar5.getTime(),
        };

        documents.add(new LogModel(ips[0], urls[0], dates[0], 1000000));
        documents.add(new LogModel(ips[0], urls[2], dates[1], 5000000));
        documents.add(new LogModel(ips[0], urls[3], dates[2], 3000000));
        documents.add(new LogModel(ips[0], urls[1], dates[3], 2000000));
        documents.add(new LogModel(ips[0], urls[4], dates[4], 1000000));

        documents.add(new LogModel(ips[1], urls[2], dates[0], 8000000));
        documents.add(new LogModel(ips[1], urls[4], dates[1], 1000000));
        documents.add(new LogModel(ips[1], urls[0], dates[3], 3000000));
        documents.add(new LogModel(ips[1], urls[3], dates[4], 9000000));

        documents.add(new LogModel(ips[2], urls[1], dates[0], 5000000));
        documents.add(new LogModel(ips[2], urls[2], dates[1], 2000000));
        documents.add(new LogModel(ips[2], urls[3], dates[2], 1000000));
        documents.add(new LogModel(ips[2], urls[4], dates[4], 7000000));

        documents.add(new LogModel(ips[3], urls[1], dates[0], 4000000));
        documents.add(new LogModel(ips[3], urls[2], dates[3], 1000000));

        documents.add(new LogModel(ips[4], urls[0], dates[1], 6000000));
        documents.add(new LogModel(ips[4], urls[2], dates[3], 7000000));
        documents.add(new LogModel(ips[4], urls[3], dates[4], 4000000));

        return documents;
    }

    public static void printIterable(MongoIterable<Document> documentFindIterable) throws IllegalArgumentException {
        if (documentFindIterable == null) {
            throw new IllegalArgumentException();
        }

        try (MongoCursor<Document> cursor = documentFindIterable.iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        }
    }
}
