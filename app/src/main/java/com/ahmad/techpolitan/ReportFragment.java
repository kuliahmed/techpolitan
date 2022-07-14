package com.ahmad.techpolitan;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.ahmad.techpolitan.tableview.TableViewAdapter;
import com.ahmad.techpolitan.tableview.TableViewListener;
import com.ahmad.techpolitan.tableview.TableViewModel;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.pagination.Pagination;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TableView mTableView;
    private Pagination mPagination;
    private TextView tvNext;
    private TextView tvPrev;
    private TextView tvPageMesage;
    private TextView tvDateEnd;
    private TextView tvDateStart;
    private CardView cvDownloads;
    private String month = "";
    private String year = "";
    TableViewModel tableViewModel;

    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    public ReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
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
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        mTableView = view.findViewById(R.id.tableview);
        tvNext = view.findViewById(R.id.tvNext);
        tvPrev = view.findViewById(R.id.tvPrev);
        tvPageMesage = view.findViewById(R.id.tvPageMesage);
        tvDateStart = view.findViewById(R.id.tvDateStart);
        cvDownloads = view.findViewById(R.id.cvDownload);
        tvDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu dropDownMenu = new PopupMenu(getActivity().getApplicationContext(), tvDateStart);
                dropDownMenu.getMenuInflater().inflate(R.menu.menu_years, dropDownMenu.getMenu());
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        //Toast.makeText(getActivity().getApplicationContext(), "Kamu memilih bulan " + menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        Log.d("ReportFragment", "Tahun " + menuItem.getTitle());
                        year = menuItem.getTitle().toString();
                        tvDateStart.setText(menuItem.getTitle());
                        return true;
                    }
                });
                dropDownMenu.show();
            }
        });

        tvDateEnd = view.findViewById(R.id.tvDateEnd);
        tvDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu dropDownMenu = new PopupMenu(getActivity().getApplicationContext(), tvDateEnd);
                dropDownMenu.getMenuInflater().inflate(R.menu.dropdown_menu, dropDownMenu.getMenu());
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        //Toast.makeText(getActivity().getApplicationContext(), "Kamu memilih bulan " + menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        Log.d("ReportFragment", "Bulan " + menuItem.getItemId());
                        month = menuItem.getTitle().toString();
                        tvDateEnd.setText(menuItem.getTitle());
                        return true;
                    }
                });
                dropDownMenu.show();
            }
        });

        SharedPreferences sharedpreferences = getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String myID = sharedpreferences.getString(LoginActivity.MY_ID, "4");
        cvDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog = new ProgressDialog(getContext());
                pDialog.setMessage("Report sedang di unduh. mohon tunggu...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.setCancelable(true);

                if (year.isEmpty() && month.isEmpty()) {
                    Toast.makeText(getContext(), "Mohon pilih tahun dan bulan", Toast.LENGTH_LONG).show();
                } else {
                    new DownloadFileFromURL().execute("https://nachoscloth.xyz/api/download-absen?id=" + myID + "&tahun=" + year + "&bulan=" + month);
                }

            }
        });

        initializeTableView();

        tvPrev.setOnClickListener(view1 -> previousTablePage());
        tvNext.setOnClickListener(view1 -> nextTablePage());

        // Create an instance for the TableView pagination and pass the created TableView.
        mPagination = new Pagination(mTableView);

        // Sets the pagination listener of the TableView pagination to handle
        // pagination actions. See onTableViewPageTurnedListener variable declaration below.
        mPagination.setOnTableViewPageTurnedListener(onTableViewPageTurnedListener);
        tvPrev.performClick();

        return view;
    }

    private void initializeTableView() {
// Create TableView View model class  to group view models of TableView
        tableViewModel = new TableViewModel();

        // Create TableView Adapter
        TableViewAdapter tableViewAdapter = new TableViewAdapter(tableViewModel);

        mTableView.setAdapter(tableViewAdapter);
        mTableView.setTableViewListener(new TableViewListener(mTableView));

        // Create an instance of a Filter and pass the TableView.
        //mTableFilter = new Filter(mTableView);

        // Load the dummy data to the TableView
        tableViewAdapter.setAllItems(tableViewModel.getColumnHeaderList(), tableViewModel
                .getRowHeaderList(), tableViewModel.getCellList());
    }

    // The following four methods below: nextTablePage(), previousTablePage(),
    // goToTablePage(int page) and setTableItemsPerPage(int itemsPerPage)
    // are for controlling the TableView pagination.
    public void nextTablePage() {
        if (mPagination != null) {
            mPagination.nextPage();
        }
    }

    public void previousTablePage() {
        if (mPagination != null) {
            mPagination.previousPage();
        }
    }

    public void setTableItemsPerPage(int itemsPerPage) {
        if (mPagination != null) {
            mPagination.setItemsPerPage(itemsPerPage);
        }
    }

    /**
     * Background Async Task to download file
     */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);


                File folder = new File(Environment.getExternalStorageDirectory() + "/Download/Techpolitan/Report/");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                File file = new File(folder, "REPORT_ABSEN_" + month.toUpperCase() + "_" + year.toUpperCase() + ".csv");

                // Output stream
                OutputStream output = new FileOutputStream(file, true);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                Toast.makeText(getContext(), "Report berhasil di unduh", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Report gagal di unduh", Toast.LENGTH_LONG).show();
                    });
                }
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            pDialog.dismiss();

        }

    }


    // Handler for the changing of pages in the paginated TableView.
    @NonNull
    private final Pagination.OnTableViewPageTurnedListener onTableViewPageTurnedListener = new
            Pagination.OnTableViewPageTurnedListener() {
                @Override
                public void onPageTurned(int numItems, int itemsStart, int itemsEnd) {
                    int currentPage = mPagination.getCurrentPage();
                    int pageCount = mPagination.getPageCount();
                    tvPrev.setVisibility(View.VISIBLE);
                    tvNext.setVisibility(View.VISIBLE);
                    Log.d("CurrentPage", String.valueOf(currentPage));

                    if (currentPage == 1 && pageCount == 1) {
                        tvPrev.setVisibility(View.INVISIBLE);
                        tvNext.setVisibility(View.INVISIBLE);
                    }

                    if (currentPage == 1) {
                        tvPrev.setVisibility(View.INVISIBLE);
                    }

                    if (currentPage == pageCount) {
                        tvNext.setVisibility(View.INVISIBLE);
                    }

                    tvPageMesage.setText(getString(R.string.table_pagination_details, String.valueOf(itemsStart + 1),
                            String.valueOf(itemsEnd + 1), String.valueOf(tableViewModel.getRowHeaderList().size())));
                }
            };
}