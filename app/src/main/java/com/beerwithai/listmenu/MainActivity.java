package com.beerwithai.listmenu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.support.v4.app.*;

import static android.R.attr.lines;

public class MainActivity extends FragmentActivity {

    static final int NUM_ITEMS = 3;

    MyAdapter mAdapter;

    ViewPager mPager;

    static String[] sCheeseStrings = {"tab1", "tab2", "tab3"};
    static String[] lines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.goto_first);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(0);
            }
        });
        button = (Button)findViewById(R.id.goto_last);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(NUM_ITEMS-1);
            }
        });

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mPager);

        String result="", url="http://10.0.2.2:8000/thanks/";
        try {
            result = new RetrieveFeedTask().execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Debug", result);
        lines = result.split(" ");

    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return new SubTabFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Title #" + position;
        }
    }

    public static class SubTabFragment extends Fragment {

        MyAdapter2 mAdapter;

        ViewPager mPager;

        SubTabFragment newInstance(int num) {
            SubTabFragment f = new SubTabFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.content_main, container, false);
            mAdapter = new MyAdapter2(getChildFragmentManager());

            mPager = (ViewPager)v.findViewById(R.id.pager2);
            mPager.setAdapter(mAdapter);

            TabLayout tabLayout = (TabLayout)v.findViewById(R.id.tab_layout2);
            tabLayout.setupWithViewPager(mPager);

            return v;
        }
    }

    public static class MyAdapter2 extends FragmentPagerAdapter {
        public MyAdapter2(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return ArrayListFragment.newInstance(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Title #" + position;
        }
    }

    public static class ArrayListFragment extends ListFragment {
        int mNum;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.listview, container, false);
            View tv = v.findViewById(R.id.text);
            if(mNum < lines.length)
            ((TextView)tv).setText("Fragment #" + mNum);
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, lines));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            String output = "";
            try {
                URL url = new URL(urls[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                url.openStream()));

                String inputLine;

                while((inputLine = in.readLine()) != null)
                    output += inputLine;

                in.close();
                return output;
            } catch (Exception e) {
                e.printStackTrace();

                return null;
            }
        }

    }
}