package com.shaunhossain.sentimentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


  public class MainActivity extends AppCompatActivity {
      EditText mResultEt;
      ImageView mPreviewIv;
      Button mAnalyze;
      //Button mAddMore;
      FloatingActionButton fab , fabAdd , fabClear;
      Animation fabOpen , fabClose , rotateForward , rotateBackward;

      boolean isOpen = false;

      TextView pagetitle, pagesubtitle;


      String text;
      String toneStr;

      String DisplayResult;

      Animation atg, atgtwo, atgthree;

      private static final int CAMERA_REQUEST_CODE = 200;
      private static final int STORAGE_REQUEST_CODE = 400;
      private static final int IMAGE_PICK_GALLERY_CODE = 1000;
      private static final int IMAGE_PICK_CAMERA_CODE = 1001;


      String[] cameraPermission;
      String[] storagePermission;

      Uri image_uri;

      DatabaseReference databaseReference;

      ToneAnalyst tone = new ToneAnalyst();




      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);
          ActionBar actionBar = getSupportActionBar();
          assert actionBar != null;
          actionBar.setSubtitle("Click + button to insert Image");
          BitmapDrawable background = new BitmapDrawable (BitmapFactory.decodeResource(getResources(), R.drawable.bgheader4));
          background.setTileModeX(Shader.TileMode.CLAMP);
          actionBar.setBackgroundDrawable(background);

          SharedPreferences pref = getApplicationContext().getSharedPreferences("add", MODE_PRIVATE);
          SharedPreferences.Editor AddText = pref.edit();
          AddText.clear();
          AddText.apply();

          atg = AnimationUtils.loadAnimation(this, R.anim.atg);
          atgtwo = AnimationUtils.loadAnimation(this, R.anim.atgtwo);
          atgthree = AnimationUtils.loadAnimation(this, R.anim.atgthree);


          mResultEt  = findViewById(R.id.resultEt);
          mPreviewIv = findViewById(R.id.imageIv);
          mAnalyze = findViewById(R.id.analyzer);
          //mAddMore = findViewById(R.id.addMore);
          pagetitle = findViewById(R.id.pagetitle);
          pagesubtitle = findViewById(R.id.pagesubtitle);

          setDafultImage();

         /* mAddMore.setOnClickListener(new View.OnClickListener(){
              @Override
              public void onClick(final View v){
                  text = mResultEt.getText().toString();
                  if(text.isEmpty()){
                      Toast.makeText(MainActivity.this, "Input the sentence first", Toast.LENGTH_LONG).show();

                  }else {
                      SharedPreferences pref = getApplicationContext().getSharedPreferences("add", MODE_PRIVATE);
                      SharedPreferences.Editor AddText = pref.edit();
                      AddText.clear();
                      AddText.apply();
                      showImageImportDialog();
                      addMoreText();
                  }
              }
          });*/

          mAnalyze.setOnClickListener(new View.OnClickListener(){
              @Override
              public void onClick(final View v){
                  text = mResultEt.getText().toString();
                if(text.isEmpty()){

                    Toast.makeText(MainActivity.this, "Input the sentence first", Toast.LENGTH_LONG).show();

                }else {

                    new Thread(new Runnable () {
                        public void run() {
                            getToneStr();
                            v.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast();
                                    openDialogResult(this);
                                }
                            });

                        }
                    }).start();
                }
              }
          });


          databaseReference = FirebaseDatabase.getInstance().getReference("Data");

          mPreviewIv.startAnimation(atg);
          pagetitle.startAnimation(atgtwo);
          pagesubtitle.startAnimation(atgtwo);

          mAnalyze.startAnimation(atgthree);

          cameraPermission = new String[]{
                  Manifest.permission.CAMERA,
                  Manifest.permission.WRITE_EXTERNAL_STORAGE };

          storagePermission = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE };

          fab = findViewById(R.id.fab);
          fabAdd = findViewById(R.id.fab_add);
          fabClear = findViewById(R.id.fab_clear);

          fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
          fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);
          rotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
          rotateBackward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);

          fab.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  animationFab();

              }
          });

          fabAdd.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  text = mResultEt.getText().toString();
                  if(text.isEmpty()){
                      showImageImportDialog();

                  }else {
                      SharedPreferences pref = getApplicationContext().getSharedPreferences("add", MODE_PRIVATE);
                      SharedPreferences.Editor AddText = pref.edit();
                      AddText.clear();
                      AddText.apply();
                      showImageImportDialog();
                      addMoreText();
                  }
              }
          });

          fabClear.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

                  text = mResultEt.getText().toString();
                  if(text.isEmpty()){
                      Toast.makeText(MainActivity.this, "Scan a image before clear Screen", Toast.LENGTH_LONG).show();

                  }else {
                      SharedPreferences pref = getApplicationContext().getSharedPreferences("add", MODE_PRIVATE);
                      SharedPreferences.Editor AddText = pref.edit();
                      AddText.clear();
                      AddText.apply();
                      mResultEt.setText(null);
                      setDafultImage();
                  }
              }
          });






      }

      private void setDafultImage(){

          Glide.with(this)
                  .load(R.raw.question)
                  .into(mPreviewIv);
      }

      private void animationFab(){

          if(isOpen){
              fab.startAnimation(rotateBackward);
              fabAdd.startAnimation(fabClose);
              fabClear.startAnimation(fabClose);
              fabAdd.setClickable(false);
              fabClear.setClickable(false);
              isOpen=false;

          }else {
              fab.startAnimation(rotateForward);
              fabAdd.startAnimation(fabOpen);
              fabClear.startAnimation(fabOpen);
              fabAdd.setClickable(true);
              fabClear.setClickable(true);
              isOpen=true;
          }
      }

      private void addMoreText() {
          String valueText= mResultEt.getText().toString();
          SharedPreferences pref = getApplicationContext().getSharedPreferences("add", MODE_PRIVATE);
          SharedPreferences.Editor AddText = pref.edit();
          AddText.putString("text",valueText);
          AddText.apply();

      }


      public void getToneStr()
      {

          tone.setText(text);
          toneStr = tone.sendTone();

      }


      public void showToast() {
          Toast.makeText(MainActivity.this, "Wanna add more text !!\nclick on camera button", Toast.LENGTH_LONG).show();

      }




      // Actionbar menu
      @Override
      public boolean onCreateOptionsMenu(Menu menu) {
          // Inflate menu
          getMenuInflater().inflate(R.menu.main_menu, menu);
          return super.onCreateOptionsMenu(menu);
      }


      @SuppressLint("RestrictedApi")
      @Override
      public boolean onOptionsItemSelected(MenuItem item) {
          int id = item.getItemId();
          if (id == R.id.addImage) {
              text = mResultEt.getText().toString();
              if(text.isEmpty()){
                  showImageImportDialog();

              }else {
                  SharedPreferences pref = getApplicationContext().getSharedPreferences("add", MODE_PRIVATE);
                  SharedPreferences.Editor AddText = pref.edit();
                  AddText.clear();
                  AddText.apply();
                  showImageImportDialog();
                  addMoreText();
              }

          }else if (id == R.id.clear) {
              text = mResultEt.getText().toString();
              if(text.isEmpty()){
                  Toast.makeText(MainActivity.this, "Scan a image before clear Screen", Toast.LENGTH_LONG).show();

              }else {
                  SharedPreferences pref = getApplicationContext().getSharedPreferences("add", MODE_PRIVATE);
                  SharedPreferences.Editor AddText = pref.edit();
                  AddText.clear();
                  AddText.apply();
                  mResultEt.setText(null);
                 setDafultImage();
              }

          }else if (id == R.id.save) {
              if(text==null){
                  Toast.makeText(MainActivity.this, "Analyze the sentence first", Toast.LENGTH_LONG).show();
              }else {
                  SaveData();
              }
          }else if (id == R.id.view_all) {
              Intent intent = new Intent(this,View_All_Activity.class);
              startActivity(intent);
          }
          return super.onOptionsItemSelected(item);
      }

      private boolean checkCameraPermission() {

          boolean result  = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
          boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
          return result && result1;
      }
      private boolean checkStoragePermission() {
          return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
      }
      private void pickCamera(){

          ContentValues values = new ContentValues();
          values.put(MediaStore.Images.Media.TITLE, "NewPic");
          values.put(MediaStore.Images.Media.DESCRIPTION, "Image To text");
          image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

          Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
          startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
      }
      private void pickGallery() {

          Intent intent = new Intent(Intent.ACTION_PICK);

          intent.setType("image/*");
          startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
      }
      private void requestCameraPermission() {
          ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
      }
      private void requestStoragePermission() {
          ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
      }
      private void showImageImportDialog() {
          String[] items = {" Camera", " Gallery"};
          AlertDialog.Builder dialog = new AlertDialog.Builder(this);

          dialog.setTitle("Select Image");
          dialog.setItems(items, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  if (which == 0) {

                      if (!checkCameraPermission()) requestCameraPermission(); else pickCamera();
                  }
                  if (which == 1) {

                      if (!checkStoragePermission()) requestStoragePermission(); else pickGallery();
                  }
              }
          });
          dialog.create().show();
      }

      @Override
      public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
          switch (requestCode) {
              case CAMERA_REQUEST_CODE:
                  if (grantResults.length > 0) {
                      boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                      boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                      if (cameraAccepted && writeStorageAccepted) pickCamera(); else Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                  }
                  break;

              case STORAGE_REQUEST_CODE:
                  if (grantResults.length > 0) {
                      boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                      if (writeStorageAccepted) pickGallery(); else Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                  }
                  break;
          }
      }

      // Handle image result
      @SuppressLint("RestrictedApi")
      @Override
      protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

          if (resultCode == RESULT_OK) {
              if (requestCode == IMAGE_PICK_CAMERA_CODE) {

                  CropImage.activity(image_uri)
                          .setGuidelines(CropImageView.Guidelines.ON)
                          .start(this);
              }
              if (requestCode == IMAGE_PICK_GALLERY_CODE) {

                  CropImage.activity(data.getData())
                          .setGuidelines(CropImageView.Guidelines.ON)
                          .start(this);
              }
          }


          if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
              CropImage.ActivityResult result = CropImage.getActivityResult(data);
              if (resultCode == RESULT_OK) {
                  Uri resultUri = result.getUri();


                  mPreviewIv.setImageURI(resultUri);


                  BitmapDrawable bitmapDrawable = (BitmapDrawable)mPreviewIv.getDrawable();
                  Bitmap bitmap = bitmapDrawable.getBitmap();

                  TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                  if (!recognizer.isOperational()) Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                  else {
                      Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                      SparseArray<TextBlock> items = recognizer.detect(frame);
                      StringBuilder stringBuilder = new StringBuilder();

                      for (int i = 0; i < items.size(); i++) {
                          TextBlock myItem = items.valueAt(i);
                          stringBuilder.append(myItem.getValue());
                          stringBuilder.append("\n");
                      }

                      SharedPreferences pref = getApplicationContext().getSharedPreferences("add", MODE_PRIVATE);
                      String addingText=pref.getString("text", null);
                      if (addingText==null){

                          mResultEt.setText(stringBuilder.toString());

                      }else {
                          String newText = addingText + " " + stringBuilder.toString();
                          mResultEt.setText(newText);

                      }


                      String mResult = mResultEt.getText().toString();

                      if(!mResult.isEmpty()){

                         // mAddMore.setVisibility(View.VISIBLE);
                          fab.setVisibility(View.INVISIBLE);

                      }


                  }
              }
              else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                  Exception error = result.getError();
                  Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
              }
          }
      }

      public void openDialogResult(Runnable view) {
          final Dialog dialog = new Dialog(MainActivity.this);
          dialog.setContentView(R.layout.show_result_dialog);
          dialog.setTitle("Sentiment");
          TextView result = dialog.findViewById(R.id.show_result);
          ImageView image = findViewById(R.id.image);
          Button saveButton = dialog.findViewById(R.id.save);
          Button cancelButton = dialog.findViewById(R.id.cancel);

          LottieAnimationView animation_view = dialog.findViewById(R.id.animation_view);
          animation_view.playAnimation();
          animation_view.loop(true);





          cancelButton.setOnClickListener(new View.OnClickListener(){
              @Override
              public void onClick(final View v){
                 dialog.dismiss();
              }
          });

          saveButton.setOnClickListener(new View.OnClickListener(){
              @Override
              public void onClick(final View v){

                  SaveData();

              }
          });


          if(!toneStr.isEmpty()){

              switch (toneStr) {
                  case "openness_big5":
                      DisplayResult = "openness";
                      animation_view.setAnimation(R.raw.openness);
                      break;
                  case "agreeableness_big5":
                      DisplayResult = "agreeness";
                      animation_view.setAnimation(R.raw.agreeness);
                      break;
                  case "joy":
                      DisplayResult = "joy";
                      animation_view.setAnimation(R.raw.joy);
                      break;
                  case "analytical":
                      DisplayResult = "analytical";
                      animation_view.setAnimation(R.raw.analytical);
                      break;
                  case "sadness":
                      DisplayResult = "sadness";
                      animation_view.setAnimation(R.raw.sadness);
                      break;
                  case "fear":
                      DisplayResult = "fear";
                      animation_view.setAnimation(R.raw.fear);
                      break;
                  case "emotional_range_big5":
                      DisplayResult = "emotional";
                      animation_view.setAnimation(R.raw.emotional);
                      break;
                  case "anger":
                      DisplayResult = "anger";
                      animation_view.setAnimation(R.raw.anger);
                      break;
                  case "confident":
                      DisplayResult = "confident";
                      animation_view.setAnimation(R.raw.confident);
                      break;
                  case "tentative":
                      DisplayResult = "tentative";
                      animation_view.setAnimation(R.raw.tentative);
                      break;
                  case "extraversion_big5":
                      DisplayResult = "extra";
                      animation_view.setAnimation(R.raw.extra);
                      break;
                  case "disgust":
                      DisplayResult = "disgust";
                      animation_view.setAnimation(R.raw.disgust);
                      break;
                  case "conscientiousness_big5":
                      DisplayResult = "conscientiousness";
                      animation_view.setAnimation(R.raw.conscientiousness);
                      break;

                  default:
                      DisplayResult = toneStr;
                      break;

              }

          }else {

              toneStr=DisplayResult;
          }





          result.setText(DisplayResult);
          dialog.show();
      }

      public void SaveData(){

          String sentence = mResultEt.getText().toString();
          String sentiment = DisplayResult.toString();
          if((!TextUtils.isEmpty(sentence))&&(!TextUtils.isEmpty(sentiment))){
              String token = databaseReference.push().getKey();

              SaveData saveData = new SaveData(token,sentence,sentiment);
              databaseReference.child(token).setValue(saveData);

              Toast.makeText(MainActivity.this, "Data is saved on firebase", Toast.LENGTH_SHORT).show();
          }else {
              Toast.makeText(MainActivity.this, "Analyze your sentence first", Toast.LENGTH_SHORT).show();
          }

      }


  }
