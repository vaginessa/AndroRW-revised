package alepacheco.com.rw.activityes;

import android.app.Activity;
import android.os.Bundle;

import alepacheco.com.rw.R;

/**
 * Created by joao on 05/07/2017.
 */

public class HappyActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy);
    }

    @Override
    public void onBackPressed() {

    }
}
