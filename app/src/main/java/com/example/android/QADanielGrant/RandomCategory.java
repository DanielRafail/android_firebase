package com.example.android.QADanielGrant;

import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class used to get a random category
 */
public class RandomCategory {
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private long questionCount;
    private List<String> categoryList = new ArrayList<>();
    private String[] values = new String[3];
    private String imgUrl;
    private String category;


    /**
     * No argument constructor prepares class fields
     */
    public RandomCategory() {
        //get database
        database = FirebaseDatabase.getInstance();

        //get references inside the database given the category passed in from the QAActivity
        reference = database.getReference();
        prepareCategoryList();
        prepareNumQuestions();
        prepareImgUrl();
    }

    /**
     * Method used to return the random fields selected to the class that selected the random category
     * @return
     */
    public String[] getRandom(){
        Random randQuestion = new Random();
        //indexed starting at 0
        this.values[0] = category;
        //indexed starting at 1
        this.values[1] = "Question " + String.valueOf(randQuestion.nextInt((int) questionCount) + 1);
        this.values[2] = imgUrl;
        return this.values;
    }

    /**
     * Helper method used to fill the categoryList, fetching data from the database
     */
    private void prepareCategoryList(){
        reference.child("Categories").child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot categories) {
                for (DataSnapshot category : categories.getChildren()) {
                    Log.e("category", ""+category);
                    categoryList.add(String.valueOf(category.getValue()));
                }
                Random randomCat = new Random();
                category = categoryList.get(randomCat.nextInt(categoryList.size()));
                Log.i("Read Success", "Read successful");
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("Read failed", firebaseError.getMessage());
            }
        });
    }

    /**
     * Helper method used to get number of questions a category contains.
     */
    private void prepareNumQuestions(){
        reference.child("Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot categories) {
                for (DataSnapshot category : categories.getChildren()) {
                    questionCount = category.getChildrenCount();
                }
                Log.i("Read Success", "Read successful");
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("Read failed", firebaseError.getMessage());
            }
        });
    }

    /**
     * Helper method used to prepare an img URL to be converted to a byte[]
     */
    private void prepareImgUrl(){
        reference.child("Images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot categories) {
                for(DataSnapshot dbcategory : categories.getChildren()) {
                    if(dbcategory.getKey().equals(category)) {
                        imgUrl = String.valueOf(dbcategory.getValue());
                    }
                }
                Log.i("Read Success", "Read successful");
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("Read failed", firebaseError.getMessage());
            }
        });
    }
}