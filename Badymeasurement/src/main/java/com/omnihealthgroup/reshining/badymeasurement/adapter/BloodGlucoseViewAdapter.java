package com.omnihealthgroup.reshining.badymeasurement.adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omnihealthgroup.reshining.badymeasurement.R;
import com.omnihealthgroup.reshining.custom.object.BioData;

import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */
public class BloodGlucoseViewAdapter extends RecyclerView.Adapter<BloodGlucoseViewAdapter.ViewHolder> {
    private static final String TAG = BloodGlucoseViewAdapter.class.getSimpleName();
    private List<BioData> listData;
    private Context context;

    public BloodGlucoseViewAdapter(Context context, List<BioData> listData) {
        this.context = context;
        this.listData = listData;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mcardView;
        public TextView dialog_time_care, dialog_status_care, dialog_BStype_care, dialog_BSnumber_care;
        public ImageView middleLine, dialog_status_care_icon, dialog_note_icon;
        private LinearLayout dialog_note;

        public ViewHolder(View v) {
            super(v);
            mcardView = (CardView) v.findViewById(R.id.bloodglucoseview_item_care);
            dialog_time_care = (TextView) v.findViewById(R.id.dialog_time_care);
            dialog_status_care = (TextView) v.findViewById(R.id.dialog_status_care);
            dialog_BStype_care = (TextView) v.findViewById(R.id.dialog_BStype_care);
            dialog_BSnumber_care = (TextView) v.findViewById(R.id.dialog_BSnumber_care);

            dialog_note = (LinearLayout) v.findViewById(R.id.dialog_note);
            dialog_status_care_icon = (ImageView) v.findViewById(R.id.dialog_status_care_icon);
            dialog_note_icon = (ImageView) v.findViewById(R.id.dialog_note_icon);

        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bloodglucoseview_item_care, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.dialog_time_care.setText(listData.get(position).getDeviceTime());
        if (listData.get(position).getUploaded() == 1) {
            holder.dialog_status_care.setText(context.getString(R.string.user_upload_blood_glucose).substring(2, 5));
            holder.dialog_status_care.setTextColor(context.getResources().getColor(R.color.mainColor2Darker));
            holder.dialog_status_care_icon.setImageResource(R.mipmap.bloodglucose_updata_success);
        } else {
            holder.dialog_status_care.setText(context.getString(R.string.user_notupload_blood_glucose).substring(3, 6));
            holder.dialog_status_care.setTextColor(context.getResources().getColor(R.color.event_type_yellow));
            holder.dialog_status_care_icon.setImageResource(R.mipmap.bloodglucose_updata_success);
        }

        if (listData.get(position).getAc() != null) {
            holder.dialog_BStype_care.setText(context.getString(R.string.before) + context.getString(R.string.blood_glucose_string) + "：");
            holder.dialog_BSnumber_care.setText(listData.get(position).getAc() + " " + context.getString(R.string.blood_glucose_unit));
        } else if (listData.get(position).getPc() != null) {
            holder.dialog_BStype_care.setText(context.getString(R.string.after) + context.getString(R.string.blood_glucose_string) + "：");
            holder.dialog_BSnumber_care.setText(listData.get(position).getPc() + " " + context.getString(R.string.blood_glucose_unit));
        } else if (listData.get(position).getNm() != null) {
            holder.dialog_BStype_care.setText(context.getString(R.string.usually) + context.getString(R.string.blood_glucose_string) + "：");
            holder.dialog_BSnumber_care.setText(listData.get(position).getNm() + " " + context.getString(R.string.blood_glucose_unit));
        }

        if (listData.get(position).getDescription() != null
                && !listData.get(position).getDescription().equals("null")
                && !listData.get(position).getDescription().equals("")) {
            holder.dialog_note.setVisibility(View.VISIBLE);

            //閃爍
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
            alphaAnimation.setDuration(3000);
            alphaAnimation.setRepeatCount(Animation.INFINITE);
            alphaAnimation.setRepeatMode(Animation.REVERSE);
            holder.dialog_note_icon.setAnimation(alphaAnimation);
            alphaAnimation.start();
        } else {
            holder.dialog_note.setVisibility(View.GONE);
        }

        //        Picasso.with(holder.mimageView.getContext()).load(mPictureset.get(position)).into(holder.mimageView);
        holder.mcardView.setCardBackgroundColor(context.getResources().getColor(R.color.mainColor1));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void add(String text, int position) {
        //        mDataset.add(position, text);
        //        mPictureset.add(position, text);
        notifyItemInserted(position);  //有一个新项插入到了 position 位置
        //        notifyItemRangeInserted(position, count) - 在 position 位置插入了 count 个新项目
    }

    public void remove(int position) {
        //        mDataset.remove(position);
        //        mPictureset.remove(position);
        notifyItemRemoved(position);
        //        notifyItemRangeRemoved(position, count)
    }


}
