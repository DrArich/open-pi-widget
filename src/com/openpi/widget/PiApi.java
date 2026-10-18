package com.openpi.widget;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import org.json.JSONObject;

public class PiApi {
    private static final String URL_API =
        "https://sezimqala-1.tailf34f92.ts.net/api/status";

    public static String[] load() throws Exception {
        HttpURLConnection connection = (HttpURLConnection)
            new URL(URL_API).openConnection();

        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);

        try {
            if (connection.getResponseCode() != 200) {
                throw new Exception("HTTP " + connection.getResponseCode());
            }

            InputStream input = connection.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count;

            try {
                while ((count = input.read(buffer)) != -1) {
                    output.write(buffer, 0, count);
                }
            } finally {
                input.close();
            }

            JSONObject data = new JSONObject(
                output.toString("UTF-8")
            );

            String metrics = String.format(
                java.util.Locale.US,
                "CPU: %.1f%%   RAM: %.1f%%\nTEMP: %.1f°C   DISK: %.1f%%",
                data.getDouble("cpu"),
                data.getDouble("ram"),
                data.getDouble("temperature"),
                data.getDouble("disk")
            );

            return new String[]{"Online", metrics};
        } finally {
            connection.disconnect();
        }
    }
}
