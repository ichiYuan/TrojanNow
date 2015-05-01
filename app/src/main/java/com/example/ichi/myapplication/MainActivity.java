package com.example.ichi.myapplication;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ichi.clientcontroller.MyResultReceiver;
import com.example.ichi.servercomm.HTTPRequest;
import com.example.ichi.session.SessionController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener,
        MyResultReceiver.Receiver,
        MicropostFragment.OnMicropostFragmentInteractionListener,
        MsgFragment.OnMsgFragmentInteractionListener,
        UserFragment.OnUserFragmentInteractionListener
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    int id = -1;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setVisibility(View.INVISIBLE);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        checkLogin();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            sendRequestLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendRequestLogout(){
        String url = "https://rails-tutorial-cosimo-dw.c9.io/logout.json";
        //todo
        Intent intent = HTTPRequest.makeIntent(this, this, url, "DELETE", null);
        startService(intent);
        return;
    }
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onMicropostFragmentInteraction(String id) {

    }

    @Override
    public void onMsgFragmentInteraction(int id, String name) {
        Intent intent = new Intent();
        intent.setClass(this,EditMsgActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("name",name);
        startActivity(intent);
    }

    @Override
    public void onUserFragmentInteraction(int id, String name) {
        Intent intent = new Intent();
        intent.setClass(this,ProfileActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("name",name);
        startActivity(intent);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    MicropostFragment mMicropostFragment = MicropostFragment.newInstance(id);
                    return mMicropostFragment;
                case 1:
                    UserFragment mUserFragment = UserFragment.newInstance(id);
                    return mUserFragment;
                case 2:
                    MsgFragment mMsgFragment = MsgFragment.newInstance(id);
                    return mMsgFragment;
            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    private class SimpleUserItem{
        int id;
    }
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
                    if (results.get(0).startsWith("NOT LOGGED IN")) {
                        Intent intent = new Intent();
                        intent.setClass(this,LoginActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<SimpleUserItem>(){}.getType();
                        SimpleUserItem curUser = (SimpleUserItem) gson.fromJson(results.get(0), listType);

                        id = curUser.id;
                        MicropostFragment frg = (MicropostFragment)mSectionsPagerAdapter.getRegisteredFragment(0);
                        if (frg != null)
                            frg.setId(id);
                        UserFragment frg2 = (UserFragment)mSectionsPagerAdapter.getRegisteredFragment(1);
                        if (frg2 != null)
                            frg2.setId(id);
                        MsgFragment frg3 = (MsgFragment)mSectionsPagerAdapter.getRegisteredFragment(2);
                        if (frg3 != null)
                            frg3.setId(id);
                        mViewPager.setVisibility(View.VISIBLE);

                    }
                }
                else {
                    checkLogin();
                }
                //showProgress(false);
                break;
            case ERROR:
                // handle the error;
                break;
        }
    }

    void checkLogin() {
        String url = "https://rails-tutorial-cosimo-dw.c9.io/check.json";

        Intent intent = HTTPRequest.makeIntent(this, this, url,"GET",null);

        startService(intent);
    }
}
