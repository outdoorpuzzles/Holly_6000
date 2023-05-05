package cz.FCBcoders.holly6000;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import cz.FCBcoders.holly6000.R;

import java.util.HashMap;
import java.util.Map;

public class TeamLoginDialogFragment extends androidx.fragment.app.DialogFragment {
   private Button teamLoginBtn;
   private EditText teamLoginET;
   private String submittedPSW = "";
   private Holly6000ViewModel holly6000ViewModel;
   private String appScriptURL;

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);

      View view = (View) inflater.inflate(R.layout.fragment_login,container,false);

      holly6000ViewModel = new ViewModelProvider(requireActivity()).get(Holly6000ViewModel.class);
      appScriptURL = holly6000ViewModel.getAppScriptURL();

      teamLoginET = (EditText) view.findViewById(R.id.dialogFragmentET);
      teamLoginET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
         @Override
         public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
               submittedPSW = teamLoginET.getText().toString().trim();
               logTeam();
               return true;
            }
            return false;
         }
      });

      teamLoginBtn = (Button) view.findViewById(R.id.dialogFragmentBtn);
      teamLoginBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            submittedPSW = teamLoginET.getText().toString().trim();
            logTeam();
         }
      });

      getDialog().setCanceledOnTouchOutside(false);
      //getDialog().setCancelable(false);


      return view;
   }

   @Override
   public void onDismiss(final DialogInterface dialog) {
      super.onDismiss(dialog);
      final Activity activity = getActivity();
      if (activity instanceof DialogInterface.OnDismissListener) {
         ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
      }
   }

   private void logTeam() {

      final Activity activity = getActivity();
      final ProgressDialog loading =  ProgressDialog.show(activity,"Loading","please wait",false,true);

      StringRequest stringRequest = new StringRequest(Request.Method.POST, appScriptURL,
              new Response.Listener<String>() {
                 @Override
                 public void onResponse(String response) {
                    loading.dismiss();
                    //Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                    teamName(response);
                 }
              },

              new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {

                 }
              }
      ){
         @Override
         protected Map<String, String> getParams() {
            Map<String, String> parmas = new HashMap<>();

            //here we pass params
            parmas.put("action", "logTeam");
            //parmas.put("action", "findLastPlanet");
            parmas.put("teamPSW", submittedPSW);

            return parmas;
         }
      };

      int socketTimeOut = 50000;
      RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

      stringRequest.setRetryPolicy(policy);

      RequestQueue queue = Volley.newRequestQueue(activity);
      queue.add(stringRequest);

   }

   private void teamName(String teamName) {

      if(teamName.equals("Chybné PSW")) {
         new AlertDialog.Builder(getActivity())
                 .setTitle("Chybné heslo!")
                 .setMessage("Tebou zadané heslo neodpovídá žádnému týmu. Zkus to prosím znovu.")

                 // Specifying a listener allows you to take an action before dismissing the dialog.
                 // The dialog is automatically dismissed when a dialog button is clicked.
                 .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       teamLoginET.setText("");
                       teamLoginET.setHint(getResources().getString(R.string.teamLoginET_string));
                    }
                 })
                 .setIcon(android.R.drawable.ic_dialog_alert)
                 .show();
      } else {
         holly6000ViewModel.setTeamName(teamName);
         holly6000ViewModel.setSubmittedPSW(submittedPSW);
         dismiss();
      }

   }

}
