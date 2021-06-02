package com.work.cafe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.work.cafe.R;
import com.work.cafe.data.CafePreviewData;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    class SearchItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView address;

        public SearchItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cafe_name);
            address = itemView.findViewById(R.id.cafe_address);
        }
    }

    private ArrayList<CafePreviewData> cafePreviewDataList = new ArrayList<>();

    private OnClickCafe onClickCafe;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_search, parent, false);
        SearchItemViewHolder searchItemViewHolder = new SearchItemViewHolder(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CafePreviewData cafePreviewData = cafePreviewDataList.get(searchItemViewHolder.getAdapterPosition());
                onClickCafe.onClickCafe(cafePreviewData, searchItemViewHolder.getAdapterPosition());
            }
        });

        return searchItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CafePreviewData cafePreviewData = cafePreviewDataList.get(position);
        SearchItemViewHolder searchItemViewHolder = (SearchItemViewHolder) holder;

        searchItemViewHolder.address.setText(cafePreviewData.getFormattedAddress());
        searchItemViewHolder.name.setText(cafePreviewData.getName());
    }

    @Override
    public int getItemCount() {
        return cafePreviewDataList.size();
    }

    interface OnClickCafe {
        public void onClickCafe(CafePreviewData cafePreviewData, int position);
    }
}
