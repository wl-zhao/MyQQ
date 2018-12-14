package com.johnwilliams.qq.tools;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.johnwilliams.qq.Activities.ChatActivity;
import com.johnwilliams.qq.Activities.LoginActivity;
import com.johnwilliams.qq.Activities.MainActivity;

public class OnClearFromRecentService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.v("ClearFromRecentService", "END");
        //Code here
        try{
            MainActivity.messageReceiver.stop();
            LoginActivity.connectionTool.ConnectionEnd();
            ChatActivity.messageSender.ConnectionEnd();
        } catch (Exception e){

        }
        stopSelf();
    }
}
