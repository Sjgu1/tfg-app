package sergiojuliogu.myapplication.Activities;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Adapters.SearchUserAdapter;
import sergiojuliogu.myapplication.Session;


public class SearchUserActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    // Declare Variables
    private GridView list;
    private SearchUserAdapter adapter;
    private SearchView editsearch;
    private Spinner dropDown;

    private View mProgressView;


    private JSONArray users = new JSONArray();
    private JSONArray roles = new JSONArray();
    private JSONArray usersParticipantes = new JSONArray();
    private JSONObject project;

    private GetRolesTask mGetRolesTask = null;
    private GetUsersTask mGetUsersTask = null;
    private AddUserTask mAddUserTask = null;

    private Context c;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        c = this.getApplicationContext();

        dropDown = (Spinner) findViewById(R.id.spinner_roles_search);

        Bundle b = getIntent().getExtras();
        String value = ""; // or other values
        String valueProject = "";
        if(b != null)
            value = b.getString("usersParticipantes");
            valueProject = b.getString("proyecto");

        try {
            usersParticipantes = new JSONArray(value);
            project = new JSONObject(valueProject);

        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + value + "\"");
        }

        mGetUsersTask = new GetUsersTask();
        mGetUsersTask.execute((Void) null);

        mGetRolesTask = new GetRolesTask();
        mGetRolesTask.execute((Void) null);


        list = (GridView) findViewById(R.id.grid_search_users);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                try{
                    JSONObject userSeleccionado = (JSONObject) users.get(position);
                    mAddUserTask = new AddUserTask(project, userSeleccionado.getString("username"), dropDown.getSelectedItem().toString());
                    mAddUserTask.execute((Void) null);
                }catch (JSONException e){
                    Log.e("JSONException", e.toString());
                }


            }
        });


        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) findViewById(R.id.simpleSearchView);
        editsearch.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }

    ///setResult(RESULT_OK);


    /**
     * Represents an asynchronous task used to get roles.
     */
    public class GetRolesTask extends AsyncTask<Void, Void, Boolean> {
        JSONArray rolesObtenidos;

        GetRolesTask() { }


        @Override
        protected Boolean doInBackground(Void... params) {
            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/" + Session.getUsername()+"/roles";
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
                // Log.e("Response", connection.getResponseMessage() + "");
                StringBuffer sb = new StringBuffer();

                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                String status = connection.getResponseCode() + "";
                result = sb.toString();
                if(status.equals("200")) {
                    try {

                        roles = new JSONArray(result);
                        rolesObtenidos = new JSONArray(result);
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
            mGetRolesTask = null;

            if (success) {
                try{
                    List<String> spinnerArray =  new ArrayList<String>();
                    JSONObject roleLeido;
                    for(int i = 0; i< rolesObtenidos.length(); i++){
                        roleLeido = rolesObtenidos.getJSONObject(i);
                        if(roleLeido.has("name")){
                            spinnerArray.add(roleLeido.get("name").toString());
                        }
                    }

                    ArrayAdapter adapter = new ArrayAdapter<String>(c, R.layout.spinner_item,R.id.textview, spinnerArray);
                    dropDown.setAdapter(adapter);

                    // sItems = (Spinner) findViewById(R.id.spinner_roles_search);
                }catch (JSONException e){
                    Log.e("JSONException", e.toString());
                }
            } else {
                Toast.makeText(SearchUserActivity.this, "Error al obtener los usuario." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mGetRolesTask = null;
        }
    }

    /**
     * Represents an asynchronous task used to get users.
     */
    public class GetUsersTask extends AsyncTask<Void, Void, Boolean> {

        GetUsersTask() { }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users";
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
                // Log.e("Response", connection.getResponseMessage() + "");
                StringBuffer sb = new StringBuffer();

                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                String status = connection.getResponseCode() + "";
                result = sb.toString();
                if(status.equals("200")) {
                    try {

                        users = new JSONArray(result);
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
            mGetUsersTask = null;

            if (success) {
                try{
                    // Pass results to ListViewAdapter Class
                    JSONObject usuarioParticipa = new JSONObject();
                    JSONObject randomuser = new JSONObject();
                    for (int i = 0; i < usersParticipantes.length(); i ++){
                        usuarioParticipa = (JSONObject) usersParticipantes.getJSONObject(i);
                        usuarioParticipa = (JSONObject) usuarioParticipa.getJSONObject("user");
                        boolean participa = false;
                            for (int j=0; j< users.length(); j++ ){
                                if(!participa) {
                                    randomuser = (JSONObject) users.getJSONObject(j);
                                    if (randomuser.get("username").toString().equals(usuarioParticipa.get("username").toString())){
                                        participa = true;
                                        users.remove(j);
                                }
                            }
                        }
                    }
                    adapter = new SearchUserAdapter(c, users);
                    // Binds the Adapter to the ListView
                    list.setAdapter(adapter);
                }catch (JSONException e){
                    Log.i("JSONException", e.toString());
                    Toast.makeText(SearchUserActivity.this, "Error al obtener los usuarios." ,
                            Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(SearchUserActivity.this, "Error al obtener los usuarios." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mGetUsersTask = null;
        }
    }


    /**
     * Represents an asynchronous task used to add user to project.
     */
    public class AddUserTask extends AsyncTask<Void, Void, Boolean> {
        private String idProject = null;
        private String error = null;
        private String name = null;
        private String description = null;
        private String repository = null;
        private String start_date = "vacio";
        private String end_date = "vacio";
        private String estimated_end = "vacio";
        private String username = null;
        private String role = null;

        AddUserTask(JSONObject project, String username, String role) {
            try{
                if(project.has("_id")){
                    this.idProject = project.get("_id").toString();
                }
                if(project.has("name")){
                    this.name = project.get("name").toString();
                }
                if(project.has("description")){
                    this.description = project.get("description").toString();
                }
                if(project.has("repository")){
                    this.repository = project.get("repository").toString();
                }
                if(project.has("start_date")){
                    this.start_date = project.get("start_date").toString();
                }
                if(project.has("estimated_end")){
                    this.estimated_end = project.get("estimated_end").toString();
                }
                if(project.has("end_date")){
                    if(!project.getString("end_date").equals(""))
                        this.end_date = project.get("end_date").toString();
                }

                this.username = username;
                this.role = role;
            }catch (JSONException e){
                Log.e("JSONException", e.toString());
            }
        }


        @Override
        protected Boolean doInBackground(Void... params) {


            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+"/projects/"+this.idProject;
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            JSONObject nuevoUsuario = new JSONObject();
            try{
                if(name != null){
                    body.put("name", name);
                }
                if(description != null){
                    body.put("description", description);
                }
                if(repository != null){
                    body.put("repository", repository);
                }
                Log.i("end", "Ahi viene el endate");

                Log.i("end", end_date);
                if(!start_date.equals("vacio")){
                    body.put("start_date", start_date);
                }
                if(!estimated_end.equals("vacio")){
                    body.put("estimated_end", estimated_end);
                }
                if(!end_date.equals("vacio")){
                    body.put("end_date", end_date);
                }

                nuevoUsuario.put("user", username);
                nuevoUsuario.put("role", role);
                body.put("users", nuevoUsuario);

                URL url = new URL(urlPedida);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Authorization", Session.getToken());

                connection.setDoOutput(true);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(body.toString());
                writer.close();

                connection.connect();

                connection.connect();
                BufferedReader br;
                if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }

                StringBuffer sb = new StringBuffer();

                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                result = sb.toString();
                String status = connection.getResponseCode() + "";

                if(status.equals("400")){
                    if(result.equals("El usuario a agregar ya participa en el proyecto")){
                        error = "participa";
                        return false;
                    }else{
                        error = "otros";
                        return false;
                    }
                }
                if(status.equals("404")){
                    error = "otros";
                    return false;
                }
                Thread.sleep(2000);
                return true;
            } catch (InterruptedException e) {
                Log.i("exception", e.toString());
            } catch (Exception e){
                Log.i("exception", e.toString());
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAddUserTask = null;
            if (success) {
                setResult(RESULT_OK);
                finish();
            } else {
                mostrarErrores();
            }
        }

        @Override
        protected void onCancelled() {
            mAddUserTask = null;
        }
        public String parseDate(String time) {



            String inputPattern = "yyyy-dd-MM";

            String outputPattern = "MM/dd/yyyy";

            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

            Date date = null;
            String str = null;

            try {
                date = inputFormat.parse(time);
                str = outputFormat.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return str;
        }
        private  void mostrarErrores(){
            if(error.equals("participa")){
                Toast.makeText(SearchUserActivity.this, "El usuario ya participa." ,
                        Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SearchUserActivity.this, "Error al agregar usuario." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
}