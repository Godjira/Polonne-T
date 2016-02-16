package com.polonne.godjira.polonne_t;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class TimeActivity extends AppCompatActivity {
    ArrayList<String> listPointId;
    ArrayList<String> listSchedule;
    ArrayList<String> listAdditional;
    ArrayList<String> listTrue;
    ListView listView;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listPointId = new ArrayList<String>();
        listSchedule = new ArrayList<String>();
        listAdditional = new ArrayList<String>();
        listTrue = new ArrayList<>();

        setTitle(getIntent().getStringExtra("title"));
        id = getIntent().getStringExtra("id");
        DataBaseHelper dbase = RoutesActivity.getBase();
        SQLiteDatabase db = dbase.getReadableDatabase();
        listView = new ListView(this);
        setContentView(listView);
        Cursor c = db.query("schedules", null, null, null, null, null, null);
        if (c.moveToFirst()){
            int pointIdColIndex = c.getColumnIndex("pointId");
            int scheduleColIndex = c.getColumnIndex("schedule");
            int additionalColIndex = c.getColumnIndex("additional");

            do {
                if (c.getString(pointIdColIndex).equals(id)){
                    String adt = c.getString(scheduleColIndex);
                    if (c.getString(additionalColIndex).equals("null")){}else
                        adt = adt + " (" + c.getString(additionalColIndex) + ")";
                    listTrue.add(adt);
                }
            }
            while (c.moveToNext());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listTrue);
        listView.setAdapter(adapter);
    }
}
