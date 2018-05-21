package de.pixart.messenger.services;

import android.content.Intent;

import java.util.Map;

public class PushMessageReceiver extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Map<String, String> data = message.getData();
        Intent intent = new Intent(this, XmppConnectionService.class);
        intent.setAction(XmppConnectionService.ACTION_FCM_MESSAGE_RECEIVED);
        intent.putExtra("account", data.get("account"));
        startService(intent);
    }
}
