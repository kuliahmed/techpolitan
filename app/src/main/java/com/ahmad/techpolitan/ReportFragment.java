package com.ahmad.techpolitan;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ahmad.techpolitan.tableview.TableViewAdapter;
import com.ahmad.techpolitan.tableview.TableViewListener;
import com.ahmad.techpolitan.tableview.TableViewModel;
import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.pagination.Pagination;

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
    TableViewModel tableViewModel;

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
        tvDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu dropDownMenu = new PopupMenu(getActivity().getApplicationContext(), tvDateStart);
                dropDownMenu.getMenuInflater().inflate(R.menu.menu_years, dropDownMenu.getMenu());
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        //Toast.makeText(getActivity().getApplicationContext(), "Kamu memilih bulan " + menuItem.getTitle(), Toast.LENGTH_LONG).show();
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
                        tvDateEnd.setText(menuItem.getTitle());
                        return true;
                    }
                });
                dropDownMenu.show();
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