package Clase_16.src.main.java.org.example.final2B;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class Controller {

   @Autowired
   private RedesRepository redesRepository;

   @GetMapping("/")
   public String index() {
      return "API de Redes Sociales funcionando correctamente.";
   }

   @GetMapping(value = "/datos", produces = MediaType.TEXT_PLAIN_VALUE)
   public String obtenerDatos() {
      StringBuilder salida = new StringBuilder();
      redesRepository.findAll().forEach(red -> {
         salida.append(red.getUsuario()).append(",")
               .append(red.getAccion()).append(",")
               .append(red.getFecha()).append(",")
               .append(red.getHora()).append(",")
               .append(red.getShortVideo()).append("\n");
      });
      return salida.toString();
   }

   @GetMapping(value = "/mapreduce", produces = MediaType.TEXT_PLAIN_VALUE)
   public String executeMapReduce() {
      try {
         List<Redes> data = redesRepository.findAll();

         if (data.isEmpty()) {
            return "No data to process.";
         }

         Map<String, Long> viewsPerVideo = data.stream()
               .filter(r -> "view".equalsIgnoreCase(r.getAccion()))
               .collect(Collectors.groupingBy(Redes::getShortVideo, Collectors.counting()));

         Map<String, Long> likesPerVideo = data.stream()
               .filter(r -> "like".equalsIgnoreCase(r.getAccion()))
               .collect(Collectors.groupingBy(Redes::getShortVideo, Collectors.counting()));

         Map<String, Long> commentsPerVideo = data.stream()
               .filter(r -> "comment".equalsIgnoreCase(r.getAccion()))
               .collect(Collectors.groupingBy(Redes::getShortVideo, Collectors.counting()));

         Map<String, Long> sharesPerVideo = data.stream()
               .filter(r -> "share".equalsIgnoreCase(r.getAccion()))
               .collect(Collectors.groupingBy(Redes::getShortVideo, Collectors.counting()));

         Map<String, Long> userInteractions = data.stream()
               .collect(Collectors.groupingBy(Redes::getUsuario, Collectors.counting()));

         Map<String, Long> hourInteractions = data.stream()
               .collect(Collectors.groupingBy(r -> r.getHora().toString(), Collectors.counting()));

         StringBuilder result = new StringBuilder("# Resultados\n\n");

         viewsPerVideo.entrySet().stream().max(Map.Entry.comparingByValue())
               .ifPresent(e -> result.append("Video más visto: ").append(e.getKey()).append("\n\n"));

         likesPerVideo.entrySet().stream().max(Map.Entry.comparingByValue())
               .ifPresent(e -> result.append("Video con más likes: ").append(e.getKey()).append("\n\n"));

         commentsPerVideo.entrySet().stream().max(Map.Entry.comparingByValue())
               .ifPresent(e -> result.append("Video más comentado: ").append(e.getKey()).append("\n\n"));

         userInteractions.entrySet().stream().max(Map.Entry.comparingByValue())
               .ifPresent(e -> result.append("Usuario más recurrente: ").append(e.getKey()).append("\n\n"));

         hourInteractions.entrySet().stream().max(Map.Entry.comparingByValue())
               .ifPresent(e -> result.append("Hora con mayor interacción: ").append(e.getKey()).append("\n\n"));

         String bestRatioVideo = null;
         double bestRatio = -1.0;

         for (String video : viewsPerVideo.keySet()) {
            long views = viewsPerVideo.getOrDefault(video, 0L);
            if (views > 0) {
               long interactions = likesPerVideo.getOrDefault(video, 0L) +
                     commentsPerVideo.getOrDefault(video, 0L) +
                     sharesPerVideo.getOrDefault(video, 0L);
               double ratio = (double) interactions / views;
               if (ratio > bestRatio) {
                  bestRatio = ratio;
                  bestRatioVideo = video;
               }
            }
         }

         if (bestRatioVideo != null) {
            result.append(String.format("Mayor ratio de interacción: %s (%.2f)\n", bestRatioVideo, bestRatio));
         }

         return result.toString();

      } catch (Exception e) {
         return "Error ejecutando proceso analítico: " + e.getMessage();
      }
   }
}