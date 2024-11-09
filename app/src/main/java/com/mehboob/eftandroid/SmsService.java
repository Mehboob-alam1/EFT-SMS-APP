package com.mehboob.eftandroid;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmsService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String sender = intent.getStringExtra("sender");
        String messageBody = intent.getStringExtra("messageBody");
        long timestamp = intent.getLongExtra("timestamp", 0);
        String receivedOnNumber = intent.getStringExtra("receivedOnNumber");

        // Default values if any field is null
        sender = (sender != null) ? sender : "Unknown";
        messageBody = (messageBody != null) ? messageBody : "No Content";
        receivedOnNumber = (receivedOnNumber != null) ? receivedOnNumber : "Unknown";

        try {
            pushSmsData(sender, messageBody, receivedOnNumber, timestamp);
        } catch (UnsupportedEncodingException e) {
            Log.e("SmsService", "Encoding error", e);
        }

        return START_NOT_STICKY;
    }

    private void pushSmsData(String sender, String messageBody, String receivedOnNumber, long timestamp) throws UnsupportedEncodingException {
        String date = convertTimestampToDate(timestamp);


        // Construct URL parameters
        String postData = "post=true" +
                "&timestamp=" + URLEncoder.encode(date, "UTF-8") +
                "&from_number=" + URLEncoder.encode(sender, "UTF-8") +
                "&to_number=" + URLEncoder.encode(receivedOnNumber, "UTF-8") +
                "&content=" + URLEncoder.encode(messageBody, "UTF-8");

        Log.d("SmsService", "Post data: " + postData);

        new SendSmsDataTask().execute(postData);
    }

    private String convertTimestampToDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static class SendSmsDataTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... postDataArray) {
            String postData = postDataArray[0];
            HttpURLConnection conn = null;

            try {
                URL url = new URL("https://eftpay.eft.xyz/dcb/lk/sms-tester");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                // Send the POST data
                try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
                    writer.write(postData);
                    writer.flush();
                }

                // Handle the response
                int responseCode = conn.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d("SmsService", "Response Code: " + responseCode);
                Log.d("SmsService", "Response Message: " + response.toString());

            } catch (Exception e) {
                Log.e("SmsService", "Error sending SMS data", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }
    }
}
