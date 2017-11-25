package com.android.ressin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HomeActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener
        , HomeFragment.OnFragmentInteractionListener, ToDoFragment.OnFragmentInteractionListener
        , TextDialogFragment.NoticeDialogListener, ResultFragment.OnFragmentInteractionListener,
        MapFrag.OnFragmentInteractionListener {
    private static final String TAG = "HomeActivity";
    String Lat = "Lat";
    String Lon = "Lon";
    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private FusedLocationProviderClient mFusedLocationClient;
    private String lat;
    private String lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        setContentView(R.layout.activity_drawer);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("ToDo");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    lat = location.getLatitude() + "";
                    lon = location.getLongitude() + "";
                    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                        String query = intent.getStringExtra(SearchManager.QUERY);
                        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getApplicationContext(),
                                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
                        suggestions.saveRecentQuery(query, null);
                        Fragment fragment = new ResultFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.Main_content, fragment, "Result Fragment")
                                .commit();

                    } else {
                        Fragment fragment = HomeFragment.newInstance(lat, lon);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.Main_content, fragment, "Home Fragment")
                                .commit();
                    }
                } else {
                    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                        String query = intent.getStringExtra(SearchManager.QUERY);
                        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getApplicationContext(),
                                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
                        suggestions.saveRecentQuery(query, null);
                        Fragment fragment = new ResultFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.Main_content, fragment, "Result Fragment")
                                .commit();

                    } else {
                        Fragment fragment = new HomeFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.Main_content, fragment, "Home Fragment")
                                .commit();
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent set = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission
                        (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
            return;
        }
        locationManager.requestLocationUpdates("gps", 100000, 1000, locationListener);
        initClient();
        initDrawer();
        initSearch();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates("gps", 100000, 1000, locationListener);
        }
    }

    private void initSearch() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = findViewById(R.id.search_bar);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(getApplicationContext(), HomeActivity.class)));
        searchView.setIconifiedByDefault(false);
    }

    private void initDrawer()
    {
        NavigationView navigationView = findViewById(R.id.nav_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        ImageView imageView = header.findViewById(R.id.user_pic);
        if(mUser.getPhotoUrl()!=null)
        Picasso.with(this).load(mUser.getPhotoUrl())
                .transform(new CircleTransform()).
                into(imageView);
        TextView name = header.findViewById(R.id.disp_name);
        TextView email = header.findViewById(R.id.user_email);
        name.setText(mUser.getDisplayName());
        email.setText(mUser.getEmail());
        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (drawerView != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void initClient()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id ))
                .build();
        // [END configure_signin]
        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void signOut() {
        Toast.makeText(getApplicationContext(),"Signing out",Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        finishActivity(0);
                    }
                });
    }

    @NonNull
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        String fragTag = getFragmentManager()
                .findFragmentById(R.id.Main_content)
                .getTag();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragTag.equals("Map Fragment")) {
            Fragment fragment;
            if (lat != null) {
                fragment = HomeFragment.newInstance(lat, lon);
            } else {
                fragment = new HomeFragment();
            }
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.Main_content, fragment)
                    .commit();
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        switch(item.getItemId()) {
            case R.id.sign_out:
                signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            case R.id.Home:
                Fragment fragment;
                if (lat != null && lon != null) {
                    fragment = HomeFragment.newInstance(lat, lon);
                } else {
                    fragment = new HomeFragment();
                }
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.Main_content, fragment, "Home Fragment")
                        .commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.Todo:
                Fragment fragment1 = new ToDoFragment();
                FragmentManager fragmentManager1 = getFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.Main_content, fragment1, "ToDo Fragment")
                        .commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.clear:
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                        MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
                suggestions.clearHistory();
                Toast.makeText(getBaseContext(), "History Cleared!", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public void genCard(String input) {
        mDatabase.child(mUser.getUid())
                .push()
                .setValue(input);
    }

    @Override
    public void onDialogPositiveClick(String input) {
        genCard(input);
    }

    @Override
    public void onDialogNegativeClick(TextDialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    private String readFromFile(Context context, String filename) {

        String ret = null;

        try {
            InputStream inputStream = context.openFileInput(filename);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }

        return ret;
    }

    @Override
    public void cardClicked(View v, int position) {
        Fragment fragment = new MapFrag();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.Main_content, fragment, "Map Fragment")
                .commit();
    }
}
