package com.mehboob.eftandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    StringBuilder messageBodyBuilder = new StringBuilder();
                    String sender = null;
                    long timestamp = System.currentTimeMillis();
                    String receivedOnNumber = null;

                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        if (sender == null) {
                            sender = smsMessage.getDisplayOriginatingAddress();
                        }
                        messageBodyBuilder.append(smsMessage.getMessageBody());
                        if (receivedOnNumber == null) {
                            int subscriptionId = bundle.getInt("subscription", -1);
                            receivedOnNumber = getPhoneNumberFromSubscriptionId(context, subscriptionId);
                        }
                    }

                    // Final concatenated message body
                    String messageBody = messageBodyBuilder.toString();

                    // Log the full message
                    Log.d("SMS Receiver", "Sender: " + sender);
                    Log.d("SMS Receiver", "Full Message: " + messageBody);
                    Log.d("SMS Receiver", "Received On: " + receivedOnNumber);
                    Log.d("SMS Receiver", "Timestamp: " + timestamp);

                    // Start Service to push SMS with SIM phone number
                    Intent serviceIntent = new Intent(context, SmsService.class);
                    serviceIntent.putExtra("sender", sender);
                    serviceIntent.putExtra("messageBody", messageBody);
                    serviceIntent.putExtra("timestamp", timestamp);
                    serviceIntent.putExtra("receivedOnNumber", receivedOnNumber);
                    context.startService(serviceIntent);
                }
            }
        }
    }

//    private String getSimPhoneNumber(Context context) {
//        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (tm != null) {
//            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            return "Unknown";
//            }
//
//            return tm.getLine1Number(); // Gets the phone number of the current active SIM
//        }
//        return "Unknown";
//    }

    // Method to get phone number from Subscription ID
    private String getPhoneNumberFromSubscriptionId(Context context, int subscriptionId) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            SubscriptionInfo info = subscriptionManager.getActiveSubscriptionInfo(subscriptionId);
            if (info != null) {
                return info.getNumber();  // Returns the phone number associated with the subscription
            }
        }
        return "Unknown";
    }
}

