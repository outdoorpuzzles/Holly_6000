package cz.FCBcoders.holly6000;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

public class MainActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {
    private VideoView videoView = null;
    private Button logStanovisteBtn, napovedaBtn, reseniBtn;
    private TextView logInstructionsTV;
    private EditText planetLogCodeET;
    private boolean hollyMsg = true;

    Holly6000ViewModel holly6000ViewModel;
    private String appScriptURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (VideoView) findViewById(R.id.videoView);

        logStanovisteBtn = (Button) findViewById(R.id.logStanovisteBtn);
        napovedaBtn = (Button) findViewById(R.id.napovedaBtn);
        reseniBtn = (Button) findViewById(R.id.reseniBtn);
        logInstructionsTV = (TextView) findViewById(R.id.logInstructionsTV);
        planetLogCodeET = (EditText) findViewById(R.id.planetLogCodeTV);

        holly6000ViewModel = new ViewModelProvider(this).get(Holly6000ViewModel.class);
        appScriptURL = holly6000ViewModel.getAppScriptURL();

        FragmentManager fm = getSupportFragmentManager();
        TeamLoginDialogFragment teamLoginDialogFragment = new TeamLoginDialogFragment();
        teamLoginDialogFragment.show(fm,"Team Login Dialog Fragment");

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                logStanovisteBtn.setVisibility(View.VISIBLE);
                napovedaBtn.setVisibility(View.VISIBLE);
                reseniBtn.setVisibility(View.VISIBLE);

                //videoView.setVisibility(View.INVISIBLE);
                //planetLogCodeET.setVisibility(View.VISIBLE);
            }
        });

        //final MediaController mediaController = new MediaController(MainActivity.this, true);
        //mediaController.setEnabled(false);
        //videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.holly2));
        //videoView.start();

        /*videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaController.setEnabled(true);
            }
        });*/

        logStanovisteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"Tlačítko zmáčknuto", Toast.LENGTH_SHORT).show();
                getLastPlanet();
            }
        });

        /*napovedaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Tlačítko zmáčknuto", Toast.LENGTH_SHORT).show();
                writeToDatabase();
            }
        });

        reseniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(),holly6000ViewModel.getTeamName(), Toast.LENGTH_SHORT).show();

            }
        });*/

    }

    private void getLastPlanet() {

        final ProgressDialog loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, appScriptURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String lastPlanet = response;
                        String nextPlanet;
                        loading.dismiss();
                        String[][] planetCodes = holly6000ViewModel.getPlanetCodes();
                        if (lastPlanet.equals("Planeta nenalezena")) {
                            nextPlanet = planetCodes[0][0];}
                        else {
                            byte i;
                            for (i=0;i<planetCodes[0].length;i++)
                                if (planetCodes[0][i].equals(lastPlanet)) {
                                    break;
                                }
                            nextPlanet = planetCodes[0][i+1];
                        }
                        Log.d("Log Planet", "Obdrzel jsem jmeno dalsi planety "+nextPlanet);
                        //displayLogInstructions(nextPlanet);
                        holly6000ViewModel.setNextPlanet(nextPlanet);


                        byte i;
                        for (i=0;i<planetCodes[0].length;i++)
                            if (planetCodes[0][i].equals(nextPlanet)) {
                                Log.d("Log Planet", "Porovnavam planetu "+planetCodes[0][i]);
                                Log.d("Log Planet", "S planetou "+nextPlanet);
                                break;
                            }
                        Log.d("Log Planet", "Dalsi planeta je na pozici "+i);
                        holly6000ViewModel.setCorrectPlanetPSW(planetCodes[1][i]);
                        Log.d("Log Planet", "Kod dalsi planety je "+planetCodes[1][i]);
                        //Toast.makeText(MainActivity.this, holly6000ViewModel.getCorrectPlanetPSW(), Toast.LENGTH_LONG).show();
                        FragmentManager fm = getSupportFragmentManager();
                        PlanetLoginDialogFragment planetLoginDialogFragment = new PlanetLoginDialogFragment();
                        planetLoginDialogFragment.show(fm,"Planet Login Dialog Fragment");
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
                parmas.put("action", "getLastPlanet");
                //parmas.put("action", "findLastPlanet");
                parmas.put("team", holly6000ViewModel.getTeamName());
                Log.d("Log Planet", "Posilam jmeno tymu "+holly6000ViewModel.getTeamName());

                return parmas;
            }
        };

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    private void displayLogInstructions(String lastPlanet) {
        /*byte lastPlanetNum;

        for (lastPlanetNum=0; lastPlanetNum<planetCodes.length; lastPlanetNum++) {
            if(planetCodes[0][lastPlanetNum].equals(lastPlanet))
                break;
        }*/
        if(lastPlanet.equals("Planeta nenalezena"))
            lastPlanet = holly6000ViewModel.getPlanetCodes()[0][0];
        String logInstructionsText = "Zadejte vstupní kód k planetě\n" + lastPlanet;//planetCodes[0][lastPlanetNum+1];
        logInstructionsTV.setText(logInstructionsText);
        logInstructionsTV.setVisibility(View.VISIBLE);
    }

    private void writeToDatabase() {

        final ProgressDialog loading = ProgressDialog.show(this, "Adding Item", "Please wait");
        final String planetLogCodeTVtext = planetLogCodeET.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, appScriptURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
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
                parmas.put("team", "FCB");
                parmas.put("planet", planetLogCodeTVtext);

                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        //Fragment dialog had been dismissed
        videoView.start();
        //Toast.makeText(MainActivity.this, "Ted se melo pustit video", Toast.LENGTH_LONG).show();
    }

}