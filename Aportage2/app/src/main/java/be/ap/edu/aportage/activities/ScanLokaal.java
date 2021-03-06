package be.ap.edu.aportage.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonRequest;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.json.JSONObject;

import be.ap.edu.aportage.R;
import be.ap.edu.aportage.interfaces.IVolleyCallback;
import be.ap.edu.aportage.managers.MyDatamanger;
import be.ap.edu.aportage.managers.MyMailManager;

public class ScanLokaal extends AppCompatActivity  implements SurfaceHolder.Callback, Detector.Processor  {

    private String LOG_TAG = this.getClass().toString();
    private SurfaceView cameraView;
    private TextView txtView;
    private CameraSource cameraSource;
    private Button btn_ok;
    private Button btn_annuleren;
    private String[] gelezenTekst;

    private String s_campusAfk;
    private String s_verdiepNr;
    private String s_lokaalNr;
    private Intent uitgaandeIntent;

    private MyDatamanger datamanger;


    public boolean exist;

    private MyMailManager mailManager;


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanlokaal);
        this.cameraView = (SurfaceView) findViewById(R.id.surface_view);
        this.txtView = (TextView) findViewById(R.id.txtview);
        this.datamanger = MyDatamanger.getInstance(this.getApplicationContext());

        initTextRecognizer();

        this.btn_ok = findViewById(R.id.btn_ocr_ok);
        this.btn_annuleren = findViewById(R.id.btn_ocr_annuleer);
        final Activity activity = this;

        this.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lokaalInfo = gelezenTekst[gelezenTekst.length-1].toUpperCase();

                if (lokaalInfo.equals("")) {
                    Toast.makeText(ScanLokaal.this, "Lokaal bestaat niet", Toast.LENGTH_LONG).show();
                } else {
                    haalCampusVerdiepLokaalDataUitGelezenString(gelezenTekst[gelezenTekst.length - 1]);
                    Log.d("testLokaalInfo", lokaalInfo);

                    checkLokaal();
                }
            }
        });

        btn_annuleren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScanLokaal.this, Overzicht.class);
                startActivity(intent);
                ScanLokaal.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Overzicht.class);
        startActivity(intent);
        ScanLokaal.this.finish();
    }

    public void initTextRecognizer() {
        TextRecognizer txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!txtRecognizer.isOperational()) {
            Log.e("ScanLokaal", "Detector dependencies are not yet available");
        } else {
            this.cameraSource = new CameraSource.Builder(getApplicationContext(), txtRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            this.cameraView.getHolder().addCallback(this);
            txtRecognizer.setProcessor(this);
        }
    }

    private void gaNaarMeldingen() {
        this.uitgaandeIntent = new Intent(this, Meldingen.class);
        this.uitgaandeIntent.putExtra(getString(R.string.campus_intent), this.s_campusAfk);
        this.uitgaandeIntent.putExtra(getString(R.string.verdieping_intent), this.s_verdiepNr);
        this.uitgaandeIntent.putExtra(getString(R.string.lokaal_intent), this.s_lokaalNr);
        startActivity(this.uitgaandeIntent);
        ScanLokaal.this.finish();
    }


    private void checkLokaal() {
        JsonRequest jsonObjectRequest = this.datamanger.checkLokaalExists(s_campusAfk, s_verdiepNr, s_lokaalNr, new IVolleyCallback() {
            @Override
            public void onCustomSuccess(Object data) {
                gaNaarMeldingen();
            }

            @Override
            public void onPostSuccess(JSONObject response) {

                //ignore
            }

            @Override
            public void onFailure() {
                //todo: maak popup/bericht zichtbaar dat het lokaal niet bestaat.
                Toast.makeText(ScanLokaal.this, "Lokaal bestaat niet", Toast.LENGTH_LONG).show();
            }
        });
        this.datamanger.addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},1);
//                return;
            }
            cameraSource.start(cameraView.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraSource.stop();
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections detections) {


        SparseArray items = detections.getDetectedItems();
        final StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < items.size(); i++)
        {
            TextBlock item = (TextBlock)items.valueAt(i);
            strBuilder.append(item.getValue());

        }
        Log.v("strBuilder.toString()", strBuilder.toString());
        gelezenTekstDelimiteren(strBuilder.toString());

        txtView.post(new Runnable() {
            @Override
            public void run() {
                txtView.setText(strBuilder.toString());
            }

        });
    }


    public void gelezenTekstDelimiteren(String teSplittenString) {
        String delimiter = "/";
        this.gelezenTekst = teSplittenString.split(delimiter);

        Log.v("gelezen tekst", this.gelezenTekst[gelezenTekst.length-1]);
    }

    public void haalCampusVerdiepLokaalDataUitGelezenString(String gelezenText){
        //todo_done: omzetten van lokaalinfo in 3 extras: afk, verdiepnr, lokaalnr
        //todo_done: als er geen correct lokaal kon worden gelezen nadat user op "doorsturen" klikt -> popup tonen en naar zoeken redirecten
        Log.d(LOG_TAG + "maakAparteExtras", gelezenText );
        try {
            String[] individueleWoorden = gelezenText.split("[^\\w\\-]+|--+");
            Log.d(LOG_TAG + "split words", individueleWoorden[0]);
            this.s_campusAfk = individueleWoorden[1];
            this.s_verdiepNr = individueleWoorden[2];
            this.s_lokaalNr = individueleWoorden[3];
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            Toast.makeText(ScanLokaal.this, "Er werd geen tekst herkend, probeer het lokaal manueel te zoeken.", Toast.LENGTH_LONG).show();
//            gaNaarCampussenActivity();
        }
    }

    private void gaNaarCampussenActivity() {
        this.uitgaandeIntent = new Intent(ScanLokaal.this, Campussen.class);
        this.uitgaandeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(this.uitgaandeIntent);
    }
}
