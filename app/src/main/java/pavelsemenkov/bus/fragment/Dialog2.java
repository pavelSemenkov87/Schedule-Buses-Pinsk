package pavelsemenkov.bus.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;

import pavelsemenkov.bus.R;

public class Dialog2 extends DialogFragment {
 
  final String LOG_TAG = "myLogs";
  private String mes;
  private void setMes (String mes) {
    this.mes = mes;
  }
  public static Dialog2 getInstance(String mes){
    Dialog2 dialog2 = new Dialog2();
    dialog2.setMes(mes);
    return dialog2;
  }
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
        .setTitle(getString(R.string.intBusInf)).setMessage(Html.fromHtml(mes));
    return adb.create();
  }
}