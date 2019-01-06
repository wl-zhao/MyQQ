package com.johnwilliams.qq.tools.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.johnwilliams.qq.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Utils {
    public final static String STU_NUM_REGEX = "^[0-9]{10}$";
    public final static String IPV4_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    public final static String MY_STUNUM_EXTRA = "my_stunum";
    public final static String FRIEND_STUNUM_EXTRA = "friend_stunum";
    public final static String FRIEND_NAME_EXTRA = "friend_name";
    public final static String DEFAULT_PATH = "/johnwilliams/qq/";
    public final static String AUDIO_SUBDIR = "recording/";
    public final static String IMAGE_SUBDIR = "image/";
    public final static String FILE_SUBDIR = "file/";
    public final static int NEW_MESSAGE = 0;
    public final static int CLEAR_CHAT = 1;
    public final static int CLEAR_CONTACT = 2;
    public final static int REMOVE_CHAT = 3;
    public final static int REMOVE_CONTACT = 4;
    public final static int NEW_CHAT = 5;
    public final static int UPDATE_CONTACT = 6;
    public final static int LOAD_DONE = 7;
    public final static int UPDATE_PROGRESS = 8;
    public final static int UPDATE_RECORDING = 9;
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

    public static String convertAudioLength(Integer seconds) {
        String time = "";
        if (seconds >= 60) {
            Integer minutes = seconds / 60;
            seconds = seconds % 60;
            time += seconds.toString() + "\'";
        }
        time += String.format(Locale.CHINA, "%2d", seconds) + "\"";
        return time;
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
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url.getPath()));
        intent.setDataAndType(uri, type);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    public static boolean checkSdCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    // remove my_stunum from stu_nums
    public static Object removeMyStunum(String stu_nums, String my_stunum, boolean return_list) {
        List<String> stu_num_list = new LinkedList<>(Arrays.asList(stu_nums.split(",")));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < stu_num_list.size(); i++) {
            if (stu_num_list.get(i).equals(my_stunum)) {
                stu_num_list.remove(i);
                continue;
            }
            builder.append(stu_num_list.get(i));
            builder.append(",");
        }
        if (return_list) {
            return stu_num_list;
        } else {
            return builder.toString().substring(0, builder.length() - 1);
        }
    }

    public static boolean isGroupChat(String friend_num) {
        return friend_num.contains(",");
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
}
