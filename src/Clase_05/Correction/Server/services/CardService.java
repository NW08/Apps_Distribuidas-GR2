package Clase_05.Correction.Server.services;

import Clase_05.Correction.Server.model.Card;
import Clase_05.Correction.Server.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
public class CardService {
   private final CardRepository cardRepository;

   public BigDecimal rechargeCard(String userDbId, BigDecimal amount) {
      log.info("Attempting to recharge ${} for user ID: {}", amount, userDbId);

      Card card = findCardByUserId(userDbId);
      card.chargeCard(amount);
      cardRepository.updateBalance(card);

      log.info("Recharge successful. New balance for user ID {}: ${}", userDbId, card.getBalance());
      return card.getBalance();
   }

   public BigDecimal processPayment(String userDbId, boolean isPreferred) {
      Card card = findCardByUserId(userDbId);
      card.payTicket(isPreferred);
      cardRepository.updateBalance(card);
      return card.getBalance();
   }

   public Card findCardByUserId(String userIdentification) {
      return cardRepository.findByUserId(userIdentification)
            .orElseThrow(() -> new IllegalArgumentException("Card not found for user ID: " + userIdentification));
   }
}