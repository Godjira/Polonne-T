package com.polonne.godjira.polonne_t;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Godjira on 14.02.2016.
 */
public class LoadTasks {
        Context actContext;
        SQLiteDatabase db;
        DataBaseHelper dbase;

    public LoadTasks(Context actContext) {
        this.actContext = actContext;
        dbase = RoutesActivity.getBase();
        db = dbase.getWritableDatabase();
    }
    public void startTaskPreLoad(){
        new preLoad().execute();
    }
    public void startTaskUpdate(){
        new updateTask().execute();
    }
    private class preLoad extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            PreLoad preLoad = new PreLoad();
            String strPreLoad = preLoad.ReadFromfile("preload.json", actContext);
            try {
                JSONObject jObj = new JSONObject(strPreLoad);
                JSONObject jsonResponse = jObj.getJSONObject("response");
                JSONArray arjRoutes = jsonResponse.getJSONArray("routes");

                //Load routes
                for (int i = 0; arjRoutes.length() > i; i++) {
                    ContentValues cv = new ContentValues();
                    JSONObject routesJsonObj = (JSONObject) arjRoutes.get(i);
                    cv.put("id", routesJsonObj.getString("id"));
                    cv.put("name", routesJsonObj.getString("name"));
                    cv.put("specification", routesJsonObj.getString("specification"));
                    cv.put("points", routesJsonObj.getString("points"));
                    db.insert("routes", null, cv);
                }
                JSONArray arjPoints = jsonResponse.getJSONArray("points");
                for (int i = 0; arjPoints.length() > i; i++) {
                    ContentValues cv = new ContentValues();
                    JSONObject pointsJsonObj = (JSONObject) arjPoints.get(i);
                    cv.put("id", pointsJsonObj.getString("id"));
                    cv.put("routeId", pointsJsonObj.getString("routeId"));
                    cv.put("name", pointsJsonObj.getString("name"));
                    db.insert("points", null, cv);
                }
                JSONArray arjSchedules = jsonResponse.getJSONArray("schedules");
                for (int i = 0; arjSchedules.length() > i; i++) {
                    ContentValues cv = new ContentValues();

                    JSONObject schedulesJsonObj = (JSONObject) arjSchedules.get(i);
                    cv.put("id", schedulesJsonObj.getString("id"));
                    cv.put("pointId", schedulesJsonObj.getString("pointId"));
                    cv.put("schedule", schedulesJsonObj.getString("schedule"));
                    cv.put("additional", schedulesJsonObj.getString("additional"));
                    db.insert("schedules", null, cv);
                }
                Toast.makeText(actContext, "Дані оновленні.", Toast.LENGTH_SHORT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private class updateTask extends AsyncTask<Void, Void, Boolean> {
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        String strRoutesJSON = "";
        String strSchedulesJSON = "";
        String strPointsJSON = "";
        BaseJSON bb = null;
        final ProgressDialog progressDialog = new ProgressDialog(actContext);
        private Exception m_error = null;

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Загрузка ...");
                progressDialog.setCancelable(false);
                progressDialog
                        .setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            }

        @Override
        protected Boolean doInBackground(Void... params) {
            //Получаем данные с внешнего ресурса.
            try {
                URL url = new URL("http://data-server.esy.es/API.php?routes");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                }

                strRoutesJSON = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            urlConnection = null;
            bufferedReader = null;
            //Получаем данные с внешнего ресурса.
            try {
                URL url = new URL("http://data-server.esy.es/API.php?schedules");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                }

                strSchedulesJSON =  buffer.toString();


            } catch (Exception e) {
                e.printStackTrace();
            }

            urlConnection = null;
            bufferedReader = null;
            //Получаем данные с внешнего ресурса.
            try {
                URL url = new URL("http://data-server.esy.es/API.php?points");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                }

                strPointsJSON =  buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            bb = new BaseJSON(strRoutesJSON, strSchedulesJSON, strPointsJSON);

            JSONObject jsonObjectRoutes = null;
            JSONObject jsonObjectSchedules = null;
            JSONObject jsonObjectPoints = null;
            try {
                jsonObjectRoutes = new JSONObject(bb.getStrRoutesJSON());
                jsonObjectSchedules = new JSONObject(bb.getStrSchedulesJSON());
                jsonObjectPoints = new JSONObject(bb.getStrPointsJSON());

                JSONArray routes = jsonObjectRoutes.getJSONArray("routes");
                JSONArray schedules = jsonObjectSchedules.getJSONArray("schedules");
                JSONArray points = jsonObjectPoints.getJSONArray("points");

                if (routes.length()>0 && schedules.length()>0 && points.length()>0){
                    deleteDataBase();
                    createDataBase();
                }

                for (int i = 0; i < routes.length(); i++) {

                    JSONObject route = routes.getJSONObject(i);

                    String sId = route.getString("id");
                    String sName = route.getString("name");
                    String sSpecification = route.getString("specification");
                    String sPoints = route.getString("points");

                    // создаем объект для данных
                    ContentValues cv = new ContentValues();

                    cv.put("id", sId);
                    cv.put("name", sName);
                    cv.put("specification", sSpecification);
                    cv.put("points", sPoints);
                    db.insert("routes", null, cv);
                }

                for (int i = 0; i < schedules.length(); i++) {

                    JSONObject schedule = schedules.getJSONObject(i);

                    String sId = schedule.getString("id");
                    String sName = schedule.getString("pointId");
                    String sSpecification = schedule.getString("schedule");
                    String sPoints = schedule.getString("additional");

                    // создаем объект для данных
                    ContentValues cv = new ContentValues();

                    cv.put("id", sId);
                    cv.put("pointId", sName);
                    cv.put("schedule", sSpecification);
                    cv.put("additional", sPoints);
                    db.insert("schedules", null, cv);
                }

                for (int i = 0; i < points.length(); i++) {

                    JSONObject point = points.getJSONObject(i);

                    String sId = point.getString("id");
                    String sRouteId = point.getString("routeId");
                    String sName = point.getString("name");

                    // создаем объект для данных
                    ContentValues cv = new ContentValues();

                    cv.put("id", sId);
                    cv.put("routeId", sRouteId);
                    cv.put("name", sName);
                    db.insert("points", null, cv);
                }
                return true;
            }catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                progressDialog.hide();
                RoutesActivity
                        .showToast("Дані оновленні.");
            }else{
                progressDialog.hide();
                RoutesActivity
                        .showToast("Оновлення не відбулося.");
            }
        }
    }
    public void deleteDataBase() {
        actContext.deleteDatabase("routes.db");
    }
    public void createDataBase(){
        dbase = new DataBaseHelper(actContext);
        db = dbase.getReadableDatabase();
    }

    class BaseJSON {
        String strRoutesJSON;
        String strSchedulesJSON;
        String strPointsJSON;

        public BaseJSON(String strRoutesJSON, String strSchedulesJSON, String strPointsJSON) {
            this.strRoutesJSON = strRoutesJSON;
            this.strSchedulesJSON = strSchedulesJSON;
            this.strPointsJSON = strPointsJSON;
        }

        public String getStrRoutesJSON() {
            return strRoutesJSON;
        }

        public String getStrSchedulesJSON() {
            return strSchedulesJSON;
        }

        public String getStrPointsJSON() {
            return strPointsJSON;
        }
    }
}
