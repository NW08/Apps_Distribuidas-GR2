package Clase_07.RMI.Server.Classes;

import Clase_07.RMI.Shared.Person;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Optional;

@Slf4j
public class Repository {
   private final String jdbcUrl;
   private final String username;
   private final String password;

   public Repository(String host, int port, String database, String username, String password) {
      this.jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
      this.username = username;
      this.password = password;
   }

   public Optional<Person> findById(int id) {
      String sql = "SELECT * FROM empleados WHERE clave = ?";

      try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
           PreparedStatement statement = connection.prepareStatement(sql)) {

         statement.setInt(1, id);

         try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
               return Optional.of(new Person(
                     resultSet.getInt("clave"),
                     resultSet.getString("nombre"),
                     resultSet.getString("correo"),
                     resultSet.getString("cargo"),
                     resultSet.getDouble("sueldo")
               ));
            }
         }
      } catch (SQLException e) {
         log.error("Database connection or query failed for person id: {}", id, e);
      }

      return Optional.empty();
   }
}