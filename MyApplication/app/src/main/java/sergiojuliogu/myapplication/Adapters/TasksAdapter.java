package sergiojuliogu.myapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sergiojuliogu.myapplication.Activities.ProjectActivity;
import sergiojuliogu.myapplication.Activities.StatusActivity;
import sergiojuliogu.myapplication.Activities.TaskActivity;
import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class TasksAdapter extends BaseAdapter {
    private final Context mContext;
    private JSONArray tasksArray;
    private final String idStatus;
    private  JSONArray statusArray;
    private Context c;
    private UpdateTaskStatus mUpdateTaskStatus;
    private Activity act;
    // 1
    public TasksAdapter(Activity act,Context context,String idStatus, JSONArray tasks, JSONArray statusArray) {
        //mStatusGetInfo = new StatusGetInfo(idStatus);
        //mStatusGetInfo.execute((Void) null);
        this.tasksArray = tasks;
        this.mContext = context;
        this.idStatus = idStatus;
        this.statusArray = statusArray;
        this.c = context;
        this.act = act;

    }

    // 2
    @Override
    public int getCount() {
        return tasksArray.length();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        try{
            final JSONObject taskObject = tasksArray.getJSONObject(position);

            // 2
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.linearlayout_tasks, null);
            }

            // 3
            final TextView nameTextView = (TextView)convertView.findViewById(R.id.nombreTarea);

            final String name = taskObject.get("name").toString();
            final String id = taskObject.getString("_id");
            nameTextView.setText(name);
            nameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Session.setTaskSelected(id);
                    Session.setStatusSelected(idStatus);
                    Intent intent = new Intent(c, TaskActivity.class);
                    Bundle b = new Bundle();
                    b.putString("key", idStatus); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //c.startActivity(intent);
                    act.startActivityForResult(intent, 0);
                   // ((Activity)c).startActivityForResult(intent, 0);
                    //c.getApplicationContext().startActivityForResult(intent, 0);
                }
            });
            // 4
            final CardView colorTarjeta = (CardView)convertView.findViewById(R.id.colorTarea);
            int color = Color.parseColor("#"+taskObject.getString("color"));
            colorTarjeta.setCardBackgroundColor(color);

            final Spinner statusSpinner = (Spinner)convertView.findViewById(R.id.spinnerTarea);
            // you need to have a list of data that you want the spinner to display
            List<String> spinnerArray =  new ArrayList<String>();
            JSONObject estadoLeido;
            int elegido=0;
            for(int i=0; i<statusArray.length(); i++){
                estadoLeido = statusArray.getJSONObject(i);
                if(estadoLeido.getString("_id").equals(idStatus)){
                    elegido = i;
                }
                spinnerArray.add(estadoLeido.getString("name"));

            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext.getApplicationContext(), R.layout.spinner_item,R.id.textview, spinnerArray);
            //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statusSpinner.setAdapter(adapter);
            statusSpinner.setSelection(elegido);

            statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    JSONObject estadoLeidoBucle;
                    String tareaLeidaBucle;

                    String name = "";
                    String text = statusSpinner.getSelectedItem().toString();
                    String idNueva= idStatus;
                    int posicionNueva =0;
                    try{
                        for(int i=0; i<statusArray.length(); i++ ) {
                            estadoLeidoBucle = statusArray.getJSONObject(i);
                            if(estadoLeidoBucle.getString("name").equals(text)){
                                idNueva = estadoLeidoBucle.getString("_id");
                                posicionNueva=i;
                            }
                        }
                        for(int i=0; i<statusArray.length(); i++ ){
                            estadoLeidoBucle = statusArray.getJSONObject(i);
                            JSONArray estadoLeidoTareas = estadoLeidoBucle.getJSONArray("tasks");
                            if(estadoLeidoBucle.getString("_id").equals(idStatus)){
                                if(!estadoLeidoBucle.getString("name").equals(text)){
                                    mUpdateTaskStatus = new UpdateTaskStatus(taskObject.getString("_id"),idStatus, idNueva);
                                    mUpdateTaskStatus.execute((Void) null);
                                }else{
                                    return;
                                }
                            }
                        }
                    }catch (JSONException e){
                        Log.e("JSONException", e.toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    return;
                }
            });

        }catch (JSONException e){
            Log.e("JavaException", e.toString());
        }

        return convertView;
    }



    /**
     * Represents an asynchronous task used to change task status.
     */
    public class UpdateTaskStatus extends AsyncTask<Void, Void, Boolean> {
        private String idTask = null;
        private String idStatus = null;
        private String idStatusNuevo =null;


        UpdateTaskStatus(String idTask, String  idStatus, String idStatusNuevo) {
            this.idStatus = idStatus;
            this.idTask = idTask;
            this.idStatusNuevo = idStatusNuevo;
        }


        @Override
        protected Boolean doInBackground(Void... params) {


            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+
                    "/projects/"+Session.getProjectSelected()+
                    "/sprints/"+Session.getSprintSelected()+
                    "/status/"+idStatus+
                    "/tasks/"+idTask;
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            JSONObject nuevoUsuario = new JSONObject();
            try{
                body.put("operation", "cambiarEstado");
                body.put("statusId", idStatusNuevo);

                Log.i("antiguo", idStatus);

                Log.i("nuevoEstado", idStatusNuevo);

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

                Log.i("El status", status);
                Log.i("La respuesta", result);


                if(!status.equals("204")){
                    return false;
                }

                JSONObject taskLei;
                for (int i =0; i < tasksArray.length(); i++){
                    taskLei = tasksArray.getJSONObject(i);
                    if(taskLei.getString("_id").equals(idTask)){
                        tasksArray.remove(i);
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
            mUpdateTaskStatus = null;
            if (success) {
                Toast.makeText(mContext, "Tarea actualizada." ,
                        Toast.LENGTH_SHORT).show();

               Session.setCambios(true);
               TasksAdapter.super.notifyDataSetChanged();
            } else {
                Toast.makeText(mContext, "Tarea no actualizado." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUpdateTaskStatus = null;
        }
    }

}
