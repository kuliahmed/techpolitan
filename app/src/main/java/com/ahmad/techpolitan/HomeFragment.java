package com.ahmad.techpolitan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    SharedPreferences sharedpreferences;
    private TextView tvNip;
    private TextView tvName;
    private TextView tvType;
    private TextView tvDate;
    private TextView tvLocationIn;
    private TextView tvLocationOut;
    private TextView lblTimeIn;
    private TextView lblTimeOut;
    private TextView lblStatusOut;
    private TextView lblStatusIn;
    private ImageView profileImage;
    private ImageView ivSignIn;
    private ImageView imageView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Skeleton skeleton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvName = view.findViewById(R.id.tvName);
        tvNip = view.findViewById(R.id.tvNip);
        tvType = view.findViewById(R.id.tvType);
        tvDate = view.findViewById(R.id.tvDate);
        tvLocationIn = view.findViewById(R.id.tvLocationIn);
        tvLocationOut = view.findViewById(R.id.tvLocationOut);
        lblTimeIn = view.findViewById(R.id.lblTimeIn);
        lblTimeOut = view.findViewById(R.id.lblTimeOut);
        profileImage = view.findViewById(R.id.profile_image);
        ivSignIn = view.findViewById(R.id.ivSignIn);
        imageView = view.findViewById(R.id.imageView);
        lblStatusOut = view.findViewById(R.id.lblStatusOut);
        lblStatusIn = view.findViewById(R.id.lblStatusIn);

        skeleton = SkeletonLayoutUtils.createSkeleton(view.findViewById(R.id.clHomeStatus));

        skeleton.showSkeleton();
        try {
            getDataHome();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    public void getDataHome() throws JSONException {
        sharedpreferences = getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String myID = sharedpreferences.getString(LoginActivity.MY_ID, "4");
        JSONObject obj = new JSONObject();
        obj.put("id", myID);

        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String SPHERE_URL = "https://nachoscloth.xyz/api/home";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, SPHERE_URL, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response Rest", SPHERE_URL + response.toString() + myID);
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject respobject = response.getJSONObject("data");

                                Log.d("Response Object", "onResponse: " + respobject.toString());

                                tvName.setText(respobject.getJSONObject("user").getString("nama"));
                                tvNip.setText(respobject.getJSONObject("user").getString("nip"));
                                tvType.setText(respobject.getJSONObject("absensi").getString("tipe_absen"));
                                tvDate.setText(respobject.getJSONObject("absensi").getString("tanggal"));
                                tvLocationIn.setText(respobject.getJSONObject("absensi").getString("lokasi_masuk"));
                                tvLocationOut.setText(respobject.getJSONObject("absensi").getString("lokasi_pulang"));
                                lblTimeIn.setText(respobject.getJSONObject("absensi").getString("jam_masuk"));
                                lblTimeOut.setText(respobject.getJSONObject("absensi").getString("jam_pulang"));
                                lblStatusIn.setText((respobject.getJSONObject("absensi").getString("foto_masuk").equals("")) ? "-" : "APPROVED");
                                lblStatusOut.setText((respobject.getJSONObject("absensi").getString("foto_pulang").equals("")) ? "-" : "APPROVED");
//                                profileImage.setImageBitmap(respobject.getJSONObject("absensi").getString("foto"));
//                                ivSignIn.setImageBitmap(respobject.getJSONObject("useabsensir").getString("foto_masuk"));
//                                imageView.setImageBitmap(respobject.getJSONObject("absensi").getString("foto_pulang"));

                                RequestOptions options = new RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_image_null)
                                        .error(R.drawable.ic_image_null);


                                Glide.with(getContext()).load(respobject.getJSONObject("user").getString("foto")).apply(options).into(profileImage);
                                Glide.with(getContext()).load(respobject.getJSONObject("absensi").getString("foto_masuk")).apply(options).into(ivSignIn);
                                Glide.with(getContext()).load(respobject.getJSONObject("absensi").getString("foto_pulang")).apply(options).into(imageView);

                                skeleton.showOriginal();

                            }
//                            Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                                hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response Error", SPHERE_URL + " error " + error.toString());
//                                hideProgressDialog();
                    }
                });
        queue.add(jsObjRequest);
    }
}