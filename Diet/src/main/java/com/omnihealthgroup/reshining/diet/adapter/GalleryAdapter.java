package com.omnihealthgroup.reshining.diet.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.omnihealthgroup.reshining.diet.PhotoGalleryDialog;
import com.omnihealthgroup.reshining.diet.R;

import java.util.ArrayList;

/**
 * Created by lhm05 on 2016/08/19.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{
    private static final String TAG = GalleryAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<Bitmap> data;

    public GalleryAdapter(Context context,ArrayList<Bitmap> data)
    {
        this.context=context;
        this.data=data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Scale the pics







        holder.iv.setImageBitmap(data.get(position));
            holder.iv.setTag(data.get(position));
            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView iv=(ImageView)v;
                    BitmapDrawable drawable= (BitmapDrawable) iv.getDrawable();
                    Bitmap bm=drawable.getBitmap();
                    PhotoGalleryDialog dialog = new PhotoGalleryDialog(context, android.R.style.Theme_Translucent_NoTitleBar
                            , bm);
                    dialog.show();


                }
            });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;

        public ViewHolder(View itemView) {
            super(itemView);
            iv=(ImageView)itemView.findViewById(R.id.gallery_photo);

        }
    }
}
