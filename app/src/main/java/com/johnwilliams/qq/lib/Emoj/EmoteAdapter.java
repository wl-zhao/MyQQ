package com.johnwilliams.qq.lib.Emoj;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.johnwilliams.qq.R;
import com.johnwilliams.qq.lib.Base.BaseArrayListAdapter;

import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class EmoteAdapter extends BaseArrayListAdapter {

    public EmoteAdapter(Context context, List<FaceText> datas) {
        super(context, datas);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_face_text, null);
            holder = new ViewHolder();
            holder.mIvImage = convertView
                    .findViewById(R.id.v_face_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FaceText faceText = (FaceText) getItem(position);
        String key = faceText.text.substring(1);
        if (key.startsWith("mc")) {
            try {
                GifDrawable drawable = new GifDrawable(mContext.getResources(), mContext.getResources().getIdentifier(key, "drawable", mContext.getPackageName()));
                holder.mIvImage.setImageDrawable(drawable);
            } catch (Exception e) {

            }
        } else {
            Drawable drawable = mContext.getResources().getDrawable(mContext.getResources().getIdentifier(key, "drawable", mContext.getPackageName()));
            holder.mIvImage.setImageDrawable(drawable);
        }
        return convertView;
    }

    class ViewHolder {
        GifImageView mIvImage;
    }
}
