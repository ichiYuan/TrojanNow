package com.example.ichi.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.example.ichi.myapplication.dummy.DummyContent;
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
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class MicropostFragment extends ListFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TYPE = "type";
    private static final String ARG_CONTENT = "content";

    // TODO: Rename and change types of parameters
    private String type;
    private String content;

    private OnFragmentInteractionListener mListener;

    private List<MicropostItem> mMicroposts;
    private MicropostAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static MicropostFragment newInstance(String type, String content) {
        MicropostFragment fragment = new MicropostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
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

        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
            content = getArguments().getString(ARG_CONTENT);
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<List<MicropostItem>>(){}.getType();
        mMicroposts = (List<MicropostItem>) gson.fromJson(content, listType);
        // TODO: Change Adapter to display your content
        mAdapter = new MicropostAdapter(mMicroposts);
        setListAdapter(mAdapter);
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
            mListener = (OnFragmentInteractionListener) activity;
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
            mListener.onFragmentInteraction(mMicroposts.get(position).url);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
