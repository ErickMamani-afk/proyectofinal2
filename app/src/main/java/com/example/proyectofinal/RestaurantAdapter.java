package com.example.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private Context context;
    private List<Restaurant> restaurantList;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Aquí "inflamos" el diseño de la fila que creamos antes
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        // Asignamos los datos a los textos
        holder.tvName.setText(restaurant.getName());
        holder.tvType.setText(restaurant.getType());

        // Evento Click: Al tocar una fila, abrimos los detalles
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RestaurantDetailActivity.class);
            intent.putExtra("REST_ID", restaurant.getId()); // Pasamos el ID a la siguiente pantalla
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            // Buscamos los IDs dentro de item_restaurant.xml
            tvName = itemView.findViewById(R.id.tvItemName);
            tvType = itemView.findViewById(R.id.tvItemType);
        }
    }
}