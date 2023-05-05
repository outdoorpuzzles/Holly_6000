package cz.FCBcoders.holly6000;

import androidx.lifecycle.ViewModel;

public class Holly6000ViewModel extends ViewModel {
   private String teamName = "";
   private String submittedPSW = "";
   private String nextPlanet = "";
   private String correctPlanetPSW = "";
   private final String appScriptURL = "https://script.google.com/macros/s/AKfycby9ALif9w9x-18qVMPLJ5Nkz3RKp6kW-zU1hDKTuMg6SsJ_hew3COnENXlw7apct-1ptw/exec";
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

   public String getAppScriptURL() {
      return appScriptURL;
   }

   public String[][] getPlanetCodes() {
      return planetCodes;
   }
}
