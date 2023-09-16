package cz.FCBcoders.holly6000;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private boolean hollyMsg = true;
    public static final int VIDEO_FRAGMENT = 0;
    public static final int TEXT_FRAGMENT = 1;
    public static final int PLANET_NAME_COLUMN = 0;
    public static final int COORDINATES_COLUMN = 1;
    public static final int PLANET_CODE_COLUMN = 2;
    public static final int HELP_COLUMN = 3;
    public static final int SOLUTION_COLUMN = 4;
    public static final int B_CODE_COLUMN = 5;
    public static final String ACTION_LOG_TEAM = "logTeam";
    public static final String ACTION_LOG_PLANET = "logPlanet";
    public static final String ACTION_REQUEST_HELP = "requestHelp";
    public static final String ACTION_REGUEST_SOLUTION = "requestSolution";
    public static final String ACTION_COMMIT_SOLUTION = "logSolution";
    public static final String ACTION_COMMIT_B_CODE = "logBCode";
    public static final String ACTION_GET_COORDINATES = "getCoordinates";
    public static final String ACTION_REQUEST_TREASURE_HELP = "requestTreasureHelp";
    public static final String ACTION_COMMIT_TREASURE_SOLUTION = "commitTreasureSolution";
    public static final String ACTION_LOG_TREASURE = "logTreasure";
    public static final String ACTION_GET_NEWS = "getNews";
    private final int FINISH_APP_TIME_INTERVAL = 2000;
    AppCompatImageButton logPlanetBtn, helpRequestBtn,solutionRequestBtn, solutionBtn, bCodeBtn,
    navigationBtn, treasureHelpRequestBtn, treasureSolutionBtn, logTreasureBtn, notificationsBtn;
    ImageView smallHolly6000Monitor;
    private long mLastBackPressedTime;
    Handler digitDisplayHandler = null;
    Runnable digitDisplayRunnable = null;
    Holly6000ViewModel holly6000ViewModel;
    int currentFragment = VIDEO_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        holly6000ViewModel = new ViewModelProvider(this).get(Holly6000ViewModel.class);

        logPlanetBtn = (AppCompatImageButton) findViewById(R.id.holly6000ConsoleLogPlanetBtn);
        helpRequestBtn = (AppCompatImageButton) findViewById(R.id.holly6000ConsoleHelpRequestBtn);
        solutionRequestBtn = (AppCompatImageButton) findViewById(R.id.holly6000ConsoleSolutionRequestBtn);
        solutionBtn = (AppCompatImageButton) findViewById(R.id.holly6000ConsoleSolutionBtn);
        bCodeBtn = (AppCompatImageButton) findViewById(R.id.holly6000ConsoleBCodeBtn);
        navigationBtn = (AppCompatImageButton) findViewById(R.id.holly6000ConsoleNavigationBtn);
        treasureHelpRequestBtn = (AppCompatImageButton) findViewById(R.id.holly6000ConsoleTreasureHelpRequestBtn);
        treasureSolutionBtn = (AppCompatImageButton) findViewById(R.id.holly6000ConsoleTreasureSolutionBtn);
        logTreasureBtn = (AppCompatImageButton) findViewById(R.id.holly6000ConsoleLogTreasureBtn);
        notificationsBtn = (AppCompatImageButton) findViewById(R.id.holly6000ConsoleNotificationsBtn);

        smallHolly6000Monitor = (ImageView) findViewById(R.id.holly6000ConsoleSmallHolly6000Monitor);
        ImageView smallHolly6000MonitorPicture = (ImageView) findViewById(R.id.holly6000ConsoleHolly6000Head);

        int[][] VIDEO_LIST = holly6000ViewModel.getVIDEO_LIST();
        final int VIDEO_LIST_LOGIN = 0;
        final int VIDEO_LIST_HELP = 1;
        final int VIDEO_LIST_SOLUTION = 2;
        final int VIDEO_LIST_LOGOFF = 3;

        logPlanetBtn.setEnabled(false);
        helpRequestBtn.setEnabled(false);
        solutionRequestBtn.setEnabled(false);
        solutionBtn.setEnabled(false);
        bCodeBtn.setEnabled(false);
        navigationBtn.setEnabled(false);
        treasureHelpRequestBtn.setEnabled(false);
        treasureSolutionBtn.setEnabled(false);
        logTreasureBtn.setEnabled(false);

        logPlanetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTextToDisplay = getResources().getString(R.string.planet_login_text);
                newTextToDisplay = newTextToDisplay + holly6000ViewModel.getGameData()[holly6000ViewModel.getLastPlanetNum()+1][PLANET_NAME_COLUMN];
                runAction(ACTION_LOG_PLANET, newTextToDisplay, true);
            }
        });

        helpRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int planetNum = holly6000ViewModel.getLastPlanetNum();
                String newTextToDisplay = "";
                if (holly6000ViewModel.isHelpRequested()) {
                    newTextToDisplay = getResources().getString(R.string.help_starting_text);
                    newTextToDisplay = newTextToDisplay + holly6000ViewModel.getGameData()[planetNum][MainActivity.HELP_COLUMN];
                    holly6000ViewModel.setNewTextToDisplay(newTextToDisplay);

                    holly6000ViewModel.setVideoToPlay(VIDEO_LIST[holly6000ViewModel.getLastPlanetNum()][VIDEO_LIST_HELP]);

                    if (currentFragment == TEXT_FRAGMENT) {
                        runAction(ACTION_REQUEST_HELP, newTextToDisplay, false);
                    } else {
                        playVideo();
                    }
                } else {
                    newTextToDisplay = getResources().getString(R.string.help_request_confirmation_text);
                    runAction(ACTION_REQUEST_HELP, newTextToDisplay, true);
                }
            }
        });

        solutionRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int planetNum = holly6000ViewModel.getLastPlanetNum();
                String newTextToDisplay = "";
                if (holly6000ViewModel.isSolutionRequested()) {
                    newTextToDisplay = getResources().getString(R.string.solution_starting_text);
                    newTextToDisplay = newTextToDisplay + holly6000ViewModel.getGameData()[planetNum][MainActivity.SOLUTION_COLUMN];
                    holly6000ViewModel.setNewTextToDisplay(newTextToDisplay);

                    holly6000ViewModel.setVideoToPlay(VIDEO_LIST[holly6000ViewModel.getLastPlanetNum()][VIDEO_LIST_SOLUTION]);

                    if (currentFragment == TEXT_FRAGMENT) {
                        runAction(ACTION_REGUEST_SOLUTION, newTextToDisplay, false);
                    } else {
                        playVideo();
                    }
                } else {
                    newTextToDisplay = getResources().getString(R.string.solution_request_confirmation_text);
                    runAction(ACTION_REGUEST_SOLUTION, newTextToDisplay, true);
                }
            }
        });

        solutionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTextToDisplay = getResources().getString(R.string.solution_commit_text);
                runAction(ACTION_COMMIT_SOLUTION, newTextToDisplay, true);
            }
        });

        bCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTextToDisplay = getResources().getString(R.string.b_code_commit_text);
                runAction(ACTION_COMMIT_B_CODE, newTextToDisplay, true);
            }
        });

        navigationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTextToDisplay = "";
                String planetNavigation = "";
                String treasureNavigation = "";

                int nextPlanetRow = holly6000ViewModel.getLastPlanetNum() + 1;
                if (holly6000ViewModel.isSolutionCommitted())
                    planetNavigation = getResources().getString(R.string.planet_navigation_text_1) +
                            holly6000ViewModel.getGameData()[nextPlanetRow][PLANET_NAME_COLUMN] +
                            getResources().getString(R.string.planet_navigation_text_2) +
                            holly6000ViewModel.getGameData()[nextPlanetRow][COORDINATES_COLUMN] + "\n";

                int treasureGameDataRow = holly6000ViewModel.getGameData().length - 1;
                if (holly6000ViewModel.isTreasureSolutionCommitted() && !holly6000ViewModel.isTreasureLogged())
                    treasureNavigation = getResources().getString(R.string.treasure_navigation_text) +
                            holly6000ViewModel.getGameData()[treasureGameDataRow][COORDINATES_COLUMN];

                newTextToDisplay = planetNavigation + treasureNavigation;

                runAction(ACTION_GET_COORDINATES, newTextToDisplay, false);
            }
        });

        treasureHelpRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int treasureGameDataRow = holly6000ViewModel.getGameData().length - 1;
                String newTextToDisplay = "";
                if (holly6000ViewModel.isTreasureHelpRequested()) {
                    newTextToDisplay = getResources().getString(R.string.treasure_help_starting_text);
                    newTextToDisplay = newTextToDisplay + holly6000ViewModel.getGameData()[treasureGameDataRow][MainActivity.HELP_COLUMN];
                    holly6000ViewModel.setNewTextToDisplay(newTextToDisplay);

                    holly6000ViewModel.setVideoToPlay(VIDEO_LIST[holly6000ViewModel.getGameData().length - 1][VIDEO_LIST_HELP]);

                    if (currentFragment == TEXT_FRAGMENT) {
                        runAction(ACTION_REQUEST_TREASURE_HELP, newTextToDisplay, false);
                    } else {
                        playVideo();
                    }
                } else {
                    newTextToDisplay = getResources().getString(R.string.treasure_help_request_confirmation_text);
                    runAction(ACTION_REQUEST_TREASURE_HELP, newTextToDisplay, true);
                }
            }
        });

        treasureSolutionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTextToDisplay = getResources().getString(R.string.treasure_solution_commit_text);
                runAction(ACTION_COMMIT_TREASURE_SOLUTION, newTextToDisplay, true);
            }
        });

        logTreasureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTextToDisplay = getResources().getString(R.string.treasure_login_text);
                runAction(ACTION_LOG_TREASURE, newTextToDisplay, true);
            }
        });

        smallHolly6000Monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getSupportFragmentManager();
                Holly6000VideoFragment holly6000VideoFragment = (Holly6000VideoFragment) fm.findFragmentByTag("Holly6000VideoFragment");

                if (holly6000VideoFragment != null && holly6000VideoFragment.isVisible()) {
                    smallHolly6000MonitorPicture.setImageResource(R.drawable.holly6000_head);
                    currentFragment = TEXT_FRAGMENT;

                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.fragment_transition_enter, R.anim.fragment_transition_exit)
                            .replace(R.id.holly6000_monitor_fragment_container, Holly6000TextDisplayFragment.class, null, "Holly6000TextDisplayFragment")
                            .setReorderingAllowed(true)
                            .addToBackStack("Holly6000TextDisplayFragment") // Name can be null
                            .commit();
                } else {
                    smallHolly6000MonitorPicture.setImageResource(R.drawable.holly6000_text);
                    currentFragment = VIDEO_FRAGMENT;

                    //holly6000ViewModel.setVideoToPlay(0);

                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.fragment_transition_enter, R.anim.fragment_transition_exit)
                            .replace(R.id.holly6000_monitor_fragment_container, Holly6000VideoFragment.class, null, "Holly6000VideoFragment")
                            .setReorderingAllowed(true)
                            .addToBackStack("Holly6000VideoFragment") // Name can be null
                            .commit();
                }
            }
        });

        checkInternetAvailability();

        exitAppOnBackPressed();

        animateHolly6000ConsoleItems();

        String GUID = "";
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.contains("GUID")) {
            GUID = sharedPref.getString("GUID", "");
        } else {
            GUID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("GUID", GUID);
            editor.apply();
        }
        holly6000ViewModel.setGUID(GUID);

        runAction(ACTION_LOG_TEAM, getResources().getString(R.string.log_team_and_load_game_data_text), true);
    }

    public void runAction(String currentAction, String newTextToDisplay, boolean userInputAwaited) {
        holly6000ViewModel.setNewTextToDisplay(newTextToDisplay);
        holly6000ViewModel.setCurrentAction(currentAction);
        holly6000ViewModel.setUserInputAwaited(userInputAwaited);

        FragmentManager fm = getSupportFragmentManager();
        Holly6000TextDisplayFragment holly6000TextDisplayFragment = (Holly6000TextDisplayFragment) fm.findFragmentByTag("Holly6000TextDisplayFragment");

        if (holly6000TextDisplayFragment == null || !holly6000TextDisplayFragment.isVisible()) {
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_transition_enter, R.anim.fragment_transition_exit)
                    .replace(R.id.holly6000_monitor_fragment_container, Holly6000TextDisplayFragment.class, null, "Holly6000TextDisplayFragment")
                    .setReorderingAllowed(true)
                    .addToBackStack("Holly6000TextDisplayFragment") // Name can be null
                    .commit();
        } else {
            holly6000TextDisplayFragment.retroComputerTextAnimation(holly6000ViewModel.getNewTextToDisplay());
        }
    }

    public void playVideo(){

        FragmentManager fm = getSupportFragmentManager();
        Holly6000VideoFragment holly6000VideoFragment = (Holly6000VideoFragment) fm.findFragmentByTag("Holly6000VideoFragment");

        if (holly6000VideoFragment == null || !holly6000VideoFragment.isVisible()) {
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_transition_enter, R.anim.fragment_transition_exit)
                    .replace(R.id.holly6000_monitor_fragment_container, Holly6000VideoFragment.class, null, "Holly6000VideoFragment")
                    .setReorderingAllowed(true)
                    .addToBackStack("Holly6000VideoFragment") // Name can be null
                    .commit();
        } else {
            holly6000VideoFragment.playVideo();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        runConsoleDigitDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*Hiding Status Bar*/
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        //ActionBar actionBar = getActionBar();
        //actionBar.hide();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (digitDisplayRunnable != null)
            digitDisplayHandler.removeCallbacks(digitDisplayRunnable);
    }

    // private void getLastPlanet() {
    /*private void getLastPlanet() {

        final ProgressDialog loading;

        if (!holly6000ViewModel.isInternetAvailable()) {
            noInternetConnectionWarning();
            return;
        }

        loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, holly6000ViewModel.getAppScriptURL(),
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
                        networkProblemWarning();
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

        int socketTimeOut = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }*/

    //private void writeToDatabase() {
    /*private void writeToDatabase() {

        final ProgressDialog loading = ProgressDialog.show(this, "Adding Item", "Please wait");
        final String planetLogCodeTVtext = planetLogCodeET.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, holly6000ViewModel.getAppScriptURL(),
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
                        myActivity.networkProblemWarning();
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

        int socketTimeOut = 15000;// u can change this .. here it is 15 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }*/

    public void checkInternetAvailability() {
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
    }

    public void exitAppOnBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mLastBackPressedTime + FINISH_APP_TIME_INTERVAL < System.currentTimeMillis()) {
                    Toast.makeText(getBaseContext(), R.string.app_exit_warning, Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                    System.exit(0);
                }
                mLastBackPressedTime = System.currentTimeMillis();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    //public void noInternetConnectionWarning() {
    public void noInternetConnectionWarning() {

        holly6000ViewModel.setUserInputAwaited(true);
        holly6000ViewModel.setNewTextToDisplay(getResources().getString(R.string.no_internet_connection_warning));

        FragmentManager fm = getSupportFragmentManager();
        Holly6000TextDisplayFragment holly6000TextDisplayFragment = (Holly6000TextDisplayFragment) fm.findFragmentByTag("Holly6000TextDisplayFragment");

        if (holly6000TextDisplayFragment == null || !holly6000TextDisplayFragment.isVisible()) {
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_transition_enter, R.anim.fragment_transition_exit)
                    .replace(R.id.holly6000_monitor_fragment_container, Holly6000TextDisplayFragment.class, null, "Holly6000TextDisplayFragment")
                    .setReorderingAllowed(true)
                    .addToBackStack("Holly6000TextDisplayFragment") // Name can be null
                    .commit();
        } else {
            holly6000TextDisplayFragment.retroComputerTextAnimation(holly6000ViewModel.getNewTextToDisplay());
        }
    }

    //public void networkProblemWarning() {
   /* public void networkProblemWarning() {

        holly6000ViewModel.setUserInputAwaited(true);
        holly6000ViewModel.setNewTextToDisplay(getResources().getString(R.string.network_problem_warning));

        FragmentManager fm = getSupportFragmentManager();
        Holly6000TextDisplayFragment holly6000TextDisplayFragment = (Holly6000TextDisplayFragment) fm.findFragmentByTag("Holly6000TextDisplayFragment");

        if (holly6000TextDisplayFragment == null || !holly6000TextDisplayFragment.isVisible()) {
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_transition_enter, R.anim.fragment_transition_exit)
                    .replace(R.id.holly6000_monitor_fragment_container, Holly6000TextDisplayFragment.class, null, "Holly6000TextDisplayFragment")
                    .setReorderingAllowed(true)
                    .commitNow();
        } else {
            holly6000TextDisplayFragment.retroComputerTextAnimation(holly6000ViewModel.getNewTextToDisplay());
        }
    }*/

    public void animateHolly6000ConsoleItems() {
        // Console controls animations
        /*ImageView holly6000ConsoleRedSquareControl_1 = (ImageView) findViewById(R.id.holly6000ConsoleRedSquareControl_1);
        ImageView holly6000ConsoleBlueSquareControl_1 = (ImageView) findViewById(R.id.holly6000ConsoleBlueSquareControl_1);

        Animation holly6000ConsoleRedSquareControl_1Animation = AnimationUtils.loadAnimation(this, R.anim.holly6000_console_controls_animation);
        Animation holly6000ConsoleBlueSquareControl_1Animation = AnimationUtils.loadAnimation(this, R.anim.holly6000_console_controls_animation);

        holly6000ConsoleRedSquareControl_1Animation.setStartOffset(3000);
        holly6000ConsoleBlueSquareControl_1Animation.setStartOffset(500);

        holly6000ConsoleRedSquareControl_1.startAnimation(holly6000ConsoleRedSquareControl_1Animation);
        holly6000ConsoleBlueSquareControl_1.startAnimation(holly6000ConsoleBlueSquareControl_1Animation);*/

        // Console controls animators
        AnimatorSet holly6000ConsoleRedSquareControl_1_AS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_red_square_1);
         holly6000ConsoleRedSquareControl_1_AS.setTarget(findViewById(R.id.holly6000ConsoleRedSquareControl_1));
         holly6000ConsoleRedSquareControl_1_AS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                 holly6000ConsoleRedSquareControl_1_AS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
         holly6000ConsoleRedSquareControl_1_AS.start();

        AnimatorSet holly6000ConsoleRedSquareControl_2_AS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_red_square_2);
        holly6000ConsoleRedSquareControl_2_AS.setTarget(findViewById(R.id.holly6000ConsoleRedSquareControl_2));
        holly6000ConsoleRedSquareControl_2_AS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleRedSquareControl_2_AS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleRedSquareControl_2_AS.start();

        AnimatorSet holly6000ConsoleBlueSquareControl_1_AS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_blue_square_1);
        holly6000ConsoleBlueSquareControl_1_AS.setTarget(findViewById(R.id.holly6000ConsoleBlueSquareControl_1));
        holly6000ConsoleBlueSquareControl_1_AS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleBlueSquareControl_1_AS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleBlueSquareControl_1_AS.start();

        AnimatorSet holly6000ConsoleBlueSquareControl_2_AS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_blue_square_2);
        holly6000ConsoleBlueSquareControl_2_AS.setTarget(findViewById(R.id.holly6000ConsoleBlueSquareControl_2));
        holly6000ConsoleBlueSquareControl_2_AS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleBlueSquareControl_2_AS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleBlueSquareControl_2_AS.start();

        AnimatorSet holly6000ConsoleGreenSquareControlAS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_green_square);
        holly6000ConsoleGreenSquareControlAS.setTarget(findViewById(R.id.holly6000ConsoleGreenSquareControl));
        holly6000ConsoleGreenSquareControlAS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleGreenSquareControlAS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleGreenSquareControlAS.start();

        AnimatorSet holly6000ConsoleYellowSquareControlAS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_yellow_square);
        holly6000ConsoleYellowSquareControlAS.setTarget(findViewById(R.id.holly6000ConsoleYellowSquareControl));
        holly6000ConsoleYellowSquareControlAS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleYellowSquareControlAS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleYellowSquareControlAS.start();

        AnimatorSet holly6000ConsoleBlueCircleControlAS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_blue_circle);
        holly6000ConsoleBlueCircleControlAS.setTarget(findViewById(R.id.holly6000ConsoleBlueCircleControl));
        holly6000ConsoleBlueCircleControlAS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleBlueCircleControlAS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleBlueCircleControlAS.start();

        AnimatorSet holly6000ConsoleGreenCircleControlAS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_green_circle);
        holly6000ConsoleGreenCircleControlAS.setTarget(findViewById(R.id.holly6000ConsoleGreenCircleControl));
        holly6000ConsoleGreenCircleControlAS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleGreenCircleControlAS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleGreenCircleControlAS.start();

        AnimatorSet holly6000ConsoleYellowCircleControl = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_yellow_circle);
        holly6000ConsoleYellowCircleControl.setTarget(findViewById(R.id.holly6000ConsoleYellowCircleControl));
        holly6000ConsoleYellowCircleControl.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleYellowCircleControl.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleYellowCircleControl.start();


        //Console Green Bar animation
        ImageView holly6000ConsoleGreenBar = (ImageView) findViewById(R.id.holly6000ConsoleGreenBar);
        Animation holly6000ConsoleGreenBarAnimation = AnimationUtils.loadAnimation(this, R.anim.holly6000_console_green_bar_animation);
        holly6000ConsoleGreenBar.startAnimation(holly6000ConsoleGreenBarAnimation);
    }

    public void runConsoleDigitDisplay() {
        Random random = new Random();
        TextView holly6000ConsoleDigitDisplayTV = (TextView) findViewById(R.id.holly6000ConsoleDigitDisplay);
        /*ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        holly6000ConsoleDigitDisplayTV.setText(String.format(Locale.GERMANY, "%04d", random.nextInt(10000)));
                    }
                }, 0, 2, TimeUnit.SECONDS);*/
        final int[] randomNumberForDigitDisplay = new int[1];
        digitDisplayHandler = new Handler();
        digitDisplayRunnable = new Runnable() {
            @Override
            public void run() {
                randomNumberForDigitDisplay[0] = random.nextInt(10000);
                holly6000ConsoleDigitDisplayTV.setText(String.format(Locale.GERMANY, "%04d", randomNumberForDigitDisplay[0]));
                digitDisplayHandler.postDelayed(digitDisplayRunnable, randomNumberForDigitDisplay[0]);
            }
        };
        digitDisplayHandler.removeCallbacks(digitDisplayRunnable);
        digitDisplayHandler.post(digitDisplayRunnable);
    }

    public boolean[] disableAllButtons() {
        MainActivity myActivity = (MainActivity) this;
        boolean[] currentBtnsState = new boolean[10];

        currentBtnsState[0] = myActivity.logPlanetBtn.isEnabled();
        currentBtnsState[1] = myActivity.helpRequestBtn.isEnabled();
        currentBtnsState[2] = myActivity.solutionRequestBtn.isEnabled();
        currentBtnsState[3] = myActivity.solutionBtn.isEnabled();
        currentBtnsState[4] = myActivity.bCodeBtn.isEnabled();
        currentBtnsState[5] = myActivity.navigationBtn.isEnabled();
        currentBtnsState[6] = myActivity.treasureHelpRequestBtn.isEnabled();
        currentBtnsState[7] = myActivity.treasureSolutionBtn.isEnabled();
        currentBtnsState[8] = myActivity.logTreasureBtn.isEnabled();
        currentBtnsState[9] = myActivity.notificationsBtn.isEnabled();

        myActivity.logPlanetBtn.setEnabled(false);
        myActivity.helpRequestBtn.setEnabled(false);
        myActivity.solutionRequestBtn.setEnabled(false);
        myActivity.solutionBtn.setEnabled(false);
        myActivity.bCodeBtn.setEnabled(false);
        myActivity.navigationBtn.setEnabled(false);
        myActivity.treasureHelpRequestBtn.setEnabled(false);
        myActivity.treasureSolutionBtn.setEnabled(false);
        myActivity.logTreasureBtn.setEnabled(false);
        myActivity.notificationsBtn.setEnabled(false);

        myActivity.smallHolly6000Monitor.setEnabled(false);

        return currentBtnsState;
    }

    public void restoreButtonsState(boolean[] buttonsState) {
        MainActivity myActivity = (MainActivity) this;
        int planetNum = holly6000ViewModel.getLastPlanetNum();

        int treasureGameDataRow;
        if (holly6000ViewModel.getGameData() != null)
            treasureGameDataRow = holly6000ViewModel.getGameData().length - 1;
        else
            return;

        if (planetNum != treasureGameDataRow - 1) {
            myActivity.logPlanetBtn.setEnabled(buttonsState[0]);
            myActivity.helpRequestBtn.setEnabled(buttonsState[1]);
            myActivity.solutionRequestBtn.setEnabled(buttonsState[2]);
            myActivity.solutionBtn.setEnabled(buttonsState[3]);
            myActivity.bCodeBtn.setEnabled(buttonsState[4]);
            myActivity.navigationBtn.setEnabled(buttonsState[5]);
            myActivity.treasureHelpRequestBtn.setEnabled(buttonsState[6]);
            myActivity.treasureSolutionBtn.setEnabled(buttonsState[7]);
            myActivity.logTreasureBtn.setEnabled(buttonsState[8]);
        }

        if (planetNum == treasureGameDataRow - 2) {
            holly6000ViewModel.setSolutionCommitted(true);

            myActivity.logPlanetBtn.setEnabled(true);
            myActivity.helpRequestBtn.setEnabled(false);
            myActivity.solutionRequestBtn.setEnabled(false);
            myActivity.solutionBtn.setEnabled(false);
        }

        myActivity.notificationsBtn.setEnabled(buttonsState[9]);
        myActivity.smallHolly6000Monitor.setEnabled(true);
    }
}