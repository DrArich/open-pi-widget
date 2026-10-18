package com.openpi.widget;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Iterator;
import org.json.JSONObject;

public class PiApi {
    private static final String URL_API = "https://sezimqala-1.tailf34f92.ts.net/api/status";

    public static String[] load() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(URL_API).openConnection();
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);
        try {
            if (connection.getResponseCode() != 200) throw new Exception("HTTP " + connection.getResponseCode());
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (InputStream input = connection.getInputStream()) {
                byte[] buffer = new byte[2048];
                int count;
                while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
            }
            JSONObject data = new JSONObject(output.toString("UTF-8"));
            String temp = data.isNull("temperature") ? "--" :
                String.format(Locale.US, "%.1f", data.getDouble("temperature"));
            String metrics = String.format(Locale.US,
                "CPU %.1f%%  ·  RAM %.1f%%\nTEMP %s°C  ·  DISK %.1f%%",
                data.getDouble("cpu"), data.getDouble("ram"), temp, data.getDouble("disk"));
            JSONObject services = data.optJSONObject("services");
            StringBuilder serviceLines = new StringBuilder();
            int active = 0, total = 0;
            if (services != null) {
                Iterator<String> names = services.keys();
                while (names.hasNext()) {
                    String name = names.next();
                    String state = services.optString(name, "unknown");
                    if (serviceLines.length() > 0) serviceLines.append("\n");
                    serviceLines.append("active".equals(state) ? "● " : "○ ")
                        .append(name).append(" — ").append(state);
                    if ("active".equals(state)) active++;
                    total++;
                }
            }
            return new String[]{"Online", metrics, serviceLines.toString(),
                total == 0 ? "Службы: нет данных" : "Службы: " + active + "/" + total + " активны"};
        } finally { connection.disconnect(); }
    }
}
