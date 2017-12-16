import org.junit.Assert;
import org.junit.Test;

public class MainTest {
    @Test
    public void canGenerateLogs() {
        Assert.assertNotNull(Main.generateLogs());
    }

    @Test(expected = IllegalArgumentException.class)
    public void canNotPerformPrintigNull() {
        Main.printIterable(null);
    }
}