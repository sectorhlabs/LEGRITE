# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repositorio

Monorepo con dos proyectos independientes:

- **LEGRITE-src/** — Aplicación Java 17 / Maven. Genera memorias y certificados de instalaciones térmicas rellenando formularios AcroForm de plantillas PDF. Tiene UI Swing (`ui.MainFrame`) y CLI (`cli.LegriteCLI`).
- **catastro-cli/** — Utilidad Python + Node (Playwright) para descargar la Consulta Descriptiva y Gráfica del Catastro español. Tiene su propio `CLAUDE.md` con el flujo operativo del agente; consúltalo antes de tocar nada ahí.

Los `.jar` en la raíz (`LEGRITE-0.5.jar`, `LEGRITE-cli.jar`) son artefactos prebuild distribuibles, no se editan.

## Build y ejecución (LEGRITE-src)

```bash
cd LEGRITE-src && mvn package          # produce target/LEGRITE-0.2.jar (fat jar via shade)
./legrite                              # lanzador bash que ejecuta cli.LegriteCLI
java -jar LEGRITE-src/target/LEGRITE-0.2.jar    # arranca la GUI Swing (ui.MainFrame)
```

CLI:
```bash
java -cp LEGRITE-src/target/LEGRITE-0.2.jar cli.LegriteCLI \
    --json datos.json --plantillas Modelos/ --salida salida/
java -cp ... cli.LegriteCLI --campos          # lista campos AcroForm soportados
cat datos.json | java -cp ... cli.LegriteCLI --json - --plantillas Modelos/ --salida salida/
```

No hay tests definidos en el `pom.xml`. Si añades lógica no trivial, crea el módulo de tests (JUnit) antes.

## Arquitectura LEGRITE

Flujo único: **datos en memoria → plantilla PDF con AcroForm → PDF rellenado**. Hay dos frontends sobre el mismo motor:

- `data.PDFProcessor` — núcleo. Itera todos los PDF en `inputPath`, abre con PDFBox 3 (`Loader.loadPDF`), y para cada `PDField` cuyo `getFullyQualifiedName()` aparece como clave en el `Map<String,String>` de datos, escribe el valor. Plantillas sin AcroForm se copian sin modificar.
- `cli.LegriteCLI` — entrypoint headless. Tiene un **parser JSON minimalista propio** (sin Jackson/Gson) que solo soporta objetos planos `{"k":"v"}`. Si necesitas valores anidados o arrays, añade una dependencia o amplía el parser; no asumas que funciona.
- `ui.MainFrame` + `ui.*Panel` — entrypoint Swing. Pestañas: Titular, Empresa+Instalador, Local 1-4, Rutas. Cada panel reúne campos cuyos nombres mapean 1:1 a nombres de campos AcroForm. `RutaPanel` recolecta todo en un `Map` y delega en `PDFProcessor`. `MainFrame.enableTabReordering` implementa drag&drop de pestañas vía glassPane.
- `data.DataLoader` / `data.CsvReader` / `data.MaquinasCsvReader` — cargan los CSV de `resources/` al arrancar la GUI. El parser usa `split(",")` simple, así que **comas dentro de campos rompen el parsing** (ver `Empresas.csv` real con direcciones).
- `data.ModelosLoader` — usado por los paneles de Local para autocompletar modelos de máquinas.

### Convenciones críticas

- **Los nombres de campo del JSON / del Map deben coincidir exactamente con el `FullyQualifiedName` del AcroForm en el PDF.** No hay capa de mapeo. `LegriteCLI --campos` imprime el catálogo canónico esperado.
- Varios `.java` llevan cabecera `Decompiled with CFR 0.152.` — el código fue descompilado de un jar anterior y reintegrado. Trátalos como código fuente normal pero no te sorprendas con estilos peculiares (locales `var19`, switches sobre `int` para constantes Swing como `JOptionPane.ERROR_MESSAGE`, etc.).
- **Layout de runtime esperado** (`MainFrame.obtenerDirectorioRecursos`): el jar busca una carpeta `resources/` *hermana* del jar, no embebida. Debe contener `Instaladores.csv`, `Empresas.csv`, `maquinas/index.txt` (lista de CSV de modelos por marca) y los iconos PNG. Si falta alguno, la GUI sale con `System.exit(1)`. Para desarrollo local desde `target/`, copia o symlinka `resources/` al lado del jar.
- `pom.xml` copia `Instaladores.csv`, `Empresas.csv` y `maquinas/` desde la raíz del módulo (`LEGRITE-src/`) a `target/resources/` durante `package`. La carpeta `resources/` real con datos vive en la **raíz del repo** (`/resources/`), no en `LEGRITE-src/`. Si reorganizas, ajusta el `maven-resources-plugin` y el `legrite` launcher.
- `Modelos/` (raíz) contiene las plantillas PDF maestras con AcroForm. Los códigos de prefijo (`AN_`, `CA_MA_`, `CT_`, `PV_`, etc.) corresponden a tipos de instalación térmica regulados (Aire Acondicionado, Calefacción Mayor, Climatización, etc.).
- `legrite.bat` apunta a `LEGRITE-cli.jar` en raíz; `legrite` (bash) apunta a `LEGRITE-src/target/LEGRITE-0.2.jar`. Versiones desincronizadas — al actualizar la versión en `pom.xml` revisa ambos lanzadores.

## catastro-cli

Ver `catastro-cli/CLAUDE.md` para el protocolo de descarga (parseo de dirección → `catastro_agent.py` sin planta/puerta → identificación posicional → `catastro_download.js` con Playwright en paralelo → verificación con PyPDF2 → limpieza archivo a archivo). Reglas no obvias:
- No usar los PDFs guardados por `catastro_agent.py` (salen corruptos).
- Pasar planta/puerta al agent devuelve 0 resultados; siempre filtrar a posteriori leyendo PDFs.
- Limpieza final: borrar archivo por archivo, nunca con globs (`rm *.pdf`).
