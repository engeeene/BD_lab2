import org.bson.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.Date;

public class LogModel implements Serializable{
    public static final String IP = "IP";
    public static final String URL = "URL";
    public static final String TIME_STAMP = "timeStamp";
    public static final String TIME_SPENT = "timeSpent";

    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")
    private String userIpAddress;
    @Pattern(regexp = "^(http://|https://|www.)?((((\\\\S+/)+\\\\S)(\\\\S*))|(\\\\S*\\\\.\\\\S*))$")
    private String sourceUrl;
    @NotNull
    private Date timeStamp;
    @Positive
    private long timeSpent;

    public LogModel(String userIpAddress, String sourceUrl, Date timeStamp, long timeSpent) {
        this.userIpAddress = userIpAddress;
        this.sourceUrl = sourceUrl;
        this.timeStamp = timeStamp;
        this.timeSpent = timeSpent;
    }
    public LogModel(Document document) {
        this.userIpAddress = document.getString(IP);
        this.sourceUrl = document.getString(URL);
        this.timeStamp = document.getDate(TIME_STAMP);
        this.timeSpent = document.getLong(TIME_SPENT);
    }

    public Document toDocument() {
        Document document = new Document();
        document.append(URL, this.sourceUrl);
        document.append(IP, this.userIpAddress);
        document.append(TIME_STAMP, this.timeStamp);
        document.append(TIME_SPENT, this.timeSpent);

        return document;
    }

    public String getUserIpAddress() {
        return userIpAddress;
    }
    public void setUserIpAddress(String userIpAddress) {
        this.userIpAddress = userIpAddress;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTimeSpent() {
        return timeSpent;
    }
    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    @Override
    public String toString() {
        return "[" +
                "userIpAddress=" + userIpAddress +
                ", sourceUrl=" + sourceUrl +
                ", timeStamp=" + timeStamp +
                ", timeSpent=" + timeSpent +
                ']';
    }
}
