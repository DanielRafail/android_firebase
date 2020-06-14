package com.example.android.QADanielGrant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Class used to create a menubar for the application.
 */
public class MenuActivity extends AppCompatActivity {
    //firebasestorage used to access image url's
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private byte[] imgBytes;
    private String questionNum;
    private String category;
    private RandomCategory rc = new RandomCategory();

    /**
     * Inflates the menu in every activity that extends this class
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mymenu, menu);
        return true;
    }

    /**
     * Event handler for menubar options
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            //click happened on the 'About' option
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
                //click happened on the 'Random' option
            case R.id.random:
                String[] values = rc.getRandom();
                rc = new RandomCategory();
                getImgBytes(values);
                return true;
                //click happened on the 'Last' option
            case R.id.last:
                if(sharedPrefsExist()){
                    startActivity(createIntentForQA(this.category, this.questionNum, this.imgBytes));
                    return true;
                } else{
                    return false;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Private helper method used to get image bytes using an url to the image inside of the database
     * @param values
     * @return
     */
    private byte[] getImgBytes(final String[] values){
        long ONE_MEGABYTE = 1024 * 1024;
        StorageReference gsReference = storage.getReferenceFromUrl(values[2]);
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes){
                imgBytes = bytes;
                startActivity(createIntentForQA(values[0], values[1], imgBytes));
                Log.i("Read Success.", "Successfully read bytes");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Read Failure.", "Cannot read bytes");
            }
        });
        return this.imgBytes;
    }

    /**
     * Helper method used to create an intent either from shared preferences or from the random category class
     * @param category
     * @param questionNum
     * @param imgBytes
     * @return
     */
    private Intent createIntentForQA(String category, String questionNum, byte[] imgBytes){
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra("category", category);
        intent.putExtra("questionNum", questionNum);
        intent.putExtra("imgBytes", imgBytes);
        return intent;
    }

    /**
     * Checks if the necessary values in the sharedPreferences exist.
     * @return
     */
    private boolean sharedPrefsExist(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //question_activity.xml cannot run without any one of those
        if(prefs.contains("questionNum") && prefs.contains("category") && prefs.contains("imgBytes")) {
            this.questionNum = prefs.getString("questionNum", null);
            this.category = prefs.getString("category", null);
            this.imgBytes = Base64.decode(prefs.getString("imgBytes", null), Base64.DEFAULT);
            return true;
        } else{
            Toast.makeText(this, "You have not previously opened a question!", Toast.LENGTH_LONG).show();
            return false;
        }

    }
}
