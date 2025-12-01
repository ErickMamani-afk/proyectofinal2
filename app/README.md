# 🥑 El Suculento

> **"Encuentra, prueba y reseña."**

**El Suculento** es una aplicación nativa de Android diseñada para los amantes de la comida ("foodies"). Permite descubrir restaurantes cercanos mediante geolocalización, registrar nuevos "huecos" gastronómicos y compartir experiencias mediante reseñas fotográficas.

La aplicación fue construida utilizando **Java** en Android Studio y gestiona toda su información de manera local y eficiente mediante **SQLite**.

---

## 📱 Características Principales

* **🗺️ Exploración Geográfica:** Visualiza restaurantes en un mapa interactivo de Google Maps.
* **📍 Geolocalización Inteligente:** Calcula la distancia real desde tu ubicación hasta el restaurante (Ej: *"A 350 metros"*).
* **📸 Reseñas Visuales:** Toma fotos de tus platillos usando la cámara y guárdalas junto a tu calificación de estrellas.
* **📂 Base de Datos Local:** Sistema robusto con SQLite que incluye gestión de Usuarios, Restaurantes y Reseñas.
* **🔍 Búsqueda Avanzada:** Filtra por nombre o categoría (ej: "Pizza", "Sushi") en tiempo real.
* **➕ Gestión de Contenido:**
    * **Clic Largo en Mapa:** Añade un restaurante manteniendo presionado un punto en el mapa.
    * **Clic Corto:** Accede rápidamente a reseñar un lugar.
* **🔐 Autenticación:** Sistema completo de Registro e Inicio de Sesión de usuarios.

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java (JDK 11/17)
* **Entorno:** Android Studio
* **Base de Datos:** SQLite (SQLiteOpenHelper)
* **APIs:**
    * Google Maps SDK for Android
    * Google Location Services (FusedLocationProvider)
    * Android Camera API & FileProvider
* **Componentes UI:** RecyclerView, CardView, ConstraintLayout, Material Design.

---

## 📂 Arquitectura del Proyecto (12 Activities)

La aplicación cumple y supera el requisito de estructura, organizándose en las siguientes actividades:

### 🟢 Acceso y Bienvenida
1.  **`SplashActivity`:** Pantalla de carga inicial con el logo de "El Suculento".
2.  **`LoginActivity`:** Autenticación de usuarios contra la base de datos.
3.  **`RegisterActivity`:** Formulario para crear nuevos usuarios en SQLite.

### 🔵 Navegación Principal
4.  **`MainActivity`:** Panel de control (Dashboard) con acceso a todas las funciones.
5.  **`SettingsActivity`:** Configuración de la aplicación y exportación de BD.
6.  **`ProfileActivity`:** Visualización de datos del usuario logueado.

### 🟡 Funcionalidad Core
7.  **`MapsActivity`:** Mapa interactivo. (Muestra pines, permite añadir con clic largo).
8.  **`RestaurantListActivity`:** Lista vertical de todos los locales registrados.
9.  **`SearchActivity`:** Buscador inteligente con cálculo de distancia GPS.
10. **`RestaurantDetailActivity`:** Ficha técnica del restaurante + Lista de reseñas con fotos.

### 🔴 Acciones de Creación
11. **`AddRestaurantActivity`:** Formulario para guardar un nuevo local (recibe coordenadas del mapa).
12. **`AddReviewActivity`:** Interfaz de cámara y formulario para guardar opinión y rating.

---

## 💾 Modelo de Base de Datos

El sistema utiliza una base de datos relacional llamada `RestaurantesDB.db` (Versión 2).

### 1. Tabla `usuarios`
| Columna | Tipo | Descripción |
| :--- | :--- | :--- |
| `user_id` | INTEGER (PK) | ID único del usuario |
| `username` | TEXT | Nombre de usuario |
| `password` | TEXT | Contraseña |

### 2. Tabla `restaurantes`
| Columna | Tipo | Descripción |
| :--- | :--- | :--- |
| `id` | INTEGER (PK) | ID del restaurante |
| `nombre` | TEXT | Nombre del local (Ej: "El Wagon") |
| `latitud` | REAL | Coordenada GPS |
| `longitud` | REAL | Coordenada GPS |
| `tipo_comida`| TEXT | Categoría (Ej: "Mariscos") |

### 3. Tabla `resenas`
| Columna | Tipo | Descripción |
| :--- | :--- | :--- |
| `review_id` | INTEGER (PK) | ID de la reseña |
| `rest_id_fk` | INTEGER (FK) | Relación con la tabla restaurantes |
| `comentario` | TEXT | Opinión del cliente |
| `calificacion`| REAL | Valor de 1.0 a 5.0 ⭐ |
| `foto_uri` | TEXT | Ruta local de la imagen (`.jpg`) |

---

## 🚀 Instalación y Configuración

Sigue estos pasos para ejecutar el proyecto en tu entorno local:

### 1. Requisitos Previos
* Android Studio instalado.
* Un dispositivo Android físico (recomendado para probar la Cámara y GPS) o Emulador.
* Una **API Key de Google Maps**.

### 2. Configurar API Key
Abre el archivo `app/src/main/AndroidManifest.xml` y busca la etiqueta meta-data. Reemplaza el valor con tu clave:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="api???" />