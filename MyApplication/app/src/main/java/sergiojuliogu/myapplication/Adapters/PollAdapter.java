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

public class PollAdapter extends BaseAdapter {

    private final Context mContext;
    private final JSONArray votes;

    // 1
    public PollAdapter(Context context, JSONArray votes) {
        this.mContext = context;
        this.votes = votes;
    }

    // 2
    @Override
    public int getCount() {
        return votes.length();
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
        try {
            final JSONObject voteObject = votes.getJSONObject(position);
           // final JSONObject userObject = voteObject.getJSONObject("user");

            // 2
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.linearlayout_poll_votes, null);
            }

            // 3
            final TextView voteTextView = (TextView) convertView.findViewById(R.id.user_vote_vote);
            final TextView commentTextView = (TextView) convertView.findViewById(R.id.user_vote_comment);

            // 4

           // String name = userObject.get("username").toString();
            //nameTextView.setText(name);

            String vote = voteObject.get("value").toString();
            voteTextView.setText(vote);

            String comment = voteObject.getString("comment");
            commentTextView.setText(comment);
        } catch (JSONException e) {
            Log.e("JavaException", e.toString());
        }


        return convertView;
    }
}