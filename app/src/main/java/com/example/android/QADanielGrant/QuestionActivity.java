package com.example.android.QADanielGrant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Class used to handle the question_activity.xml
 */
public class QuestionActivity extends MenuActivity {
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ImageButton imgButton;
    private RandomCategory rc;
    private TextView questionTextView;
    private TextView authorTextView;
    private TextView dateTextView;

    private byte[] imgBytes = null;
    private String questionNum = "";
    private static String category = "";
    private static String author = "";
    private static String hint = "";
    private static String answer = "";
    private static String date = "";
    private static String question = "";
    private static String source =  "";

    /**
     * Empty no param constructor
     */
    public QuestionActivity(){}

    /**
     * Overridden lifecycle method for the QuestionActivity class
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rc = new RandomCategory();
        //set the view
        setContentView(R.layout.question_activity);
        //this activity will never run without an intent
        getValuesFromIntent();
        //get database
        database = FirebaseDatabase.getInstance();
        //get references inside the database given the category passed in from the QAActivity
        reference = database.getReference().child("Questions").child(this.category);
        setListeners(this.questionNum);
        //set the textView of the question_activity.xml
        questionTextView = (TextView) findViewById(R.id.questionTextView);
    }

    /**
     * Overridden lifecycle method
     */
    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        String imgBytesString = Base64.encodeToString(this.imgBytes, Base64.DEFAULT);
        //store values
        String cat = this.category;
        String questionNum = this.questionNum;
        Log.e("cat ", this.category);
        Log.e("question", this.questionNum);
        Log.e("bytes", imgBytesString);
        editor.putString("category", cat);
        editor.putString("questionNum", questionNum);
        editor.putString("imgBytes", imgBytesString);
        editor.commit();
    }

    /**
     * Helper method used to set class variables from Intent
     */
    private void getValuesFromIntent(){
        this.imgBytes = getIntent().getByteArrayExtra("imgBytes");
        Bitmap imgBitmap = BitmapFactory.decodeByteArray(this.imgBytes, 0, this.imgBytes.length);
        imgButton = (ImageButton) findViewById(R.id.imgButton);
        imgButton.setImageBitmap(imgBitmap);
        this.questionNum = getIntent().getStringExtra("questionNum");
        //Category is either animals, cereal, programming or videogames
        this.category = getIntent().getStringExtra("category");
    }

    /**
     * Helper method used to set activity fields within the Question_Activity.XML view
     */
    private void setActivityFields(){

        questionTextView = (TextView) findViewById(R.id.questionTextView);
        questionTextView.setText(question);

        authorTextView = (TextView) findViewById(R.id.authorTextView);
        authorTextView.setText("Authors: " + author);

        dateTextView = (TextView) findViewById(R.id.dateTextView);
        dateTextView.setText(date);
    }

    /**
     * Helper method used fetch data from the database
     * @param questionNumber
     */
    private void setListeners(String questionNumber){
        reference.child(questionNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot qInfo) {
                for (DataSnapshot info: qInfo.getChildren()) {
                    if(info.getKey().equals("author")){
                        author = String.valueOf(info.getValue());
                    }
                    else if(info.getKey().equals("date")){
                        date = String.valueOf(info.getValue());
                    }
                    else if(info.getKey().equals("source")){
                        source = String.valueOf(info.getValue());
                    }
                    else if(info.getKey().equals("question")){
                        question = String.valueOf(info.getValue());
                    }
                    else if(info.getKey().equals("answer")){
                        answer = String.valueOf(info.getValue());
                    }
                    else if(info.getKey().equals("hint")){
                        hint = String.valueOf(info.getValue());
                    }
                }
                setActivityFields();
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("Read failed.", firebaseError.getMessage());
            }
    });
    }

    /**
     * Display a toast to show category once the imageButton was clicked
     * @param view
     */
    public void showCategory(View view){
        Toast.makeText(this, "The category is " + this.category, Toast.LENGTH_LONG).show();
    }

    /**
     * Creates a web link to the source of the question
     * @param view
     */
    public void showSource(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(source));
        startActivity(intent);
    }

    /**
     * Display a snackbar once the question was clicked
     * @param view
     */
    public void showHint(View view){
        Snackbar.make(view, "Hint! " + this.hint, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Display a snackbar once the floating action button was clicked
     * @param view
     */
    public void showAnswer(View view){
        Snackbar.make(view, "The answer is " + this.answer + " !", Snackbar.LENGTH_LONG).show();
    }
}
