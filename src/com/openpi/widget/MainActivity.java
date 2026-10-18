package com.openpi.widget;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends Activity {
    private TextView status;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(13, 20, 31));
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 50, 32, 32);
        TextView title = new TextView(this);
        title.setText("● OPEN PI 2.0");
        title.setTextSize(27);
        title.setTextColor(Color.rgb(118, 228, 176));
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        status = new TextView(this);
        status.setText("\nRaspberry Pi 5\n\nПодключение...");
        status.setTextSize(17);
        status.setTextColor(Color.WHITE);
        status.setPadding(10, 24, 10, 24);
        root.addView(status);
        Button refreshButton = new Button(this);
        refreshButton.setText("Обновить статус");
        refreshButton.setOnClickListener(v -> refresh());
        root.addView(refreshButton);
        scroll.addView(root);
        setContentView(scroll);
        refresh();
    }
    private void refresh() {
        status.setText("\nПодключение к Raspberry Pi...");
        new Thread(() -> {
            String message;
            try {
                String[] data = PiApi.load();
                message = "\nRaspberry Pi 5  ● " + data[0] + "\n\n" +
                    data[1] + "\n\n" + data[3] + "\n\n" + data[2];
            } catch (Exception e) {
                message = "\nRaspberry Pi 5\n\nНет соединения\nПроверьте Tailscale и API";
            }
            final String result = message;
            runOnUiThread(() -> status.setText(result));
        }).start();
    }
}
