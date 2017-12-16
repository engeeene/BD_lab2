import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class MongoDBTest {
    private MongoDB mongoDB;
    private String[] ips, urls;
    private Date[] dates;

    @Before
    public void before() {
        mongoDB = new MongoDB("logsDbTest");

        try (MongoCursor<Document> cursor = mongoDB.getAllLogs().iterator()) {
            while (cursor.hasNext()) {
                mongoDB.getCollection().deleteOne(cursor.next());
            }
        }

        List<LogModel> logModels = generateLogs();
        logModels.forEach(log -> mongoDB.insert(log.toDocument()));
    }

    @After
    public void after() {
        mongoDB.close();
    }

    private List<LogModel> generateLogs() {
        List<LogModel> documents = new ArrayList<>();

        ips = new String[] {
                "109.108.22.11",
                "105.132.12.15",
        };

        urls = new String[] {
                "google.com",
                "facebook.com",
        };

        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH, -1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_MONTH, -2);

        dates = new Date[] {
                calendar1.getTime(),
                calendar2.getTime(),
        };

        documents.add(new LogModel(ips[0], urls[0], dates[0], 1000000));
        documents.add(new LogModel(ips[1], urls[1], dates[1], 8000000));

        return documents;
    }

    @Test
    public void canInsertItem() {
        Assert.assertTrue(mongoDB.insert(generateLogs().get(0).toDocument()));
    }

    @Test
    public void canGetAllLogs() {
        ArrayList<Document> logModels = toArrayList(mongoDB.getAllLogs());

        Assert.assertEquals(logModels.size(), 2);
        Assert.assertEquals(logModels.get(0).get(LogModel.IP), "109.108.22.11");
        Assert.assertEquals(logModels.get(1).get(LogModel.IP), "105.132.12.15");
    }

    @Test
    public void canGetVisitorsIpsOfUrl() {
        ArrayList<Document> logModels = toArrayList(mongoDB.getVisitorsIpsOfUrl(urls[0]));

        Assert.assertEquals(logModels.size(), 1);
        Assert.assertEquals(logModels.get(0).get(LogModel.IP), "109.108.22.11");
    }

    @Test
    public void canGetVisitedUrlsInPeriod() {
        ArrayList<Document> logModels = toArrayList(mongoDB.getVisitedUrlsInPeriod(dates[0], dates[0]));

        Assert.assertEquals(logModels.size(), 1);
        Assert.assertEquals(logModels.get(0).get(LogModel.URL), "google.com");
    }

    @Test
    public void canGetVisitedUrlsByIp() {
        ArrayList<Document> logModels = toArrayList(mongoDB.getVisitedUrlsByIp(ips[0]));

        Assert.assertEquals(logModels.size(), 1);
        Assert.assertEquals(logModels.get(0).get(LogModel.URL), "google.com");
    }

    @Test
    public void canGetTotalVisitTimeOfUrls() {
        ArrayList<Document> logModels = toArrayList(mongoDB.getTotalVisitTimeOfUrls());

        Assert.assertEquals(logModels.size(), 2);
        Assert.assertEquals(logModels.get(0).get("value"), 8000000L);
        Assert.assertEquals(logModels.get(1).get("value"), 1000000L);
    }

    @Test
    public void canGetTotalVisitCountOfUrls() {
        ArrayList<Document> logModels = toArrayList(mongoDB.getTotalVisitCountOfUrls());

        Assert.assertEquals(logModels.size(), 2);
        Assert.assertEquals(logModels.get(0).get("_id"), "facebook.com");
        Assert.assertEquals(logModels.get(1).get("_id"), "google.com");
        Assert.assertEquals(logModels.get(0).get("value"), 1D);
        Assert.assertEquals(logModels.get(1).get("value"), 1D);
    }

    @Test
    public void canGetVisitsCountOfUrlsInPeriod() {
        ArrayList<Document> logModels = toArrayList(mongoDB.getVisitsCountOfUrlsInPeriod(dates[0], dates[0]));

        Assert.assertEquals(logModels.size(), 1);
        Assert.assertEquals(logModels.get(0).get("_id"), "google.com");
        Assert.assertEquals(logModels.get(0).getDouble("value"), 1D, 0.0001);
    }

    @Test
    public void canGetTotalVisitsCountAndTimeOfIps() {
        ArrayList<Document> logModels = toArrayList(mongoDB.getTotalVisitsCountAndTimeOfIps());
        Assert.assertEquals(logModels.size(), 2);
    }


    private ArrayList<Document> toArrayList(MongoIterable<Document> documentFindIterable) {
        ArrayList<Document> result = new ArrayList<>();

        try (MongoCursor<Document> cursor = documentFindIterable.iterator()) {
            while (cursor.hasNext()) {
                result.add(cursor.next());
            }
        }

        return result;
    }
}
