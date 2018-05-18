package sergiojuliogu.myapplication.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import sergiojuliogu.myapplication.Adapters.PollAdapter;
import sergiojuliogu.myapplication.Adapters.UserTaskAdapter;
import sergiojuliogu.myapplication.Adapters.VotesAdapter;
import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class PokerActivity extends AppCompatActivity {

    private Context c;

    private GridView votosGrid;
    private GridView tarjetasGrid;
    private TextView mediaVotos;
    private TextView tuVoto;
    private TextView newVoto;
    private TextView isSeleccionado;

    private EditText comentario;

    private TextView eliminarButton;
    private Button votarButton;

    private JSONObject pollObject;
    private JSONArray votesObject;

    private ArrayList<String>  array= new ArrayList<String>();
    private InfoPollTask mInfoPollTask;
    private NewVoteTask mNewVoteTask;
    private UpdateVoteTask mUpdateVoteTask;
    private DeletePollTask mDeletePollTask;


    private View mProgressView;
    private View mLoginFormView;
    private View mButton;

    private boolean actualizarVoto = false;
    private String idVoto = "";


    static final String[] numbers = new String[] { "1", "2", "3", "5", "8",
            "13", "20", "40", "100" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poker);

        for (String s : numbers) {
            array.add(s);
        }

        c = getApplicationContext();
        findViewsById();


        showProgress(true);
        mInfoPollTask = new InfoPollTask(c);
        mInfoPollTask.execute((Void) null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mButton.setVisibility(show ? View.GONE : View.VISIBLE);
            mButton.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mButton.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mButton.setVisibility(show ? View.GONE : View.VISIBLE);

        }
    }

    private void findViewsById(){
        mProgressView = findViewById(R.id.poker_progress);
        mLoginFormView = findViewById(R.id.poker_form);
        mButton = findViewById(R.id.botones_poker);

        votosGrid = (GridView) findViewById(R.id.votos_grid);
        tarjetasGrid = (GridView) findViewById(R.id.tarjetas_poll);

        isSeleccionado = (TextView) findViewById(R.id.selecciona_valor);
        newVoto = (TextView) findViewById(R.id.voto_seleccionado);

        VotesAdapter votesAdapter = new VotesAdapter(c);
        tarjetasGrid.setAdapter(votesAdapter);

        tarjetasGrid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });

        tarjetasGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                isSeleccionado.setText("Voto seleccionado:");
                newVoto.setText(array.get(position));
            }
        });

        votosGrid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });


        mediaVotos = (TextView) findViewById(R.id.media_voto);
        mediaVotos.setText("0");
        tuVoto = (TextView) findViewById(R.id.voto_usuario);

        comentario = (EditText) findViewById(R.id.comentario_voto);

        eliminarButton = (TextView) findViewById(R.id.delete_poll);
        eliminarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptDeletePoll(v);
            }
        });
        votarButton = (Button) findViewById(R.id.button_update_poll);
        votarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptNewVote();
            }
        });
    }

    public void attemptNewVote(){
        String isVoto = isSeleccionado.getText().toString();
        String comentarioEnviar = comentario.getText().toString();
        if(!isVoto.equals("Voto seleccionado:")){
            Toast.makeText(PokerActivity.this, "Selecciona un voto." ,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(comentarioEnviar.equals("")){
            Toast.makeText(PokerActivity.this, "Indica tus razones.." ,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(actualizarVoto){
            showProgress(true);

            mUpdateVoteTask = new UpdateVoteTask(c, newVoto.getText().toString(), comentarioEnviar);
            mUpdateVoteTask.execute((Void) null);
        }else{
            showProgress(true);

            mNewVoteTask = new NewVoteTask(c, newVoto.getText().toString(),comentarioEnviar);
            mNewVoteTask.execute((Void) null);
        }
    }

    private void  attemptDeletePoll(View v){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        showProgress(true);
                        mDeletePollTask = new DeletePollTask(c);
                        mDeletePollTask.execute((Void) null);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("¿Estás seguro que quieres eliminar la votación?").setPositiveButton("Sí", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    /**
     * Represents an asynchronous task used to get poll info.
     */
    public class InfoPollTask extends AsyncTask<Void, Void, Boolean> {
        Context c;
        InfoPollTask(Context c) {
            this.c=c;
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+
                    "/projects/"+Session.getProjectSelected()+
                    "/sprints/"+Session.getSprintSelected()+
                    "/status/"+Session.getStatusSelected()+
                    "/tasks/"+Session.getTaskSelected()+
                    "/polls/"+Session.getPollSelected();
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
                //Log.i("estado", result);

                if(status.equals("200")) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        pollObject = obj;
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
            mInfoPollTask = null;
            showProgress(false);

            if (success) {
                pintarDatos();
            } else {
                Toast.makeText(PokerActivity.this, "Error al obtener los datos de la votación." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);

            mInfoPollTask = null;
        }

        private void pintarDatos(){
            try{
                votesObject = pollObject.getJSONArray("votes");
                int media = 0;

                JSONObject userVote;
                JSONObject vote;
                for (int i =0; i<votesObject.length(); i++){
                    vote = votesObject.getJSONObject(i);
                    if(vote.has("user")){
                        if(vote.getString("user").equals(Session.getIdUsername())) {
                            tuVoto.setText(vote.getString("value"));
                            actualizarVoto = true;
                            idVoto = vote.getString("_id");
                        }
                    }
                    media +=  Integer.parseInt(vote.getString("value"));
                }

                if(media != 0){
                    media = media / votesObject.length();
                    mediaVotos.setText(String.valueOf(media));
                }

                PollAdapter votesDoneAdapter = new PollAdapter(c, votesObject);
                votosGrid.setAdapter(votesDoneAdapter);
            }catch (JSONException e){
                Log.e("JSONException:", e.toString());
            }
        }

    }
    /**
     * Represents an asynchronous task used to add new vote.
     */
    public class NewVoteTask extends AsyncTask<Void, Void, Boolean> {

        private  String error;
        private String value;
        private String user;
        private String comment;
        Context c;

        NewVoteTask(Context c, String value, String comment) {
            this.c = c;
            this.value=value;
            this.comment=comment;
            this.user = Session.getUsername();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida =  Session.URL+"/users/"+Session.getUsername()+
                    "/projects/"+Session.getProjectSelected()+
                    "/sprints/"+Session.getSprintSelected()+
                    "/status/"+Session.getStatusSelected()+
                    "/tasks/"+Session.getTaskSelected()+
                    "/polls/"+Session.getPollSelected()+
                    "/votes";
            //String to place our result in
            Log.i("La url", urlPedida);
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            try{

                body.put("value", value);
                body.put("user", user);
                body.put("comment", comment);

                URL url = new URL(urlPedida);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Authorization", Session.getToken());

                connection.setDoOutput(true);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(body.toString());
                writer.close();

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
                result = sb.toString();
                Log.i("result", result);
                String status = connection.getResponseCode() + "";
                if(!status.equals("201")){
                    return false;
                }

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.i("exception", e.toString());
            } catch (Exception e){
                Log.i("exception", e.toString());
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mNewVoteTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(PokerActivity.this, "Se ha agreado un voto." ,
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(c, TaskActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivityForResult(intent, 0);
            } else {
                mostrarErroresRespuesta();
            }
        }

        @Override
        protected void onCancelled() {
            mNewVoteTask = null;
            showProgress(false);
        }

        private void mostrarErroresRespuesta(){
            Toast.makeText(PokerActivity.this, "Ha ocurrido algún problema." ,
                    Toast.LENGTH_SHORT).show();

        }
    }
    /**
     * Represents an asynchronous task used to update a vote.
     */
    public class UpdateVoteTask extends AsyncTask<Void, Void, Boolean> {

        private  String error;
        private String value;
        private String user;
        private String comment;
        Context c;

        UpdateVoteTask(Context c, String value, String comment) {
            this.c = c;
            this.value=value;
            this.comment=comment;
            this.user = Session.getUsername();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida =  Session.URL+"/users/"+Session.getUsername()+
                    "/projects/"+Session.getProjectSelected()+
                    "/sprints/"+Session.getSprintSelected()+
                    "/status/"+Session.getStatusSelected()+
                    "/tasks/"+Session.getTaskSelected()+
                    "/polls/"+Session.getPollSelected()+
                    "/votes/"+idVoto;
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            try{

                body.put("value", value);
                body.put("user", user);
                body.put("comment", comment);

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
                result = sb.toString();
                String status = connection.getResponseCode() + "";
                if(!status.equals("204")){
                    return false;
                }

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.i("exception", e.toString());
            } catch (Exception e){
                Log.i("exception", e.toString());
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mNewVoteTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(PokerActivity.this, "Se ha actualizado un voto." ,
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(c, PokerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivityForResult(intent, 0);
            } else {
                mostrarErroresRespuesta();
            }
        }

        @Override
        protected void onCancelled() {
            mNewVoteTask = null;
            showProgress(false);
        }

        private void mostrarErroresRespuesta(){
            Toast.makeText(PokerActivity.this, "Ha ocurrido algún problema." ,
                    Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Represents an asynchronous task used to delete a poll.
     */
    public class DeletePollTask extends AsyncTask<Void, Void, Boolean> {
        Context c;
        DeletePollTask(Context c) {
            this.c=c;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+
                    "/projects/"+Session.getProjectSelected()+
                    "/sprints/"+Session.getSprintSelected()+
                    "/status/"+Session.getStatusSelected()+
                    "/tasks/"+Session.getTaskSelected()+
                    "/polls/"+Session.getPollSelected();
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url
            try{
                URL url = new URL(urlPedida);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
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

                if(status.equals("201")) {
                    return true;
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
            mInfoPollTask = null;
            showProgress(false);

            if (success) {
                Session.setPollSelected(null);
                setResult(300);
                finish();
            } else {
                Toast.makeText(PokerActivity.this, "Error al obtener los datos de la votación." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            mInfoPollTask = null;
        }

    }
}
