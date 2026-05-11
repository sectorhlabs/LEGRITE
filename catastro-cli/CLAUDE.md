# Agente Catastro España

Este proyecto permite descargar automáticamente la **Consulta Descriptiva y Gráfica (CDG)** del Catastro español dada una dirección.

## Cómo funciona

Cuando el usuario te dé una dirección española (ej: "Calle Don Juan 61 2B, Sevilla"), debes:

1. **Parsear la dirección** extrayendo: provincia, municipio, nombre de vía, número, planta y puerta
2. **Ejecutar `catastro_agent.py`** SIN planta/puerta (el filtro del catastro no funciona y devuelve 0 resultados):
   ```bash
   python catastro_agent.py <PROVINCIA> <MUNICIPIO> "<NOMBRE_VIA>" <NUMERO>
   ```
   En Linux/Mac usar `python3` en vez de `python`.
3. **Identificar el inmueble correcto por posición**: los inmuebles se numeran secuencialmente en el edificio. En un edificio típico, los primeros son locales/garajes, luego van subiendo por planta y puerta (A, B, C...). Para Pl:02 Pt:B en un edificio con 3 puertas por planta, el inmueble sería aprox. el 7º-8º de la lista.
4. **Descargar solo ~3 candidatos probables con `catastro_download.js`** (Playwright), no todos:
   ```bash
   node catastro_download.js <REFCAT_20_CHARS> <COD_DELEGACION> <COD_MUNICIPIO>
   ```
   Lanzar las descargas en paralelo para mayor velocidad.
5. **Extraer texto de los PDFs con PyPDF2** para confirmar cuál es el correcto buscando el patrón `Pl:XX Pt:Y`:
   ```python
   from PyPDF2 import PdfReader
   reader = PdfReader("archivo.pdf")
   text = "".join(page.extract_text() or "" for page in reader.pages)
   # Buscar "Pl:02 Pt:B" en text
   ```
   En Windows `pdftotext` y la herramienta Read no funcionan para PDFs — usar siempre PyPDF2.
6. **Borrar todos los PDFs que no sean el correcto** (los `.html` de catastro_agent.py, los `.pdf` copiados, y los `_ok.pdf` de candidatos descartados). Solo debe quedar `CDG_<REFCAT>_ok.pdf` del inmueble solicitado. **Importante: borrar archivo por archivo, nunca con `rm *.pdf` o similar, para no eliminar el PDF correcto por error. Verificar con `ls` que el PDF final sigue presente tras la limpieza.**
7. El PDF final queda en `catastro_output/CDG_<REFCAT>_ok.pdf`

## Importante

- **NO uses los PDFs descargados por `catastro_agent.py`** (los `.html` que guarda via requests) — salen corruptos/en negro
- **USA SIEMPRE `catastro_download.js`** con Playwright para la descarga final del PDF
- **NO pases planta/puerta a `catastro_agent.py`** — el filtro no funciona y devuelve 0 resultados. Busca solo por dirección y número, luego filtra leyendo los PDFs
- **Limpia siempre `catastro_output/`** al final: borra los `.html` del agent y los PDFs de candidatos descartados. Solo debe quedar el `_ok.pdf` correcto
- Si el usuario no especifica provincia, pregúntale

## Dependencias

- Python 3 + requests + beautifulsoup4 + lxml
- Node.js + playwright (con Chromium)

## Instalación

- **Windows:** doble clic en `install.bat`
- **Linux/Mac:** `./install.sh`
