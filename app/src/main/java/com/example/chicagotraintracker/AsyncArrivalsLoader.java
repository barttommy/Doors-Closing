package com.example.chicagotraintracker;

import android.os.AsyncTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AsyncArrivalsLoader extends AsyncTask<String, Void, String> {

    private static final String API_BASE = "http://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?";
    private static final String API_KEY = "73436616b5af4465bc65790aa9d4886c";

    AsyncArrivalsLoader() {
    }

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {

    }

    private String currentTime() {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return formatter.format(date);
    }

    // TODO: Clean up, refactor into separate functions
    private String[] parseArrivalTime(String arrivalTime) {
        String[] result = {"", ""};
        try {
            arrivalTime = arrivalTime.substring(11);

            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date current = format.parse(currentTime());
            Date arrival = format.parse(arrivalTime);
            long difference = (arrival.getTime() - current.getTime()) / 60000;

            Calendar c = Calendar.getInstance();
            c.setTime(arrival);
            String meridiem = (c.get(Calendar.HOUR_OF_DAY) >= 12) ? "pm" : "am";

            String minutes = String.valueOf(c.get(Calendar.MINUTE));
            minutes = (minutes.length() == 2) ? minutes : "0" + minutes;

            String timestamp = String.format("%s:%s", c.get(Calendar.HOUR_OF_DAY), minutes);

            result[0] = String.format("Arriving at %s %s", timestamp, meridiem);
            result[1] = (difference <= 1) ? "Due" : difference + " min";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
