package keqing.aimc;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class AIRequestHandler {
    private static final int TIMEOUT = 15000;

    public static String sendToAI(String message) throws IOException {
        String formattedBody = String.format(AIConfig.jsonBody, message.replace("\"", "\\\""));

        HttpURLConnection conn = (HttpURLConnection) new URL(AIConfig.url).openConnection();
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(formattedBody.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        if (status >= 200 && status < 300) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                return br.lines().collect(Collectors.joining());
            }
        } else {
            throw new IOException("HTTP Error: " + status);
        }
    }
}