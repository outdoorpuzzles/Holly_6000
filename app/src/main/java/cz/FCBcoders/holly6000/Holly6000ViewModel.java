package cz.FCBcoders.holly6000;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class Holly6000ViewModel extends ViewModel {
   String GUID = "";
   private String teamName = "";
   private String submittedPSW = "";
   private String nextPlanet = "";
   private String correctPlanetPSW = "";
   private String displayText = "";
   private String newTextToDisplay = "";
   private boolean internetAvailable = false;
   private String currentAction = "";
   private final String appScriptURL = "https://script.google.com/macros/s/AKfycbwMjX2gfFlAuxrEPyu67FQBsRQO4pqmsJX3ImYaIG7cIVaqY--VpSDMB8fn7k5hUekJEQ/exec";
   private final String[][] planetCodes = {{"Merkur", "Venuše", "Země", "Mars", "Jupiter", "Saturn", "Uran", "Neptun"},
           {"Kod1", "Kod2", "Kod3", "Kod4", "Kod5", "Kod6", "Kod7", "Kod8"}};
   private String[][] gameData = null;
   private int lastPlanetNum = -1;
   private boolean helpRequested = false;
   private boolean solutionRequested = false;
   private boolean solutionCommitted = false;
   private boolean treasureHelpRequested = false;
   private boolean treasureSolutionCommitted = false;
   private boolean treasureLogged = false;
   private boolean userInputAwaited = false;
   private HashMap<String, Boolean> bCodes = new HashMap<String, Boolean>();
   private int videoToPlay = 0;

   int[][] VIDEO_LIST = {
           {R.raw.cejch_login, R.raw.cejch_help, R.raw.cejch_solution, R.raw.cejch_logoff},
           {0, R.raw.trek_help, R.raw.trek_solution, R.raw.trek_logoff},
           {0, R.raw.theta_help, R.raw.theta_solution, R.raw.theta_logoff},
           {0, R.raw.sigma__help, R.raw.sigma_solution, R.raw.sigma_logoff},
           {0, R.raw.mogagon_help, R.raw.mogagon_solution, R.raw.mogagon_logoff},
           {0, R.raw.adelphi_help, R.raw.adelphi_solution, R.raw.adelphi_logoff},
           {0, R.raw.sharmutt_help, R.raw.sharmutt_solution, R.raw.sharmutt_logoff},
           {0, R.raw.argon_help, R.raw.argon_solution, R.raw.argon_logoff},
           {0, R.raw.psychomesic_help, R.raw.psychomesic_solution, R.raw.psychomesic_logoff},
           {R.raw.miranda_login, 0, 0, 0},
           {0, 0, 0, 0},
           {R.raw.treasure_login, R.raw.treasure_help, 0, R.raw.treasure_logoff}
   };


   /* Implementace */

   public String getGUID() {
      return GUID;
   }
   public void setGUID(String GUID) {
      this.GUID = GUID;
   }

   public String[][] getGameData() {
      return gameData;
   }

   public void setGameData(String[][] gameData) {
      this.gameData = gameData;
   }

   public int getLastPlanetNum() {
      return lastPlanetNum;
   }
   public void setLastPlanetNum(int lastPlanetNum) {
      this.lastPlanetNum = lastPlanetNum;
   }

   public boolean isHelpRequested() {
      return helpRequested;
   }

   public void setHelpRequested(boolean helpRequested) {
      this.helpRequested = helpRequested;
   }

   public boolean isSolutionRequested() {
      return solutionRequested;
   }

   public void setSolutionRequested(boolean solutionRequested) {
      this.solutionRequested = solutionRequested;
   }

   public boolean isSolutionCommitted() {
      return solutionCommitted;
   }

   public void setSolutionCommitted(boolean solutionCommitted) {
      this.solutionCommitted = solutionCommitted;
   }

   public boolean isTreasureHelpRequested() {
      return treasureHelpRequested;
   }

   public void setTreasureHelpRequested(boolean treasureHelpRequested) {
      this.treasureHelpRequested = treasureHelpRequested;
   }

   public boolean isTreasureSolutionCommitted() {
      return treasureSolutionCommitted;
   }

   public void setTreasureSolutionCommitted(boolean treasureSolutionCommitted) {
      this.treasureSolutionCommitted = treasureSolutionCommitted;
   }

   public boolean isTreasureLogged() {
      return treasureLogged;
   }

   public void setTreasureLogged(boolean treasureLogged) {
      this.treasureLogged = treasureLogged;
   }

   public String getTeamName() {
      return teamName;
   }

   public void setTeamName(String teamName) {
      this.teamName = teamName;
   }

   public String getSubmittedPSW() {
      return submittedPSW;
   }

   public void setSubmittedPSW(String submittedPSW) {
      this.submittedPSW = submittedPSW;
   }

   public String getNextPlanet() {
      return nextPlanet;
   }

   public void setNextPlanet(String nextPlanet) {
      this.nextPlanet = nextPlanet;
   }

   public String getCorrectPlanetPSW() {
      return correctPlanetPSW;
   }

   public void setCorrectPlanetPSW(String correctPlanetPSW) {
      this.correctPlanetPSW = correctPlanetPSW;
   }

   public String getDisplayText() {
      return displayText;
   }

   public void setDisplayText(String displayText) {
      this.displayText = displayText;
   }

   public String getNewTextToDisplay() {
      return newTextToDisplay;
   }

   public void setNewTextToDisplay(String newTextToDisplay) {
      this.newTextToDisplay = newTextToDisplay;
   }

   public boolean isInternetAvailable() {
      Log.d("Log Planet", "Testuje dostupnost internetu");
      return internetAvailable;
   }

   public void setInternetAvailable(boolean internetAvailable) {
      Log.d("Log Planet", "Nastavuje dostupnost internetu");
      this.internetAvailable = internetAvailable;
   }

   public String getCurrentAction() {
      return currentAction;
   }

   public void setCurrentAction(String currentAction) {
      this.currentAction = currentAction;
   }

   public String getAppScriptURL() {
      return appScriptURL;
   }

   public String[][] getPlanetCodes() {
      return planetCodes;
   }

   public boolean isUserInputAwaited() {
      return userInputAwaited;
   }

   public void setUserInputAwaited(boolean userInputAwaited) {
      this.userInputAwaited = userInputAwaited;
   }

   public HashMap<String, Boolean> getBCodes() {
      return bCodes;
   }

   public void setBCodes(HashMap<String, Boolean> bCodes) {
      this.bCodes = bCodes;
   }

   public int getVideoToPlay() {
      return videoToPlay;
   }

   public void setVideoToPlay(int videoToPlay) {
      this.videoToPlay = videoToPlay;
   }

   public int[][] getVIDEO_LIST() {
      return VIDEO_LIST;
   }
}
