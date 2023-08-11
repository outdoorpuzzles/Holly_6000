package cz.FCBcoders.holly6000;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class DatabaseOperationsLibrary {

   public static void writeToDatabase(FragmentActivity fragmentActivity, String appScriptURL, String team, String planet, DialogFragment currDialogFragment) {

      final ProgressDialog loading = ProgressDialog.show(fragmentActivity, "Adding Item", "Please wait");

      StringRequest stringRequest = new StringRequest(Request.Method.POST, appScriptURL,
              new Response.Listener<String>() {
                 @Override
                 public void onResponse(String response) {
                    loading.dismiss();
                    currDialogFragment.dismiss();
                 }
              },
              new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {

                 }
              }
      ) {
         @Override
         protected Map<String, String> getParams() {
            Map<String, String> parmas = new HashMap<>();

            //here we pass params
            parmas.put("action", "logPlanet");
            parmas.put("team", team);
            parmas.put("planet", planet);

            return parmas;
         }
      };

      int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

      RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
      stringRequest.setRetryPolicy(retryPolicy);

      RequestQueue queue = Volley.newRequestQueue(fragmentActivity);

      queue.add(stringRequest);

   }


   public static void logTeam(FragmentActivity fragmentActivity, String appScriptURL, String team, String submittedPSW, DialogFragment currDialogFragment) {

      final ProgressDialog loading =  ProgressDialog.show(fragmentActivity,"Loading","please wait",false,true);

      StringRequest stringRequest = new StringRequest(Request.Method.POST, appScriptURL,
              new Response.Listener<String>() {
                 @Override
                 public void onResponse(String response) {
                    loading.dismiss();
                    //Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                    teamName(fragmentActivity, response);
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
            parmas.put("teamPSW", submittedPSW);

            return parmas;
         }
      };

      int socketTimeOut = 50000;
      RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

      stringRequest.setRetryPolicy(policy);

      RequestQueue queue = Volley.newRequestQueue(fragmentActivity);
      queue.add(stringRequest);

   }

   private static void teamName(FragmentActivity fragmentActivity, String teamName) {

      if(teamName.equals("Chybné PSW")) {
         new AlertDialog.Builder(fragmentActivity)
                 .setTitle("Chybné heslo!")
                 .setMessage("Tebou zadané heslo neodpovídá žádnému týmu. Zkus to prosím znovu.")

                 // Specifying a listener allows you to take an action before dismissing the dialog.
                 // The dialog is automatically dismissed when a dialog button is clicked.
                 .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       //teamLoginET.setText("");
                       //  teamLoginET.setHint(getResources().getString(R.string.teamLoginET_string));
                    }
                 })
                 .setIcon(android.R.drawable.ic_dialog_alert)
                 .show();
      } else {
         //holly6000ViewModel.setTeamName(teamName);
         //holly6000ViewModel.setSubmittedPSW(submittedPSW);
         //dismiss();
      }

   }

}

