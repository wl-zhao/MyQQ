package com.johnwilliams.qq.tools.Message;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.johnwilliams.qq.Activities.MainActivity;
import com.johnwilliams.qq.R;
import com.johnwilliams.qq.lib.Base.BaseListAdapter;
import com.johnwilliams.qq.lib.Base.ViewHolder;
import com.johnwilliams.qq.lib.Emoj.FaceTextUtils;
import com.johnwilliams.qq.tools.RecordManager;
import com.johnwilliams.qq.tools.TimeUtil;
import com.johnwilliams.qq.tools.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

public class MessageAdapter extends BaseListAdapter<ChatMessage> {

    DisplayImageOptions options;
    RecordManager recordManager;
    public static AnimationDrawable anim;

    public MessageAdapter(Context context, List<ChatMessage> messageList){
        super(context, messageList);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_launcher_background)
                .showImageOnFail(R.drawable.ic_launcher_background)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        recordManager = new RecordManager(context);
    }


    @Override
    public int getItemViewType(int position){
        ChatMessage msg = list.get(position);
        int offset = MainActivity.my_stunum.equals(msg.getFrom_stunum()) ? 5 : 0;
        return msg.getType().getValue() + offset;
    }

    @Override
    public int getViewTypeCount(){
        return 10;
    }

    private View createViewByType(ChatMessage msg, int position){
        ChatMessage.MSG_TYPE type = msg.getType();
        switch (type){
            case CMD:
                return null;
            case IMG:
                return MainActivity.my_stunum.equals(msg.getFrom_stunum()) ? //send
                        mInflater.inflate(R.layout.item_chat_sent_image, null) :
                        mInflater.inflate(R.layout.item_chat_received_image, null);
            case FILE:
                return MainActivity.my_stunum.equals(msg.getFrom_stunum()) ? //send
                        mInflater.inflate(R.layout.item_chat_sent_file, null) :
                        mInflater.inflate(R.layout.item_chat_received_file, null);
            case TEXT:
                return MainActivity.my_stunum.equals(msg.getFrom_stunum()) ? //send
                        mInflater.inflate(R.layout.item_chat_sent_message, null) :
                        mInflater.inflate(R.layout.item_chat_received_message, null);
            case AUDIO:
                return MainActivity.my_stunum.equals(msg.getFrom_stunum()) ? //send
                        mInflater.inflate(R.layout.item_chat_sent_voice, null) :
                        mInflater.inflate(R.layout.item_chat_received_voice, null);
            default:
                return null;
        }
    }

    @Override
    public View bindView(final int position, View convertView, ViewGroup parent){
        final ChatMessage message = list.get(position);
        if (convertView == null){
            convertView = createViewByType(message, position);
        }

        final ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
        final ImageView iv_fail_resend = ViewHolder.get(convertView, R.id.iv_fail_resend);
//        final TextView tv_send_status = ViewHolder.get(convertView, R.id.tv_send_status);
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
        TextView tv_sender = ViewHolder.get(convertView, R.id.tv_sender);
        TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);

        final ImageView iv_picture = ViewHolder.get(convertView, R.id.iv_picture);
        final ProgressBar progress_load = ViewHolder.get(convertView, R.id.progress_load);
        final TextView tv_location = ViewHolder.get(convertView, R.id.tv_location);
        final ImageView iv_voice = ViewHolder.get(convertView, R.id.iv_voice);
        // time
        tv_time.setText(TimeUtil.formatTime(message.getTime()));

        // file
        ImageView iv_file = ViewHolder.get(convertView, R.id.iv_file);
        TextView tv_file_name = ViewHolder.get(convertView, R.id.tv_file_name);
        TextView tv_file_size = ViewHolder.get(convertView, R.id.tv_file_size);
        ProgressBar pb_loading = ViewHolder.get(convertView, R.id.pb_file_loading);

        //voice
        LinearLayout layout_voice = ViewHolder.get(convertView, R.id.layout_voice);
        TextView tv_voice_length = ViewHolder.get(convertView, R.id.tv_voice_length);

        // message
        try {
            switch (message.getStatus()){
                case SENT:
                    iv_fail_resend.setVisibility(View.INVISIBLE);
                    progress_load.setVisibility(View.INVISIBLE);
                    break;
                case FAILED:
                    iv_fail_resend.setVisibility(View.VISIBLE);
                    progress_load.setVisibility(View.INVISIBLE);
                    break;
                case SENDING:
                    progress_load.setVisibility(View.VISIBLE);
                    iv_fail_resend.setVisibility(View.INVISIBLE);
                    break;
            }
        } catch (Exception e){

        }

        final String text = message.getContent();
        switch (message.getType()){
            case TEXT:
                if (Utils.isGroupChat(message.getTo_stunum())) {
                    tv_sender.setVisibility(View.VISIBLE);
                    tv_sender.setText(message.getFrom_stunum());
                } else {
                    tv_sender.setVisibility(View.GONE);
                }
                try {
                    SpannableString spannableString = FaceTextUtils.toSpannableString(mContext, text);
                    tv_message.setText(spannableString);
                } catch (Exception e){
                }
                break;
            case FILE:
                tv_file_name.setText(Utils.convertFileName(message.getContent()));
                tv_file_size.setText(Utils.convertFileSize(message.getFile_length()));
                if (message.getProgress() == 101){
                    pb_loading.setVisibility(View.INVISIBLE);
                } else {
                    pb_loading.setVisibility(View.VISIBLE);
                    pb_loading.setProgress(message.getProgress());
                }
                iv_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (MainActivity.my_stunum.equals(message.getFrom_stunum())){// send
                                File file = new File(message.getContent());
                                Utils.openFile(mContext, file);
                            } else { // receive
                                File file = new File(Environment.getExternalStorageDirectory()
                                        + mContext.getString(R.string.default_path) +
                                Utils.convertFileName(message.getContent(), false));
                                Utils.openFile(mContext, file);
                            }
                            anim = (AnimationDrawable)iv_voice.getDrawable();
                            anim.start();
                        } catch (Exception e){
                            anim.stop();
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case AUDIO:
                tv_voice_length.setText(Utils.convertAudioLength(message.getAudio_length()));
                layout_voice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            recordManager.stopPlaying();
                            if (MainActivity.my_stunum.equals(message.getFrom_stunum())){// send
                                recordManager.startPlaying(message.getContent());
                                iv_voice.setImageResource(R.drawable.anim_chat_voice_right);
                            } else {
                                recordManager.startPlaying(Environment.getExternalStorageDirectory() +
                                mContext.getString(R.string.default_path) +
                                mContext.getString(R.string.record_path) +
                                Utils.convertFileName(message.getContent(), false));
                                iv_voice.setImageResource(R.drawable.anim_chat_voice_left);
                            }
                            anim = (AnimationDrawable) iv_voice.getDrawable();
                            anim.start();
                            recordManager.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    anim.stop();
                                    recordManager.stopPlaying();
                                    if (MainActivity.my_stunum.equals(message.getFrom_stunum())){// send
                                        iv_voice.setImageResource(R.drawable.voice_left3);
                                    } else {
                                        iv_voice.setImageResource(R.drawable.voice_right3);
                                    }
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case CMD:
                break;
            case IMG:
                break;
        }
        // TODO: handle other type of input
        return convertView;
    }

    public void updateMessage(ChatMessage message) {
        for (int i = getCount() - 1; i >= 0; i--) {
            if (get(i).getTime().equals(message.getTime())){ // same message
                set(i, message);
                return;
            }
        }
        // else add to the message list
        add(message);
    }
}
