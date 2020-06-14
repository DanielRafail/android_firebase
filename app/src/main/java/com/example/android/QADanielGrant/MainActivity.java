package com.example.android.QADanielGrant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Class used to take care of the MainActivity and its utility
 */
public class MainActivity extends MenuActivity {
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private ListView listview;
    Map<String, Bitmap> map = new HashMap<String, Bitmap>();
    private RandomCategory rc;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    /**
     * Method used to instantiate most variable and all database variables during on create
     * override of onCreate method
     * @param savedInstanceState the bundle with the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myRef = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        rc = new RandomCategory();
        if(currentUser == null){
            signIn();
        }else{
            getInformation();
        }
        listview = (ListView) findViewById(R.id.list);
    }

    /**
     * Method used to get the information from the database once the dataChanges or the data is called for the first time
     */
    private void getInformation(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                try {
                    callData(dataSnapshot);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,"Error : " + databaseError);
            }
        });
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
     * Method used to sign in (authenticate)
     */
    private void signIn(){
        mAuth.signInWithEmailAndPassword("other.1633028@gmail.com", "8bae86edb")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            getInformation();
                            // Sign in success, update UI with the signed-in user's information
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }

                });
    }

    /**
     * Method used to get the data from the database
     * @param dataSnapShot the datasnapshot containing the database information
     * @throws IOException
     */
    private void callData(final DataSnapshot dataSnapShot) throws IOException {
        final Iterator<DataSnapshot> iterator = dataSnapShot.child("Categories").child("name").getChildren().iterator();
        long ONE_MEGABYTE = 1024 * 1024;
        for (int i = 0; i < dataSnapShot.child("Categories").child("name").getChildrenCount(); i++) {

            String category = (String) iterator.next().getValue();
            String link = (String)
                    dataSnapShot.child("Images").child(category).getValue();

            final StorageReference gsReference = storage.getReferenceFromUrl(link);
            gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    map.put(gsReference.getName().substring(0, gsReference.getName().indexOf("."))
                            , BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    MainActivityAdapter adapter = new MainActivityAdapter
                            (MainActivity.this, map);
                    listview.setAdapter(adapter);
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG, "Error Occured : " + exception);
                }
            });
        }
    }
}
