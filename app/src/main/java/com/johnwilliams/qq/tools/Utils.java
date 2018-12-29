package com.johnwilliams.qq.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.johnwilliams.qq.BuildConfig;

import java.io.File;
import java.io.IOException;

public class Utils {
    public final static String STU_NUM_REGEX = "^[0-9]{10}$";
    public final static String IPV4_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    public final static String MY_STUNUM_EXTRA = "my_stunum";
    public final static String FRIEND_STUNUM_EXTRA = "friend_stunum";
    public final static String FRIEND_NAME_EXTRA = "friend_name";
    public final static int NEW_MESSAGE = 0;
    public final static int CLEAR_CHAT = 1;
    public final static int CLEAR_CONTACT = 2;
    public final static int REMOVE_CHAT = 3;
    public final static int REMOVE_CONTACT = 4;
    public final static int NEW_CHAT = 5;
    public final static int UPDATE_CONTACT = 6;
    public final static int LOAD_DONE = 7;
    public final static int UPDATE_PROGRESS = 8;
    public final static int DO_NOTHING = -1;

    // convert file length to file size
    public static String convertFileSize(Long file_length) {
        if (file_length < 1000) {//B
            return String.valueOf(file_length) + "B";
        } else if (file_length < 1000000L) {//KB
            Double file_length_double = file_length / 1000.0;
            return String.format("%.2f", file_length_double) + "KB";
        } else if (file_length < 1000000000L) {//MB
            Double file_length_double = file_length / 1000000.0;
            return String.format("%.2f", file_length_double) + "MB";
        } else {//GB
            Double file_length_double = file_length / 1000000000.0;
            return String.format("%.2f", file_length_double) + "GB";
        }
    }

    // convert path to file name and convert long file name to short (...)
    public static String convertFileName(String file_name, boolean truncate){
        file_name = file_name.substring(file_name.lastIndexOf("/") + 1);
        if (!truncate) {
            return file_name;
        }
        int max_length = 15;
        if (file_name.length() <= max_length) {
            return file_name;
        }
        return "..." + file_name.substring(file_name.length() - max_length + 3);
    }


    public static String convertFileName(String file_name){
        return convertFileName(file_name, true);
    }

    public static void openFile(Context context, File url) throws IOException {
        // Create URI
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", url);
        else {
            uri = Uri.fromFile(url);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
//        // Check what kind of file you are trying to open, by comparing the url with extensions.
//        // When the if condition is matched, plugin sets the correct intent (mime) type,
//        // so Android knew what application to use to open the file
//        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
//            // Word document
//            intent.setDataAndType(uri, "application/msword");
//        } else if(url.toString().contains(".pdf")) {
//            // PDF file
//            intent.setDataAndType(uri, "application/pdf");
//        } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
//            // Powerpoint file
//            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
//        } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
//            // Excel file
//            intent.setDataAndType(uri, "application/vnd.ms-excel");
//        } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
//            // WAV audio file
//            intent.setDataAndType(uri, "application/zip");
//        } else if(url.toString().contains(".rtf")) {
//            // RTF file
//            intent.setDataAndType(uri, "application/rtf");
//        } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
//            // WAV audio file
//            intent.setDataAndType(uri, "audio/x-wav");
//        } else if(url.toString().contains(".gif")) {
//            // GIF file
//            intent.setDataAndType(uri, "image/gif");
//        } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
//            // JPG file
//            intent.setDataAndType(uri, "image/jpeg");
//        } else if(url.toString().contains(".txt")) {
//            // Text file
//            intent.setDataAndType(uri, "text/plain");
//        } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
//            // Video files
//            intent.setDataAndType(uri, "video/*");
//        } else {
//            //if you want you can also define the intent type for any other file
//
//            //additionally use else clause below, to manage other unknown extensions
//            //in this case, Android will show all applications installed on the device
//            //so you can choose which application to use
//            intent.setDataAndType(uri, "*/*");
//        }

        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url.getPath()));
        intent.setDataAndType(uri, type);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}
