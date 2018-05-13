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

import sergiojuliogu.myapplication.R;

public class UserTaskAdapter extends BaseAdapter {

    private final Context mContext;
    private final JSONArray users;

    // 1
    public UserTaskAdapter(Context context, JSONArray users) {
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
            final JSONObject userTask = users.getJSONObject(position);

            // 2
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.linearlayout_users_task, null);
            }

            // 3
            final TextView nameTextView = (TextView)convertView.findViewById(R.id.textViewName_task);
            final ImageView avatarImageView = (ImageView) convertView.findViewById(R.id.imageViewAvatar_task);

            // 4

            String name = userTask.get("username").toString();
            nameTextView.setText(name);

            if(userTask.has("avatar") ){
                if(!userTask.get("avatar").equals("")){
                    int resourceId = this.mContext.getResources().getIdentifier(userTask.get("avatar").toString(), "drawable",mContext.getPackageName());
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

}