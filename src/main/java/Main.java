import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Main {
    private static String PATH_DOC = "src/main/resources/doc.json";
    private static String SIGNATURE = "test signature string";

    public static CrptApi.Document getDocument() {
        String jsonString = "";
        Gson gson = new Gson();
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PATH_DOC));
            jsonString = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Ошибка при чтении документа doc.json");
        }
        return gson.fromJson(jsonString, CrptApi.Document.class);
    }

    public static void main(String[] args) {
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 1);
        CrptApi.Document document = getDocument();
        crptApi.createDocument(document, SIGNATURE);
    }
}
