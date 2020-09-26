package com.diatracker;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.FileWriter;
import android.os.Environment;
import android.util.Log;
import android.content.Context;
import android.database.Cursor;
import com.opencsv.CSVWriter;

public class DiaTrackerDB {
    //region DB Constants
    // creating the db constants
    public static final String DB_NAME = "diatracker.db";
    public static final int DB_VERSION = 1;

    // dietary table constants
    public static final String DIETARY_TABLE = "dietary_intake";

    public static final String DIET_ID = "diet_id";
    public static final int    DIET_ID_COL = 0;

    public static final String DIET_DATE = "diet_date";
    public static final int    DIET_DATE_COL = 1;

    public static final String CARBS = "carbs";
    public static final int    CARBS_COL = 2;

    public static final String CALORIES = "calories";
    public static final int    CALORIES_COL = 3;

    public static final String SUGAR = "sugar";
    public static final int    SUGAR_COL = 4;

    // glucose table constants
    public static final String GLUCOSE_TABLE = "glucose_levels";

    public static final String GLUC_ID = "gluc_id";
    public static final int    GLUC_ID_COL = 0;

    public static final String GLUC_DATE = "gluc_date";
    public static final int    GLUC_DATE_COL = 1;

    public static final String LEVEL = "level";
    public static final int    LEVEL_COL = 2;

    // insulin table constants
    public static final String INSULIN_TABLE = "insulin_injections";

    public static final String INSU_ID = "insu_id";
    public static final int    INSU_ID_COL = 0;

    public static final String INSU_DATE = "insu_date";
    public static final int    INSU_DATE_COL = 1;

    public static final String INSU_IN = "insulin_injected";
    public static final int    INSU_IN_COL = 2;

    //endregion

    //region Drop DB Table Statements

    public static final String DROP_DIETARY_TABLE =
            "DROP TABLE IF EXISTS " + DIETARY_TABLE;

    public static final String DROP_GLUCOSE_TABLE =
            "DROP TABLE IF EXISTS " + GLUCOSE_TABLE;

    public static final String DROP_INSULIN_TABLE =
            "DROP TABLE IF EXISTS " + INSULIN_TABLE;

    //endregion

    //region Create DB Table Statements

    public static final String CREATE_DIETARY_TABLE =
            "CREATE TABLE " + DIETARY_TABLE + " (" +
                    DIET_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DIET_DATE   + " INTEGER NOT NULL, " +
                    CARBS + " INTEGER, " +
                    CALORIES + " INTEGER," +
                    SUGAR + " INTEGER);";

    public static final String CREATE_GLUCOSE_TABLE =
            "CREATE TABLE " + GLUCOSE_TABLE + " (" +
                    GLUC_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    GLUC_DATE   + " INTEGER NOT NULL, " +
                    LEVEL + " INTEGER NOT NULL);";

    public static final String CREATE_INSULIN_TABLE =
            "CREATE TABLE " + INSULIN_TABLE + " (" +
                    INSU_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    INSU_DATE   + " INTEGER NOT NULL, " +
                    INSU_IN + " INTEGER NOT NULL);";

    //endregion

