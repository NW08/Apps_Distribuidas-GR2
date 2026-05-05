package Clase_05.Correction.Client.UI.utils;

import lombok.Getter;

@Getter
public enum ViewRoute {
   SEARCH("/Clase_05/Correction/Client/UI/view/SearchView.fxml"),
   CREATE("/Clase_05/Correction/Client/UI/view/CreateView.fxml"),
   SHOW("/Clase_05/Correction/Client/UI/view/ShowView.fxml");

   private final String fxmlPath;

   ViewRoute(String fxmlPath) {
      this.fxmlPath = fxmlPath;
   }
}