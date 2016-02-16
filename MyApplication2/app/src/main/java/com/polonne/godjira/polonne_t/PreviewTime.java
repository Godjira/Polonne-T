package com.polonne.godjira.polonne_t;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class PreviewTime extends AppCompatActivity {
    ListView listView;
    ArrayList<String> listId;
    ArrayList<String> listRoutId;
    ArrayList<String> listTrueId;
    ArrayList<String> listName;
    ArrayList<String> listTrue;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getIntExtra("id", 0);
        id++;
        listRoutId = new ArrayList<String>();
        listTrueId = new ArrayList<String>();
        listName = new ArrayList<String>();
        listTrue = new ArrayList<>();
        listId = new ArrayList<>();


        DataBaseHelper dbase = RoutesActivity.getBase();
        SQLiteDatabase db = dbase.getReadableDatabase();
        listView = new ListView(this);
        setContentView(listView);
        setTitle(getIntent().getStringExtra("title"));


        Cursor c = db.query("points", null, null, null, null, null, null);
        if (c.moveToFirst()){

            int idColIndex = c.getColumnIndex("id");
            int pointIdColIndex = c.getColumnIndex("routeId");
            int scheduleColIndex = c.getColumnIndex("name");

            do {
                listRoutId.add(c.getString(pointIdColIndex));
                listName.add(c.getString(scheduleColIndex));
                listId.add(c.getString(idColIndex));
            }while (c.moveToNext());
        }
        updateTrueList();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listTrue);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TimeActivity.class);
                intent.putExtra("title", listTrue.get(position));
                intent.putExtra("id", listTrueId.get(position));
                startActivity(intent);
            }
        });
    }

    private void updateTrueList(){
        for (int i = 0; listRoutId.size()>i; i++){
            if (Integer.parseInt(listRoutId.get(i))==id) {
                listTrue.add(listName.get(i));
                listTrueId.add(listId.get(i));
            }
        }
    }

}
