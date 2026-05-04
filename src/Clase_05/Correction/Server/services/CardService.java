package Clase_05.Correction.Server.services;

import Clase_05.Correction.Server.model.Card;
import Clase_05.Correction.Server.model.User;
import Clase_05.Correction.Server.repository.CardRepository;
import Clase_05.Correction.Server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
public class CardService {
   private final CardRepository cardRepository;
   private final UserRepository userRepository;

   public BigDecimal rechargeCard(String userIdentification, BigDecimal amount) {
      log.info("Attempting to recharge ${} for user ID: {}", amount, userIdentification);

      Card card = findCardByUserId(userIdentification);
      card.chargeCard(amount);
      cardRepository.updateBalance(card);

      log.info("Recharge successful. New balance for user ID {}: ${}", userIdentification, card.getBalance());
      return card.getBalance();
   }

   public BigDecimal processPayment(String userIdentification) {
      Card card = findCardByUserId(userIdentification);
      User user = userRepository.findById(userIdentification)
            .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + userIdentification));

      card.payTicket(user.isPreferred());
      cardRepository.updateBalance(card);

      log.info("Payment successful for user ID {} (preferred: {}). Balance: ${}",
            userIdentification, user.isPreferred(), card.getBalance());
      return card.getBalance();
   }

   private Card findCardByUserId(String userIdentification) {
      return cardRepository.findByUserId(userIdentification)
            .orElseThrow(() -> new IllegalArgumentException("Card not found for user ID: " + userIdentification));
   }
}