package cli;

import data.PDFProcessor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LegriteCLI {

    public static void main(String[] args) {
        if (args.length < 3) {
            printUsage();
            System.exit(1);
        }

        String jsonPath = null;
        String inputPath = null;
        String outputPath = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--json":
                    if (i + 1 < args.length) jsonPath = args[++i];
                    break;
                case "--plantillas":
                    if (i + 1 < args.length) inputPath = args[++i];
                    break;
                case "--salida":
                    if (i + 1 < args.length) outputPath = args[++i];
                    break;
                case "--campos":
                    printCampos();
                    System.exit(0);
                    break;
                case "--help":
                    printUsage();
                    System.exit(0);
                    break;
            }
        }

        if (jsonPath == null || inputPath == null || outputPath == null) {
            System.err.println("Error: faltan argumentos obligatorios.");
            printUsage();
            System.exit(1);
        }

        // Leer JSON
        Map<String, String> formData;
        try {
            String jsonContent;
            if (jsonPath.equals("-")) {
                jsonContent = readStdin();
            } else {
                jsonContent = readFile(jsonPath);
            }
            formData = parseJson(jsonContent);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON: " + e.getMessage());
            System.exit(1);
            return;
        }

        // Validar rutas
        File inputDir = new File(inputPath);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            System.err.println("Error: la carpeta de plantillas no existe: " + inputPath);
            System.exit(1);
        }

        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Procesar PDFs
        PDFProcessor processor = new PDFProcessor();
        try {
            processor.procesarPDFs(inputPath, outputPath, formData);
            System.out.println("OK: PDFs generados en " + outputPath);
            System.out.println("Campos rellenados: " + formData.size());
        } catch (IOException e) {
            System.err.println("Error al procesar PDFs: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String readStdin() throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static String readFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * Parser JSON minimalista para Map<String, String> sin dependencias externas.
     * Soporta: {"clave": "valor", "clave2": "valor2"}
     */
    static Map<String, String> parseJson(String json) {
        HashMap<String, String> map = new HashMap<>();
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new IllegalArgumentException("JSON invalido: debe ser un objeto {}");
        }
        json = json.substring(1, json.length() - 1).trim();
        if (json.isEmpty()) return map;

        int i = 0;
        while (i < json.length()) {
            // Buscar clave
            i = json.indexOf('"', i);
            if (i == -1) break;
            int keyStart = i + 1;
            int keyEnd = findClosingQuote(json, keyStart);
            String key = unescape(json.substring(keyStart, keyEnd));
            i = keyEnd + 1;

            // Buscar ':'
            i = json.indexOf(':', i);
            if (i == -1) break;
            i++;

            // Saltar espacios
            while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;

            // Leer valor
            String value;
            if (i < json.length() && json.charAt(i) == '"') {
                int valStart = i + 1;
                int valEnd = findClosingQuote(json, valStart);
                value = unescape(json.substring(valStart, valEnd));
                i = valEnd + 1;
            } else {
                // valor no string (null, number, etc) - leer hasta , o }
                int valStart = i;
                while (i < json.length() && json.charAt(i) != ',' && json.charAt(i) != '}') i++;
                value = json.substring(valStart, i).trim();
                if (value.equals("null")) value = "";
            }

            map.put(key, value);

            // Buscar siguiente ',' o fin
            while (i < json.length() && (json.charAt(i) == ',' || Character.isWhitespace(json.charAt(i)))) i++;
        }

        return map;
    }

    private static int findClosingQuote(String s, int start) {
        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) == '\\') {
                i++; // saltar caracter escapado
            } else if (s.charAt(i) == '"') {
                return i;
            }
        }
        throw new IllegalArgumentException("Comilla de cierre no encontrada desde posicion " + start);
    }

    private static String unescape(String s) {
        return s.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\t", "\t");
    }

    private static void printUsage() {
        System.out.println("LEGRITE CLI - Generador de documentos de instalaciones termicas");
        System.out.println();
        System.out.println("Uso:");
        System.out.println("  java -cp LEGRITE.jar cli.LegriteCLI --json <datos.json> --plantillas <carpeta> --salida <carpeta>");
        System.out.println();
        System.out.println("Opciones:");
        System.out.println("  --json <ruta>        Archivo JSON con los datos (usar '-' para stdin)");
        System.out.println("  --plantillas <ruta>  Carpeta con las plantillas PDF");
        System.out.println("  --salida <ruta>      Carpeta donde guardar los PDFs generados");
        System.out.println("  --campos             Mostrar todos los campos disponibles");
        System.out.println("  --help               Mostrar esta ayuda");
        System.out.println();
        System.out.println("Ejemplo:");
        System.out.println("  java -cp LEGRITE.jar cli.LegriteCLI --json datos.json --plantillas Modelos/ --salida salida/");
        System.out.println();
        System.out.println("Ejemplo con Claude:");
        System.out.println("  cat datos.json | java -cp LEGRITE.jar cli.LegriteCLI --json - --plantillas Modelos/ --salida salida/");
    }

    private static void printCampos() {
        System.out.println("=== CAMPOS DISPONIBLES PARA EL JSON ===");
        System.out.println();
        System.out.println("--- DATOS DEL TITULAR (TI) ---");
        System.out.println("  TI1   Primer apellido del titular");
        System.out.println("  TI2   Segundo apellido del titular");
        System.out.println("  TI3   Nombre o razon social");
        System.out.println("  TI4   NIF del titular");
        System.out.println("  TI6   Tipo de via (titular)");
        System.out.println("  TI7   Nombre de via (titular)");
        System.out.println("  TI8   Numero (titular)");
        System.out.println("  TI9   Bloque (titular)");
        System.out.println("  TI10  Portal (titular)");
        System.out.println("  TI11  Escalera (titular)");
        System.out.println("  TI12  Piso (titular)");
        System.out.println("  TI13  Puerta (titular)");
        System.out.println("  TI14  Localidad (titular)");
        System.out.println("  TI15  Provincia (titular)");
        System.out.println("  TI16  Codigo postal (titular)");
        System.out.println("  TI17  Telefono (titular)");
        System.out.println("  TI18  E-mail (titular)");
        System.out.println();
        System.out.println("--- DATOS DE LA INSTALACION (DI) ---");
        System.out.println("  DI6   Tipo de via (instalacion)");
        System.out.println("  DI7   Nombre de via (instalacion)");
        System.out.println("  DI8   Numero (instalacion)");
        System.out.println("  DI9   Bloque (instalacion)");
        System.out.println("  DI10  Portal (instalacion)");
        System.out.println("  DI11  Escalera (instalacion)");
        System.out.println("  DI12  Piso (instalacion)");
        System.out.println("  DI13  Puerta (instalacion)");
        System.out.println("  DI14  Localidad (instalacion)");
        System.out.println("  DI15  Provincia (instalacion)");
        System.out.println("  DI16  Codigo postal (instalacion)");
        System.out.println("  DI17  Numero de locales climatizados");
        System.out.println("  DI18  Vivienda aislada");
        System.out.println("  DI19  Vivienda en bloque");
        System.out.println("  DI20  Numero de dormitorios: 1");
        System.out.println("  DI21  Numero de dormitorios: 2");
        System.out.println("  DI22  Numero de dormitorios: 3");
        System.out.println("  DI23  Numero de dormitorios: 4");
        System.out.println("  DI24  Ubicacion de la condensadora");
        System.out.println();
        System.out.println("--- DATOS DEL INSTALADOR (IA) ---");
        System.out.println("  IA1   Primer apellido del instalador");
        System.out.println("  IA2   Segundo apellido del instalador");
        System.out.println("  IA3   Nombre del instalador");
        System.out.println("  IA4   NIF del instalador");
        System.out.println("  IA5   Numero de carnet de instalador");
        System.out.println("  IA6   Tipo de via (instalador)");
        System.out.println("  IA7   Nombre de via (instalador)");
        System.out.println("  IA8   Numero (instalador)");
        System.out.println("  IA9   Bloque (instalador)");
        System.out.println("  IA10  Portal (instalador)");
        System.out.println("  IA11  Escalera (instalador)");
        System.out.println("  IA12  Piso (instalador)");
        System.out.println("  IA13  Puerta (instalador)");
        System.out.println("  IA14  Localidad (instalador)");
        System.out.println("  IA15  Provincia (instalador)");
        System.out.println("  IA16  Codigo postal (instalador)");
        System.out.println("  IA17  Telefono (instalador)");
        System.out.println("  IA18  E-mail (instalador)");
        System.out.println();
        System.out.println("--- DATOS DE LA EMPRESA (EI) ---");
        System.out.println("  REFEI Referencia empresa");
        System.out.println("  EI1   Primer apellido (empresa)");
        System.out.println("  EI2   Segundo apellido (empresa)");
        System.out.println("  EI3   Nombre o razon social (empresa)");
        System.out.println("  EI4   CIF (empresa)");
        System.out.println("  EI5   Numero de Registro (empresa)");
        System.out.println("  EI6   Tipo de via (empresa)");
        System.out.println("  EI7   Nombre de via (empresa)");
        System.out.println("  EI8   Numero (empresa)");
        System.out.println("  EI9   Bloque (empresa)");
        System.out.println("  EI10  Portal (empresa)");
        System.out.println("  EI11  Escalera (empresa)");
        System.out.println("  EI12  Piso (empresa)");
        System.out.println("  EI13  Puerta (empresa)");
        System.out.println("  EI14  Localidad (empresa)");
        System.out.println("  EI15  Provincia (empresa)");
        System.out.println("  EI16  Codigo Postal (empresa)");
        System.out.println("  EI17  Telefono (empresa)");
        System.out.println("  EI18  Email (empresa)");
        System.out.println();
        System.out.println("--- DATOS DEL LOCAL 1 (sufijo _1) ---");
        System.out.println("  NLC_1    Nombre del local");
        System.out.println("  MRAA_1   Marca de la maquina");
        System.out.println("  MAA1_1   Modelo de la maquina");
        System.out.println("  MAA2_1   Potencia calefaccion kW");
        System.out.println("  MAA3_1   SCOP");
        System.out.println("  MAA4_1   Potencia frigorifica kW");
        System.out.println("  MAA5_1   Potencia de compresor kW");
        System.out.println("  MAA6_1   Prestacion energetica");
        System.out.println("  MAA7_1   SEER");
        System.out.println("  MAA8_1   Potencia electrica nominal kW");
        System.out.println("  MAA9_1   Tipo de maquina");
        System.out.println("  MAA10_1  Ficha tecnica");
        System.out.println("  MAA11_1  COP");
        System.out.println("  MAA12_1  EER");
        System.out.println("  MAA13_1  Declaracion conformidad");
        System.out.println("  MAA14_1  Tuberia gas");
        System.out.println("  MAA15_1  Tuberia liquido");
        System.out.println("  MAA16_1  Carga de refrigerante Kg");
        System.out.println();
        System.out.println("--- DATOS DEL LOCAL 2/3/4 (sufijo _2, _3, _4) ---");
        System.out.println("  (Mismos campos que Local 1 pero con sufijo _2, _3 o _4)");
    }
}
