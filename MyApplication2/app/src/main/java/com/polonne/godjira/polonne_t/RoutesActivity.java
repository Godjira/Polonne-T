package com.polonne.godjira.polonne_t;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class RoutesActivity extends AppCompatActivity {
    static private DataBaseHelper dbase;
    static private Context routesContext;
    LoadTasks loadTasks;
    static ArrayList<String> list;
    static ArrayList<Integer> listId;
    static ArrayAdapter<String> adapter;
    ListView listView;
    static SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbase = new DataBaseHelper(this);
        db = dbase.getReadableDatabase();
        routesContext = this;
        listView = new ListView(this);
        setContentView(listView);
        setTitle("Маршрути");
        loadTasks = new LoadTasks(this);

        list = new ArrayList<String>();
        listId = new ArrayList<Integer>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        Cursor c = db.query("routes", null, null, null, null, null, null);
        if (c.moveToFirst()){
            int nameColIndex = c.getColumnIndex("name");
            int speceficationColIndex = c.getColumnIndex("specification");
            do {
                String str = c.getString(nameColIndex);
                if (c.getString(speceficationColIndex).equals("null")){}else
                    str = str + " (" + c.getString(speceficationColIndex)+ ")";
                list.add(str);
            } while (c.moveToNext());
        } else {
            Toast.makeText(RoutesActivity.this, "Завантаження інформації, зачекайте...", Toast.LENGTH_LONG).show();
            loadTasks.startTaskPreLoad();
            updateListViewAdapter();
            }
        c.close();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PreviewTime.class);
                intent.putExtra("title", list.get(position));
                intent.putExtra("id", position);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if (id==R.id.action_update_base) {
            updateBase();
        }
        if(id==R.id.menu_item_map){
            Intent i = new Intent(this ,MapsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
    public static DataBaseHelper getBase(){
        return dbase;
    }
    private void updateBase (){
        loadTasks.startTaskUpdate();
        updateListViewAdapter();
    }
    public static void updateListViewAdapter() {
        Cursor c = db.query("routes", null, null, null, null, null, null);
        list.clear();
        listId.clear();
        if (c.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int nameColIndex = c.getColumnIndex("name");
            int speceficationColIndex = c.getColumnIndex("specification");

            do {
                String str = c.getString(nameColIndex);
                if (c.getString(speceficationColIndex).equals("null")) {
                } else str = str + " (" + c.getString(speceficationColIndex) + ")";
                list.add(str);
            } while (c.moveToNext());
        } else {
            list.add("Oновіть базу");
        }
        c.close();
            adapter.notifyDataSetChanged();
    }
    public static Context getActContext(){
        return routesContext;
    }
    public static void showToast(String show){
        Toast.makeText(getActContext(), show, Toast.LENGTH_SHORT).show();
    }

}
