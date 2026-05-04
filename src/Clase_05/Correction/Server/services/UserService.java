package Clase_05.Correction.Server.services;

import Clase_05.Correction.Server.model.User;
import Clase_05.Correction.Server.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService {
   private final UserRepository userRepository;

   public User getUser(String userIdentification) {
      return userRepository.findById(userIdentification)
            .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + userIdentification));
   }
}
