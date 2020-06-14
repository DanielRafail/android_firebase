package com.example.android.QADanielGrant;


import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Iterator;

import static android.content.ContentValues.TAG;

/**
 * Class used to take care of QAACtivity and its utility
 */
public class QAActivity extends MenuActivity {

    private ListView listview;
    private DatabaseReference myRef;
    private RandomCategory rc;
    private String category;

    /**
     * Override of the onCreate to instantiate the variables and the database variables
     * @param savedInstanceState the saved instance state of the program
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        rc = new RandomCategory();
        category = getIntent().getExtras().getString("category");
        setContentView(R.layout.qa_activity);
        listview = (ListView) findViewById(R.id.list2);
        myRef = FirebaseDatabase.getInstance().getReference();
        getInformation();
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
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,"Error : " + databaseError);
            }
        });
    }

    /**
     * Method used to get the data from the database and display it using the adapter
     * @param dataSnapShot the data snapshot containing the database information
     */
    private void showData(final DataSnapshot dataSnapShot){
        int count = 0;
        Iterator<DataSnapshot> iterator = dataSnapShot.child("Questions").child(category).getChildren().iterator();
        DataSnapshot[] questionNumber =  new DataSnapshot[(int) dataSnapShot.child("Questions").child(category).getChildrenCount()];
        String[] questionsArray = new String[(int) dataSnapShot.child("Questions").child(category).getChildrenCount()];
        while(iterator.hasNext()) {
            questionNumber[count]  = iterator.next();
            questionsArray[count] = (String) questionNumber[count].child("shortquestion").getValue();
            count++;
        }
         QAAdapter adapter = new QAAdapter(QAActivity.this, questionsArray, category, getIntent().getExtras().getByteArray("imgBytes"), questionNumber);
         listview.setAdapter(adapter);
    }

}
