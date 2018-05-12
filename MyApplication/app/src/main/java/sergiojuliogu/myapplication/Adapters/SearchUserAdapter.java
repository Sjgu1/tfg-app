package sergiojuliogu.myapplication.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import sergiojuliogu.myapplication.R;

public class SearchUserAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private JSONArray users;
    private JSONArray usersFiltrar;

    public SearchUserAdapter(Context context, JSONArray usuarios) {

        mContext = context;
        users = usuarios;
        inflater = LayoutInflater.from(mContext);
        usersFiltrar = new JSONArray();
        try {
            JSONObject objetoSubir = new JSONObject();
            for(int i = 0; i < users.length(); i++) {
                // 1st object
                objetoSubir = (JSONObject) users.get(i);
                usersFiltrar.put(objetoSubir);
            }
        } catch (JSONException e1) {
            Log.e("JSON ERROR", e1.toString());
        }
    }

    public class ViewHolder {
        TextView name;
    }

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

    public View getView(final int position,  View convertView, ViewGroup parent) {
        // 1
        try{
            final JSONObject userProject = users.getJSONObject(position);

            // 2
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.linearlayout_search_users, null);
            }

            // 3
            final TextView nameTextView = (TextView)convertView.findViewById(R.id.textViewNameSearch);
            final ImageView avatarImageView = (ImageView) convertView.findViewById(R.id.imageViewAvatarSearch);

            // 4

            String name = userProject.get("username").toString();
            nameTextView.setText(name);

            if(userProject.has("avatar") ){
                if(!userProject.get("avatar").equals("")){
                    int resourceId = this.mContext.getResources().getIdentifier(userProject.get("avatar").toString(), "drawable",mContext.getPackageName());
                    avatarImageView.setImageResource(resourceId);
                }
            }
        }catch (JSONException e){
            Log.e("JavaException", e.toString());
        }


        return convertView;
    }


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        try {
            for(int i = 0; i < users.length(); i++) {
                users.remove(i);
            }
            JSONObject objetoProbar = new JSONObject();

            for(int i = 0; i < usersFiltrar.length(); i++) {
                objetoProbar = (JSONObject) usersFiltrar.getJSONObject(i);
                if(objetoProbar.get("username").toString().toLowerCase(Locale.getDefault()).contains(charText)){
                    users.put(objetoProbar);
                }
                users.remove(i);
            }
        } catch (JSONException e1) {
            Log.e("JSON ERROR", e1.toString());
        }
        notifyDataSetChanged();
    }

}