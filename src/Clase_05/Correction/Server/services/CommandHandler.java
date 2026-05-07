package Clase_05.Correction.Server.services;

@FunctionalInterface
interface CommandHandler {
   String handle(String[] parts, CardService cardService,
                 RegistrationService registrationService, UserService userService);
}