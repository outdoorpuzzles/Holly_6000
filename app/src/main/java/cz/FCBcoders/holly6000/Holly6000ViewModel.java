package cz.FCBcoders.holly6000;

import androidx.lifecycle.ViewModel;

public class Holly6000ViewModel extends ViewModel {
   private String teamName = "";
   private String submittedPSW = "";
   private String nextPlanet = "";
   private String correctPlanetPSW = "";
   private String holly0000DisplayText = "yfkj kjky dd lji aff drtwe sfdfw";
   private boolean internetAvailable = false;
   private final String appScriptURL = "https://script.google.com/macros/s/AKfycbwtVOAom7oz-BbbBPNOhB2hXJYS97ljfaEkr_OIQykwWe3_sX_zcWICiUV3jCkAahLKZA/exec";
   private final String[][] planetCodes = {{"Merkur", "Venuše", "Země", "Mars", "Jupiter", "Saturn", "Uran", "Neptun"},
           {"Kod1", "Kod2", "Kod3", "Kod4", "Kod5", "Kod6", "Kod7", "Kod8"}};


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

   public String getHolly0000DisplayText() {
      return holly0000DisplayText;
   }

   public void setHolly0000DisplayText(String holly0000DisplayText) {
      this.holly0000DisplayText = holly0000DisplayText;
   }

   public boolean isInternetAvailable() {
      return internetAvailable;
   }

   public void setInternetAvailable(boolean internetAvailable) {
      this.internetAvailable = internetAvailable;
   }
   public String getAppScriptURL() {
      return appScriptURL;
   }

   public String[][] getPlanetCodes() {
      return planetCodes;
   }
}
