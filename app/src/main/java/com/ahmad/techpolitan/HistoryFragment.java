package com.ahmad.techpolitan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmad.techpolitan.adapter.ItemAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private ArrayList<HistoryObject> itemList;
    private Skeleton skeleton;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(new ItemAdapter(initData(), getContext()));
        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.row_item_history);

        skeleton.showSkeleton();

        try {
            showDataHistory();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;

    }

    private void showDataHistory() throws JSONException {
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String myID = sharedpreferences.getString(LoginActivity.MY_ID, "4");
        JSONObject obj = new JSONObject();
        obj.put("id", myID);

        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String SPHERE_URL = "https://nachoscloth.xyz/api/list-absen";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SPHERE_URL, obj, response -> {

            try {

                if (response.getBoolean("status")) {

                    JSONArray attentionList = response.getJSONArray("data");
                    List<HistoryObject> historyObjectList = new ArrayList<>();


                    for (int i = 0; i < attentionList.length(); i++) {
                        JSONObject object = attentionList.getJSONObject(i);
                        String timeSignIn = object.getString("jam_masuk");
                        String timeSignOut = object.getString("jam_pulang");
                        String status = (!timeSignIn.equals("-") && !timeSignOut.equals("")) ? "APPROVED" : "-";
                        HistoryObject historyObject = new HistoryObject(status, timeSignIn, timeSignOut, object.getString("lokasi_masuk"), object.getString("lokasi_pulang"), object.getString("tipe_absen"), object.getJSONObject("user").getString("nama"), object.getJSONObject("user").getString("nip"), object.getString("tanggal"));
                        historyObjectList.add(historyObject);
                    }

                    recyclerView.setAdapter(new ItemAdapter(historyObjectList, getContext()));
                    skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.row_item_history);
                    skeleton.showOriginal();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Log.e("Response Error", SPHERE_URL + " error " + error.toString()));

        queue.add(request);

    }

    private List<HistoryObject> initData() {
        itemList = HistoryObject.createHistoryObjects(6);
        return itemList;
    }
}