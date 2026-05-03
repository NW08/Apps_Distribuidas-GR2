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

   public BigDecimal rechargeCard(Long userId, BigDecimal amount) {
      log.info("Attempting to recharge ${} for user ID: {}", amount, userId);

      Card card = findCardByUserId(userId);  // lanza IllegalArgumentException si no existe
      card.chargeCard(amount);               // lanza IllegalArgumentException si amount <= 0
      cardRepository.updateBalance(card);

      log.info("Recharge successful. New balance for user ID {}: ${}", userId, card.getBalance());
      return card.getBalance();
   }

   public BigDecimal processPayment(Long userId) {
      Card card = findCardByUserId(userId);
      User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + userId));

      card.payTicket(user.isPreferred());
      cardRepository.updateBalance(card);

      log.info("Payment successful for user ID {} (preferred: {}). Balance: ${}",
            userId, user.isPreferred(), card.getBalance());
      return card.getBalance();
   }

   public BigDecimal getBalance(Long userId) {
      return findCardByUserId(userId).getBalance();
   }


   private Card findCardByUserId(Long userId) {
      return cardRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Card not found for user ID: " + userId));
   }
}