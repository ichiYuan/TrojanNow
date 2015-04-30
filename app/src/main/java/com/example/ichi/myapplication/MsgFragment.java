package com.example.ichi.myapplication;

import android.app.Activity;
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
 * Activities containing this fragment MUST implement the {@link OnMsgFragmentInteractionListener}
 * interface.
 */
public class MsgFragment extends ListFragment implements MyResultReceiver.Receiver {

    private int id;

    private OnMsgFragmentInteractionListener mListener;

    private List<MsgItem> mMessages;
    private MsgAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static MsgFragment newInstance(int id) {
        MsgFragment fragment = new MsgFragment();
        fragment.setId(id);
        return fragment;
    }

    public void setId(int ID) {
        id = ID;
    }

    public void loadData(String content) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<MsgItem>>(){}.getType();
        mMessages = (List<MsgItem>) gson.fromJson(content, listType);
        mAdapter.clear();
        mAdapter.addAll(mMessages);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MsgFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMessages = new ArrayList<MsgItem>();
        mAdapter = new MsgAdapter(mMessages);
        setListAdapter(mAdapter);
        sendRequestMessages(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.msg_layout, container, false);
        Button button = (Button) myFragmentView.findViewById(R.id.inbox_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendRequestMessages(true);
            }
        });
        button = (Button) myFragmentView.findViewById(R.id.outbox_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendRequestMessages(false);
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

    public void sendRequestMessages(boolean inbox) {
        if (id < 0)
            return;
        String url = "https://rails-tutorial-cosimo-dw.c9.io/users/"+id+"/"+(inbox?"inbox":"outbox")+".json";

        Intent intent = HTTPRequest.makeIntent(getActivity(), this, url, "GET", null);

        getActivity().startService(intent);
    }

    private class MsgItem{
        int id;
        String receiver_name;
        String content;
        String sender_name;
        String url;
    }

    private class MsgAdapter extends ArrayAdapter<MsgItem>{

        public MsgAdapter(List<MsgItem> mMessages) {
            super(getActivity(), 0, mMessages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.msg_item, null);
            }

            MsgItem Msg = getItem(position);

            TextView senderText = (TextView)convertView.findViewById(R.id.message_sender);
            senderText.setText(Msg.sender_name);

            TextView contentText = (TextView)convertView.findViewById(R.id.message_content);
            contentText.setText(Msg.content);

            TextView receiverText = (TextView)convertView.findViewById(R.id.message_receiver);
            receiverText.setText(Msg.receiver_name);

            return convertView;
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMsgFragmentInteractionListener) activity;
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
            mListener.onMsgFragmentInteraction(mMessages.get(position).url);
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
    public interface OnMsgFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onMsgFragmentInteraction(String id);
    }

}
