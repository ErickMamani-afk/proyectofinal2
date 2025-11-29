package com.example.proyectofinal;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearchQuery;
    private Button btnSearchAction;
    private RecyclerView rvSearchResults;
    private RestaurantAdapter adapter;
    private List<Restaurant> searchResults;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search); // Asegúrate de que este nombre coincida con tu XML

        // Inicializar base de datos y lista
        dbHelper = new DatabaseHelper(this);
        searchResults = new ArrayList<>();

        // Referencias a la interfaz (Los IDs deben coincidir con activity_search.xml)
        etSearchQuery = findViewById(R.id.etSearchQuery);
        btnSearchAction = findViewById(R.id.btnSearchAction);
        rvSearchResults = findViewById(R.id.rvSearchResults);

        // Configurar RecyclerView
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantAdapter(this, searchResults);
        rvSearchResults.setAdapter(adapter);

        // Lógica del Botón Buscar
        btnSearchAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = etSearchQuery.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    Toast.makeText(SearchActivity.this, "Escribe algo para buscar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void performSearch(String query) {
        searchResults.clear(); // Limpiar búsquedas anteriores
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Consulta SQL: SELECT * FROM restaurantes WHERE nombre LIKE '%query%'
        // El símbolo % sirve para buscar texto que contenga la palabra
        String selection = DatabaseHelper.COL_NOMBRE + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%"};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_REST, // Tabla
                null,                      // Columnas (null = todas)
                selection,                 // WHERE
                selectionArgs,             // Argumentos
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Extraer datos de forma segura
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COL_ID);
                int nameIndex = cursor.getColumnIndex(DatabaseHelper.COL_NOMBRE);
                int typeIndex = cursor.getColumnIndex(DatabaseHelper.COL_TIPO);
                int latIndex = cursor.getColumnIndex(DatabaseHelper.COL_LAT);
                int lngIndex = cursor.getColumnIndex(DatabaseHelper.COL_LNG);

                if (idIndex != -1 && nameIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    String type = (typeIndex != -1) ? cursor.getString(typeIndex) : "";
                    double lat = (latIndex != -1) ? cursor.getDouble(latIndex) : 0;
                    double lng = (lngIndex != -1) ? cursor.getDouble(lngIndex) : 0;

                    searchResults.add(new Restaurant(id, name, type, lat, lng));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Si no hay resultados
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "No se encontraron restaurantes", Toast.LENGTH_SHORT).show();
        }

        // Actualizar la lista visual
        adapter.notifyDataSetChanged();
    }
}