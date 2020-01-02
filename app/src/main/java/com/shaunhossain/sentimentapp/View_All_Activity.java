package com.shaunhossain.sentimentapp;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class View_All_Activity extends AppCompatActivity {


    ListView listView;

    FirebaseListAdapter adapter;

    SaveData saveData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        BitmapDrawable background = new BitmapDrawable (BitmapFactory.decodeResource(getResources(), R.drawable.bgheader4));
        background.setTileModeX(Shader.TileMode.CLAMP);
        actionBar.setBackgroundDrawable(background);

        saveData = new SaveData();


        listView = (ListView) findViewById(R.id.listview);

        Query query = FirebaseDatabase.getInstance().getReference("Data");


        FirebaseListOptions<SaveData> options = new FirebaseListOptions.Builder<SaveData>()
                .setLayout(R.layout.view_result_listitem)
                .setLifecycleOwner(View_All_Activity.this)
                .setQuery(query, SaveData.class)
                .build();

        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View v, Object model, int position) {

                TextView sentence = v.findViewById(R.id.show_sentence);
                TextView sentiment = v.findViewById(R.id.show_sentiment);

                SaveData data = (SaveData) model;

                sentence.setText(data.getSentence().toString());
                sentiment.setText(data.getSentiment().toString());

            }
        };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DatabaseReference itemRef = adapter.getRef(position);

                Intent intent = new Intent(View_All_Activity.this, View_Details_Activity.class);
                intent.putExtra("item", itemRef.getKey());
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
