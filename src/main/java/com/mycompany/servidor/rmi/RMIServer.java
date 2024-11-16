package com.mycompany.servidor.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.net.*;
import java.util.*;

public class RMIServer implements RMIInterface {

    @Override
    public String processFile(String filePath) {
        try {

            return "Archivo " + filePath + " procesado con éxito.";
        } catch (Exception e) {
            return "Error al procesar el archivo: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        try {

            RMIServer server = new RMIServer();
            RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("RMIInterface", stub);
            System.out.println("Servidor RMI iniciado en el puerto 1099...");

            Thread tcpServerThread = new Thread(() -> startTCPServer());
            tcpServerThread.start();

        } catch (Exception e) {
            System.err.println("Error en el servidor RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void startTCPServer() {
        try (ServerSocket serverSocket = new ServerSocket(5050)) {
            System.out.println("Servidor TCP escuchando en el puerto 5050...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                // Manejar la conexión en un nuevo hilo
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor TCP: " + e.getMessage());
        }
    }
    public static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
            String filePath = in.readLine();
            System.out.println("Archivo recibido: " + filePath);
    
            String result = "Archivo " + filePath + " procesado con éxito.";
            System.out.println("Resultado generado: " + result);
    
            out.println(result);  
            System.out.println("Resultado enviado al cliente.");
            out.flush();
        } catch (IOException e) {
            System.err.println("Error al manejar cliente: " + e.getMessage());
        }
    }
}    