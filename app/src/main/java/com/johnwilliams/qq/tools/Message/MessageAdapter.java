package com.johnwilliams.qq.tools.Message;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.johnwilliams.qq.Activities.MainActivity;
import com.johnwilliams.qq.R;
import com.johnwilliams.qq.lib.Base.BaseListAdapter;
import com.johnwilliams.qq.lib.Base.ViewHolder;
import com.johnwilliams.qq.lib.Emoj.FaceTextUtils;
import com.johnwilliams.qq.tools.TimeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.List;

public class MessageAdapter extends BaseListAdapter<ChatMessage> {

    DisplayImageOptions options;

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
    }


    @Override
    public int getItemViewType(int position){
        ChatMessage msg = list.get(position);
        int offset = MainActivity.my_stunum.equals(msg.getFrom_stunum()) ? 4 : 0;
        return msg.getType().getValue() + offset;
    }

    @Override
    public int getViewTypeCount(){
        return 8;
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
                return null;
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

        ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
        final ImageView iv_fail_resend = ViewHolder.get(convertView, R.id.iv_fail_resend);
//        final TextView tv_send_status = ViewHolder.get(convertView, R.id.tv_send_status);
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
        TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);

        final ImageView iv_picture = ViewHolder.get(convertView, R.id.iv_picture);
        final ProgressBar progress_load = ViewHolder.get(convertView, R.id.progress_load);
        final TextView tv_location = ViewHolder.get(convertView, R.id.tv_location);
        final ImageView iv_voice = ViewHolder.get(convertView, R.id.iv_voice);
        final TextView tv_voice_length = ViewHolder.get(convertView, R.id.tv_voice_length);
        // time
        tv_time.setText(TimeUtil.formatTime(message.getTime()));

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
                try {
                    SpannableString spannableString = FaceTextUtils.toSpannableString(mContext, text);
                    tv_message.setText(spannableString);
                } catch (Exception e){
                }
                break;
            case FILE:
                break;
            case AUDIO:
                break;
            case CMD:
                break;
            case IMG:
                break;
        }
        // TODO: handle other type of input

        return convertView;
    }
}
