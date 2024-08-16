
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.concurrent.TimedSemaphore;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Класс для работы с API Честного знака.
 */
public class CrptApi {
    private final TimedSemaphore semaphore;
    private final String CREATE_DOC_URL = "https://ismp.crpt.ru/api/v3/lk/documents/create";

    /**
     * @param timeUnit     промежуток времени
     * @param requestLimit максимальное количество запросов в промежутке времени
     */
    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.semaphore = new TimedSemaphore(1, timeUnit, requestLimit);
    }

    /**
     * Метод для создания документа.
     *
     * @param document  документ для создания.
     * @param signature подпись для документа.
     */
    public void createDocument(Document document, String signature) {
        HttpClient client = HttpClient.newHttpClient();
        try {
            semaphore.acquire();
            HttpRequest request = createRequest(document, signature);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println(response.body());
                JsonElement jsonElement = JsonParser.parseString(response.body());
                if (!jsonElement.isJsonObject()) {
                    System.out.println("Ответ от сервера не соответствует ожидаемому.");
                    return;
                }
                JsonObject jsonObject = jsonElement.getAsJsonObject();
            } else {
                System.out.println("Во время выполнения запроса возникла ошибка " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Document {
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private String production_date;
        private String production_type;
        private List<Product> products;
        private String reg_date;
        private String reg_number;

    }

    public static class Description {
        private String participantInn;
    }

    public static class Product {
        private String certificate_document;
        private String certificate_document_date;
        private String certificate_document_number;
        private String owner_inn;
        private String producer_inn;
        private String production_date;
        private String tnved_code;
        private String uit_code;
        private String uitu_code;
    }

    /**
     * @param document  документ для POST запроса.
     * @param signature подпись
     */
    private HttpRequest createRequest(Document document, String signature) {
        Gson gson = new Gson();
        String json = gson.toJson(document);
        return HttpRequest.newBuilder()
                .uri(URI.create(CREATE_DOC_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
    }
}