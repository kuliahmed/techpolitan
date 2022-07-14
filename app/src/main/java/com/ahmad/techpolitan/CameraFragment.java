package com.ahmad.techpolitan;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ahmad.techpolitan.adapter.ItemAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.gms.location.LocationRequest;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.patloew.rxlocation.RxLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {
    static {
        System.loadLibrary("opencv_java4");
    }

    private String TAG = "CameraFragment";
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private int lastSeenFaces = 0;
    private Mat mRgba;
    private Mat mGray;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    private boolean isLoaderSucceed = false;

    private CameraBridgeViewBase mOpenCvCameraView;
    private ProgressBar progressBar;
    private ConstraintLayout constraintAbsen;
    private AppCompatTextView warningText;
    private BaseLoaderCallback mLoaderCallback = null;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView tvLocation;
    private TextView tvDate;
    private CardView cvSignIn;
    private CardView cvSignOut;
    private Handler imageHandler;
    private Bitmap croppedBitmap;
    ObjectAnimator animator;
    View scannerLayout;
    View scannerBar;
    int selectedType = 0;
    int typeWFH = 0;
    int typeWFO = 1;
    int typeVisit = 2;
    Context context;
    double longitude = 0.0;
    double latitude = 0.0;
    String tipe = "VISIT";
    String jenis = "";
    String lokasi = "";
    String imageBase64 = "";
    SharedPreferences sharedpreferences;
    AlertDialog dialog;
    boolean isSignIn;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
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
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        TextView tvWFH = view.findViewById(R.id.tvWFH);
        TextView tvWFO = view.findViewById(R.id.tvWFO);
        TextView tvVisit = view.findViewById(R.id.tvVisit);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvDate = view.findViewById(R.id.tvDate);
        cvSignIn = view.findViewById(R.id.cvSignIn);
        cvSignOut = view.findViewById(R.id.cvSignOut);
        progressBar = view.findViewById(R.id.progressBar);
        constraintAbsen = view.findViewById(R.id.constraintAbsen);
        warningText = view.findViewById(R.id.warningText);
        context = view.getContext();
        sharedpreferences = getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);

        tvWFH.setOnClickListener(view13 -> {
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvWFH.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.outline_blue_button_selected));
                tvWFH.setTextColor(ContextCompat.getColor(context, R.color.white));

                tvWFO.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.outline_blue_button));
                tvWFO.setTextColor(ContextCompat.getColor(context, R.color.purple_500));

                tvVisit.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.outline_blue_button));
                tvVisit.setTextColor(ContextCompat.getColor(context, R.color.purple_500));
            } else {
                tvWFH.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_blue_button_selected));
                tvWFH.setTextColor(ContextCompat.getColor(context, R.color.white));

                tvWFO.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_blue_button));
                tvWFO.setTextColor(ContextCompat.getColor(context, R.color.purple_500));

                tvVisit.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_blue_button));
                tvVisit.setTextColor(ContextCompat.getColor(context, R.color.purple_500));
            }

            tipe = "WFH";

        });

        tvWFO.setOnClickListener(view12 -> {
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvWFH.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.outline_blue_button));
                tvWFH.setTextColor(ContextCompat.getColor(context, R.color.purple_500));

                tvWFO.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.outline_blue_button_selected));
                tvWFO.setTextColor(ContextCompat.getColor(context, R.color.white));

                tvVisit.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.outline_blue_button));
                tvVisit.setTextColor(ContextCompat.getColor(context, R.color.purple_500));
            } else {
                tvWFH.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_blue_button));
                tvWFH.setTextColor(ContextCompat.getColor(context, R.color.purple_500));

                tvWFO.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_blue_button_selected));
                tvWFO.setTextColor(ContextCompat.getColor(context, R.color.white));

                tvVisit.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_blue_button));
                tvVisit.setTextColor(ContextCompat.getColor(context, R.color.purple_500));
            }

            tipe = "WFO";

        });

        tvVisit.setOnClickListener(view1 -> {
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvWFH.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.outline_blue_button));
                tvWFH.setTextColor(ContextCompat.getColor(context, R.color.purple_500));

                tvWFO.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.outline_blue_button));
                tvWFO.setTextColor(ContextCompat.getColor(context, R.color.purple_500));

                tvVisit.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.outline_blue_button_selected));
                tvVisit.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                tvWFH.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_blue_button));
                tvWFH.setTextColor(ContextCompat.getColor(context, R.color.purple_500));

                tvWFO.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_blue_button));
                tvWFO.setTextColor(ContextCompat.getColor(context, R.color.purple_500));

                tvVisit.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_blue_button_selected));
                tvVisit.setTextColor(ContextCompat.getColor(context, R.color.white));
            }

            tipe = "VISIT";

        });
        return view;
    }

    private void initDateText() {
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = MonthParser.getMonthByNumber(calendar.get(Calendar.MONTH));
        String date = String.valueOf(calendar.get(Calendar.DATE));
        String day = DayParser.getDayByNumber(calendar.get(Calendar.DAY_OF_WEEK));
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        String seconds = String.valueOf(calendar.get(Calendar.SECOND));
        if (hour.length() == 1)
            hour = "0" + hour;
        if (minute.length() == 1)
            minute = "0" + minute;
        if (seconds.length() == 1)
            seconds = "0" + seconds;
        tvDate.setText(day + " " + date + " " + month + " " + year + " - " + hour + ":" + minute + ":" + seconds);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDateText();
        imageHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.obj == "IMAGE") {
                    Canvas canvas = new Canvas();
                    canvas.setBitmap(croppedBitmap);
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    LayoutInflater inflater = LayoutInflater.from(requireContext());
                    View inflateView = inflater.inflate(R.layout.dialog_image_cropped, null);
                    builder.setView(inflateView);
                    ImageView imageView = inflateView.findViewById(R.id.imageView);
                    CardView cvAttend = inflateView.findViewById(R.id.cvAttend);
                    TextView tvAttend = inflateView.findViewById(R.id.tvAttend);
                    ProgressBar pbAttend = inflateView.findViewById(R.id.pbAttend);

                    tvAttend.setText(jenis);

                    imageView.setImageBitmap(croppedBitmap);
                    dialog = builder.create();
                    dialog.show();

                    cvAttend.setOnClickListener(v -> {
                        pbAttend.setVisibility(View.VISIBLE);
                        tvAttend.setVisibility(View.GONE);
                        try {
                            doAttendance(new CallbackAttend() {
                                @Override
                                public void onSuccessAttend(String message) {
                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                    pbAttend.setVisibility(View.GONE);
                                    tvAttend.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailureAttend(String reason) {
                                    Toast.makeText(getContext(), reason, Toast.LENGTH_LONG).show();
                                    pbAttend.setVisibility(View.GONE);
                                    tvAttend.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            pbAttend.setVisibility(View.GONE);
                            tvAttend.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }
                    });
                }
            }
        };
        mOpenCvCameraView = view.findViewById(R.id.surfaceViewCamera);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(1);
        mLoaderCallback = new BaseLoaderCallback(requireContext()) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                        try {
                            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                            File cascadeDir = requireActivity().getDir("cascade", Context.MODE_PRIVATE);
                            mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                            FileOutputStream os = new FileOutputStream(mCascadeFile);

                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }
                            is.close();
                            os.close();

                            mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                            if (mJavaDetector.empty()) {
                                mJavaDetector = null;
                                cascadeDir.delete();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mOpenCvCameraView.enableView();
                        break;
                    default: {
                        super.onManagerConnected(status);
                    }
                    break;
                }
            }
        };

        showWarningText();

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        } else {
            getUserLocation();
            mOpenCvCameraView.setCameraPermissionGranted();
            constraintAbsen.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            warningText.setVisibility(View.GONE);
        }
        scannerLayout = view.findViewById(R.id.scannerLayout);
        scannerBar = view.findViewById(R.id.scannerBar);

        animator = null;

        ViewTreeObserver vto = scannerLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                scannerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    scannerLayout.getViewTreeObserver().
                            removeGlobalOnLayoutListener(this);

                } else {
                    scannerLayout.getViewTreeObserver().
                            removeOnGlobalLayoutListener(this);
                }

                float destination = (float) (scannerLayout.getY() +
                        scannerLayout.getHeight());

                animator = ObjectAnimator.ofFloat(scannerBar, "translationY",
                        scannerLayout.getY(),
                        destination);

                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(3000);
                animator.start();

            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        RxLocation rxLocation = new RxLocation(context);
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000);

        rxLocation.location().updates(locationRequest)
                .flatMap(location -> rxLocation.geocoding().fromLocation(location).toObservable())
                .subscribe(address -> {
                    tvLocation.setText(address.getAddressLine(0));
                    lokasi = address.getAddressLine(0);
                    latitude = address.getLatitude();
                    longitude = address.getLongitude();
                });
    }

    private void showWarningText() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            warningText.setVisibility(View.VISIBLE);
            warningText.setText("Harap beri akses aplikasi untuk akses lokasi");
        }
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
            warningText.setVisibility(View.VISIBLE);
            warningText.setText("Harap beri akses aplikasi untuk akses kamera");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0 && grantResults[0] == 0 && grantResults[1] == 0 && grantResults[2] == 0) {
                    constraintAbsen.setVisibility(View.VISIBLE);
                    mOpenCvCameraView.setCameraPermissionGranted();
                    progressBar.setVisibility(View.GONE);
                    warningText.setVisibility(View.GONE);
                    getUserLocation();
                } else {
                    showWarningText();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, requireContext(), mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.d(TAG, "CameraView has started!");
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mJavaDetector = null;
    }

    @Override
    public void onCameraViewStopped() {
        Log.d(TAG, "CameraView has stopped!");
        mGray.release();
        mRgba.release();
    }

    public Mat featureJavaDetector(Mat mRgba2, final Mat Gray) {
        String myID = sharedpreferences.getString(LoginActivity.MY_ID, "4");
        isSignIn = sharedpreferences.getBoolean(myID + "SIGNIN", false);
        if (mAbsoluteFaceSize == 0) {
            int height = Gray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }
        MatOfRect faces = new MatOfRect();
        mJavaDetector.detectMultiScale(Gray, faces, 1.1, 2, 2,
                new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 0) {
            Log.d(TAG, "Face(s) have been detected.");
            lastSeenFaces = facesArray.length;

            Log.d("CameraFragment", "is Sign in " + isSignIn);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    cvSignIn.setEnabled(!isSignIn);
                    cvSignIn.setClickable(!isSignIn);
                    cvSignOut.setEnabled(isSignIn);
                    cvSignOut.setClickable(isSignIn);
                    cvSignIn.setCardBackgroundColor(context.getResources().getColor((!isSignIn) ? R.color.green : R.color.material_gray_1));
                    cvSignOut.setCardBackgroundColor(context.getResources().getColor((isSignIn) ? R.color.red : R.color.material_gray_1));

                    cvSignIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Matrix matrix = new Matrix();
                            matrix.postRotate(90);
                            Mat croppedMat = new Mat();
                            croppedMat = mRgba;
                            croppedBitmap = Bitmap.createBitmap(croppedMat.width(), croppedMat.height(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(croppedMat, croppedBitmap);
                            Bitmap rotatedBitmap = Bitmap.createBitmap(croppedBitmap, 0, 0, croppedBitmap.getWidth(), croppedBitmap.getHeight(), matrix, true);
                            croppedBitmap = rotatedBitmap;
                            Message msg = new Message();
                            String textTochange = "IMAGE";
                            msg.obj = textTochange;
                            imageHandler.sendMessage(msg);
                            jenis = "Masuk";
                            imageBase64 = bitmapToBase64(resizeBitmap(croppedBitmap));

                            if (mOpenCvCameraView != null) {
                                mOpenCvCameraView.disableView();
                                mJavaDetector = null;
                            }

                        }
                    });

                    cvSignOut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(90);
                            Mat croppedMat = new Mat();
                            croppedMat = mRgba;
                            croppedBitmap = Bitmap.createBitmap(croppedMat.width(), croppedMat.height(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(croppedMat, croppedBitmap);
                            Bitmap rotatedBitmap = Bitmap.createBitmap(croppedBitmap, 0, 0, croppedBitmap.getWidth(), croppedBitmap.getHeight(), matrix, true);
                            croppedBitmap = resizeBitmap(rotatedBitmap);
                            Message msg = new Message();
                            String textTochange = "IMAGE";
                            msg.obj = textTochange;
                            imageHandler.sendMessage(msg);
                            jenis = "Keluar";
                            imageBase64 = bitmapToBase64(croppedBitmap);

                            if (mOpenCvCameraView != null) {
                                mOpenCvCameraView.disableView();
                                mJavaDetector = null;
                            }

                        }
                    });

                });
            }

        } else {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cvSignIn.setEnabled(false);
                        cvSignIn.setClickable(false);
                        cvSignOut.setEnabled(false);
                        cvSignOut.setClickable(false);
                        cvSignIn.setCardBackgroundColor(context.getResources().getColor(R.color.material_gray_1));
                        cvSignOut.setCardBackgroundColor(context.getResources().getColor(R.color.material_gray_1));
                        cvSignIn.setOnClickListener(null);
                        cvSignOut.setOnClickListener(null);
                    }
                });
            }
        }
        // Kalau mau ilangin penanda kotak hijau di wajah nya , hapus looping for di bawah , kalau mau ubah warna penanda kotak nnya ubah variabel FACE_RECT_COLOR dan masukin kode warna nya
