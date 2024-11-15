/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.servidor.rmi;

/**
 *
 * @author jhavy
 */
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.File;

public class RMIServer implements RMIInterface {


    @Override
    public String processFile(String filePath) {
        // simulacion
        KMeansProcessor processor = new KMeansProcessor();
        return processor.process(filePath);
    }

    public static void main(String[] args) {
        try {

            RMIServer server = new RMIServer();
            RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(server, 0);


            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("RMIInterface", stub);

            System.out.println("Servidor RMI iniciado y esperando conexiones...");
        } catch (Exception e) {
            System.err.println("Error en el servidor RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

