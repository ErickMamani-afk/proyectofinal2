package com.example.proyectofinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "RestaurantesDB.db";
    private static final int DB_VERSION = 1;

    // Tabla Restaurantes
    public static final String TABLE_REST = "restaurantes";
    public static final String COL_ID = "id";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_LAT = "latitud";
    public static final String COL_LNG = "longitud";
    public static final String COL_TIPO = "tipo_comida";

    // Tabla Reseñas
    public static final String TABLE_REVIEW = "resenas";
    public static final String COL_R_ID = "review_id";
    public static final String COL_R_REST_ID = "rest_id_fk";
    public static final String COL_COMENTARIO = "comentario";
    public static final String COL_RATING = "calificacion";
    public static final String COL_FOTO_URI = "foto_uri";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRest = "CREATE TABLE " + TABLE_REST + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOMBRE + " TEXT, " +
                COL_LAT + " REAL, " +
                COL_LNG + " REAL, " +
                COL_TIPO + " TEXT)";
        db.execSQL(createRest);

        String createReview = "CREATE TABLE " + TABLE_REVIEW + " (" +
                COL_R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_R_REST_ID + " INTEGER, " +
                COL_COMENTARIO + " TEXT, " +
                COL_RATING + " REAL, " +
                COL_FOTO_URI + " TEXT, " +
                "FOREIGN KEY(" + COL_R_REST_ID + ") REFERENCES " + TABLE_REST + "(" + COL_ID + "))";
        db.execSQL(createReview);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEW);
        onCreate(db);
    }

    // Insertar Restaurante
    public boolean addRestaurant(String nombre, double lat, double lng, String tipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOMBRE, nombre);
        cv.put(COL_LAT, lat);
        cv.put(COL_LNG, lng);
        cv.put(COL_TIPO, tipo);
        long result = db.insert(TABLE_REST, null, cv);
        return result != -1;
    }

    // Insertar Reseña con Foto
    public boolean addReview(int restId, String comentario, float rating, String fotoUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_R_REST_ID, restId);
        cv.put(COL_COMENTARIO, comentario);
        cv.put(COL_RATING, rating);
        cv.put(COL_FOTO_URI, fotoUri);
        long result = db.insert(TABLE_REVIEW, null, cv);
        return result != -1;
    }
    // ... dentro de DatabaseHelper.java ...

    public void checkAndInsertDummyData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + TABLE_REST, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();

            // Si la base de datos está vacía, insertamos restaurantes de IQUIQUE
            if (count == 0) {
                // 1. El Wagon (Península)
                insertarDummy(db, "El Wagon", -20.2185, -70.1532, "Pescados y Mariscos");

                // 2. Cantaba la Rana (Península)
                insertarDummy(db, "Cantaba la Rana", -20.2175, -70.1528, "Internacional");

                // 3. Neptuno (Centro)
                insertarDummy(db, "Restaurante Neptuno", -20.2135, -70.1505, "Chilena");

                // 4. Rayu (Cerca de Cavancha)
                insertarDummy(db, "Rayu Iquique", -20.2340, -70.1425, "Peruana");

                // 5. Sushi Otaku (Simulado en Playa Brava)
                insertarDummy(db, "Sushi Otaku", -20.2450, -70.1380, "Japonesa");
            }
        }
    }

    private void insertarDummy(SQLiteDatabase db, String nombre, double lat, double lng, String tipo) {
        ContentValues cv = new ContentValues();
        cv.put(COL_NOMBRE, nombre);
        cv.put(COL_LAT, lat);
        cv.put(COL_LNG, lng);
        cv.put(COL_TIPO, tipo);
        db.insert(TABLE_REST, null, cv);
    }
    // ... dentro de DatabaseHelper.java ...

    // Método para obtener reseñas de un restaurante específico
    public java.util.List<Review> getReviewsByRestaurant(int restaurantId) {
        java.util.List<Review> lista = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_REVIEW + " WHERE " + COL_R_REST_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(restaurantId)});

        if (cursor.moveToFirst()) {
            do {
                int idxComentario = cursor.getColumnIndex(COL_COMENTARIO);
                int idxRating = cursor.getColumnIndex(COL_RATING);
                int idxFoto = cursor.getColumnIndex(COL_FOTO_URI);

                if (idxComentario != -1) {
                    String comentario = cursor.getString(idxComentario);
                    float rating = cursor.getFloat(idxRating);
                    String foto = cursor.getString(idxFoto);

                    lista.add(new Review(comentario, rating, foto));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public Cursor getAllRestaurants() {
        return this.getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_REST, null);
    }
}