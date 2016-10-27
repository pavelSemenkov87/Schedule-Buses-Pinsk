package pavelsemenkov.bus.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.SpannableStringBuilder;

import pavelsemenkov.bus.R;

public class Dialog1 extends DialogFragment {

  final String LOG_TAG = "myLogs";
  private String mes;

  private void setMes (String mes) {
    this.mes = mes;
  }
  public static Dialog1 getInstance(String mes){
    Dialog1 dialog1 = new Dialog1();
    dialog1.setMes(mes);
    return dialog1;
  }
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    SpannableStringBuilder aboutBody = new SpannableStringBuilder();
    aboutBody.append(Html.fromHtml(getString(R.string.RemindMe_body, mes)));
    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
        .setTitle(getString(R.string.intBusInf)).setMessage(aboutBody);
    return adb.create();
  }
}