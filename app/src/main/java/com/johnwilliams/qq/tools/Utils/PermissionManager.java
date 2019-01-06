package com.johnwilliams.qq.tools.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionManager {
    protected static int READ_PERMISSION_REQUEST_CODE = 1;
    protected static int WRITE_PERMISSION_REQUEST_CODE = 2;
    protected static int AUDIO_PERMISSION_REQUEST_CODE = 3;
    public static void CheckReadPermission(Context context){
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_REQUEST_CODE);
//        }
        CheckPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE, READ_PERMISSION_REQUEST_CODE);
    }

    public static void CheckWritePermission(Context context){
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
//        }
        CheckPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_PERMISSION_REQUEST_CODE);
    }

    public static void CheckAudioPermission(Context context){
        CheckPermission(context, Manifest.permission.RECORD_AUDIO, AUDIO_PERMISSION_REQUEST_CODE);
    }
    private static void CheckPermission(Context context, String permission, int code) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, code);
        }
    }
}
