package sergiojuliogu.myapplication.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class UsersUpdateAdapter extends BaseAdapter {

    private final Context mContext;
    private final JSONArray users;
    private final JSONArray roles;

    private UpdateProjectUserTask mUpdateProjectUser;

    // 1
    public UsersUpdateAdapter(Context context, JSONArray users, JSONArray roles) {
        this.mContext = context;
        this.users = users;
        this.roles = roles;
        mUpdateProjectUser = null;
    }

    // 2
    @Override
    public int getCount() {
        return users.length();
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
            final JSONObject userProject = users.getJSONObject(position);
            final JSONObject userObject = userProject.getJSONObject("user");
            final JSONObject roleObject = userProject.getJSONObject("role");

            // 2
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.linearlayout_update_user_project, null);
            }

            // 3
            final TextView nameTextView = (TextView)convertView.findViewById(R.id.update_user_project_name);
            final Spinner roleSpinnerView = (Spinner) convertView.findViewById(R.id.update_user_project_spinner);
            final ImageView avatarImageView = (ImageView) convertView.findViewById(R.id.update_user_project_avatar);
            final ImageButton deleteButtonView = (ImageButton) convertView.findViewById(R.id.update_user_project_delete_button);
            deleteButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if(userObject.getString("username").equals(Session.getUsername())){
                            Toast.makeText(mContext, "No te puedes eliminar a ti mismo." ,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        mUpdateProjectUser = new UpdateProjectUserTask("null", userObject.get("username").toString(), "DeleteUser");
                        mUpdateProjectUser.execute((Void) null);
                    }catch (JSONException e){
                        Log.e("JSONException", e.toString());
                    }
                }
            });

            // 4

            List<String> spinnerArray =  new ArrayList<String>();
            JSONObject roleLeido;
            for(int i = 0; i< roles.length(); i++){
                roleLeido = roles.getJSONObject(i);
                if(roleLeido.has("name")){
                    spinnerArray.add(roleLeido.get("name").toString());
                }
            }

            ArrayAdapter adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item,R.id.textview, spinnerArray);
            roleSpinnerView.setAdapter(adapter);
            for (int i =0; i< spinnerArray.size(); i++){
                if(roleObject.get("name").toString().equals(spinnerArray.get(1))){
                    roleSpinnerView.setSelection(i);
                }
            }
            roleSpinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    JSONObject roleLeido;
                    String name = "";
                    String text = roleSpinnerView.getSelectedItem().toString();
                    try{
                        if(userObject.getString("username").equals(Session.getUsername())){
                            return;
                        }
                        boolean encontrador = false;
                        for(int i = 0; i< roles.length(); i++){
                            roleLeido = roles.getJSONObject(i);
                            if(roleLeido.getString("name").equals(text))
                                if(!roleLeido.getString("name").equals(roleObject.getString("name"))){
                                    mUpdateProjectUser = new UpdateProjectUserTask(roleLeido.getString("name"), userObject.get("username").toString(), "UpdateRole");
                                    mUpdateProjectUser.execute((Void) null);
                                }
                        }
                    }catch (JSONException e){
                        Log.e("JSONException", e.toString());
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    return;
                }

            });

            String name = userObject.get("username").toString();
            nameTextView.setText(name);


            if(userObject.has("avatar") ){
                if(!userObject.get("avatar").equals("")){
                    int resourceId = this.mContext.getResources().getIdentifier(userObject.get("avatar").toString(), "drawable",mContext.getPackageName());
                    avatarImageView.setImageResource(resourceId);
                }else{
                    int resourceId = this.mContext.getResources().getIdentifier("avatar_051", "drawable",mContext.getPackageName());
                    avatarImageView.setImageResource(resourceId);
                }
            }else{
                int resourceId = this.mContext.getResources().getIdentifier("avatar_051", "drawable",mContext.getPackageName());
                avatarImageView.setImageResource(resourceId);
            }
        }catch (JSONException e){
            Log.e("JavaException", e.toString());
        }


        return convertView;
    }


    /**
     * Represents an asynchronous task used to add user to project.
     */
    public class UpdateProjectUserTask extends AsyncTask<Void, Void, Boolean> {
        private String idProject = null;
        private String username = null;
        private String role = null;
        private String method = null;

        UpdateProjectUserTask(String role, String username, String  method) {
                this.username = username;
                this.role = role;
                this.method = method;
        }


        @Override
        protected Boolean doInBackground(Void... params) {


            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+"/projects/"+Session.getProjectSelected();
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            JSONObject nuevoUsuario = new JSONObject();
            try{
                body.put("operation", method);
                body.put("username", username);
                body.put("role", role);


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
                if (method.equals("DeleteUser")){
                    JSONObject userComprobar = new JSONObject();
                    JSONObject user = new JSONObject();
                    for(int i=0; i<users.length(); i++){
                        userComprobar = users.getJSONObject(i);
                        user = userComprobar.getJSONObject("user");
                        if(user.getString("username").equals(username)){
                            users.remove(i);
                        }
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
            mUpdateProjectUser = null;
            if (success) {
                Toast.makeText(mContext, "Proyecto actualizado." ,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Proyecto no actualizado." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUpdateProjectUser = null;
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

    }
}