package sergiojuliogu.myapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sergiojuliogu.myapplication.Adapters.StatusAdapter;
import sergiojuliogu.myapplication.Adapters.StatusClosedAdapter;
import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class ClosedStatusActivity extends AppCompatActivity {

    private SprintInfoTask mSprintTask = null;

    private JSONObject sprintObject;
    private JSONArray statusObject;

    private Context c;
    private ListView gridView;

    private String sprintID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed_status);

        c = this.getApplicationContext();

        gridView = (ListView) findViewById(R.id.closed_status_list);

        sprintID = Session.getSprintSelected();
        mSprintTask = new SprintInfoTask(sprintID);
        mSprintTask.execute((Void) null);
    }

    public void refrescar(){

    }
    /**
     * Represents an asynchronous task used to get sprint info information.
     */
    public class SprintInfoTask extends AsyncTask<Void, Void, Boolean> {
        private String sprint;
        private String sprintObtenido;
        private JSONArray statusClosedObject;

        SprintInfoTask(String idSprint) {
            sprint = idSprint;
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/" + Session.getUsername()+"/projects/"+ Session.getProjectSelected()+"/sprints/"+sprint;
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            try{

                URL url = new URL(urlPedida);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", Session.getToken());
                connection.connect();


                BufferedReader br;
                if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }
                // Response: 400
                StringBuffer sb = new StringBuffer();

                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                String status = connection.getResponseCode() + "";
                result = sb.toString();
                if(status.equals("200")) {
                    try {

                        JSONObject obj = new JSONObject(result);
                        sprintObject = obj;
                        statusObject = (JSONArray) sprintObject.get("status");
                        JSONObject stadoLeido;
                        statusClosedObject = new JSONArray();
                        for (int i = 0; i< statusObject.length(); i++){
                            stadoLeido = statusObject.getJSONObject(i);
                            if(stadoLeido.getString("open").equals("false")){
                                statusClosedObject.put(stadoLeido);
                            }
                        }
                        return true;

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
                        return false;
                    }
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.i("exception", e.toString());
            } catch (Exception e){
                Log.i("exception", e.toString());
            }

            return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mSprintTask = null;
            if (success) {
                pintarDatos();
            } else {
                Toast.makeText(ClosedStatusActivity.this, "Error al obtener los datos del sprint." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mSprintTask = null;
        }

        private void pintarDatos(){

            try{

                if(statusClosedObject.toString().equals("[]") ){
                    Toast.makeText(ClosedStatusActivity.this, "No existen estados." ,
                            Toast.LENGTH_SHORT).show();
                }else{

                    statusObject = (JSONArray) sprintObject.get("status");

                    StatusClosedAdapter statusAdapter = new StatusClosedAdapter(c, statusClosedObject);
                    gridView.setAdapter(statusAdapter);
                }

            }catch (JSONException e){
                Log.e("JSON", e.toString());
            }catch (Exception e){
                Log.e("Error", e.toString());

            }

        }

    }
}
