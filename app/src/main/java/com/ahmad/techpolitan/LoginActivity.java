package com.ahmad.techpolitan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    CardView cvLogin;
    EditText etUsername;
    EditText etPassword;
    TextView tvSign;
    ProgressBar pbLogin;
    Context mContext;
    public static final String MY_ID = "MY_ID";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cvLogin = findViewById(R.id.cvLogin);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvSign = findViewById(R.id.tvSign);
        pbLogin = findViewById(R.id.pbLogin);
        mContext = this;
        sharedpreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String myID = sharedpreferences.getString(MY_ID,"defaultValue");

        isUserIdExits(myID);
        etUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_GO) {
                    cvLogin.performClick();
                    return true;
                }
                return false;
            }
        });

        cvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbLogin.setVisibility(View.VISIBLE);
                tvSign.setVisibility(View.GONE);
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                JSONObject obj = new JSONObject();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                try {
                    obj.put("uname", username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    obj.put("pword", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String SPHERE_URL = "https://nachoscloth.xyz/api/login";
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,SPHERE_URL,obj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("Response Rest",SPHERE_URL+ response.toString());
                                try {
                                    if(response.getBoolean("status")){
                                        Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_SHORT).show();
                                        JSONObject data = response.getJSONObject("data");
                                        String myID = data.getString("id");
                                        editor.putString(MY_ID, myID);
                                        editor.apply();
                                        Log.d("change My Id", "onResponse: "+myID);
                                        navigateToStartActivity();
                                    }
                                    pbLogin.setVisibility(View.GONE);
                                    tvSign.setVisibility(View.VISIBLE);
                                    Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                hideProgressDialog();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pbLogin.setVisibility(View.GONE);
                                tvSign.setVisibility(View.VISIBLE);
                                Log.e("Response Error",SPHERE_URL+ " error " +error.toString());
//                                hideProgressDialog();
                            }
                        });
                queue.add(jsObjRequest);
            }
        });
    }
    private void isUserIdExits(String myID){
        Log.d("ID ", "isUserIdExits: "+ myID);
        if (myID != "defaultValue"){
            navigateToStartActivity();
        }
    }

    private void navigateToStartActivity(){
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
    }

}