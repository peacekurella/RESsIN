package com.android.ressin;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener , NavigationView.OnNavigationItemSelectedListener
    , HomeFragment.OnFragmentInteractionListener , ToDoFragment.OnFragmentInteractionListener
        , TextDialogFragment.NoticeDialogListener, ResultFragment.OnFragmentInteractionListener,
        MapFrag.OnFragmentInteractionListener
{
    private static final String TAG = "HomeActivity";
    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("ToDo");
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
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

        initClient();
        initDrawer();
        initSearch();

    }

    @Override
    protected void onStart() {
        super.onStart();
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager()
                .findFragmentById(R.id.Main_content)
                .getTag().equals("Map Fragment")) {
            Fragment fragment = new HomeFragment();
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
                Fragment fragment = new HomeFragment();
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


    @Override
    public void cardClicked(View v, int position) {
        Fragment fragment = new MapFrag();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.Main_content, fragment, "Map Fragment")
                .commit();
    }
}
