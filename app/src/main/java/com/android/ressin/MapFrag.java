package com.android.ressin;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Activities that contain this fragment must implement the
 * {@link MapFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFrag extends Fragment implements OnMapReadyCallback {

    private static final String lLat = "lLat";
    private static final String lLon = "lLon";
    private static final String dLat = "dLat";
    private static final String dLon = "dLon";
    private double llat;
    private double llon;
    private double latd;
    private double lond;

    private OnFragmentInteractionListener mListener;

    public MapFrag() {
        // Required empty public constructor
    }

    public static MapFrag newInstance(String lat, String lon, String dlat, String dlon) {
        MapFrag fragment = new MapFrag();
        Bundle args = new Bundle();
        args.putString(lLat, lat);
        args.putString(lLon, lon);
        args.putString(dLat, dlat);
        args.putString(dLon, dlon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            llat = Double.parseDouble(getArguments().getString(lLat));
            llon = Double.parseDouble(getArguments().getString(lLon));
            latd = Double.parseDouble(getArguments().getString(dLat));
            lond = Double.parseDouble(getArguments().getString(dLon));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng dest = new LatLng(latd, lond);
        googleMap.addMarker(new MarkerOptions().position(dest).title("Destination"));
        if (llat >= 0) {
            LatLng src = new LatLng(llat, llon);
            googleMap.addMarker(new MarkerOptions().position(src).title("source"));
        }
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(dest));

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

    }
}
