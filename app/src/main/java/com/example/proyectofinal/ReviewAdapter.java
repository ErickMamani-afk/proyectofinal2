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
import java.io.File; // Importante para verificar si el archivo existe
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el diseño de la fila (item_review.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        // 1. Asignar Texto y Estrellas
        holder.tvComment.setText(review.getComentario());
        holder.ratingBar.setRating(review.getRating());

        // 2. Lógica de la Foto (Solo se declara una vez)
        String photoPath = review.getFotoUri();

        // Verificamos si hay ruta, si no está vacía y si el archivo existe en el celular
        if (photoPath != null && !photoPath.isEmpty()) {
            File imgFile = new File(photoPath);
            if (imgFile.exists()) {
                holder.ivPhoto.setVisibility(View.VISIBLE);
                holder.ivPhoto.setImageURI(Uri.parse(photoPath));
            } else {
                // Si la ruta existe pero el archivo no (se borró), ocultamos la imagen
                holder.ivPhoto.setVisibility(View.GONE);
            }
        } else {
            // Si no hay ruta, ocultamos la imagen
            holder.ivPhoto.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    // Clase interna para vincular con el XML
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