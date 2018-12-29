package com.johnwilliams.qq.tools;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.johnwilliams.qq.Activities.ChatActivity;
import com.johnwilliams.qq.R;
import com.johnwilliams.qq.tools.Message.ChatMessage;
import com.johnwilliams.qq.tools.Message.MessageAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordManager {
    MediaRecorder mRecorder = null;
    public MediaPlayer mPlayer = null;
    File mOutputFile;
    Context mContext;
    Long mStartTime;
    int mSeconds;
    private Handler mHandler = new Handler();
    private Runnable mTickExecutor = new Runnable() {
        @Override
        public void run() {
            tick();
            mHandler.postDelayed(mTickExecutor, 100);
        }
    };
    public RecordManager(Context context) {
        mContext = context;
    }
    public void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioEncodingBitRate(48000);
        mRecorder.setAudioSamplingRate(16000);
        mOutputFile = getOutputFile();
        mOutputFile.getParentFile().mkdirs();
        mRecorder.setOutputFile(mOutputFile.getAbsolutePath());
        mSeconds = -1;
        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartTime = SystemClock.elapsedRealtime();
            mHandler.postDelayed(mTickExecutor, 100);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int stopRecording(boolean saveFile) {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        mStartTime = 0L;
        mHandler.removeCallbacks(mTickExecutor);
        if (!saveFile && mOutputFile != null) {
            mOutputFile.delete();
        }
        return mSeconds;
    }

    public void deleteFile() {
        if (mOutputFile != null) {
            mOutputFile.delete();
        }
    }

    public void startPlaying(String audiofilename) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });
            mPlayer.setDataSource(audiofilename);
            mPlayer.prepare();
            mPlayer.start();
        } catch (Exception e) {

        }
    }

    public void stopPlaying(){
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.release();
            mPlayer = null;
        }
        if (MessageAdapter.anim != null){
            MessageAdapter.anim.stop();
        }
    }

    private File getOutputFile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + mContext.getString(R.string.default_path) + "recording/"
                + dateFormat.format(new Date())
                + ".m4a");
    }

    public String getFileName() {
        return mOutputFile.getAbsolutePath();
    }

    private void tick() {
        long time = (mStartTime < 0) ? 0 : (SystemClock.elapsedRealtime() - mStartTime);
        mSeconds = (int) (time / 1000);

        if (mRecorder != null) {
            int amp = mRecorder.getMaxAmplitude();
            Message msg = new Message();
            msg.what = Utils.UPDATE_RECORDING;
            msg.obj = amp * 100 / 32767;
            ChatActivity.chatMessageHandler.sendMessage(msg);
            //Log.d("Voice Recorder","amplitude: "+(amplitudes[i] * 100 / 32767));
        }
    }
}
