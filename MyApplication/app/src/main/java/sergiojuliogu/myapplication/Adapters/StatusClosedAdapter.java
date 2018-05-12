package sergiojuliogu.myapplication.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.Date;

import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class StatusClosedAdapter extends BaseAdapter{
    private final Context mContext;
    private final JSONArray statusArray;
    private StatusUpdateTask mStatusUpdateTask;
    // 1
    public StatusClosedAdapter(Context context, JSONArray status) {
        this.mContext = context;
        this.statusArray = status;
        mStatusUpdateTask = null;
    }

    // 2
    @Override
    public int getCount() {
        return statusArray.length();
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
            final JSONObject statusSprint = statusArray.getJSONObject(position);
            final JSONArray tasksObject = statusSprint.getJSONArray("tasks");

            // 2
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.linear_layout_closed_status, null);
            }

            // 3
            final TextView nameTextView = (TextView)convertView.findViewById(R.id.nom_tareas_status_closed);
            final TextView numTextView = (TextView)convertView.findViewById(R.id.num_tareas_status_closed);

            final String name = statusSprint.get("name").toString();
            nameTextView.setText(name);

            String num = String.valueOf(tasksObject.length());
            numTextView.setText(num);
            final TextView buttonActivar = (TextView)convertView.findViewById(R.id.button_open_status);
            buttonActivar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        mStatusUpdateTask = new StatusUpdateTask(statusSprint.getString("_id"), name);
                        mStatusUpdateTask.execute((Void) null);

                    }catch (JSONException e){
                        Log.e("JSONException", e.toString());
                    }
                }
            });
            // 4



        }catch (JSONException e){
            Log.e("JavaException", e.toString());
        }

        return convertView;
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

                    JSONObject statusComprobar = new JSONObject();
                    JSONObject statusActual = new JSONObject();
                    for(int i=0; i<statusArray.length(); i++){
                        statusComprobar = statusArray.getJSONObject(i);
                        if(statusComprobar.getString("_id").equals(this.idStatus)){
                            statusArray.remove(i);
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
            mStatusUpdateTask = null;
            if (success) {
                Toast.makeText(mContext, "Sprint actualizado." ,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Sprint no actualizado." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mStatusUpdateTask = null;
        }
    }
}
