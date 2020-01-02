package com.shaunhossain.sentimentapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class View_Details_Activity extends AppCompatActivity {


    String emojiSelect;
    LottieAnimationView animation_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        BitmapDrawable background = new BitmapDrawable (BitmapFactory.decodeResource(getResources(), R.drawable.bgheader4));
        background.setTileModeX(Shader.TileMode.CLAMP);
        actionBar.setBackgroundDrawable(background);


        final String data = getIntent().getExtras().getString("item","defaultKey");


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Data/"+data);


        final TextView sentence = (TextView) findViewById(R.id.sentence);
        final TextView sentiment = (TextView) findViewById(R.id.sentiment);
        animation_view = (LottieAnimationView) findViewById(R.id.animation_view);
        animation_view.playAnimation();
        animation_view.loop(true);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SaveData saveData = dataSnapshot.getValue(SaveData.class);
                sentence.setText(saveData.getSentence().toString());
                sentiment.setText(saveData.getSentiment().toString());
                emojiSelect=saveData.getSentiment().toString();
               selectEmoji();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void selectEmoji(){

        if(!emojiSelect.isEmpty()){

            switch (emojiSelect) {
                case "openness":
                    animation_view.setAnimation(R.raw.openness);
                    break;
                case "agreeness":
                    animation_view.setAnimation(R.raw.agreeness);
                    break;
                case "joy":
                    animation_view.setAnimation(R.raw.joy);
                    break;
                case "analytical":
                    animation_view.setAnimation(R.raw.analytical);
                    break;
                case "sadness":
                    animation_view.setAnimation(R.raw.sadness);
                    break;
                case "fear":
                    animation_view.setAnimation(R.raw.fear);
                    break;
                case "emotional":
                    animation_view.setAnimation(R.raw.emotional);
                    break;
                case "anger":
                    animation_view.setAnimation(R.raw.anger);
                    break;
                case "confident":
                    animation_view.setAnimation(R.raw.confident);
                    break;
                case "tentative":
                    animation_view.setAnimation(R.raw.tentative);
                    break;
                case "extra":
                    animation_view.setAnimation(R.raw.extra);
                    break;
                case "disgust":
                    animation_view.setAnimation(R.raw.disgust);
                    break;
                case "conscientiousness":
                    animation_view.setAnimation(R.raw.conscientiousness);
                    break;

                default:
                    animation_view.setAnimation(R.raw.confident);
                    break;

            }

        }else {

            Toast.makeText(this, "Emoji not found" , Toast.LENGTH_SHORT).show();
        }
    }
}
