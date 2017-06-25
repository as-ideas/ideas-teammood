import de.axelspringer.ideas.team.mood.TeamMoodHttpClientFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Test;

import javax.net.ssl.SSLHandshakeException;

public class TeamMoodHttpClientFactoryTest {

    private TeamMoodHttpClientFactory tested;

    @Test
    public void ownClientWithKeystore_shouldWorkOnLetsEncryptPage() throws Exception {
        tested = new TeamMoodHttpClientFactory();

        final CloseableHttpClient client = tested.init();

        final CloseableHttpResponse response = client.execute(new HttpHost("app.teammood.com", 443, "https"), new HttpGet("/api/01770399-e272-4032-ad1e-c96e0dc90e58/moods?end=26-06-2017&start=25-05-2017"));
        Assert.assertNotNull(response);
    }

    @Test(expected = SSLHandshakeException.class)
    public void defaultClient_shouldWorkOnLetsEncryptPage() throws Exception {
        final CloseableHttpClient client = HttpClients.createDefault();

        final CloseableHttpResponse response = client.execute(new HttpHost("app.teammood.com", 443, "https"), new HttpGet("/api/01770399-e272-4032-ad1e-c96e0dc90e58/moods?end=26-06-2017&start=25-05-2017"));
        Assert.assertNotNull(response);
    }

}