//        for (int i = 0; i < facesArray.length; i++) {
//            Imgproc.rectangle(mRgba2, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
//        }
        return mRgba2;
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
        return resizedBitmap;
    }

    private void doAttendance(CallbackAttend callbackAttend) throws JSONException {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String myID = sharedpreferences.getString(LoginActivity.MY_ID, "4");
        JSONObject obj = new JSONObject();
        obj.put("id", myID);
        obj.put("tipe", tipe);
        obj.put("jenis", jenis);
        obj.put("lokasi", lokasi);
        obj.put("longitude", String.valueOf(longitude));
        obj.put("latitude", String.valueOf(latitude));
        obj.put("foto", imageBase64);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String SPHERE_URL = "https://nachoscloth.xyz/api/absen";

        Log.d("Request Object", "onRequest: " + obj.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SPHERE_URL, obj, response -> {

            Log.d("Response Object", "onResponse: " + response.toString());
            try {
                if (response.getBoolean("status")) {
                    Toast.makeText(getContext(), "berhasil", Toast.LENGTH_SHORT).show();
                    editor.putBoolean(myID + "SIGNIN", jenis.toLowerCase().contains("masuk"));
                    editor.apply();
                    callbackAttend.onSuccessAttend("Absen " + jenis + " berhasil");
                } else {
                    callbackAttend.onFailureAttend("Absen " + jenis + " gagal");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                callbackAttend.onFailureAttend("Absen " + e.getMessage());
            }

        }, error -> callbackAttend.onFailureAttend("Absen " + error.getMessage()));

        queue.add(request);

    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String base64Image = "data:image/png;base64," + encoded;
        String replaceLineBase64 = base64Image.replace("\n", "").replace("\r", "");
        return replaceLineBase64;
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        Core.flip(inputFrame.gray().t(), mGray, 0);
        Core.flip(inputFrame.rgba().t(), mRgba, 1);
        mRgba = featureJavaDetector(mRgba, mGray);
        Core.flip(mRgba.t(), mRgba, 1);
        return mRgba;
    }

    private interface CallbackAttend {
        void onSuccessAttend(String message);

        void onFailureAttend(String reason);
    }
}