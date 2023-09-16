package cz.FCBcoders.holly6000;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
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

import java.util.HashMap;
import java.util.Map;

public class MainActivity_old extends AppCompatActivity implements DialogInterface.OnDismissListener {
   private VideoView videoView = null;
   private Button logStanovisteBtn, napovedaBtn, reseniBtn;
   private TextView logInstructionsTV;
   private EditText planetLogCodeET;
   private boolean hollyMsg = true;
   private final int FINISH_APP_TIME_INTERVAL = 2000;
   private long mLastBackPressedTime;

   Holly6000ViewModel holly6000ViewModel;
   private String appScriptURL;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main_old);

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
      //teamLoginDialogFragment.show(fm, "Team Login Dialog Fragment");
      reseniBtn.setVisibility(View.VISIBLE);
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
      videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.holly6000_video));
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
        });*/

      reseniBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            //Toast.makeText(getApplicationContext(),holly6000ViewModel.getTeamName(), Toast.LENGTH_SHORT).show();
            FragmentManager fm = getSupportFragmentManager();
            Holly6000TextDisplayFragment holly6000TextDisplayFragment = (Holly6000TextDisplayFragment) fm.findFragmentById(R.id.fragmentContainerView);

            if (holly6000TextDisplayFragment != null && holly6000TextDisplayFragment.isVisible()) {
               //Toast.makeText(getApplicationContext(),"Není NULL a je tam", Toast.LENGTH_SHORT).show();
               fm.beginTransaction().remove(holly6000TextDisplayFragment).commit();
            } else {
               //Toast.makeText(getApplicationContext(), "Je NULL nebo tam není", Toast.LENGTH_SHORT).show();
               fm.beginTransaction()
                       .replace(R.id.fragmentContainerView, Holly6000TextDisplayFragment.class, null, "Kuk")
                       .setReorderingAllowed(true)
                       .addToBackStack("Holly6000Monitor") // Name can be null
                       .commit();
            }
         }
      });

      // Ověří konektivitu internetu
      NetworkRequest networkRequest = new NetworkRequest.Builder()
              .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
              .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
              .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
              .build();

      ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
         @Override
         public void onAvailable(@NonNull Network network) {
            holly6000ViewModel.setInternetAvailable(true);
            super.onAvailable(network);
         }

         @Override
         public void onLost(@NonNull Network network) {
            holly6000ViewModel.setInternetAvailable(false);
            super.onLost(network);
         }
      };

      ConnectivityManager connectivityManager =
              (ConnectivityManager) getSystemService(ConnectivityManager.class);
      connectivityManager.requestNetwork(networkRequest, networkCallback);

        /*final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(getBaseContext(), "Holly 6000 termination", Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog))
                        .setTitle("Holly 6000 termination")
                        .setMessage("Opravdu chcete ukončit Holly 6000?")
                        .setPositiveButton("Ano", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finishAndRemoveTask();
                            }
                        })
                        .setNegativeButton("Ukončit Holly 6000", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);*/

      OnBackPressedCallback callback = new OnBackPressedCallback(true) {
         @Override
         public void handleOnBackPressed() {
            if (mLastBackPressedTime + FINISH_APP_TIME_INTERVAL < System.currentTimeMillis()) {
               Toast.makeText(getBaseContext(), "Opětovným stiskem zpět ukončíte aplikaci Holly 6000", Toast.LENGTH_SHORT).show();
            } else {
               finish();
               System.exit(0);
            }
            mLastBackPressedTime = System.currentTimeMillis();
         }
      };
      this.getOnBackPressedDispatcher().addCallback(this, callback);

   }

   private void getLastPlanet() {

      final ProgressDialog loading;

      if (!holly6000ViewModel.isInternetAvailable()) {
         noInternetConnectionWarning();
         return;
      }

      loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

      StringRequest stringRequest = new StringRequest(Request.Method.POST, appScriptURL,
              new Response.Listener<String>() {
                 @Override
                 public void onResponse(String response) {
                    String lastPlanet = response;
                    String nextPlanet;
                    loading.dismiss();

                    //Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();
                    String errorSubString = "<!DOC";
                    int substringLengt = Math.min(response.length(), errorSubString.length());
                    if ((response.equals("")) || (response.substring(0,substringLengt).equals(errorSubString.substring(0,substringLengt)))) {
                       networkProblemWarning();
                       return;
                    }

                    String[][] planetCodes = holly6000ViewModel.getPlanetCodes();
                    if (lastPlanet.equals("Planeta nenalezena")) {
                       nextPlanet = planetCodes[0][0];
                    } else {
                       byte i;
                       for (i = 0; i < planetCodes[0].length; i++)
                          if (planetCodes[0][i].equals(lastPlanet)) {
                             break;
                          }
                       nextPlanet = planetCodes[0][i + 1];
                    }
                    Log.d("Log Planet", "Obdrzel jsem jmeno dalsi planety " + nextPlanet);
                    //displayLogInstructions(nextPlanet);
                    holly6000ViewModel.setNextPlanet(nextPlanet);


                    byte i;
                    for (i = 0; i < planetCodes[0].length; i++)
                       if (planetCodes[0][i].equals(nextPlanet)) {
                          Log.d("Log Planet", "Porovnavam planetu " + planetCodes[0][i]);
                          Log.d("Log Planet", "S planetou " + nextPlanet);
                          break;
                       }
                    Log.d("Log Planet", "Dalsi planeta je na pozici " + i);
                    holly6000ViewModel.setCorrectPlanetPSW(planetCodes[1][i]);
                    Log.d("Log Planet", "Kod dalsi planety je " + planetCodes[1][i]);
                    //Toast.makeText(MainActivity.this, holly6000ViewModel.getCorrectPlanetPSW(), Toast.LENGTH_LONG).show();
                    FragmentManager fm = getSupportFragmentManager();
                    PlanetLoginDialogFragment planetLoginDialogFragment = new PlanetLoginDialogFragment();
                    planetLoginDialogFragment.show(fm, "Planet Login Dialog Fragment");
                 }
              },

              new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    noInternetConnectionWarning();
                 }
              }
      ) {
         @Override
         protected Map<String, String> getParams() {
            Map<String, String> parmas = new HashMap<>();

            //here we pass params
            parmas.put("action", "getLastPlanet");
            //parmas.put("action", "findLastPlanet");
            parmas.put("team", holly6000ViewModel.getTeamName());
            Log.d("Log Planet", "Posilam jmeno tymu " + holly6000ViewModel.getTeamName());

            return parmas;
         }
      };

      int socketTimeOut = 5000;
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
      if (lastPlanet.equals("Planeta nenalezena"))
         lastPlanet = holly6000ViewModel.getPlanetCodes()[0][0];
      String logInstructionsText = "Zadejte vstupní kód k planetě\n" + lastPlanet;//planetCodes[0][lastPlanetNum+1];
      logInstructionsTV.setText(logInstructionsText);
      logInstructionsTV.setVisibility(View.VISIBLE);
   }

    /*private void writeToDatabase() {

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
                        String errorSubString = "<!DOC";
                        int substringLengt = Math.min(response.length(), errorSubString.length());
                        if ((response.equals("")) || (response.substring(0,substringLengt).equals(errorSubString.substring(0,substringLengt)))) {
                            networkProblemWarning();
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                parmas.put("team", "FCB");
                parmas.put("planet", planetLogCodeTVtext);

                return parmas;
            }
        };

        int socketTimeOut = 5000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }*/

   public void noInternetConnectionWarning() {
      new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog))
              .setTitle("Holly 6000 unavilable")
              .setMessage("Spojení s mateřskou lodí nebylo navázáno. Zkuste to prosím později.")
              .setPositiveButton("Rozumím", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                 }
              })
              .setIcon(android.R.drawable.ic_dialog_alert)
              .show();
   }

   public void networkProblemWarning() {
      new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog))
              .setTitle("Holly 6000 error")
              .setMessage("Holly hlásí, že je někde problém. Zkuste to prosím později. Když to nepůjde, kontatujte mateřskou loď.")
              .setPositiveButton("Rozumím", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                 }
              })
              .setIcon(android.R.drawable.ic_dialog_alert)
              .show();
   }

   @Override
   public void onDismiss(final DialogInterface dialog) {
      //Fragment dialog had been dismissed
      //videoView.start();
      //Toast.makeText(MainActivity.this, "Ted se melo pustit video", Toast.LENGTH_LONG).show();
   }

    /*@Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        Log.d("Log_Key", "Stisknuta klávesa "+keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("Log_Key", "Stisknuta klávesa ZPĚT");
            Toast.makeText(getBaseContext(), "Holly 6000 termination", Toast.LENGTH_LONG).show();
            /*new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog))
                    .setTitle("Holly 6000 termination")
                    .setMessage("Opravdu chcete ukončit Holly 6000?")
                    .setPositiveButton("Ano", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAndRemoveTask();
                        }
                    })
                    .setNegativeButton("Ukončit Holly 6000", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();*/


       /*} else {
            Toast.makeText(getBaseContext(), "Kuk", Toast.LENGTH_LONG).show();
        }

        return true;
    }*/

    /*@Override
    public void onBackPressed() {
        if (mLastBackPressedTime + FINISH_APP_TIME_INTERVAL < System.currentTimeMillis()) {
            Toast.makeText(this, "Opětovným stiskem zpět ukončíte aplikaci Holly 6000", Toast.LENGTH_SHORT).show();
        } else {
            //super.onBackPressed();
            finish();
            System.exit(0);
        }
        mLastBackPressedTime = System.currentTimeMillis();
    }*/
}