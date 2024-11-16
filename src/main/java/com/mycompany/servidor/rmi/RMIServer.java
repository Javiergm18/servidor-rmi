import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import mpi.*;

public class RMIServer implements RMIInterface {

    @Override
    public String processFile(String filePath) {
        try {

            String sharedDir = "/home/nodo1/mpi_java/";
            Path destPath = Paths.get(sharedDir, Paths.get(filePath).getFileName().toString());
            Files.copy(Paths.get(filePath), destPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Archivo guardado en: " + destPath);

            String mpiCommand = String.format(
                "mpirun -np 4 --hostfile /home/nodo1/hosts java -cp %s %s",
                sharedDir, getClassName(destPath.toString())
            );

            System.out.println("Ejecutando comando MPI: " + mpiCommand);
            Process mpiProcess = Runtime.getRuntime().exec(mpiCommand);

            BufferedReader reader = new BufferedReader(new InputStreamReader(mpiProcess.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = mpiProcess.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Error en la ejecución MPI. Código de salida: " + exitCode);
            }

            System.out.println("Resultado de MPI:\n" + output);
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al procesar el archivo: " + e.getMessage();
        }
    }

    private String getClassName(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public static void main(String[] args) {
        try {
            RMIServer server = new RMIServer();
            RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("RMIInterface", stub);
            System.out.println("Servidor RMI iniciado en el puerto 1099...");
        } catch (Exception e) {
            System.err.println("Error en el servidor RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
