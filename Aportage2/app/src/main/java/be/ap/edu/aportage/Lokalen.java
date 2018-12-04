package be.ap.edu.aportage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import be.ap.edu.aportage.models.LokalenRecyclerAdapter;
import be.ap.edu.aportage.models.MockDataManager;

public class Lokalen extends AppCompatActivity {

    private MockDataManager datamanger = MockDataManager.getInstance();
    private LokalenRecyclerAdapter lokalenAdapter;
    private RecyclerView lokalenRV;
    private LinearLayoutManager lokaalLM;
    private int[] lokalenLijst;

    String s_campus;
    String s_verdieping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lokalen);

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if(b!=null) {
            s_campus = (String) b.get("campus_afk");
            s_verdieping = (String) b.get("verdiepnr");

            Button btnCampus = findViewById(R.id.btn_campus);
            Button btnVerdieping = findViewById(R.id.btn_verdiep);
            btnCampus.setText(s_campus);
            btnVerdieping.setText(s_verdieping);
        }

        this.lokalenLijst = datamanger.getLokalenLijst(s_campus, Integer.parseInt(s_verdieping));
        this.lokalenRV = (RecyclerView) findViewById(R.id.rv_lokalen);
        this.lokaalLM = new LinearLayoutManager(this);
        this.lokalenRV.setLayoutManager(this.lokaalLM);
        this.lokalenAdapter = new LokalenRecyclerAdapter(this, this.lokalenLijst, s_campus, s_verdieping);
        this.lokalenRV.setAdapter(lokalenAdapter);

    }

}
