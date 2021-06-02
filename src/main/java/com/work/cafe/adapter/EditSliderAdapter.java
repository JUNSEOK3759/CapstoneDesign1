package com.work.cafe.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.work.cafe.R;
import com.work.cafe.data.detail.Photo;
import com.work.cafe.model.HTTPManager;
import com.work.cafe.model.ImageModel;

import java.util.ArrayList;

public class EditSliderAdapter extends
        SliderViewAdapter<EditSliderAdapter.SliderAdapterViewHolder> {

    private Context context;
    private ArrayList<Photo> mSliderItems = new ArrayList<>();

    private OnClickRemove onClickRemove;

    private final String TAG = EditSliderAdapter.class.getName();

    public EditSliderAdapter(Context context, OnClickRemove onClickRemove) {
        this.context = context;
        this.onClickRemove = onClickRemove;
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
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_slider_img_edit, null);
        SliderAdapterViewHolder holder = new SliderAdapterViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

        Photo sliderItem = mSliderItems.get(position);

        Log.d(TAG, "onBindViewHolder: handlingPhoto(sliderItem) = " + handlingPhoto(sliderItem));


        ImageModel.downloadPhoto(context, sliderItem, viewHolder.imageViewBackground);

        viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRemove.onClick(sliderItem, position);
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

    class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        ImageView imageGifContainer;
        TextView textViewDescription;

        ImageButton imageButton;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container);

            imageButton = itemView.findViewById(R.id.image_remove);
            this.itemView = itemView;
        }
    }

    public interface OnClickRemove {
        public void onClick(Photo photo, int position);
    }

}