package pavelsemenkov.bus.model;

import android.content.Context;
import android.widget.EditText;

/**
 * Created by Павел on 13.11.2016.
 */
public class myEditText extends EditText {

    private int myEditTextId;
    public myEditText(Context context) {
        super(context);
    }

    public int getMyEditTextId() {
        return myEditTextId;
    }

    public void setMyEditTextId(int myEditTextId) {
        this.myEditTextId = myEditTextId;
    }
}
