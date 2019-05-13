package packt.mycustommap;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;


public class DBHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "markers";
    private static final String TABLE_MARKERS = "markers_data";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_COLOR = "color";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MARKERS_TABLE = "CREATE TABLE " + TABLE_MARKERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
                + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_COLOR + " TEXT" + ");";
        db.execSQL(CREATE_MARKERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS);
        onCreate(db);
    }


    public long addMarker(Marker marker) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, marker.getName());
        values.put(KEY_LATITUDE, marker.getLatitude());
        values.put(KEY_LONGITUDE, marker.getLongitude());
        values.put(KEY_ADDRESS, marker.getAddress());
        values.put(KEY_COLOR,marker.getColor());

        long id =  db.insert(TABLE_MARKERS, null, values);

        db.close();
        return id;
    }

     public Marker getMarker(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MARKERS, new String[] { KEY_ID,
                        KEY_NAME, KEY_LATITUDE,KEY_LONGITUDE,KEY_ADDRESS,KEY_COLOR }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Marker marker = new Marker(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_NAME)), cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)),
                cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)),cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)),
                cursor.getString(cursor.getColumnIndex(KEY_COLOR)));
        cursor.close();
        return marker;
    }

    public List<Marker> getAllMarkers() {
        List<Marker> markerList = new ArrayList<Marker>();

        String selectQuery = "SELECT  * FROM " + TABLE_MARKERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Marker marker = new Marker();
                marker.setID(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                marker.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                marker.setLatitude(cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)));
                marker.setLongitude(cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)));
                marker.setAddress(cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)));
                marker.setColor(cursor.getString(cursor.getColumnIndex(KEY_COLOR)));

                markerList.add(marker);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return markerList;
    }
    //This function is used to delete a marker data from table
    public void deleteMarker(Marker marker) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MARKERS, KEY_ID + " = ?",
                new String[] { String.valueOf(marker.getID()) });
        db.execSQL("UPDATE "+TABLE_MARKERS+" set id = (id - 1) WHERE id > "+marker.getID());
        db.execSQL("UPDATE SQLITE_SEQUENCE SET seq ="+ (marker.getID() - 1) +" WHERE name = '"+TABLE_MARKERS+"';");
        db.close();
    }
    //This function is used to get total number of markers present in the database
    public int getMarkerCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MARKERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    //This function is used to delete the table
    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MARKERS, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name= '"+TABLE_MARKERS+"';");
        db.close();
    }
}