package com.work.cafe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.work.cafe.R;
import com.work.cafe.data.detail.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Review> reviews;



    public void setReviews (ArrayList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public void addReview (Review review) {
        reviews.add(0, review);
        notifyItemInserted(0);
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{

        TextView nameView;
        TextView contentView;
        TextView ratingView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.author_name);
            contentView = itemView.findViewById(R.id.review_content);
            ratingView = itemView.findViewById(R.id.rating_view);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Review review = reviews.get(position);

        ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;

        reviewViewHolder.nameView.setText(review.getAuthorName());
        reviewViewHolder.ratingView.setText(review.getRating().toString() + " / 5");
        reviewViewHolder.contentView.setText(review.getText());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
