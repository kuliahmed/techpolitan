package com.ahmad.techpolitan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedpreferences;
    private EditText usernameField,passwordField;

    HomeFragment homeFragment = new HomeFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    CameraFragment cameraFragment = null;
    ReportFragment reportFragment = new ReportFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.page_1);
    }

    public void login(View view){

        //method.setText("Get Method");
        //new SigninActivity(this,status,role,0).execute(username,password);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.page_1:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homeFragment).commit();
                return true;
            case R.id.page_2:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, historyFragment).commit();
                return true;
            case R.id.page_3:
                cameraFragment = new CameraFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, cameraFragment).commit();
                return true;
            case R.id.page_4:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, reportFragment).commit();
                return true;
            case R.id.page_5:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, profileFragment).commit();
                return true;
        }
        return false;
    }
}