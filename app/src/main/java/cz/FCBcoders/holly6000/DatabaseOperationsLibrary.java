package cz.FCBcoders.holly6000;

import android.app.ProgressDialog;

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
      //final String planetLogCodeTVtext = planetLogCodeET.getText().toString().trim();


      StringRequest stringRequest = new StringRequest(Request.Method.POST, appScriptURL,
              new Response.Listener<String>() {
                 @Override
                 public void onResponse(String response) {
                    loading.dismiss();
                    //dismiss();

                    currDialogFragment.dismiss();

                    //Toast.makeText(fragmentActivity, "Funkce z knihovny", Toast.LENGTH_LONG).show();
                    //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    //startActivity(intent);

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
            //parmas.put("action", "getLastPlanet");
            parmas.put("team", team);
            parmas.put("planet", planet);
                /*String[][] planetCodes = holly6000ViewModel.getPlanetCodes();
                byte i;
                for (i=0;i<planetCodes[0].length;i++)
                    if (planetCodes[0][i].equals(holly6000ViewModel.getNextPlanet())) {
                        Log.d("Log Planet", "Porovnavam planetu "+planetCodes[0][i]);
                        Log.d("Log Planet", "S planetou "+holly6000ViewModel.getNextPlanet());
                        break;
                    }
                parmas.put("planet", planetCodes[0][i+1]);*/

            return parmas;
         }
      };

      int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

      RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
      stringRequest.setRetryPolicy(retryPolicy);

      RequestQueue queue = Volley.newRequestQueue(fragmentActivity);

      queue.add(stringRequest);


   }
}