    //region Create DB Statement

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name,
                        CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create the tables
            db.execSQL(CREATE_DIETARY_TABLE);
            db.execSQL(CREATE_GLUCOSE_TABLE);
            db.execSQL(CREATE_INSULIN_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                             int oldVersion, int newVersion) {
            // upgrade will drop and re-create the db
            Log.d("DiaTracker", "Upgrading db from version "
                   + oldVersion + " to " + newVersion);

            db.execSQL(DiaTrackerDB.DROP_DIETARY_TABLE);
            db.execSQL(DiaTrackerDB.DROP_GLUCOSE_TABLE);
            db.execSQL(DiaTrackerDB.DROP_INSULIN_TABLE);

            onCreate(db);
        }
    }

    //endregion

    //region Common DB Elements

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public DiaTrackerDB(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (db != null)
            db.close();
    }

    //endregion

    //region App Statements

    // this method will drop and re-create the tables, clearing any data stored
    public void clearDB() {
        this.openWriteableDB();
        try {
            db.execSQL(DiaTrackerDB.DROP_DIETARY_TABLE);
            db.execSQL(DiaTrackerDB.DROP_GLUCOSE_TABLE);
            db.execSQL(DiaTrackerDB.DROP_INSULIN_TABLE);
            db.execSQL(DiaTrackerDB.CREATE_DIETARY_TABLE);
            db.execSQL(DiaTrackerDB.CREATE_GLUCOSE_TABLE);
            db.execSQL(DiaTrackerDB.CREATE_INSULIN_TABLE);
        }
        catch(Exception sqlEx) {
            Log.e("DiaTrackerDB", sqlEx.getMessage(), sqlEx);
        }
    }

    // this method will make a copy of the database to a csv file in the download folder
    public void exportDB() {
        this.openReadableDB();
        File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "diatracker.csv");
        try
        {
            // Repeating code due to three different table structures
            // Most likely a better way to do this, plan to clean up later
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            // dietary table write to csv
            Cursor curCSV = db.rawQuery("SELECT * FROM " + DIETARY_TABLE,null);
            csvWrite.writeNext(curCSV.getColumnNames());
            System.out.println("test");
            while(curCSV.moveToNext())
            {
                String arrStr[] ={curCSV.getString(DIET_ID_COL),curCSV.getString(DIET_DATE_COL), curCSV.getString(CARBS_COL),
                        curCSV.getString(CALORIES_COL),curCSV.getString(SUGAR_COL)};
                System.out.println("test");
                csvWrite.writeNext(arrStr);
            }
            //csvWrite.close();

            // glucose table write to csv
            curCSV = db.rawQuery("SELECT * FROM " + GLUCOSE_TABLE,null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext())
            {
                String arrStr[] ={curCSV.getString(GLUC_ID_COL),curCSV.getString(GLUC_DATE_COL), curCSV.getString(LEVEL_COL)};
                csvWrite.writeNext(arrStr);
            }
            //csvWrite.close();

            // insulin table write to csv
            curCSV = db.rawQuery("SELECT * FROM " + INSULIN_TABLE,null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext())
            {
                String arrStr[] ={curCSV.getString(INSU_ID_COL),curCSV.getString(INSU_DATE_COL), curCSV.getString(INSU_IN_COL)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.e("DiaTrackerDB", sqlEx.getMessage(), sqlEx);
        }
    }

    // create an entry in the Dietary table
    public boolean createDiet(int cal, int carb, int sug) {
        this.openWriteableDB();
        boolean success = false;
        try {
            ContentValues cv = new ContentValues();
            cv.put(DIET_DATE, System.currentTimeMillis());
            cv.put(CARBS, carb);
            cv.put(CALORIES, cal);
            cv.put(SUGAR, sug);
            long newRowId = db.insert(DIETARY_TABLE, null, cv);
            if (newRowId > -1) {
                success = true;
            }
        }
        catch(Exception sqlEx) {
            Log.e("DiaTrackerDB", sqlEx.getMessage(), sqlEx);
        }
        return success;
    }

    // create an entry in the Glucose Table
    public boolean createGlucose(int level) {
        this.openWriteableDB();
        boolean success = false;
        try {
            ContentValues cv = new ContentValues();
            cv.put(GLUC_DATE, System.currentTimeMillis());
            cv.put(LEVEL, level);
            long newRowId = db.insert(GLUCOSE_TABLE, null, cv);
            if (newRowId > -1) {
                success = true;
            }
        }
        catch(Exception sqlEx) {
            Log.e("DiaTrackerDB", sqlEx.getMessage(), sqlEx);
        }
        return success;
    }

    // create an entry in the Insulin Table
    public boolean createInsulin(int injection) {
        this.openWriteableDB();
        boolean success = false;
        try {
            ContentValues cv = new ContentValues();
            cv.put(INSU_DATE, System.currentTimeMillis());
            cv.put(INSU_IN, injection);
            long newRowId = db.insert(INSULIN_TABLE, null, cv);
            if (newRowId > -1) {
                success = true;
            }
        }
        catch(Exception sqlEx) {
            Log.e("DiaTrackerDB", sqlEx.getMessage(), sqlEx);
        }
        return success;
    }

    //endregion
}
