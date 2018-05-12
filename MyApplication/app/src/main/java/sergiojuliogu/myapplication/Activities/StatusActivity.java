package sergiojuliogu.myapplication.Activities;


import android.app.ActionBar;
import android.app.FragmentTransaction;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

import sergiojuliogu.myapplication.Adapters.StatusClosedAdapter;
import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class StatusActivity extends AppCompatActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private SprintInfoTask mSprintTask;

    private JSONObject sprintObject;
    private JSONArray statusObject;
    protected JSONArray statusOpenObject;
    private int activityBrequestCode = 0;
    private Context c;



    private int numeroEstados=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        c = this.getApplicationContext();


        mSprintTask = new SprintInfoTask(Session.getSprintSelected());
        mSprintTask.execute((Void) null);



    }

    public void numberTabs(){
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mSectionsPagerAdapter.setN(numeroEstados);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        
        // Set up the action bar.
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });



        android.support.v7.app.ActionBar.TabListener tab = new android.support.v7.app.ActionBar.TabListener() {
            @Override
            public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
// When the given tab is selected, switch to the corresponding page in
                // the ViewPager.
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }
        };
        // For each of the sections in the app, add a tab to the action bar.
        if (numeroEstados!= 0) {
            for (int i = 0; i < numeroEstados; i++) {
                // Create a tab with text corresponding to the page title defined by
                // the adapter. Also specify this Activity object, which implements
                // the TabListener interface, as the callback (listener) for when
                // this tab is selected.

                actionBar.addTab(
                        actionBar.newTab()
                                .setText(mSectionsPagerAdapter.getPageTitle(i))
                                .setTabListener(tab));
            }
        }else{
            finish();
            Toast.makeText(StatusActivity.this, "No existen estados." ,
                    Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 300){
            activityBrequestCode =0;
            setResult(RESULT_OK);
            finish(); //finish Activity.
        }
        if (requestCode == activityBrequestCode && resultCode == RESULT_OK){
            activityBrequestCode =0;
            Intent refresh = new Intent(c, StatusActivity.class);
            finish(); //finish Activity.
            startActivity(refresh);//Start the same Activity
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_probando, menu);
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
            Intent intent = new Intent(c, ClosedStatusActivity.class);
            startActivityForResult(intent, activityBrequestCode);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private int numTab = 0;


        private Button buttonCerrar;
        private Button buttonNuevaTarea;
        private TextView nombreEstado;
        private ListView listaTareas;
        private StatusUpdateTask mStatusUpdateTask;
        private JSONObject statusActual;
        private JSONObject sprintObject;
        private JSONArray statusObject;
        protected JSONArray statusOpenObject;
        private SprintInfoTask mSprintTask;



        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            numTab = getArguments().getInt(ARG_SECTION_NUMBER)-1;
            View rootView = inflater.inflate(R.layout.fragment_status, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            findViews(rootView);

            mSprintTask = new SprintInfoTask(Session.getSprintSelected());
            mSprintTask.execute((Void) null);
            return rootView;
        }

        private void findViews(View rootView){
            nombreEstado = (TextView) rootView.findViewById(R.id.nombre_estado_columna);
            buttonCerrar = (Button) rootView.findViewById(R.id.button_close_status);

        }

        private void pintarDatos(){
            try {
                Log.i("Los abiertos", statusOpenObject.toString());
                nombreEstado.setText("Probando");
                JSONObject statusLeido = statusOpenObject.getJSONObject(numTab);
                nombreEstado.setText(statusLeido.getString("name"));
                buttonCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }catch (Exception e){
                Log.e("JSONException", e.toString());
            }

        }

        /**
         * Represents an asynchronous task used to open a status;
         */
        public class StatusUpdateTask extends AsyncTask<Void, Void, Boolean> {
            private String idStatus = null;
            private String name = "";

            StatusUpdateTask(String  idStatus, String name) {
                this.idStatus = idStatus;
                this.name = name;
            }


            @Override
            protected Boolean doInBackground(Void... params) {


                //Some url endpoint that you may have
                String urlPedida = Session.URL+"/users/"+Session.getUsername()+"/projects/"+Session.getProjectSelected() +"/sprints/"+ Session.getSprintSelected()+"/status/" + idStatus;
                //String to place our result in
                String result;
                //Instantiate new instance of our class
                //Perform the doInBackground method, passing in our url

                JSONObject body = new JSONObject();
                JSONObject nuevoUsuario = new JSONObject();
                try{
                    body.put("name", name);
                    body.put("open", true);

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

                    if(!status.equals("204")){
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
                mStatusUpdateTask = null;
                if (success) {

                } else {

                }
            }

            @Override
            protected void onCancelled() {
                mStatusUpdateTask = null;
            }
        }

        /**
         * Represents an asynchronous task used to get sprint info information.
         */
        public class SprintInfoTask extends AsyncTask<Void, Void, Boolean> {
            private String sprint;
            private String sprintObtenido;

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
                            statusOpenObject = new JSONArray();
                            for (int i = 0; i< statusObject.length(); i++){
                                stadoLeido = statusObject.getJSONObject(i);
                                if(stadoLeido.getString("open").equals("true")){
                                    statusOpenObject.put(stadoLeido);
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

                }
            }

            @Override
            protected void onCancelled() {
                mSprintTask = null;
            }

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private int NUM_VIEWS = 2;


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setN(int N) {
            this.NUM_VIEWS = N;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {

            return NUM_VIEWS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position < 0){
                return null;
            }
            try{
                JSONObject status = statusOpenObject.getJSONObject(position);
                String nombreEstado = status.getString("name");
                return nombreEstado;
            }catch (JSONException e){
                Log.e("JSONException", e.toString());
            }
            return "error";
        }
    }

    /**
     * Represents an asynchronous task used to get sprint info information.
     */
    public class SprintInfoTask extends AsyncTask<Void, Void, Boolean> {
        private String sprint;
        private String sprintObtenido;

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
                        statusOpenObject = new JSONArray();
                        for (int i = 0; i< statusObject.length(); i++){
                            stadoLeido = statusObject.getJSONObject(i);
                            if(stadoLeido.getString("open").equals("true")){
                                statusOpenObject.put(stadoLeido);
                            }
                        }
                        numeroEstados = statusOpenObject.length();
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
                numberTabs();
            } else {
                Toast.makeText(StatusActivity.this, "Error al obtener los estados del sprint." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mSprintTask = null;
        }

    }
}
