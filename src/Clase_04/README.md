# Prueba #01 (Girón María & Ortiz Josué) || `CLASE 04 (22|04|2026)`

Para la presente prueba práctica se simulará un sistema de registro y de recarga de saldo en tarjetas ciudadanas, para ello realice lo
siguiente:

- Proyecto Servidor UDP: Este proyecto consiste en la implementación de un servidor UDP en Java, diseñado para manejar información de los
  usuarios, A continuación, se presentan los componentes principales:
    - Modelo:
        - Crear la clase Usuario, que representará a cada usuario con los siguientes atributos:
            - String cedula
            - String correo
            - String teléfono
            - String nombre
            - boolean preferencial.
            - Además, se implementará un ArrayList que irá almacenando los usuarios creados.

        - Crear la clase Tarjeta, con los métodos:
            - asignarTarjeta(Usuario usuario)
            - cargarSaldo(double saldo), pagarPasaje()

    - Test:
        - Crear una clase Test para verificar que el servidor se ejecuta sin problemas.


- Proyecto Cliente UDP: El cliente UDP será responsable de enviar solicitudes al servidor y recibir respuestas. Además, contendrá la lógica
  necesaria para capturar la información del usuario y comunicarse con el servidor. Se recomienda la creación de la clase Usuario en el
  cliente, similar a la del servidor, para mapear correctamente la información. El cliente deberá comunicarse con el servidor mediante
  protocolo UDP.