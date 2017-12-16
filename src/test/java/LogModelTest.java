import org.bson.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class LogModelTest {
    private LogModel currentLogModel;

    @Before
    public void before() {
        currentLogModel = new LogModel("101.102.11.11", "google.com", new Date(), 100);
    }

    @Test
    public void canGetSetIp() {
        String ip = "105.102.11.11";
        currentLogModel.setUserIpAddress(ip);
        Assert.assertEquals(ip, currentLogModel.getUserIpAddress());
    }

    @Test
    public void canGetSetUrl() {
        String url = "yandex.ua";
        currentLogModel.setSourceUrl(url);
        Assert.assertEquals(url, currentLogModel.getSourceUrl());
    }

    @Test
    public void canGetSetTimeStamp() {
        Date date = new Date();
        currentLogModel.setTimeStamp(date);
        Assert.assertEquals(date, currentLogModel.getTimeStamp());
    }

    @Test
    public void canGetSetTimeSpent() {
        long spent = 1000;
        currentLogModel.setTimeSpent(spent);
        Assert.assertEquals(spent, currentLogModel.getTimeSpent());
    }

    @Test
    public void canConvertToDocument() {
        Document createdDocument = currentLogModel.toDocument();

        Assert.assertNotNull(createdDocument);
        Assert.assertEquals(createdDocument.get(LogModel.URL), currentLogModel.getSourceUrl());
        Assert.assertEquals(createdDocument.get(LogModel.TIME_SPENT), currentLogModel.getTimeSpent());
        Assert.assertEquals(createdDocument.get(LogModel.TIME_STAMP), currentLogModel.getTimeStamp());
        Assert.assertEquals(createdDocument.get(LogModel.IP), currentLogModel.getUserIpAddress());
    }


    @Test
    public void canConvertFromDocument() {
        Document createdDocument = new Document()
                .append(LogModel.URL, "google.com")
                .append(LogModel.TIME_STAMP, new Date())
                .append(LogModel.TIME_SPENT, 100L)
                .append(LogModel.IP, "100.111.11.11");

        LogModel logModel = new LogModel(createdDocument);
        Assert.assertNotNull(createdDocument);
        Assert.assertEquals(createdDocument.get(LogModel.URL), logModel.getSourceUrl());
        Assert.assertEquals(createdDocument.get(LogModel.TIME_SPENT), logModel.getTimeSpent());
        Assert.assertEquals(createdDocument.get(LogModel.TIME_STAMP), logModel.getTimeStamp());
        Assert.assertEquals(createdDocument.get(LogModel.IP), logModel.getUserIpAddress());
    }

    @Test
    public void canConvertToString() {
        Assert.assertNotNull(currentLogModel.toString());
    }
}
