package cz.FCBcoders.holly6000;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
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

public class PlanetLoginDialogFragment extends androidx.fragment.app.DialogFragment {
    private TextView planetLoginTV;
    private EditText planetLoginET;
    private Button planetLoginBtn;
    private String submittedPSW = "";
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

        planetLoginTV = (TextView) view.findViewById(R.id.dialogFragmentTV);
        planetLoginET = (EditText) view.findViewById(R.id.dialogFragmentET);
        planetLoginBtn = (Button) view.findViewById(R.id.dialogFragmentBtn);

        String logInstructionsText = getResources().getString(R.string.planetLoginTV_string) + holly6000ViewModel.getNextPlanet();
        String logInstructionsHint = getResources().getString(R.string.planetLoginET_string);
        String planetLoginBtnText = getResources().getString(R.string.planetLoginBtn_string);

        planetLoginTV.setText(logInstructionsText);
        planetLoginET.setHint(logInstructionsHint);
        planetLoginBtn.setText(planetLoginBtnText);


        planetLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submittedPSW = planetLoginET.getText().toString().trim();
                //logPlanet();
                if (submittedPSW.equalsIgnoreCase(holly6000ViewModel.getCorrectPlanetPSW())) {
                    writeToDatabase();
                    //Fragment currFragment = getParentFragmentManager().findFragmentByTag("Planet Login Dialog Fragment");
                    //DialogFragment currDialogFragment = (DialogFragment) currFragment;
                    //DatabaseOperationsLibrary.writeToDatabase(getActivity(), appScriptURL, holly6000ViewModel.getTeamName(),
                    //        holly6000ViewModel.getNextPlanet(), currDialogFragment);

                } else {
                    new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog))
                            .setTitle("Chybný kód planety!")
                            .setMessage("Tebou zadaný kód planety není správný. Zkus to prosím znovu.")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    planetLoginET.setText("");
                                    planetLoginET.setHint(logInstructionsHint);
                                }
                            })
                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

        getDialog().setCanceledOnTouchOutside(false);
        //getDialog().setCancelable(false);


        return view;
    }

    /*@Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }*/

    private void writeToDatabase() {

        final Activity activity = getActivity();
        final ProgressDialog loading;

        if (!holly6000ViewModel.isInternetAvailable()) {
            MainActivity myActivity = (MainActivity) getActivity();
            myActivity.noInternetConnectionWarning();
            return;
        }

        loading =  ProgressDialog.show(activity,"Loading","please wait",false,true);
        //final String planetLogCodeTVtext = planetLogCodeET.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, appScriptURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        dismiss();
                        //Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        //startActivity(intent);
                        String errorSubString = "<!DOC";
                        int substringLengt = Math.min(response.length(), errorSubString.length());
                        if ((response.equals("")) || (response.substring(0,substringLengt).equals(errorSubString.substring(0,substringLengt)))) {
                            MainActivity myActivity = (MainActivity) getActivity();
                            myActivity.networkProblemWarning();
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
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "logPlanet");
                //parmas.put("action", "getLastPlanet");
                parmas.put("team", holly6000ViewModel.getTeamName());
                parmas.put("planet", holly6000ViewModel.getNextPlanet());
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

        int socketTimeOut = 5000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(activity);

        queue.add(stringRequest);


    }
}
