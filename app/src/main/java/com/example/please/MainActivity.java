package com.example.please;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;


import com.example.please.Database.DatabaseHelper;
import com.example.please.StateMachine.StateMachine;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements SensorEventListener, AdapterView.OnItemSelectedListener {

//    Toolbar toolbar=findViewById ( R.id.topAppBar );



    private StateMachine sm;
    private static final String TAG = "MyActivity";

    // A transition handler to deal with the complexities of switching between pages
    // and dynamically adding, subtracting, or saving values
    private TransitionHandler transition;

    // isOn toggles whether the user wants to read their data currently or not.
    // Triggered by a button push
    private boolean isOn = false;

    private int modifier = -1;


    // Boolean values that stop it from repeating itself
    private boolean wasXr = false;
    private boolean wasXl = false;
    private boolean wasYr = false;
    private boolean wasYl = false;
    private boolean wasZr = false;
    private boolean wasZl = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = (MenuItem) menu.findItem(R.id.gestureMode);
        item.setActionView(R.layout.switch_layout);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchAB = item
                .getActionView().findViewById(R.id.switchAB);

        switchAB.setChecked(false);

        switchAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplication(), "ON", Toast.LENGTH_SHORT)
                            .show();
                    if(isOn) {
                        isOn = false;
                        wasZr = false;
                        wasZl = false;
                        wasYr = false;
                        wasYl = false;
                        wasXr = false;
                        wasXl = false;
//                        b.setText(R.string.off);
                    }
                } else {
                    Toast.makeText(getApplication(), "OFF", Toast.LENGTH_SHORT)
                            .show();
                    isOn = true;
//            b.setText(R.string.on);
                }
            }
        });
        return true;


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.changeGestureWord:
                Toast.makeText(getApplicationContext(), "Hello...........", Toast.LENGTH_SHORT).show();
//                setContentView(R.layout.settings);
                transition.changeGesture(false);
                return true;
            case R.id.changeGestureDefinition:
                Toast.makeText(getApplicationContext(), "Hello...........", Toast.LENGTH_SHORT).show();
                transition.changeGestureDef(false);
                return true;
             case R.id.gestureMode:
                Toast.makeText(getApplicationContext(), "Hello...........", Toast.LENGTH_SHORT).show();
                 Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();

                return true;
//            case android.R.id.home:
//                Toast.makeText(getApplicationContext(), "Home...........", Toast.LENGTH_SHORT).show();
////                setContentView(R.layout.settings);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Typical android setup stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sec);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.setting));
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
//                transition.changeMain(v);
                setContentView(R.layout.activity_main);
            }
        });




        DatabaseHelper db = new DatabaseHelper(this);

        sm = new StateMachine(this, db);
        transition = new TransitionHandler(this, db);


        // Instantiate and register each sensor variable to each of it's
        // corresponding hardware sensors

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor linear_acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Sensor gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(MainActivity.this, linear_acceleration, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(MainActivity.this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(MainActivity.this, gyro, SensorManager.SENSOR_DELAY_NORMAL);


        String[] gestureNames = db.getNames();
        String[] returnNames = new String[gestureNames.length];
        for(int i = 0; i < returnNames.length; i++) {
            returnNames[i] = gestureNames[i];
        }
        Log.i(TAG, "gestureNames" +returnNames);

        String[] mobileArray = gestureNames;
        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.activity_listview, mobileArray);

        ListView listView = (ListView) findViewById(R.id.gestureNameList);
        listView.setAdapter(adapter);
    }
    /**
     * This function needs to be here to prevent the super method from doing something weird
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * onSensorChanged
     *  - Called every time one of the sensors detects an event
     *  - Either an acceleration, gravity, or gyroscope event
     *  - Use the given value to change state or call function
     * @param event - the event object containing the sensor data
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        // If the sensor event was triggered by the accelerator, call checkMotion to play with it
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION && isOn) {
            checkMotion(event.values[0], event.values[1], event.values[2]);
            Log.i(TAG, "X !!");
            Toast.makeText(getApplicationContext(), "X", Toast.LENGTH_SHORT).show();

        } // Else, if it was a gravity event, check for the orientation
        // and change the state accordingly
        else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            Log.i(TAG, "X " + x);
            Toast.makeText(getApplicationContext(), "Hello..........."+x, Toast.LENGTH_SHORT).show();

            if(z > 8.0 && z > y && z > x) {
                sm.toBack();
            }
            else if(z < -8.0 && z < y && z < x) {
                sm.toFront();
            }
            else if(y > 8.0 && y > z && y > z) {
                sm.toUpright();
            }
        }
        // Else if the sensor was a gyroscope reading, change the isOn variable accordingly
        /*if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if(x > 1 || z > 1 || y > 1 || x < -1 || z < -1 || y < -1) {
                isOn = false;
            } else if(ax < 3 || ax > - 3.0 && az < 3 || az > - 3.0 && ay < 3 || ay > - 3.0) {
                isOn = true;
            }
        }*/
    }

    /**
     * Toggles the on/off button which determines if the user wants to be talking currently
     * @param v - the view of the event call from the button press
     */
//    public void toggleOnOff(View v) {
//        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
//        Button b = findViewById(R.id.OnOff);
//        if(isOn) {
//            isOn = false;
//            wasZr = false;
//            wasZl = false;
//            wasYr = false;
//            wasYl = false;
//            wasXr = false;
//            wasXl = false;
//            b.setText(R.string.off);
//            b.setBackground(getResources().getDrawable(R.drawable.rounded_corners_red));
//        }
//        else {
//            isOn = true;
//            b.setText(R.string.on);
//            b.setBackground(getResources().getDrawable(R.drawable.rounded_corners_green));
//        }
//    }


    // Starting the display code
    // public void changeLogo(View v) { transition.changeLogo(v) }

