package be.ap.edu.aportage;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Melding extends Activity {

    Intent binnenkomendeIntent;
    Intent uitgaandeIntent;
    Activity activity;
    Button btn_campus_afk;
    Button btn_verdiep_nr;
    Button btn_melding_lokaalnr;
    FloatingActionButton nieuweMeldingFab;

    private String s_campus;
    private String s_verdieping;
    private String s_lokaal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melding);

        this.binnenkomendeIntent = getIntent();
        this.btn_campus_afk = findViewById(R.id.btn_campus_afk);
        this.btn_verdiep_nr = findViewById(R.id.btn_verdiep_nr);
        this.btn_melding_lokaalnr = findViewById(R.id.btn_melding_lokaalnr);
        this.nieuweMeldingFab = findViewById(R.id.melding_fab);


        checkBundleForData();
        registreerButtonOnClicks();

    }

    private void registreerButtonOnClicks() {
        this.btn_melding_lokaalnr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gaNaarLokalen();
            }
        });
        this.btn_verdiep_nr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gaNaarVerdiepen();
            }
        });
        this.btn_campus_afk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gaNaarCampussen();
            }
        });
        this.nieuweMeldingFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gaNaarScanMelding();
            }
        });
    }

    private void checkBundleForData() {
        Bundle b = this.binnenkomendeIntent.getExtras();
        if(b.get("lokaalInfo") != null)
        {
            String j = (String) b.get("lokaalInfo");
            lokaalButtonsOpvullen(j);
            Log.v("Meldingen", j);
        }

    }

    private void lokaalButtonsOpvullen(String lokaalInfo) {
        try {
            lokaalInfo = lokaalInfo.replace("LOKAAL", "");
            lokaalInfo = lokaalInfo.replace(" ", "");
            lokaalInfo = lokaalInfo.replace(".", "");
        } catch (NullPointerException e) {
            Log.e("Error",e.toString());
        }

        try {
            if (lokaalInfo == null) throw new AssertionError();
            this.s_campus = lokaalInfo.substring(0,3);
            lokaalInfo = lokaalInfo.substring(3, lokaalInfo.length());
            this.s_lokaal = lokaalInfo.substring(lokaalInfo.length()-3,lokaalInfo.length());
            lokaalInfo = lokaalInfo.substring(0,lokaalInfo.length()-3);
            this.s_verdieping = lokaalInfo;
            this.btn_campus_afk.setText(s_campus);
            this.btn_verdiep_nr.setText(s_verdieping);
            this.btn_melding_lokaalnr.setText(s_lokaal);
        } catch (StringIndexOutOfBoundsException e) {
            Log.e("Error",e.toString());
            Intent intent = new Intent(this, Overzicht.class);
            startActivity(intent);
        }
    }


    private void gaNaarScanMelding(){
        Intent intent = new Intent(this, ScanMelding.class);
        intent.putExtra("lokaalInfo", this.s_campus+ this.s_verdieping+ this.s_lokaal);
        startActivity(intent);
    }

    private void gaNaarLokalen() {

        this.uitgaandeIntent = new Intent(this, Lokalen.class);
        this.uitgaandeIntent.putExtra("verdiep", this.btn_verdiep_nr.getText());
        this.uitgaandeIntent.putExtra("campus", this.btn_campus_afk.getText());
        startActivity(this.uitgaandeIntent);

    }

    private void gaNaarVerdiepen(){

        this.uitgaandeIntent = new Intent(this, Verdiepingen.class);
        this.uitgaandeIntent.putExtra("campus", this.btn_campus_afk.getText());
        startActivity(this.uitgaandeIntent);
    }

    private void gaNaarCampussen(){

        this.uitgaandeIntent = new Intent(this, Campussen.class);
        startActivity(this.uitgaandeIntent);
    }

}
