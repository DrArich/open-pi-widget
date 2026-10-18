package com.openpi.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class PiWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(
        Context context,
        AppWidgetManager manager,
        int[] widgetIds
    ) {
        final Context appContext = context.getApplicationContext();

        new Thread(() -> {
            String status;
            String metrics;

            try {
                String[] data = PiApi.load();
                status = "● Raspberry Pi Online";
                metrics = data[1] + "\n" + data[3];
            } catch (Exception e) {
                status = "● Нет соединения";
                metrics = "CPU: --%   RAM: --%\nTEMP: --°C   DISK: --%";
            }

            for (int id : widgetIds) {
                RemoteViews views = new RemoteViews(
                    appContext.getPackageName(),
                    R.layout.pi_widget
                );

                views.setTextViewText(R.id.widget_status, status);
                views.setTextViewText(R.id.widget_metrics, metrics);
                manager.updateAppWidget(id, views);
            }
        }).start();
    }
}
