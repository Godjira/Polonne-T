package com.polonne.godjira.polonne_t;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Godjira on 27.01.2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATA_BASE_NAME = "routes.db";
    private static final int DATA_VERSION = 1;
    //-------------- TABLE ROUTES----------------
    private static final String TABLE_ROUTES = "routes";
    private static final String ROUTES_ID = "id";
    private static final String ROUTES_NAME = "name";
    private static final String ROUTES_SPECIFICATION = "specification";
    private static final String ROUTES_POINTS = "points";
    //-------------- TABLE SCHEDULES----------------
    private static final String TABLE_SCHEDULES = "schedules";
    private static final String SCHEDULES_ID = "id";
    private static final String SCHEDULES_POIND_ID = "pointId";
    private static final String SCHEDULES_SCHEDULE = "schedule";
    private static final String SCHEDULES_ADDITIONAL = "additional";
    //-------------- TABLE POINTS----------------
    private static final String TABLE_POINTS = "points";
    private static final String POINTS_ID = "id";
    private static final String POINTS_ROUTES_ID = "routeId";
    private static final String POINTS_NAME = "name";



    public DataBaseHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ROUTES_TABLE = "CREATE TABLE " + TABLE_ROUTES + "("
                + ROUTES_ID + " INTEGER," + ROUTES_NAME + " TEXT,"
                + ROUTES_SPECIFICATION + " TEXT," + ROUTES_POINTS + " TEXT" + ")";

        String CREATE_SCHEDULES_TABLE = "CREATE TABLE " + TABLE_SCHEDULES + "("
                + SCHEDULES_ID + " INTEGER," + SCHEDULES_POIND_ID + " TEXT,"
                + SCHEDULES_SCHEDULE + " TEXT," + SCHEDULES_ADDITIONAL + " TEXT" + ")";

        String CREATE_POINTS_TABLE = "CREATE TABLE " + TABLE_POINTS + "("
                + POINTS_ID + " INTEGER," + POINTS_ROUTES_ID + " TEXT,"
                + POINTS_NAME + " TEXT" + ")";



        db.execSQL(CREATE_ROUTES_TABLE);
        db.execSQL(CREATE_SCHEDULES_TABLE);
        db.execSQL(CREATE_POINTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
