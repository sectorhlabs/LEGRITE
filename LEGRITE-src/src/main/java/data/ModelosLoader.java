/*
 * Decompiled with CFR 0.152.
 */
package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelosLoader {
    public static Map<String, List<String>> cargarModelosDeMaquinas(String carpetaPath) {
        HashMap<String, List<String>> modelosPorMaquina = new HashMap<String, List<String>>();
        File indexFile = new File(carpetaPath, "index.txt");
        if (!indexFile.exists()) {
            System.out.println("No se encontr\u00f3 el archivo index.txt en: " + indexFile.getAbsolutePath());
            return modelosPorMaquina;
        }
        try (BufferedReader indexReader = new BufferedReader(new FileReader(indexFile));){
            String archivoNombre;
            while ((archivoNombre = indexReader.readLine()) != null) {
                File archivoCsv = new File(carpetaPath, archivoNombre.trim());
                System.out.println("Procesando archivo CSV: " + archivoCsv.getAbsolutePath());
                if (!archivoCsv.exists()) {
                    System.out.println("No se encontr\u00f3 el archivo CSV: " + archivoCsv.getAbsolutePath());
                    continue;
                }
                List<String> modelos = ModelosLoader.leerModelosDesdeCsv(archivoCsv);
                if (modelos.isEmpty()) continue;
                modelosPorMaquina.put(archivoNombre.replace(".csv", ""), modelos);
            }
        }
        catch (IOException var17) {
            System.out.println("Error al leer el archivo index.txt.");
            var17.printStackTrace();
        }
        return modelosPorMaquina;
    }

    private static List<String> leerModelosDesdeCsv(File archivoCsv) {
        ArrayList<String> modelos = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivoCsv));){
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                System.out.println("L\u00ednea " + ++lineNumber + ": " + line);
                if (lineNumber != 1) continue;
                String[] columnas = line.split(";");
                if (columnas.length > 1 && columnas[0].trim().equalsIgnoreCase("Modelo")) {
                    for (int i = 1; i < columnas.length; ++i) {
                        String modelo = columnas[i].trim();
                        modelos.add(modelo);
                    }
                    System.out.println("Modelos cargados del archivo " + archivoCsv.getName() + ": " + String.valueOf(modelos));
                    continue;
                }
                System.out.println("Cabecera inv\u00e1lida en el archivo: " + archivoCsv.getName());
            }
        }
        catch (IOException var18) {
            System.out.println("Error al leer el archivo CSV: " + archivoCsv.getName());
            var18.printStackTrace();
        }
        return modelos;
    }

    public static String obtenerDatosDelModelo(String carpetaPath, String nombreMaquina, String modeloSeleccionado) {
        String string;
        File archivoCsv = new File(carpetaPath, nombreMaquina + ".csv");
        System.out.println("Buscando archivo CSV: " + archivoCsv.getAbsolutePath());
        if (!archivoCsv.exists()) {
            return "No se encontr\u00f3 el archivo para la m\u00e1quina: " + nombreMaquina;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivoCsv))) {
            StringBuilder datos = new StringBuilder();
            String[] headers = null;
            String line = br.readLine();
            if (line != null) {
                headers = line.split(";");
            }
            while ((line = br.readLine()) != null) {
                String[] partes = line.split(";");
                if (partes.length <= 1 || !partes[0].equalsIgnoreCase(modeloSeleccionado)) continue;
                for (int i = 0; i < partes.length; ++i) {
                    if (headers == null || i >= headers.length) continue;
                    datos.append(headers[i]).append(": ").append(partes[i]).append("\n");
                }
            }
            string = datos.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error al leer los datos del modelo: " + modeloSeleccionado;
        }
        return string;
    }
}
