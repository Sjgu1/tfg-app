package sergiojuliogu.myapplication.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class ProfileActivity extends AppCompatActivity {

    private JSONObject user;
    private UserUpdateTask mUserUpdateTask = null;
    private UserDeleteTask mUserDeleteTask = null;
    private UserInfoTask mUserTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mEmailView;
    private EditText mNameView;
    private EditText mSurnameView;
    private ImageView mAvatarView;
    private View mUpdateFormView;
    private View mProgressView;
    private View mLoginFormView;
    String usuario;


    private int activityBrequestCode = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

       obtenerBundle(getIntent().getExtras());

        mUsernameView = (EditText) findViewById(R.id.input_username);
        mEmailView = (EditText) findViewById(R.id.input_email);
        mNameView = (EditText) findViewById(R.id.input_name);
        mSurnameView = (EditText) findViewById(R.id.input_surname);
        mAvatarView = (ImageView) findViewById(R.id.input_avatar);


        mLoginFormView = findViewById(R.id.profile_form);
        mProgressView = findViewById(R.id.profile_progress);

        mUpdateFormView = findViewById(R.id.profile_form);

        Button mUpdateButton = (Button) findViewById(R.id.update_user_button);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptUpdate();
            }
        });

        ImageButton mDeleteButton = (ImageButton) findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptDelete(view);
            }
        });


        mUserTask = new UserInfoTask();
        mUserTask.execute((Void) null);

    }
    public void showAvatarBox(String user) {
        Intent i = new Intent(getApplicationContext(), AvatarsActivity.class);
        Bundle b = new Bundle();
        b.putString("user", Session.getUsername()); //Your id
        i.putExtras(b); //Put your id to your next Intent
        startActivityForResult(i, activityBrequestCode);

    }

    private void pintarDatos(){
        try{
            if(user.has("username")){
                mUsernameView.setText(user.get("username").toString());
            }
            if(user.has("email")){
                mEmailView.setText(user.get("email").toString());
            }
            if(user.has("name")){
                if(!user.get("name").toString().equals("null") && !user.get("name").toString().equals("") ) {
                    mNameView.setText(user.get("name").toString());
                }
            }

            if(user.has("surname")){
                if(!user.get("surname").toString().equals("null") && !user.get("surname").toString().equals("")) {
                    mSurnameView.setText(user.get("surname").toString());
                }

            }

            if(user.has("avatar") ){
                if(!user.get("avatar").toString().equals("") && !user.get("avatar").toString().equals("null") ){
                    int resourceId = this.getApplicationContext().getResources().getIdentifier(user.get("avatar").toString(), "drawable",this.getApplicationContext().getPackageName());
                    mAvatarView.setImageResource(resourceId);
                }else{
                    int resourceId = this.getApplicationContext().getResources().getIdentifier("avatar_051", "drawable",this.getApplicationContext().getPackageName());
                    mAvatarView.setImageResource(resourceId);
                }
            }

            usuario = user.toString();
            System.out.println(usuario);
            mAvatarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAvatarBox(usuario);
                }
            });
        }catch(JSONException e){
            Log.e("JSONException", e.toString());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 300){
            Intent intent = new Intent( getApplicationContext(), ProfileActivity.class);
            activityBrequestCode =0;
            Bundle b = new Bundle();
            b.putString("user", user.toString());
            intent.putExtras(b);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, activityBrequestCode);
        }
    }

    private void obtenerBundle(Bundle b){
        String value = ""; // or other values
        if(b != null)
            value = b.getString("user");
        try {

            JSONObject obj = new JSONObject(value);
            user = obj;

        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + value + "\"");
        }
    }


    private void attemptUpdate() {
        Log.i("Intento la conex", "Conexxion");
        if (mUserUpdateTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mEmailView.setError(null);

        // Store values at the time of the register attempt.
        String username = mUsernameView.getText().toString();
        String email = mEmailView.getText().toString();
        String name = "";
        String surname = "";
        Log.i("A ver que ", mNameView.getText().toString());

        if (mNameView.getText().toString() != null){
             name = mNameView.getText().toString();
        }
        if(mSurnameView.getText().toString() != null){
             surname = mSurnameView.getText().toString();
        }

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email) ) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }else if(!isEmailValid(email)){
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.
            showProgress(true);

            mUserUpdateTask = new UserUpdateTask(username , email , name, surname);
            mUserUpdateTask.execute((Void) null);
        }
    }

    private void attemptDelete(View v) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        showProgress(true);
                        mUserDeleteTask = new UserDeleteTask();
                        mUserDeleteTask.execute((Void) null);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("¿Estás seguro que quieres borrar tu perfil?").setPositiveButton("Sí", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
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
        }
    }
    /**
     * Represents an asynchronous task used to update users information.
     */
    public class UserUpdateTask extends AsyncTask<Void, Void, Boolean> {
        private String username;
        private String email;
        private String name;
        private String surname;
        private  String error;


        UserUpdateTask(String nombreusuario, String correo, String nombre, String apellidos) {
            username = nombreusuario;
            email = correo;
            name = nombre;
            surname = apellidos;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername();
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            try{
                Log.i("user", user.toString());
                if(user.has("avatar") ){
                    body.put("avatar", user.get("avatar").toString());
                }else{
                    body.put("avatar", "sym_def_app_icon");
                }

                body.put("username", username);
                body.put("name", name);
                body.put("email", email);
                body.put("surname", surname);

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
                    Session.setUsername(username);
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

                if(status.equals("409")){
                    if(result.equals("Existe un usuario con el mismo nombre de usuario")){
                        error = "username";
                        return false;
                    }else{
                        error = "email";
                        return false;
                    }
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
            mUserUpdateTask = null;
            showProgress(false);
            if (success) {
                Toast.makeText(ProfileActivity.this, "Perfil actualizado." ,
                        Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            } else {
                mostrarErroresRespuesta();
            }
        }

        @Override
        protected void onCancelled() {

            showProgress(false);
            mUserUpdateTask = null;
        }

        private void mostrarErroresRespuesta(){

            if(error.equals("username")){
                mUsernameView.setError("Ya existe un usuario con ese nombre.");
                mUsernameView.requestFocus();

            }else{
                mEmailView.setError("Ya existe el email en la base de datos");
                mEmailView.requestFocus();
            }

        }
    }

    public class UserDeleteTask extends AsyncTask<Void, Void, Boolean> {

        UserDeleteTask() { }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername();
            //String to place our result in
            String result;

            try{

                URL url = new URL(urlPedida);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Authorization", Session.getToken());

                connection.connect();

                connection.connect();
                BufferedReader br;
                if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                    Thread.sleep(2000);
                    return true;
                } else {
                    Thread.sleep(2000);
                    return false;
                }
            } catch (InterruptedException e) {
                Log.i("exception", e.toString());
            } catch (Exception e){
                Log.i("exception", e.toString());
            }

            return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mUserDeleteTask = null;
            showProgress(false);

            if (success) {
                Session.logOut();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(ProfileActivity.this, "No se ha podido eliminar el usuario." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            mUserDeleteTask = null;
        }

    }

    /**
     * Represents an asynchronous task used to get users information.
     */
    public class UserInfoTask extends AsyncTask<Void, Void, Boolean> {

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
                        user = obj;
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
            showProgress(false);
            if (success) {
                pintarDatos();
            } else {
                Toast.makeText(ProfileActivity.this, "Error al obtener los datos del usuario." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(true);

            mUserTask = null;
        }

    }
}

