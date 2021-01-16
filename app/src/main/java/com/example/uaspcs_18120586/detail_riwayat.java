package com.example.uaspcs_18120586;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class detail_riwayat extends AppCompatActivity {

    TextView tvNamaEvent,tvLiga, tvVenue, tvWaktu, tvHasil;
    ImageView previewImage;
    ImageButton tambahFavorit;

    String strPreviewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_riwayat);

        tvNamaEvent = findViewById(R.id.event);
        tvLiga = findViewById((R.id.liga));
        tvVenue = findViewById(R.id.venue);
        tvWaktu = findViewById(R.id.waktu);
        tvHasil = findViewById(R.id.hasil);
        previewImage = findViewById(R.id.previewImage);
        tambahFavorit = findViewById(R.id.btnTambahFavorit);

        final String id = getIntent().getStringExtra("matchId");

        final SQLiteDatabase mydatabase = openOrCreateDatabase("database_0586",MODE_PRIVATE,null);

        tambahFavorit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cursor resultSet = mydatabase.rawQuery(String.format("SELECT * FROM favorit WHERE idevent='%s'",id),null);
                resultSet.moveToFirst();
                int size = resultSet.getCount();
                if(size>0){
                    Toast.makeText(getApplicationContext(),
                            "Data Sudah Ada", Toast.LENGTH_SHORT).show();
                }else{
                    mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Favorit(idevent VARCHAR, namaevent VARCHAR, liga VARCHAR, venue VARCHAR, waktu VARCHAR, hasil VARCHAR, image VARCHAR);");
                    mydatabase.execSQL(String.format("INSERT INTO Favorit VALUES('%s','%s','%s','%s','%s','%s','%s');",id,tvNamaEvent.getText(),tvLiga.getText(),tvVenue.getText(),tvWaktu.getText(),tvHasil.getText(),strPreviewImage));
                    Toast.makeText(getApplicationContext(),
                            "Data Masook", Toast.LENGTH_SHORT).show();
                }



            }
        });



        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://www.thesportsdb.com/api/v1/json/1/lookupevent.php?id="+id;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray events = obj.getJSONArray("events");
                            JSONObject e = events.getJSONObject(0);

                            String namaEvent = e.getString("strEvent");
                            String liga = e.getString("strLeague");
                            String venue = e.getString("strVenue");
                            String waktu = e.getString("dateEvent")+" "+e.getString("strTime");
                            String hasil = e.getString("intHomeScore")+" VS "+e.getString("intAwayScore");
                            String image = e.getString("strThumb");
                            strPreviewImage = image;

                            tvNamaEvent.setText(namaEvent);
                            tvLiga.setText(liga);
                            tvVenue.setText(venue);
                            tvWaktu.setText(waktu);
                            tvHasil.setText(hasil);

                            Glide.with(getApplicationContext()).load(image).into(previewImage);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });


        queue.add(stringRequest);

    }
}
