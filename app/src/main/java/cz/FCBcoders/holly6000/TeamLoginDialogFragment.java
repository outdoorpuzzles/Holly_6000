package cz.FCBcoders.holly6000;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
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
   private TextView teamLoginTV;
   private EditText teamLoginET;
   String logInstructionsHint = "";
   private Button teamLoginBtn;

   private String submittedPSW = "";
   private final String WRONG_TEAM_NAME = "Chybné PSW";
   private Holly6000ViewModel holly6000ViewModel;
   private String appScriptURL;

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);

      View view = (View) inflater.inflate(R.layout.fragment_login,container,false);
      getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

      holly6000ViewModel = new ViewModelProvider(requireActivity()).get(Holly6000ViewModel.class);
      appScriptURL = holly6000ViewModel.getAppScriptURL();

      teamLoginTV = (TextView) view.findViewById(R.id.dialogFragmentTV);
      teamLoginET = (EditText) view.findViewById(R.id.dialogFragmentET);
      teamLoginBtn = (Button) view.findViewById(R.id.dialogFragmentBtn);

      String logInstructionsText = getResources().getString(R.string.teamLoginTV_string);
      logInstructionsHint = getResources().getString(R.string.teamLoginET_string);
      String teamLoginBtnText = getResources().getString(R.string.teamLoginBtn_string);

      teamLoginTV.setText(logInstructionsText);
      teamLoginET.setHint(logInstructionsHint);
      teamLoginBtn.setText(teamLoginBtnText);

      teamLoginET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
         @Override
         public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
               submittedPSW = teamLoginET.getText().toString().trim();
               if (submittedPSW.equals(""))
                  teamName(WRONG_TEAM_NAME);
               else
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
            if (submittedPSW.equals(""))
               teamName(WRONG_TEAM_NAME);
            else
               logTeam();
         }
      });

      getDialog().setCanceledOnTouchOutside(false);
      getDialog().setCancelable(false);
      /*getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
         @Override
         public void onCancel(DialogInterface dialog) {
            Toast.makeText(getActivity(), "Tlačítko zpět", Toast.LENGTH_LONG).show();
         }
      });*/
      getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
         @Override
         public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
               return true;

            if (keyCode == KeyEvent.KEYCODE_BACK) {
               new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog))
                       .setTitle("Neplatné přihlášení!")
                       .setMessage("Je nutné se přihlásit platným heslem týmu. Zkuste to znovu, nebo ukončete aplikaci.")
                       .setPositiveButton("Zkusit znovu", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                             teamLoginET.setText("");
                             teamLoginET.setHint(logInstructionsHint);
                          }
                       })
                       .setNegativeButton("Ukončit Holly 6000", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                             getActivity().finish();
                             System.exit(0);
                          }
                       })
                       .setIcon(android.R.drawable.ic_dialog_alert)
                       .show();
            }

            return true;
         }
      });


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
      final ProgressDialog loading;

      if (!holly6000ViewModel.isInternetAvailable()) {
         MainActivity myActivity = (MainActivity) getActivity();
         myActivity.noInternetConnectionWarning();
         return;
      }

      loading =  ProgressDialog.show(activity,"Loading","please wait",false,true);
      StringRequest stringRequest = new StringRequest(Request.Method.POST, appScriptURL,
              new Response.Listener<String>() {
                 @Override
                 public void onResponse(String response) {
                    loading.dismiss();
                    //Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();

                    String errorSubString = "<!DOC";
                    int substringLengt = Math.min(response.length(), errorSubString.length());
                    if ((response.equals("")) || (response.substring(0,substringLengt).equals(errorSubString.substring(0,substringLengt)))) {
                       MainActivity myActivity = (MainActivity) getActivity();
                       myActivity.networkProblemWarning();
                    } else {
                       teamName(response);
                    }
                 }
              },

              new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(getActivity(), "Spojení vypršelo", Toast.LENGTH_LONG).show();
                    loading.dismiss();
                    MainActivity myActivity = (MainActivity) getActivity();
                    myActivity.noInternetConnectionWarning();
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

      int socketTimeOut = 5000;// u can change this .. here it is 50 seconds
      RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

      stringRequest.setRetryPolicy(policy);

      RequestQueue queue = Volley.newRequestQueue(activity);
      queue.add(stringRequest);

   }

   private void teamName(String teamName) {

      if(teamName.equals(WRONG_TEAM_NAME)) {
         new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog))
                 .setTitle("Chybné heslo!")
                 .setMessage("Tebou zadané heslo neodpovídá žádnému týmu. Zkus to prosím znovu.")

                 // Specifying a listener allows you to take an action before dismissing the dialog.
                 // The dialog is automatically dismissed when a dialog button is clicked.
                 .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       teamLoginET.setText("");
                       teamLoginET.setHint(logInstructionsHint);
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
