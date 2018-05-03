package sergiojuliogu.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private  UserInfoTask mUserTask = null;
    private JSONArray projects;
    private JSONObject userObject;
    private int activityBrequestCode = 0;

    //UI
    private NavigationView navigationView;
    private View hView;
    private ImageView userAvatar;
    private Context c;
    private TextView nombreUsuario;
    private GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if(!Session.getLoged()){
            finish();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        c = this.getApplicationContext();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_new);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, NewProjectActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, activityBrequestCode);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        hView =  navigationView.getHeaderView(0);
        userAvatar = (ImageView) hView.findViewById(R.id.imageViewLogo);
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAvatarBox();
            }

        });

        nombreUsuario = (TextView) hView.findViewById(R.id.textViewName);
        nombreUsuario.setText(Session.getUsername());

        c = this.getApplicationContext();

        gridView = (GridView)findViewById(R.id.baseGridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                try{
                    JSONObject project = (JSONObject) projects.get(position);

                    Intent intent = new Intent(c, ProjectActivity.class);
                    Bundle b = new Bundle();
                    b.putString("key", project.get("_id").toString()); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, activityBrequestCode);
                   // finish();

                }catch (JSONException e){
                    Log.e("JsonException", e.toString());
                }


            }
        });

        Bundle b = getIntent().getExtras();
        String value = ""; // or other values
        if(b != null)
            value = b.getString("user");

        try {

            JSONObject obj = new JSONObject(value);
            userObject = obj;

        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + value + "\"");
        }

        mUserTask = new UserInfoTask();
        mUserTask.execute((Void) null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Session.logOut();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            String user = userObject.toString();

            Intent intent = new Intent(c, ProfileActivity.class);
            Bundle b = new Bundle();
            b.putString("user", user);
            intent.putExtras(b);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, activityBrequestCode);
            // finish();
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showAvatarBox() {
        Intent i = new Intent(getApplicationContext(), AvatarsActivity.class);
        Bundle b = new Bundle();
        b.putString("user", userObject.toString()); //Your id
        i.putExtras(b); //Put your id to your next Intent
        startActivityForResult(i, activityBrequestCode);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("requesctCode", requestCode + "");
        Log.i("resultCode", resultCode + "");

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
     * Represents an asynchronous task used to get users information.
     */
    public class UserInfoTask extends AsyncTask<Void, Void, Boolean> {
        private String user;


        UserInfoTask() { }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/" + Session.getUsername();
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

                        JSONObject obj = new JSONObject(result);
                        userObject = obj;
                        return true;

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON good: \"" + result + "\"");
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
            mUserTask = null;

            if (success) {
                pintarDatos(user);
            } else {
                Toast.makeText(HomeActivity.this, "Error al obtener los datos del usuario." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUserTask = null;
        }

        private void pintarDatos(String usuario){

            try{
                JSONObject user = new JSONObject();
                user = userObject;
                if(user.has("avatar") ){
                    if(!user.get("avatar").equals("")){
                        int resourceId = c.getResources().getIdentifier(user.get("avatar").toString(), "drawable",c.getPackageName());
                        userAvatar.setImageResource(resourceId);
                    }
                }

                if(user.get("projects").toString().equals("[]") ){
                    Toast.makeText(HomeActivity.this, "No existen proyectos." ,
                            Toast.LENGTH_SHORT).show();
                }else{

                    projects = (JSONArray)user.get("projects");

                    ProjectsAdapter projectsAdapter = new ProjectsAdapter(c, projects);
                    gridView.setAdapter(projectsAdapter);

                }

            }catch (JSONException e){
                Log.e("JSON", e.toString());
            }

        }
    }
}
