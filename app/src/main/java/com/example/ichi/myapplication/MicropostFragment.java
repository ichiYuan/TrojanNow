package com.example.ichi.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.example.ichi.clientcontroller.MyResultReceiver;
import com.example.ichi.myapplication.dummy.DummyContent;
import com.example.ichi.servercomm.HTTPRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnMicropostFragmentInteractionListener}
 * interface.
 */
public class MicropostFragment extends ListFragment implements MyResultReceiver.Receiver {

    private int id;

    private OnMicropostFragmentInteractionListener mListener;

    private List<MicropostItem> mMicroposts;
    private MicropostAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static MicropostFragment newInstance(int id) {
        MicropostFragment fragment = new MicropostFragment();
        fragment.setId(id);
        return fragment;
    }

    public void setId(int ID) {
        id = ID;
    }

    public void loadData(String content) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<MicropostItem>>(){}.getType();
        mMicroposts = (List<MicropostItem>) gson.fromJson(content, listType);
        mAdapter.clear();
        mAdapter.addAll(mMicroposts);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MicropostFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMicroposts = new ArrayList<MicropostItem>();
        // TODO: Change Adapter to display your content
        mAdapter = new MicropostAdapter(mMicroposts);
        setListAdapter(mAdapter);
        sendRequestMicroposts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.micropost_layout, container, false);
        Button mMakePostButton = (Button) myFragmentView.findViewById(R.id.makePost);
        mMakePostButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setClass(getActivity(),EditTwitteeActivity.class);
                startActivity(intent);
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
                    Log.d("Debug:\t", results.get(0));
                    if (results.get(0).startsWith("INVALID")) {
                        Log.d("Debug:\t",results.get(0));

                    }
                    else if (results.get(0).startsWith("NEW USER")) {

                    }
                    else {
                        loadData(results.get(0));
                    }
                }
                //showProgress(false);
                break;
            case ERROR:
                // handle the error;
                break;
        }
    }

    public void sendRequestMicroposts() {
        if (id < 0)
            return;
        String url = "https://rails-tutorial-cosimo-dw.c9.io/users/"+id+"/microposts.json";

        Intent intent = HTTPRequest.makeIntent(getActivity(), this, url, "GET", null);

        getActivity().startService(intent);
    }

    private class MicropostItem{
        int id;
        String content;
        String display_user;
        String environment;
        String url;
    }

    private class MicropostAdapter extends ArrayAdapter<MicropostItem>{

        public MicropostAdapter(List<MicropostItem> mMicroposts) {
            super(getActivity(), 0, mMicroposts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.micropost_item, null);
            }

            MicropostItem micropost = getItem(position);

            TextView nameText = (TextView)convertView.findViewById(R.id.micropost_user);
            nameText.setText(micropost.display_user);

            TextView contentText = (TextView)convertView.findViewById(R.id.micropost_content);
            contentText.setText(micropost.content);

            return convertView;
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMicropostFragmentInteractionListener) activity;
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
            mListener.onMicropostFragmentInteraction(mMicroposts.get(position).url);
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
    public interface OnMicropostFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onMicropostFragmentInteraction(String id);
    }

}
