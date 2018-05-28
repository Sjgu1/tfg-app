package sergiojuliogu.myapplication.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sergiojuliogu.myapplication.Adapters.UserTaskAdapter;
import sergiojuliogu.myapplication.Adapters.UsersAdapter;
import sergiojuliogu.myapplication.Adapters.UsersUpdateAdapter;
import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class TaskActivity extends AppCompatActivity {

    private InfoTask mInfoTask;
    private UpdateTask mUpdateTask;
    private UpdateTaskUser mUpdateTaskUser;
    private DeleteTask mDeleteTask;
    private NewPollTask mNewPollTask;


    private JSONArray usersObject;

    private JSONObject taskObject;

    private ImageButton startDate;
    private ImageButton endDate;

    //UI References
    private EditText startDateInput;
    private EditText endDateInput;

    private EditText nomInput;
    private EditText descrInput;

    private Context c;

    private ImageButton mDeleteButton;
    private GridView mListView;
    private Button updateTaskButton;
    private TextView asignarButton;
    private View mProgressView;
    private View mLoginFormView;
    private View mBoton;
    private View mPapelera;
    private String colorElegido = "FFFFFF";
    private CardView mCardView;

    private Switch mFinalizar;
    private TextView mEndDateText;


    private String statusID;
    private int activityBrequestCode =0;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_log:
                    Intent refresh = new Intent(c, ChangeActivity.class);
                    startActivity(refresh);
                    return true;
                case R.id.navigation_task:
                    return true;
                case R.id.navigation_poker:
                    try{
                        if(!taskObject.getString("poll").toString().equals("null")){
                            Session.setPollSelected(taskObject.getString("poll"));
                            Intent intent = new Intent(c, PokerActivity.class);
                            startActivityForResult(intent, 0);
                            return true;

                        }else{
                            if(Session.getRolSelected().equals("Admin") || Session.getRolSelected().equals("Jefe")){
                                attemptNewPoll();
                            }else{
                                Toast.makeText(TaskActivity.this, "No existe una votación y no tienes permisos para crearla." ,
                                        Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }

                    }catch (JSONException e){
                        Log.e("JSONException", e.toString());
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        c = getApplicationContext();
        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        if(!Session.getLoged()){
            finish();
        }

        Bundle b = getIntent().getExtras();
        String value = ""; // or other values
        if(b != null){
            value = b.getString("key");
            statusID = value;
        }

        findViewsById();
        setDateTimeField();

        showProgress(true);
        mInfoTask = new InfoTask(c);
        mInfoTask.execute((Void) null);
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
            mBoton.setVisibility(show ? View.GONE : View.VISIBLE);
            mBoton.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBoton.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mPapelera.setVisibility(show ? View.GONE : View.VISIBLE);
            mPapelera.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPapelera.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mBoton.setVisibility(show ? View.GONE : View.VISIBLE);
            mPapelera.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void findViewsById() {
        mProgressView = findViewById(R.id.task_progress);
        mLoginFormView = findViewById(R.id.task_form);
        mBoton = findViewById(R.id.botones_tareas);
        mPapelera = findViewById(R.id.papelera_task);

        startDateInput = (EditText) findViewById(R.id.etxt_fromdate_tarea_info);
        startDateInput.setInputType(InputType.TYPE_NULL);
        startDateInput.requestFocus();

        endDateInput = (EditText) findViewById(R.id.etxt_todate_tarea_info);
        endDateInput.setInputType(InputType.TYPE_NULL);

        startDate = (ImageButton) findViewById(R.id.imageButton_task_new_info);
        endDate = (ImageButton) findViewById(R.id.imageButton2_task_new_info);

        nomInput = (EditText) findViewById(R.id.input_nombre_tarea_info);
        descrInput = (EditText) findViewById(R.id.input_description_task_info);

        mEndDateText= (TextView) findViewById(R.id.text_end_update_info);
        mFinalizar = (Switch) findViewById(R.id.switch_update_task_info);

        updateTaskButton = (Button) findViewById(R.id.update_task_button);
        updateTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptUpdateTask();
            }
        });

        mCardView = (CardView) findViewById(R.id.color_selected_info);
        mListView = (GridView) findViewById(R.id.list_view_update_task);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });

        final ColorPicker cp = new ColorPicker(TaskActivity.this, 125, 125, 125);

       // colorTaskButton = (Button) findViewById(R.id.newColor_info);
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Show color picker dialog */
                cp.show();

                /* Set a new Listener called when user click "select" */
                cp.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(@ColorInt int color) {
                        // Do whatever you want

                        colorElegido = Integer.toHexString(color);

                        int colorSeleccionado = Color.parseColor("#"+colorElegido);
                        mCardView.setCardBackgroundColor(colorSeleccionado);

                        // If the auto-dismiss option is not enable (disabled as default) you have to manually dimiss the dialog
                        // cp.dismiss();
                    }
                });
            }
        });

        asignarButton = (TextView) findViewById(R.id.asignar_tarea);
        asignarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String operation;
                if(asignarButton.getText().equals("Asignar")){
                    operation = "asignarUsuario";
                }else{
                    operation = "eliminarUsuario";
                }
                showProgress(true);

                mUpdateTaskUser = new UpdateTaskUser(operation);
                mUpdateTaskUser.execute((Void) null);
            }
        });

        mDeleteButton = (ImageButton) findViewById(R.id.delete_task_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptDeleteTask(v);
            }
        });
    }
    private void attemptDeleteTask(View v){
        if(Session.getRolSelected().equals("Admin") || Session.getRolSelected().equals("Jefe")){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            showProgress(true);

                            mDeleteTask = new DeleteTask();
                            mDeleteTask.execute((Void) null);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage("¿Estás seguro que quieres eliminar la tarea?").setPositiveButton("Sí", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }else{
            Toast.makeText(TaskActivity.this, "No tienes permisos para realizar esta accion." ,
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void setDateTimeField() {
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();

            }
        });
        startDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });
        endDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();

            }
        });

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                startDateInput.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                endDateInput.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
    private void attemptUpdateTask(){
        if (mUpdateTask != null) {
            return;
        }

        // Reset errors.
        nomInput.setError(null);
        descrInput.setError(null);

        startDateInput.setError(null);
        endDateInput.setError(null);


        // Store values at the time of the register attempt.
        String nameSprint = nomInput.getText().toString();
        String descriptionSprint = descrInput.getText().toString();
        String startSprint = startDateInput.getText().toString();
        String endSprint = endDateInput.getText().toString();
        boolean finalizado = mFinalizar.isChecked();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(nameSprint)) {
            nomInput.setError(getString(R.string.error_field_required));
            focusView = nomInput;
            cancel = true;
        }

        try{
            // Check for a valid email address.
            if (!startSprint.equals("Inicio")) {
                if (!endSprint.equals("Fin")) {
                    DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    Date dateStart = format.parse(startSprint);
                    Date dateEnd = format.parse(endSprint);

                    if(dateEnd.before(dateStart)){
                        endDateInput.setError(getString(R.string.error_invalid_date));
                        focusView = endDateInput;
                        cancel = true;
                    }
                }
            }
        }catch (Exception e){
            Log.e("Error de fechas", e.toString());
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            showProgress(true);
            mUpdateTask = new UpdateTask(nameSprint , descriptionSprint , startSprint, endSprint,colorElegido, finalizado);
            mUpdateTask.execute((Void) null);
        }

    }
    private void attemptNewPoll() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        showProgress(true);

                        mNewPollTask = new NewPollTask();
                        mNewPollTask.execute((Void) null);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this);
        builder.setMessage("¿No existe una votación sobre esta tarea, deseas crearla?").setPositiveButton("Sí", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 300){
            Intent intent = new Intent(c, TaskActivity.class);

            activityBrequestCode =0;
            Bundle b = new Bundle();
            b.putString("key", statusID);
            intent.putExtras(b); //Put your id to your next Intent
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish(); //finish Activity.
            startActivity(intent);//Start the same Activity
        }
    }

    /**
     * Represents an asynchronous task used to update a task.
     */
    public class UpdateTask extends AsyncTask<Void, Void, Boolean> {
        private String idSprint = null;
        private String error = "";
        private String name = null;
        private String description = null;
        private String start_date = "vacio";
        private String end_date = "vacio";
        private String estimated_end = "vacio";
        private String color = "FFFFFF";
        private String username = null;

        UpdateTask( String name, String description, String startDate, String estimatedEnd,String color ,boolean finalizado) {

            this.idSprint = Session.getProjectSelected();
            this.name = name;
            this.description = description;
            this.start_date = startDate;
            this.estimated_end = estimatedEnd;
            this.color = color;
            if(finalizado){
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                Date date = new Date();
                this.end_date = dateFormat.format(date);
            }else{
                this.end_date = "vacio";
            }
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+
                    "/projects/"+Session.getProjectSelected()+
                    "/sprints/"+Session.getSprintSelected()+
                    "/status/"+Session.getStatusSelected()+
                    "/tasks/"+Session.getTaskSelected();
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            try{
                if(name != null){
                    body.put("name", name);
                }
                if(description != null){
                    body.put("description", description);
                }
                if(!start_date.equals("vacio")){
                    body.put("start_date", start_date);
                }
                if(!estimated_end.equals("vacio")){
                    body.put("estimated_end", estimated_end);
                }

                Log.i("LA fecha", end_date);
                if(!end_date.equals("vacio")){
                    body.put("end_date", end_date);
                }
                body.put("color", color);

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

                StringBuffer sb = new StringBuffer();

                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                result = sb.toString();
                String status = connection.getResponseCode() + "";

                if(status.equals("400")){
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
            mUpdateTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(c, TaskActivity.class);
                Bundle b = new Bundle();
                b.putString("key", statusID); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 0);
                Toast.makeText(TaskActivity.this, "Tarea actualizada." ,
                        Toast.LENGTH_SHORT).show();
            } else {
                mostrarErroresRespuesta();
            }
        }

        @Override
        protected void onCancelled() {

            showProgress(false);
            mUpdateTask = null;
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

        private void mostrarErroresRespuesta(){
            Toast.makeText(TaskActivity.this, "Ha ocurrido agún problema." , Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Represents an asynchronous task used to update a task adding a user.
     */
    public class UpdateTaskUser extends AsyncTask<Void, Void, Boolean> {

        private String operation = null;

        UpdateTaskUser(String operation) {
            this.operation=operation;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+
                    "/projects/"+Session.getProjectSelected()+
                    "/sprints/"+Session.getSprintSelected()+
                    "/status/"+Session.getStatusSelected()+
                    "/tasks/"+Session.getTaskSelected();
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            try{

                body.put("operation", operation);
                body.put("user", Session.getUsername());

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

                StringBuffer sb = new StringBuffer();

                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                result = sb.toString();
                String status = connection.getResponseCode() + "";

                if(status.equals("400")){
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
            mUpdateTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(c, TaskActivity.class);
                Bundle b = new Bundle();
                b.putString("key", statusID); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 0);
                Toast.makeText(TaskActivity.this, "Tarea actualizada." ,
                        Toast.LENGTH_SHORT).show();
            } else {
                mostrarErroresRespuesta();
            }
        }

        @Override
        protected void onCancelled() {

            showProgress(false);
            mUpdateTask = null;
        }
        private void mostrarErroresRespuesta(){
            Toast.makeText(TaskActivity.this, "Ha ocurrido agún problema." , Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Represents an asynchronous task used to get task info.
     */
    public class InfoTask extends AsyncTask<Void, Void, Boolean> {
        Context c;
        InfoTask(Context c) {
            this.c=c;
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+
                    "/projects/"+Session.getProjectSelected()+
                    "/sprints/"+Session.getSprintSelected()+
                    "/status/"+Session.getStatusSelected()+
                    "/tasks/"+Session.getTaskSelected();
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
                        taskObject = obj;
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
            mInfoTask = null;
            showProgress(false);

            if (success) {
                pintarDatos();
            } else {
                Toast.makeText(TaskActivity.this, "Error al obtener los datos de la tarea." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {

            showProgress(false);
            mInfoTask = null;
        }

        private void pintarDatos(){
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            try{
                if(taskObject.has("name") ){
                    nomInput.setText(taskObject.get("name").toString());
                }else{
                    nomInput.setText("");
                }

                if(taskObject.has("description") ){
                    descrInput.setText(taskObject.get("description").toString());
                }else{
                    descrInput.setText("");
                }

                if(taskObject.has("start_date") ){
                    //Date date = dateFormat.parse(projectObject.get("start_date").toString().substring(0,10));
                    String dateStr = parseDate(taskObject.get("start_date").toString().substring(0,10));
                    startDateInput.setText(dateStr);
                }else{
                    startDateInput.setText("Inicio");
                }

                if(taskObject.has("estimated_end") ){
                    String dateStr = parseDate(taskObject.get("estimated_end").toString().substring(0,10));

                    endDateInput.setText(dateStr);
                }else{
                    endDateInput.setText("Fin");
                }

                if(taskObject.has("end_date") ){
                    if(!taskObject.getString("end_date").equals("null")){
                        String dateStr = parseDate(taskObject.get("end_date").toString().substring(0,10));
                        mEndDateText.setText(dateStr);
                    }else{
                        mEndDateText.setText("No finalizada");

                    }

                }else{
                    mEndDateText.setText("No finalizada");
                }

                Log.i("tarea", taskObject.getString("color"));
                if(taskObject.has("color") ){
                    String color = taskObject.getString("color");
                    int colorSeleccionado = Color.parseColor("#"+color);
                    mCardView.setCardBackgroundColor(colorSeleccionado);
                    colorElegido=color;
                }else{
                    int colorSeleccionado = Color.parseColor("#FFFFFF");
                    mCardView.setCardBackgroundColor(colorSeleccionado);
                }


                if(taskObject.get("users").toString().equals("[]") ){
                    Toast.makeText(TaskActivity.this, "No hay usuarios asignados." ,
                            Toast.LENGTH_SHORT).show();
                }else{

                    usersObject = (JSONArray)taskObject.get("users");
                    JSONObject usuarioLeido;
                    for(int i=0; i<usersObject.length(); i++){
                        usuarioLeido = usersObject.getJSONObject(i);
                        if(usuarioLeido.getString("username").equals(Session.getUsername())){
                            asignarButton.setText("Desasignar");
                        }
                    }

                    String conectado = Session.getUsername();

                    UserTaskAdapter usersAdapter = new UserTaskAdapter(c, usersObject);
                    mListView.setAdapter(usersAdapter);
                }

            }catch (JSONException e){
                Log.e("JSONException", e.toString());
            }catch (Exception e){
                Log.e("Error", e.toString());

            }

        }
        public String parseDate(String time) {

            String inputPattern = "yyyy-MM-dd";
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
    }
    /**
     * Represents an asynchronous task used to delete a task .
     */
    public class DeleteTask extends AsyncTask<Void, Void, Boolean> {


        DeleteTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+
                    "/projects/"+Session.getProjectSelected()+
                    "/sprints/"+Session.getSprintSelected()+
                    "/status/"+Session.getStatusSelected()+
                    "/tasks/"+Session.getTaskSelected();
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
            mDeleteTask = null;
            showProgress(false);

            if (success) {
                setResult(300);
                finish();
            } else {
                Toast.makeText(TaskActivity.this, "No se ha podido eliminar el proyecto." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {

            showProgress(false);
            mDeleteTask = null;
        }


    }

    /**
     * Represents an asynchronous task used to create new poll.
     */
    public class NewPollTask extends AsyncTask<Void, Void, Boolean> {

        private  String error;

        NewPollTask() { }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida =  Session.URL+"/users/"+Session.getUsername()+
                    "/projects/"+Session.getProjectSelected()+
                    "/sprints/"+Session.getSprintSelected()+
                    "/status/"+Session.getStatusSelected()+
                    "/tasks/"+Session.getTaskSelected()+
                    "/polls";
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            try{

                body.put("end_date", "12/04/2018");

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
            mNewPollTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(TaskActivity.this, "Se ha creado una votación." ,
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(c, TaskActivity.class);
                Bundle b = new Bundle();
                b.putString("key", statusID); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivityForResult(intent, 0);
            } else {
                mostrarErroresRespuesta();
            }
        }

        @Override
        protected void onCancelled() {
            mNewPollTask = null;
            showProgress(false);
        }

        private void mostrarErroresRespuesta(){

            Toast.makeText(TaskActivity.this, "Ha ocurrido algún problema." ,
                    Toast.LENGTH_SHORT).show();

        }
    }
}
