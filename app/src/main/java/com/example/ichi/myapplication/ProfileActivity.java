package com.example.ichi.myapplication;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.ichi.clientcontroller.MyResultReceiver;
import com.example.ichi.servercomm.HTTPRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfileActivity extends ActionBarActivity implements MyResultReceiver.Receiver {
    private boolean following = true;
    private int id = -1;
    private String name;

    private List<UserItem> mUsers;
    private UserAdapter mAdapter;

    private void setFollowing(boolean following) {
        this.following = following;
        sendRequestUsers();
    }

    public void loadData(String content) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<UserItem>>(){}.getType();
        mUsers = (List<UserItem>) gson.fromJson(content, listType);
        mAdapter.clear();
        mAdapter.addAll(mUsers);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle b = getIntent().getExtras();
        id = b.getInt("id");
        name = b.getString("name");

        TextView textView = (TextView) findViewById(R.id.user_name);
        textView.setText(name);

        Button button = (Button) findViewById(R.id.following_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setFollowing(true);
            }
        });
        button = (Button) findViewById(R.id.followers_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setFollowing(false);
            }
        });

        button = (Button) findViewById(R.id.send_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(v.getContext(), EditMsgActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name",name);
                v.getContext().startActivity(intent);
            }
        });

        mUsers = new ArrayList<UserItem>();
        mAdapter = new UserAdapter(this,mUsers);
        ListView lv = (ListView) findViewById(R.id.user_list);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(view.getContext(),ProfileActivity.class);
                UserItem user = mUsers.get(position);
                intent.putExtra("id",user.id);
                intent.putExtra("name",user.name);
                view.getContext().startActivity(intent);
            }
        });

        sendRequestUsers();
    }

    private class UserItem{
        int id;
        String name;
        String url;
        boolean following;
    }

    private class UserAdapter extends ArrayAdapter<UserItem> {

        public UserAdapter(Context context, List<UserItem> mUsers) {
            super(context, 0, mUsers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.user_item, null);
            }

            UserItem user = getItem(position);

            TextView senderText = (TextView)convertView.findViewById(R.id.user_name);
            senderText.setText(user.name);

            ToggleButton button = (ToggleButton) convertView.findViewById(R.id.follow_button);
            button.setChecked(user.following);
            button.setOnClickListener(new FollowButton(user));

            return convertView;
        }
    }

    private class FollowButton implements View.OnClickListener, MyResultReceiver.Receiver {
        UserItem user;
        FollowButton(UserItem user) {
            this.user = user;
        }
        @Override
        public void onClick(View v) {
            if (!user.following) {
                String url = "https://rails-tutorial-cosimo-dw.c9.io/relationships.json";
                Map<String, String> params = new HashMap<String, String>();
                params.put("followed_id",String.valueOf(user.id));
                Intent intent = HTTPRequest.makeIntent(v.getContext(),this,url,"POST",params);
                v.getContext().startService(intent);
            }
            else {
                String url = "https://rails-tutorial-cosimo-dw.c9.io/relationships/"+user.id+".json";
                Intent intent = HTTPRequest.makeIntent(v.getContext(),this,url,"DELETE",null);
                v.getContext().startService(intent);
            }
            user.following = !user.following;
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle resultData) {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case RUNNING:
                //show progress
                break;
            case FINISHED:
                // do something interesting
                // hide progress
                ArrayList<String> results = resultData.getStringArrayList("results");
                if (results != null) {
                    loadData(results.get(0));
                }
                //showProgress(false);
                break;
            case ERROR:
                // handle the error;
                break;
        }
    }

    public void sendRequestUsers() {
        if (id < 0)
            return;
        String url = "https://rails-tutorial-cosimo-dw.c9.io/users/"+id+"/"+(following?"following":"followers")+".json";

        Intent intent = HTTPRequest.makeIntent(this, this, url, "GET", null);

        this.startService(intent);
    }
}