//    public void changeSettings(View v) {
//        transition.changeSettings(v);
//    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void changeGesture(View v)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transition.changeGesture(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void changeGestureDef(View v)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transition.changeGestureDef(false);
        }
    }


    public void changeMainForDefinition(View v) {
        transition.changeMainForDefinition(v);

    }
    public void changeMainForGesture(View v) {
        transition.changeMainForGesture(v);
    }
    public void saveGes(View v) {
        transition.saveGesture();
    }

    public void saveGesDef(View v) {
        transition.saveGestureDef();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void restoreToDefaultSettings(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transition.changeGesture(true);
        }
    }
//
//
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void restoreToDefaultGestureDef(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transition.changeGestureDef(true);
        }
    }
    /**
     * Check the motion of each axis when there is an acceleration event
     * If there is extraneous x movement, call x_move on the state machine
     * So on an so forth for each other axes.
     */
    public void checkMotion (final float ax, final float ay, final float az)
    {

        System.out.println(modifier);
        if(modifier == 0){
            if (ax > 10 && !(sm.isPlaying()) && ((ax > ay) && (ax > az))  && !wasXr) {
                sm.modified_y1xr();
                wasXr = true;
                wasXl = false;
                wasYr = false;
                wasYl = false;
                wasZr = false;
                wasZl = false;
                modifier = -1;
            }
            else if(ax < -10 && !sm.isPlaying() && ax < ay && ax < az && !wasXl) {
                sm.modified_y1xl();
                wasXr = false;
                wasXl = true;
                wasYr = false;
                wasYl = false;
                wasZr = false;
                wasZl = false;
                modifier = -1;
            }
            else if (az > 10 && !(sm.isPlaying()) && ((az > ay) && (az > ax)) && !wasZr) {
                sm.modified_y1zr();
                wasXr = false;
                wasXl = false;
                wasYr = false;
                wasYl = false;
                wasZr = true;
                wasZl = false;
                modifier = -1;
            } else if(az < -10 && !sm.isPlaying() && az < ay && az < ax && !wasZl) {
                sm.modified_y1zl();
                wasXr = false;
                wasXl = false;
                wasYr = false;
                wasYl = false;
                wasZr = false;
                wasZl = true;
                modifier = -1;
            }
        } else if(modifier == 1) {
            if (ax > 10 && !(sm.isPlaying()) && ((ax > ay) && (ax > az))  && !wasXr) {
                sm.modified_y2xr();
                wasXr = true;
                wasXl = false;
                wasYr = false;
                wasYl = false;
                wasZr = false;
                wasZl = false;
                modifier = -1;
            }
            else if(ax < -10 && !sm.isPlaying() && ax < ay && ax < az && !wasXl) {
                sm.modified_y2xl();
                wasXr = false;
                wasXl = true;
                wasYr = false;
                wasYl = false;
                wasZr = false;
                wasZl = false;
                modifier = -1;
            }
            else if (az > 10 && !(sm.isPlaying()) && ((az > ay) && (az > ax)) && !wasZr) {
                sm.modified_y2zr();
                wasXr = false;
                wasXl = false;
                wasYr = false;
                wasYl = false;
                wasZr = true;
                wasZl = false;
                modifier = -1;
            } else if(az < -10 && !sm.isPlaying() && az < ay && az < ax && !wasZl) {
                sm.modified_y2zl();
                wasXr = false;
                wasXl = false;
                wasYr = false;
                wasYl = false;
                wasZr = false;
                wasZl = true;
                modifier = -1;
            }
        } else {
            if (ax > 5 && !(sm.isPlaying()) && ((ax > ay) && (ax > az)) && !wasXr) {
                sm.x_mover();
                wasXr = true;
                wasXl = false;
                wasYr = false;
                wasYl = false;
                wasZr = false;
                wasZl = false;
            }
            else if(ax < -5 && !sm.isPlaying() && ax < ay && ax < az && !wasXl) {
                sm.x_movel();
                wasXr = false;
                wasXl = true;
                wasYr = false;
                wasYl = false;
                wasZr = false;
                wasZl = false;
            }
            else if (az > 5 && !(sm.isPlaying()) && ((az > ay) && (az > ax)) && !wasZr) {
                sm.z_mover();
                wasXr = false;
                wasXl = false;
                wasYr = false;
                wasYl = false;
                wasZr = true;
                wasZl = false;
            }
            else if(az < -5 && !sm.isPlaying() && az < ay && az < ax && !wasZl) {
                sm.z_movel();
                wasXr = false;
                wasXl = false;
                wasYr = false;
                wasYl = false;
                wasZr = false;
                wasZl = true;
            }
            else if ((ay > 5 && !sm.isPlaying()) && (ay > ax && ay > az) && !wasYr) {
                modifier = 0;
                wasXr = false;
                wasXl = false;
                wasYr = true;
                wasYl = false;
                wasZr = false;
                wasZl = false;
            } else if((ay < -5 && !sm.isPlaying()) && (ay < ax && ay < az) && !wasYl){
                modifier = 1;
                wasXr = false;
                wasXl = false;
                wasYr = false;
                wasYl = true;
                wasZr = false;
                wasZl = false;
            }
        }

    }

    /**
     * When the app is paused, stop all functionality so the beeper doesn't make any random noises
     * in the background
     */
    @Override
    public void onPause(){
        super.onPause();
        super.onStop();
        finish();
        isOn = false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString() + " position: " + pos + " id is : " + id,
                Toast.LENGTH_SHORT).show();
        System.out.println("OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString() + " position: " + pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


}
