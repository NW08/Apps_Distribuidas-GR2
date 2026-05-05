package Clase_05.Correction.Client.UI.utils;

import Clase_05.Correction.Client.model.User;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class UserSession {

   private final ObjectProperty<User> currentUser = new SimpleObjectProperty<>();

   public ObjectProperty<User> currentUserProperty() {
      return currentUser;
   }

   public User getCurrentUser() {
      return currentUser.get();
   }

   public void setCurrentUser(User user) {
      this.currentUser.set(user);
   }

   public void clearSession() {
      this.currentUser.set(null);
   }
}