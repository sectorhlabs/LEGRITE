/*
 * Decompiled with CFR 0.152.
 */
package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLoader {
    public static Map<String, String[]> cargarInstaladores(String filePath) {
        HashMap<String, String[]> instaladoresData = new HashMap<String, String[]>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath));){
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 3) {
                    String fullName = parts[0] + " " + parts[1] + " " + parts[2];
                    instaladoresData.put(fullName, parts);
                    continue;
                }
                System.out.println("L\u00ednea inv\u00e1lida en Instaladores.csv: " + line);
            }
        }
        catch (IOException var16) {
            System.out.println("Error al leer el archivo CSV: " + filePath);
            var16.printStackTrace();
        }
        return instaladoresData;
    }

    public static Map<String, String[]> cargarEmpresas(String filePath) {
        HashMap<String, String[]> empresasData = new HashMap<String, String[]>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath));){
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 4) {
                    empresasData.put(parts[0], parts);
                    continue;
                }
                System.out.println("L\u00ednea inv\u00e1lida en Empresas.csv: " + line);
            }
        }
        catch (IOException var15) {
            System.out.println("Error al leer el archivo CSV: " + filePath);
            var15.printStackTrace();
        }
        return empresasData;
    }

    public static Map<String, List<String>> cargarModelosDeMaquinas(String carpetaPath) {
        HashMap<String, List<String>> modelosPorMaquina = new HashMap<String, List<String>>();
        String indexPath = carpetaPath + "/index.txt";
        try (BufferedReader indexReader = new BufferedReader(new FileReader(indexPath))) {
            String archivoNombre;
            while ((archivoNombre = indexReader.readLine()) != null) {
                System.out.println("Leyendo archivo index.txt, archivo listado: " + archivoNombre);
                String archivoPath = carpetaPath + "/" + archivoNombre;
                System.out.println("Buscando archivo CSV: " + archivoPath);
                try (BufferedReader br = new BufferedReader(new FileReader(archivoPath))) {
                    String line;
                    ArrayList<String> modelos = new ArrayList<String>();
                    while ((line = br.readLine()) != null) {
                        if (!line.startsWith("Modelo,")) continue;
                        String[] partes = line.split(",");
                        modelos.addAll(Arrays.asList(partes).subList(1, partes.length));
                    }
                    modelosPorMaquina.put(archivoNombre.replace(".csv", ""), modelos);
                } catch (IOException e) {
                    System.out.println("Error al leer el archivo: " + archivoNombre);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el \u00edndice de archivos.");
            e.printStackTrace();
        }
        return modelosPorMaquina;
    }
}
