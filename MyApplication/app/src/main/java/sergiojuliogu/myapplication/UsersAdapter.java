package sergiojuliogu.myapplication;

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

public class UsersAdapter extends BaseAdapter {

    private final Context mContext;
    private final JSONArray users;

    // 1
    public UsersAdapter(Context context, JSONArray users) {
        this.mContext = context;
        this.users = users;
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
                convertView = layoutInflater.inflate(R.layout.linearlayout_users, null);
            }

            // 3
            final TextView nameTextView = (TextView)convertView.findViewById(R.id.textViewName);
            final TextView roleTextView = (TextView)convertView.findViewById(R.id.textViewRole);
            final ImageView avatarImageView = (ImageView) convertView.findViewById(R.id.imageViewAvatar);

            // 4

            String name = userObject.get("username").toString();
            nameTextView.setText(name);

            String role = roleObject.get("name").toString();
            roleTextView.setText(role);
            Log.i("Esto", name);


            if(userObject.has("avatar") ){
                if(!userObject.get("avatar").equals("")){
                    int resourceId = this.mContext.getResources().getIdentifier(userObject.get("avatar").toString(), "drawable",mContext.getPackageName());
                    avatarImageView.setImageResource(resourceId);
                }
            }
        }catch (JSONException e){
            Log.e("JavaException", e.toString());
        }


        return convertView;
    }

}