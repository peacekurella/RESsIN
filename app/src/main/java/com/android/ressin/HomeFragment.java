package com.android.ressin;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements ItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Home Fragment";

    // TODO: Rename and change types of parameters
    private String lat;
    private String lon;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<ResultObj> myDataset = new ArrayList<>();
    private DatabaseReference mDatabase;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lat = getArguments().getString(ARG_PARAM1);
            lon = getArguments().getString(ARG_PARAM2);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myDataset.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ResultObj obj = new ResultObj();
                    obj.setTitle(ds.getKey().toString());
                    obj.setLink(ds.child("link").getValue(String.class));
                    if (lat != null && lon != null) {
                        float[] dis = new float[2];
                        Location.distanceBetween(Double.parseDouble(ds.child("lat").getValue().toString()),
                                Double.parseDouble(ds.child("lon").getValue().toString()),
                                Double.parseDouble(lat), Double.parseDouble(lon), dis);
                        obj.setDistance(Math.round(dis[0] / 1000) + " Kms away ...");
                    } else {
                        obj.setDistance("Location not enabled!");
                    }
                    myDataset.add(obj);
                }
                ResAdapter adapter = new ResAdapter(myDataset, new ItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        mListener.cardClicked(v, position);
                    }
                });

                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage().toString());
            }
        };
        Query query = mDatabase.child("trends");
        query.addValueEventListener(dataListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = rootView.findViewById(R.id.rec);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(View v, int position) {
        mListener.cardClicked(v, position);
    }

    interface OnFragmentInteractionListener {
        void cardClicked(View v, int position);
    }

}
