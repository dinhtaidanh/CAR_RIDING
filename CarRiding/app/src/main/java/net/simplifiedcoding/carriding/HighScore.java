package net.simplifiedcoding.carriding;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.widget.TextView;

public class HighScore extends AppCompatActivity {

    TextView textView,textView2,textView3,textView4;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        //initializing the textViews
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);

        sharedPreferences  = getSharedPreferences("SHAR_PREF_NAME", Context.MODE_PRIVATE);

        //setting the values to the textViews
        textView.setText("1st. "+sharedPreferences.getInt("score1",0)+" point");
        textView2.setText("2nd. "+sharedPreferences.getInt("score2",0)+" point");
        textView3.setText("3rd. "+sharedPreferences.getInt("score3",0)+" point");
        textView4.setText("4th. "+sharedPreferences.getInt("score4",0)+" point");


    }
}