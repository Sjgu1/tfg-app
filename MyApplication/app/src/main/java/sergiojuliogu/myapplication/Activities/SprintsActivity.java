package sergiojuliogu.myapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import sergiojuliogu.myapplication.Adapters.ProjectsAdapter;
import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class SprintsActivity extends AppCompatActivity {

    private Context c;
    private int activityBrequestCode = 0;
    private String projectID;

    private JSONArray sprints;
    private JSONObject projectObject;
    private ProjectInfoTask mProjectInfoTask = null;


    private ListView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprints);

        if(!Session.getLoged()){
            finish();
        }

        Bundle b = getIntent().getExtras();
        String value = ""; // or other values
        if(b != null){
            value = b.getString("projectID");
            projectID = value;
        }

        c = this.getApplicationContext();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_new_sprint);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, NewSprintActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, activityBrequestCode);
            }
        });
        findViewsById();
        mProjectInfoTask = new ProjectInfoTask(projectID);
        mProjectInfoTask.execute((Void) null);
    }

    private void findViewsById() {

        gridView = (ListView)findViewById(R.id.sprints_grid);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                try{
                    JSONObject sprint = (JSONObject) sprints.get(position);

                    Intent intent = new Intent(c, SprintActivity.class);
                    Bundle b = new Bundle();
                    b.putString("key", sprint.get("_id").toString()); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, activityBrequestCode);
                    // finish();

                }catch (JSONException e){
                    Log.e("JsonException", e.toString());
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == activityBrequestCode && resultCode == RESULT_OK){
            activityBrequestCode =0;
            Intent intent = getIntent();
            finish();
            startActivity(intent);

        }if (requestCode == activityBrequestCode && resultCode == 300){
            activityBrequestCode =0;
            Intent intent = getIntent();
            finish();
            startActivity(intent);

        }
    }

    /**
     * Represents an asynchronous task used to get project info information.
     */
    public class ProjectInfoTask extends AsyncTask<Void, Void, Boolean> {
        private String project;
        private String projectObtenido;

        ProjectInfoTask(String idProject) {
            project = idProject;
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/" + Session.getUsername()+"/projects/"+ Session.getProjectSelected();
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
                        projectObject = obj;
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
            mProjectInfoTask = null;

            if (success) {
                pintarDatos();
            } else {
                Toast.makeText(SprintsActivity.this, "Error al obtener los sprints del proyecto." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mProjectInfoTask = null;
        }

        private void pintarDatos(){
            try{
                JSONObject user = new JSONObject();
                user = projectObject;

                if(user.get("sprints").toString().equals("[]") ){
                    Toast.makeText(SprintsActivity.this, "No existen sprints." ,
                            Toast.LENGTH_SHORT).show();
                }else{
                    sprints = (JSONArray)user.get("sprints");
                    ProjectsAdapter projectsAdapter = new ProjectsAdapter(c, sprints);
                    gridView.setAdapter(projectsAdapter);
                }
            }catch (JSONException e){
                Log.e("JSON", e.toString());
            }

        }
    }
}
