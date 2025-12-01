package db; // O com.example.proyectofinal si no lo moviste de paquete

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.proyectofinal.Review;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "RestaurantesDB.db";
    private static final int DB_VERSION = 2;

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

    // Tabla Usuarios
    public static final String TABLE_USERS = "usuarios";
    public static final String COL_U_ID = "user_id";
    public static final String COL_U_NAME = "username";
    public static final String COL_U_PASS = "password";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla restaurantes
        String createRest = "CREATE TABLE " + TABLE_REST + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOMBRE + " TEXT, " +
                COL_LAT + " REAL, " +
                COL_LNG + " REAL, " +
                COL_TIPO + " TEXT)";
        db.execSQL(createRest);

        // Crear tabla reseñas
        String createReview = "CREATE TABLE " + TABLE_REVIEW + " (" +
                COL_R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_R_REST_ID + " INTEGER, " +
                COL_COMENTARIO + " TEXT, " +
                COL_RATING + " REAL, " +
                COL_FOTO_URI + " TEXT, " +
                "FOREIGN KEY(" + COL_R_REST_ID + ") REFERENCES " + TABLE_REST + "(" + COL_ID + "))";
        db.execSQL(createReview);

        // Crear tabla usuarios
        String createUser = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_U_NAME + " TEXT, " +
                COL_U_PASS + " TEXT)";
        db.execSQL(createUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEW);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // --- MÉTODOS DE USUARIOS ---

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_U_NAME, username);
        cv.put(COL_U_PASS, password);
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_U_ID};
        String selection = COL_U_NAME + " = ?" + " AND " + COL_U_PASS + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // --- MÉTODOS DE RESTAURANTES ---
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

    public Cursor getAllRestaurants() {
        return this.getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_REST, null);
    }

    // --- MÉTODOS DE RESEÑAS (ACTUALIZADOS) ---

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

    // NUEVO: ACTUALIZAR RESEÑA
    public boolean updateReview(int reviewId, String comentario, float rating, String fotoPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_COMENTARIO, comentario);
        cv.put(COL_RATING, rating);
        if (fotoPath != null && !fotoPath.isEmpty()) {
            cv.put(COL_FOTO_URI, fotoPath);
        }
        int result = db.update(TABLE_REVIEW, cv, COL_R_ID + "=?", new String[]{String.valueOf(reviewId)});
        return result > 0;
    }

    // NUEVO: BORRAR RESEÑA
    public boolean deleteReview(int reviewId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_REVIEW, COL_R_ID + "=?", new String[]{String.valueOf(reviewId)});
        return result > 0;
    }

    // ACTUALIZADO: OBTENER RESEÑAS (INCLUYENDO EL ID)
    public List<Review> getReviewsByRestaurant(int restaurantId) {
        List<Review> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_REVIEW + " WHERE " + COL_R_REST_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(restaurantId)});
        if (cursor.moveToFirst()) {
            do {
                // Recuperamos índices
                int idxId = cursor.getColumnIndex(COL_R_ID);
                int idxComentario = cursor.getColumnIndex(COL_COMENTARIO);
                int idxRating = cursor.getColumnIndex(COL_RATING);
                int idxFoto = cursor.getColumnIndex(COL_FOTO_URI);

                if (idxComentario != -1) {
                    int id = cursor.getInt(idxId); // <-- OBTENEMOS EL ID
                    String comentario = cursor.getString(idxComentario);
                    float rating = cursor.getFloat(idxRating);
                    String foto = cursor.getString(idxFoto);

                    // Usamos el constructor nuevo de Review que acepta ID
                    lista.add(new Review(id, comentario, rating, foto));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // Sembrar datos Iquique
    public void checkAndInsertDummyData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + TABLE_REST, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            if (count == 0) {
                insertarDummy(db, "El Wagon", -20.2185, -70.1532, "Pescados y Mariscos");
                insertarDummy(db, "Cantaba la Rana", -20.2175, -70.1528, "Internacional");
                insertarDummy(db, "Restaurante Neptuno", -20.2135, -70.1505, "Chilena");
                insertarDummy(db, "Rayu Iquique", -20.2340, -70.1425, "Peruana");
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
}