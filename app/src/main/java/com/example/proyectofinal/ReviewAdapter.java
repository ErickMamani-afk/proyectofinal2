package com.example.proyectofinal;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;
    private OnReviewLongClickListener listener; // Interfaz para comunicar el clic

    // Interfaz creada por nosotros
    public interface OnReviewLongClickListener {
        void onReviewLongClick(Review review);
    }

    public ReviewAdapter(List<Review> reviewList, OnReviewLongClickListener listener) {
        this.reviewList = reviewList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.tvComment.setText(review.getComentario());
        holder.ratingBar.setRating(review.getRating());

        String photoPath = review.getFotoUri();
        if (photoPath != null && !photoPath.isEmpty() && new File(photoPath).exists()) {
            holder.ivPhoto.setVisibility(View.VISIBLE);
            holder.ivPhoto.setImageURI(Uri.parse(photoPath));
        } else {
            holder.ivPhoto.setVisibility(View.GONE);
        }

        // DETECTAR CLIC LARGO
        holder.itemView.setOnLongClickListener(v -> {
            listener.onReviewLongClick(review); // Avisamos a la Activity
            return true;
        });
    }

    @Override
    public int getItemCount() { return reviewList.size(); }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvComment;
        RatingBar ratingBar;
        ImageView ivPhoto;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComment = itemView.findViewById(R.id.tvReviewComment);
            ratingBar = itemView.findViewById(R.id.rbReviewRating);
            ivPhoto = itemView.findViewById(R.id.ivReviewPhoto);
        }
    }
}