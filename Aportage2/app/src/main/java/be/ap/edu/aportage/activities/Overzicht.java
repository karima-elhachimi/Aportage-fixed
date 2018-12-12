package be.ap.edu.aportage.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import be.ap.edu.aportage.R;
import be.ap.edu.aportage.interfaces.ApiContract;
import be.ap.edu.aportage.interfaces.MongoCollections;
import be.ap.edu.aportage.managers.MongoManager;
import be.ap.edu.aportage.managers.MyDatamanger;

public class Overzicht extends Activity {


    private ImageView iv_scannen_bg;
    private ImageView iv_zoeken_bg;
    private String TAG = Overzicht.class.toString();

    private MyDatamanger dataManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overzicht);

        this.dataManager = MyDatamanger.getInstance(this.getApplicationContext());

        iv_scannen_bg = findViewById(R.id.iv_scannen_bg);
        iv_zoeken_bg = findViewById(R.id.iv_zoeken_bg);

        final Activity activity = this;
        iv_scannen_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ScanLokaal.class);
                startActivity(intent);
            }
        });
        iv_zoeken_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, Campussen.class);
                startActivity(intent);
            }
        });

        loadAllData();
    }

    public void loadAllData(){
        JsonArrayRequest campusReq = this.dataManager.createGetRequest(ApiContract.createCollectionUrl(MongoCollections.CAMPUSSEN ), MongoCollections.CAMPUSSEN, null);
        JsonArrayRequest verdiepReq = this.dataManager.createGetRequest(ApiContract.createCollectionUrl(MongoCollections.VERDIEPEN ), MongoCollections.VERDIEPEN, null);
        JsonArrayRequest lokaalReq = this.dataManager.createGetRequest(ApiContract.createCollectionUrl(MongoCollections.LOKALEN), MongoCollections.LOKALEN, null);
        //JsonArrayRequest meldingenReq = this.dataManager.createGetRequest(ApiContract.createCollectionUrl(MongoCollections.MELDINGEN), MongoCollections.MELDINGEN, null);


        this.dataManager.addToRequestQueue(campusReq);
        this.dataManager.addToRequestQueue(verdiepReq);
        this.dataManager.addToRequestQueue(lokaalReq);



    }

}
