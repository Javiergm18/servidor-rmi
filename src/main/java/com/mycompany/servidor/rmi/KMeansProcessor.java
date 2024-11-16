/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.servidor.rmi;

/**
 *
 * @author jhavy
 */
import java.io.File;

public class KMeansProcessor {

    public String process(String filePath) {

        File file = new File(filePath);
        if (file.exists()) {

            return "El archivo " + file.getName() + " ha sido procesado exitosamente con k-means.";
        } else {
            return "Error: el archivo no existe.";
        }
    }
}
