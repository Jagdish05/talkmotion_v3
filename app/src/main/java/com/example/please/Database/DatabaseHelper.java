package com.example.please.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.please.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "MyActivity";

    private static final String DATABASE_NAME = "gesture_names.db";
    private static final String TABLE_NAME = "gesture_names";
    private static final String GESTURES = "gestures";
    private static final String WORDS = "words";
    private static final String SCENARIOS = "scenarios";

    private SQLiteDatabase db;
    private Context c;
    private static String[] gestureNames;
    private static String[] gestureWords;
    private static String[] gestureNewWords;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
        this.c = context;
        this.db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // All we need is a table with 2 columns.  One to hold the gesture names, and one
        // to hold the words that the gestures represent
        db.execSQL("create table " + TABLE_NAME +
                "(" + GESTURES + " STRING, " + WORDS + " STRING, " + SCENARIOS + " INTEGER)");

        ContentValues contentValues = new ContentValues();
        gestureNames = c.getResources().getStringArray(R.array.gestures);
        gestureWords = c.getResources().getStringArray(R.array.names);





        for(int x = 0; x < gestureNames.length; x++) {
            contentValues.put(GESTURES, gestureNames[x]);
            contentValues.put(WORDS, gestureWords[x]);
            contentValues.put(SCENARIOS, 0);
            try {
                db.insert(TABLE_NAME, null, contentValues);
            } catch (android.database.SQLException e){
                System.err.println("Error insert into db" + e.getMessage());
            }

            contentValues.put(GESTURES, gestureNames[x]);
            //contentValues.put(WORDS, gestureNewWords[x]);
            contentValues.put(SCENARIOS, 1);
            try {
                db.insert(TABLE_NAME, null, contentValues);
            } catch (android.database.SQLException e){
                System.err.println("Error insert into db" + e.getMessage());
            }


        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    /**
     * Iterate through the new names and add them to the database
     *
     * @param names - The array of names to be written over the current names in the database
     * @return boolean - Determines if the insertion succeeded or not
     */
    public boolean insertNames(String[] names, String[] gestures) {
        //gestureNames = c.getResources().getStringArray(R.array.gestures);

        db.delete(TABLE_NAME, SCENARIOS + "=" +0, null);

        ContentValues contentValues = new ContentValues();
        for(int i = 0; i < names.length; i++) {
            contentValues.put(WORDS, names[i]);
            contentValues.put(GESTURES, gestures[i]);
            contentValues.put(SCENARIOS, 0);
            try {
                db.insert(TABLE_NAME, null, contentValues);
            } catch (android.database.SQLException e){
                System.err.println("Error insert into db" + e.getMessage());
            }
        }
        return true;
    }

    public boolean updateGestureIds(String[] new_gesture_ids, String[] old_gesture_ids) {
        //gestureNames = c.getResources().getStringArray(R.array.gestures);

        //db.delete(TABLE_NAME, SCENARIOS + "=" +nameChoices, null);

        for(int i = 0; i < new_gesture_ids.length; i++) {

            try {
                ContentValues cv = new ContentValues();
                cv.put(GESTURES,new_gesture_ids[i]);
                db.update(TABLE_NAME, cv, GESTURES+"= ?", new String[] {old_gesture_ids[i]});
            } catch (android.database.SQLException e){
                System.err.println("Error insert into db" + e.getMessage());
            }
        }
        return true;
    }


    public String[] getNames() {
//        Log.i(TAG, Arrays.toString(new int[]{c.getResources().getStringArray(R.array.names).length}));
//        List<String> names = new ArrayList<>();
//        names.clear();
//
//
//        final String TABLE_NAME = "gesture_names";
//
//        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
//        SQLiteDatabase db  = this.getReadableDatabase();
//        Cursor cursor      = db.rawQuery(selectQuery, null);
//        String[] data      = null;
//
//        if (cursor.moveToFirst()) {
//            do {
////                Log.i(TAG, String.valueOf(cursor.getCount()));
//                Log.i(TAG,"Gesture "+Arrays.toString(new String[]{cursor.getString(0)}) +"Text "+Arrays.toString(new String[]{cursor.getString(1)}));
//                // get the data into array, or class variable
//                names.add(cursor.getString(1));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//
//        String[] returnNames = new String[names.size()];
////        returnNames.length()
//        Log.i(TAG, String.valueOf(returnNames.length));
//
//        for(int i = 0; i < names.size(); i++) {
//            returnNames[i] = names.get(i);
////            Log.i(TAG, String.valueOf(returnNames[i]));
//
//        }
//        return returnNames;
//        Log.i(TAG, String.valueOf(returnNames));


//        return c.getResources().getStringArray(R.array.names);
//        return String.valueOf(names);




        ArrayList<String> names = new ArrayList<>();

        Cursor cursor = this.db.rawQuery(
                "SELECT " + WORDS + " FROM " + TABLE_NAME+ " WHERE " + SCENARIOS + "=" + 0, null);
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(cursor.getColumnIndex(WORDS)));
                // get  the  data into array,or class variable
            } while (cursor.moveToNext());
        }
        String[] returnNames = new String[names.size()];
        for(int i = 0; i < names.size(); i++) {
            returnNames[i] = names.get(i);
        }
        return returnNames;
    }

    public String[] getGestures() {
//        Log.i(TAG, Arrays.toString(c.getResources().getStringArray(R.array.gestures)));
//        return gestureWords = c.getResources().getStringArray(R.array.gestures);

//        List<String> names = new ArrayList<>();
//        names.clear();
//
//        final String TABLE_NAME = "gesture_names";
//
//        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
//        SQLiteDatabase db  = this.getReadableDatabase();
//        Cursor cursor      = db.rawQuery(selectQuery, null);
//        String[] data      = null;
//
//        if (cursor.moveToFirst()) {
//            do {
////                Log.i(TAG, String.valueOf(cursor.getCount()));
////                Log.i(TAG,"Gesture "+Arrays.toString(new String[]{cursor.getString(0)}) +"Text "+Arrays.toString(new String[]{cursor.getString(1)}));
//                // get the data into array, or class variable
//                names.add(cursor.getString(0));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//
//        String[] returnNames = new String[names.size()];
//        for(int i = 0; i < names.size(); i++) {
//            returnNames[i] = names.get(i);
//            Log.i(TAG, String.valueOf(returnNames[i]));
//
//        }
//        return returnNames;



        ArrayList<String> gestures = new ArrayList<>();
        Cursor cursor = this.db.rawQuery(
                "SELECT " + GESTURES + " FROM " + TABLE_NAME+ " WHERE " + SCENARIOS + "=" + 0, null);
        if (cursor.moveToFirst()) {
            do {
                gestures.add(cursor.getString(cursor.getColumnIndex(GESTURES)));
                // get  the  data into array,or class variable
            } while (cursor.moveToNext());
        }
        String[] returnGestures = new String[gestures.size()];
        for(int i = 0; i < gestures.size(); i++) {
            returnGestures[i] = gestures.get(i);
        }
        return returnGestures;
    }
}
