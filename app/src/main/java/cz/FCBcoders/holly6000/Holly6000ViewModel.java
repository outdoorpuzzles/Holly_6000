package cz.FCBcoders.holly6000;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class Holly6000ViewModel extends ViewModel {
   private String teamName = "";
   private String submittedPSW = "";
   private String nextPlanet = "";
   private String correctPlanetPSW = "";
   private String displayText = "";
   private String newTextToDisplay = "";
   private boolean internetAvailable = false;
   private String currentAction = "";
   private final String appScriptURL = "https://script.google.com/macros/s/AKfycbwYt-Xt7W6zldeKB2i_yNPwDJKYVzbiWliBHq0Ok2cRbmN7MtsvpD81z60ZWCbj1rZ5Nw/exec";
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

   /* Implementace */

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
}
