package api;

import model.ExchangeRateResponse;
import com.google.gson.Gson;
import util.ConfigLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class CurrencyApiClient {
    private static final String API_KEY = ConfigLoader.getApiKey();
    private static final String BASE_URL = ConfigLoader.getBaseUrl();

    private final HttpClient httpClient;

    public CurrencyApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public HttpRequest createRequest(String endpoint) {
        String url = BASE_URL + API_KEY + endpoint;

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    public String getResponseBody(HttpRequest request) throws Exception {
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error en la API: " + response.statusCode());
        }

        return response.body();
    }

    public ExchangeRateResponse getExchangeRates(String baseCurrency) throws Exception {
        String endpoint = "/latest/" + baseCurrency;
        HttpRequest request = createRequest(endpoint);
        String responseBody = getResponseBody(request);

        Gson gson = new Gson();
        return gson.fromJson(responseBody, ExchangeRateResponse.class);
    }
}