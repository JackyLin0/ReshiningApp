package com.omnihealthgroup.reshining.communication.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnihealthgroup.reshining.custom.object.GCMData;

import java.util.List;

import com.omnihealthgroup.reshining.communication.R;

/**
 * Created by Administrator on 2016/6/22.
 */
public class GCMViewAdapter extends RecyclerView.Adapter<GCMViewAdapter.ViewHolder> {
    private static final String TAG = "GCMViewAdapter";
    private List<GCMData> listData;
    private Context context;

    public GCMViewAdapter(Context context, List<GCMData> listData) {
        this.context = context;
        this.listData = listData;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mcardView;
        public TextView dialog_time_gcm, dialog_status_care, dialog_title_gcm, dialog_body_gcm, dialog_HP_care;
        public ImageView middleLine;

        public ViewHolder(View v) {
            super(v);
            mcardView = (CardView) v.findViewById(R.id.gcmview_item_care);
            dialog_time_gcm = (TextView) v.findViewById(R.id.dialog_time_gcm);
            dialog_status_care = (TextView) v.findViewById(R.id.dialog_status_care);
            dialog_title_gcm = (TextView) v.findViewById(R.id.dialog_title_gcm);
            dialog_body_gcm = (TextView) v.findViewById(R.id.dialog_body_gcm);
            dialog_HP_care = (TextView) v.findViewById(R.id.dialog_HP_care);

        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gcmview_item_care, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.dialog_time_gcm.setText(listData.get(position).getReceivetime());
        //        if (listData.get(position).getUploaded() == 1) {
        //            holder.dialog_status_care.setText(context.getString(R.string.blood_pressure_string_description) + context.getString(R.string.user_upload_blood_glucose));
        //            holder.dialog_status_care.setTextColor(context.getResources().getColor(R.color.mainColor2Darker));
        //        } else {
        //            holder.dialog_status_care.setText(context.getString(R.string.blood_pressure_string_description) + context.getString(R.string.user_notupload_blood_glucose));
        //            holder.dialog_status_care.setTextColor(context.getResources().getColor(R.color.event_type_yellow));
        //        }
        //        holder.dialog_BHP_care.setText(context.getString(R.string.systolic_blood_pressure) + "：" + listData.get(position).getBhp() + " " + context.getString(R.string.blood_pressure_unit));
        //        holder.dialog_BLP_care.setText(context.getString(R.string.diastolic_blood_pressure) + "：" + listData.get(position).getBlp() + " " + context.getString(R.string.blood_pressure_unit));
        holder.dialog_title_gcm.setText(listData.get(position).getTitle());
        holder.dialog_body_gcm.setText(listData.get(position).getBody());

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
