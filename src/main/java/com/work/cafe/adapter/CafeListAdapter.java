package com.work.cafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.work.cafe.R;
import com.work.cafe.data.CafePreviewData;
import com.work.cafe.model.ImageModel;

import java.util.ArrayList;

public class CafeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CafePreviewData> cafePreviewDataList = new ArrayList<>();
    private Context context;
    private OnCafeClickListener onCafeClickListener;

    public CafeListAdapter(Context context, OnCafeClickListener onCafeClickListener) {
        this.context = context;
        this.onCafeClickListener = onCafeClickListener;
    }

    class CafePreviewViewHolder extends RecyclerView.ViewHolder {

        ImageView cafePreviewImg;
        TextView cafeName;
        TextView cafeAddress;

        ImageButton cafeBookmark;

        public CafePreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            cafePreviewImg = itemView.findViewById(R.id.cafe_preview_img);
            cafeName = itemView.findViewById(R.id.cafe_name);
            cafeAddress = itemView.findViewById(R.id.cafe_address);
            cafeBookmark = itemView.findViewById(R.id.bookmark_cafe);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_cafe_preview, parent, false);

        CafePreviewViewHolder cafePreviewViewHolder = new CafePreviewViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCafeClickListener.onClick(
                        cafePreviewDataList.get(cafePreviewViewHolder.getAdapterPosition()),
                        cafePreviewViewHolder.getAdapterPosition()
                );
            }
        });

        return cafePreviewViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        CafePreviewData cafePreviewData = cafePreviewDataList.get(position);

        CafePreviewViewHolder cafePreviewViewHolder = (CafePreviewViewHolder) holder;

        cafePreviewViewHolder.cafeName.setText(cafePreviewData.getName());
        cafePreviewViewHolder.cafeAddress.setText(cafePreviewData.getFormattedAddress());

        if (cafePreviewData.getPhoto() != null) {
            ImageModel.downloadPhoto(context, cafePreviewData.getPhoto(), cafePreviewViewHolder.cafePreviewImg);
        }

    }

    @Override
    public int getItemCount() {
        return cafePreviewDataList.size();
    }

    public void setCafePreviewDataList(ArrayList<CafePreviewData> cafePreviewDataList) {
        this.cafePreviewDataList = cafePreviewDataList;
        notifyDataSetChanged();
    }

    public interface OnCafeClickListener {
        public void onClick(CafePreviewData cafePreviewData, int position);
    }

}
