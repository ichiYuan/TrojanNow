package com.example.ichi.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.example.ichi.clientcontroller.MyResultReceiver;
import com.example.ichi.myapplication.dummy.DummyContent;
import com.example.ichi.servercomm.HTTPRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnUserFragmentInteractionListener}
 * interface.
 */
public class UserFragment extends ListFragment implements MyResultReceiver.Receiver {

    private boolean following = true;
    private int id = 0;

    private OnUserFragmentInteractionListener mListener;

    private List<UserItem> mUsers;
    private UserAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static UserFragment newInstance(int id) {
        UserFragment fragment = new UserFragment();
        fragment.id = id;
        return fragment;
    }

    public void setId(int ID) {
        id = ID;
        sendRequestUsers();
    }

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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUsers = new ArrayList<UserItem>();
        mAdapter = new UserAdapter(mUsers);
        setListAdapter(mAdapter);

        sendRequestUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.user_layout, container, false);
        Button button = (Button) myFragmentView.findViewById(R.id.following_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setFollowing(true);
            }
        });
        button = (Button) myFragmentView.findViewById(R.id.followers_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setFollowing(false);
            }
        });

        final EditText mEditText = (EditText) myFragmentView.findViewById(R.id.makeSearch);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendSearchUsers(v.getText().toString());

                    return true;
                }
                return false;
            }
        });
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    sendSearchUsers(mEditText.getText().toString());
            }
        });

        return myFragmentView;
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

        Intent intent = HTTPRequest.makeIntent(getActivity(), this, url, "GET", null);

        getActivity().startService(intent);
    }

    private void sendSearchUsers(String text) {
        String url = "https://rails-tutorial-cosimo-dw.c9.io/users.json?name="+text;

        Intent intent = HTTPRequest.makeIntent(getActivity(), this, url, "GET", null);

        getActivity().startService(intent);
    }

    private class UserItem{
        int id;
        String name;
        String url;
        boolean following;
    }

    private class UserAdapter extends ArrayAdapter<UserItem>{

        public UserAdapter(List<UserItem> mUsers) {
            super(getActivity(), 0, mUsers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.user_item, null);
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
                Intent intent = HTTPRequest.makeIntent(getActivity(),this,url,"POST",params);
                getActivity().startService(intent);
            }
            else {
                String url = "https://rails-tutorial-cosimo-dw.c9.io/relationships/"+user.id+".json";
                Intent intent = HTTPRequest.makeIntent(getActivity(),this,url,"DELETE",null);
                getActivity().startService(intent);
            }
            user.following = !user.following;
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle resultData) {

        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnUserFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onUserFragmentInteraction(mUsers.get(position).id,mUsers.get(position).name);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnUserFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onUserFragmentInteraction(int id, String name);
    }

}
