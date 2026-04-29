import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LoggerThread implements Runnable {

    private Cluster cluster;
    private long timer;

    public LoggerThread(Cluster cluster, long timer) {
        this.cluster = cluster;
        this.timer = timer;
    }

    @Override
    public void run() {
        // Usamos try-with-resources para que el archivo se cierre solo al final
        try (PrintWriter writer = new PrintWriter(new FileWriter("log_sistema_cluster.txt"))) {

            writer.println("=== INICIO DE LOG DE SISTEMA CLUSTER ===");

            while (true) {
                try {
                    Thread.sleep(200); // Frecuencia de muestreo según consigna

                    // Captura de datos fresca
                    int fallidos = cluster.getCantidadFallidos();
                    int validados = cluster.getCantidadValidados();
                    int total = cluster.getTotalFinal();
                    boolean vacio = cluster.sistemaVacio();

                    String metrica = String.format(
                            "Cola: %d | Ejec: %d | Final: %d | Fallidos: %d | Validados: %d",
                            cluster.getColaSize(),
                            cluster.getEjecucionSize(),
                            cluster.getFinalizadosSize(),
                            fallidos,
                            validados
                    );

                    // Imprimimos en consola y grabamos en el archivo
                    System.out.println(metrica);
                    writer.println(metrica);

                    // Condición de cierre definitiva
                    if (total == 500 && vacio) {
                        long fin = System.currentTimeMillis();
                        double segundos = (fin - timer) / 1000.0;

                        String reporteFinal = "\n--- REPORTE FINAL ---\n" +
                                "Jobs Totales Procesados: " + total + "\n" +
                                "Validados con éxito: " + validados + "\n" +
                                "Fallidos/Inconsistentes: " + fallidos + "\n" +
                                "Tiempo total de ejecución: " + segundos + " segundos\n" +
                                "PROCESAMIENTO TERMINADO";
                        // Obtenemos el uso de los 200 nodos
                        String statsNodos = cluster.obtenerEstadisticasNodos();

                        // Lo mandamos a ambos lados
                        System.out.println(reporteFinal);
                        System.out.println(statsNodos);

                        writer.println(reporteFinal);
                        writer.println(statsNodos);

                        System.out.println("PROCESAMIENTO TERMINADO - Archivo log_sistema_cluster.txt generado.");
                        break;
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            writer.println("\n=== FIN DEL LOG ===");

        } catch (IOException e) {
            System.err.println("Error al escribir el archivo de log: " + e.getMessage());
        }
    }
}