package cz.FCBcoders.holly6000;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

        holly6000ViewModel = new ViewModelProvider(requireActivity()).get(Holly6000ViewModel.class);
        appScriptURL = holly6000ViewModel.getAppScriptURL();

        planetLoginTV = (TextView) view.findViewById(R.id.dialogFragmentTV);
        String logInstructionsText = "Zadejte vstupní kód k planetě\n" + holly6000ViewModel.getNextPlanet();
        planetLoginTV.setText(logInstructionsText);
        planetLoginET = (EditText) view.findViewById(R.id.dialogFragmentET);
        planetLoginBtn = (Button) view.findViewById(R.id.dialogFragmentBtn);
        planetLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submittedPSW = planetLoginET.getText().toString().trim();
                //logPlanet();
                if (submittedPSW.equals(holly6000ViewModel.getCorrectPlanetPSW())) {
                    //writeToDatabase();
                    Fragment currFragment = getParentFragmentManager().findFragmentByTag("Planet Login Dialog Fragment");
                    //if (currFragment != null) {
                        DialogFragment currDialogFragment = (DialogFragment) currFragment;
                        //cdf.dismiss();
                    //}
                    DatabaseOperationsLibrary.writeToDatabase(getActivity(), appScriptURL, holly6000ViewModel.getTeamName(),
                            holly6000ViewModel.getNextPlanet(), currDialogFragment);

                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Chybný kód planety!")
                            .setMessage("Tebou zadaný kód planety není správný. Zkus to prosím znovu.")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    planetLoginET.setText("");
                                    planetLoginET.setHint(getResources().getString(R.string.teamLoginET_string));
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

        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Adding Item", "Please wait");
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

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        queue.add(stringRequest);


    }
}
