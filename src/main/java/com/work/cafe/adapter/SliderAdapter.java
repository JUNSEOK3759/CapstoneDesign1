package com.work.cafe.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.work.cafe.R;
import com.work.cafe.data.detail.Photo;
import com.work.cafe.model.HTTPManager;
import com.work.cafe.model.ImageModel;

import java.util.ArrayList;

public class SliderAdapter extends
        SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private ArrayList<Photo> mSliderItems = new ArrayList<>();

    private final String TAG = SliderAdapter.class.getName();

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public void renewItems(ArrayList<Photo> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Photo sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_slider_img, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        Photo sliderItem = mSliderItems.get(position);



        ImageModel.downloadPhoto(context, sliderItem, viewHolder.imageViewBackground);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String handlingPhoto(Photo photo) {

        String paramPhoto = "photoreference=" + photo.getPhotoReference();
        String key = "key=" + HTTPManager.PLACE_API_KEY;
        String maxHeight = "maxheight=" + 300;
        String url = "https://maps.googleapis.com/maps/api/place/photo?";

        return url + paramPhoto + "&" + key + "&" + maxHeight;
    }

    @Override
    public int getCount() {

        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        ImageView imageGifContainer;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
            this.itemView = itemView;
        }
    }

}