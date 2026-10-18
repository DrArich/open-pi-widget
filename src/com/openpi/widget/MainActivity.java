package com.openpi.widget;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView status;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 48, 32, 32);
        root.setBackgroundColor(Color.rgb(23, 35, 51));

        TextView title = new TextView(this);
        title.setText("Open Pi Widget");
        title.setTextSize(26);
        title.setTextColor(Color.rgb(118, 228, 176));
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        status = new TextView(this);
        status.setText("\nRaspberry Pi 5\n\nПодключение...");
        status.setTextSize(18);
        status.setTextColor(Color.WHITE);
        root.addView(status);

        setContentView(root);
        refresh();
    }

    private void refresh() {
        new Thread(() -> {
            String message;
            try {
                String[] data = PiApi.load();
                message = "\nRaspberry Pi 5\n\n" +
                    data[1] + "\n\nStatus: " + data[0];
            } catch (Exception e) {
                message = "\nRaspberry Pi 5\n\nНет соединения";
            }

            final String result = message;
            runOnUiThread(() -> status.setText(result));
        }).start();
    }
}
