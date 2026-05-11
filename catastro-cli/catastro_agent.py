#!/usr/bin/env python3
"""
Agente de Catastro de España.
Dada una dirección, descarga la Consulta Descriptiva y Gráfica (CDG) de los inmuebles.
"""

import requests
from bs4 import BeautifulSoup
import re
import json
import os
import sys
import time

BASE_URL = "https://www1.sedecatastro.gob.es"


def crear_sesion():
    session = requests.Session()
    session.headers.update({
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36",
        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "Accept-Language": "es-ES,es;q=0.9",
        "Referer": BASE_URL + "/CYCBienInmueble/OVCBusqueda.aspx",
    })
    session.get(BASE_URL + "/CYCBienInmueble/OVCBusqueda.aspx")
    time.sleep(1)
    return session


def obtener_provincia(session, nombre_provincia):
    url = BASE_URL + "/CYCBienInmueble/OVCBusqueda.aspx/ObtenerProvincias"
    headers = {"Content-Type": "application/json; charset=utf-8"}
    body = json.dumps({"filtro": nombre_provincia.upper()})
    resp = session.post(url, data=body, headers=headers)
    resp.raise_for_status()
    data = resp.json()["d"]
    if not data:
        raise ValueError(f"Provincia '{nombre_provincia}' no encontrada")
    for p in data:
        if p["Denominacion"].upper() == nombre_provincia.upper():
            return p["Codigo"], p["Denominacion"]
    return data[0]["Codigo"], data[0]["Denominacion"]


def obtener_municipio(session, nombre_municipio, cod_provincia):
    url = BASE_URL + "/CYCBienInmueble/OVCBusqueda.aspx/ObtenerMunicipios"
    headers = {"Content-Type": "application/json; charset=utf-8"}
    body = json.dumps({"filtro": nombre_municipio.upper(), "provincia": cod_provincia})
    resp = session.post(url, data=body, headers=headers)
    resp.raise_for_status()
    data = resp.json()["d"]
    if not data:
        raise ValueError(f"Municipio '{nombre_municipio}' no encontrado")
    for m in data:
        if m["Denominacion"].upper() == nombre_municipio.upper():
            return m["Codigo"], m["Denominacion"]
    return data[0]["Codigo"], data[0]["Denominacion"]


def obtener_via(session, nombre_via, cod_provincia, cod_municipio):
    url = BASE_URL + "/CYCBienInmueble/OVCBusqueda.aspx/ObtenerVias"
    headers = {"Content-Type": "application/json; charset=utf-8"}
    body = json.dumps({
        "filtro": nombre_via.upper(),
        "provincia": cod_provincia,
        "municipio": cod_municipio
    })
    resp = session.post(url, data=body, headers=headers)
    resp.raise_for_status()
    data = resp.json()["d"]
    if not data:
        raise ValueError(f"Via '{nombre_via}' no encontrada")
    # Buscar coincidencia exacta por denominacion
    for v in data:
        if v["Denominacion"].upper() == nombre_via.upper():
            return v
    # Si no hay exacta, devolver la primera
    if len(data) > 1:
        print("Varias vias encontradas, usando la primera:")
        for i, v in enumerate(data):
            print(f"  [{i}] {v['DenominacionCompleta']}")
    return data[0]


def obtener_lista_inmuebles(session, cod_provincia, cod_municipio, desc_prov, desc_muni,
                             cod_via, nombre_via, tipo_via, numero,
                             bloque="", escalera="", planta="", puerta=""):
    nombre_via_at = nombre_via.replace(" ", "@")
    params = {
        "via": nombre_via_at,
        "tipoVia": tipo_via,
        "numero": numero,
        "kilometro": "",
        "bloque": bloque,
        "escalera": escalera,
        "planta": planta,
        "puerta": puerta,
        "DescProv": desc_prov,
        "prov": cod_provincia,
        "muni": cod_municipio,
        "DescMuni": desc_muni,
        "TipUR": "U",
        "codvia": cod_via,
        "comVia": f"{nombre_via} ({tipo_via})",
        "pest": "urbana",
        "from": "OVCBusqueda",
        "nomusu": " ",
        "tipousu": "",
        "ZV": "NO",
        "ZR": "NO",
        "anyoZV": "",
        "tematicos": "",
        "anyotem": "",
        "historica": "",
        "coordinadas": "",
    }
    url = BASE_URL + "/CYCBienInmueble/OVCListaBienes.aspx"
    resp = session.get(url, params=params)
    resp.raise_for_status()

    soup = BeautifulSoup(resp.text, "lxml")

    parcela_rc = None
    panel_sec = soup.find(class_="panel-sec")
    if panel_sec:
        match = re.search(r'PARCELA CATASTRAL ([A-Z0-9]+)', panel_sec.get_text())
        if match:
            parcela_rc = match.group(1)

    refs_inmuebles = []
    pattern = re.compile(r"CargarBien\('(\d+)','(\d+)','([UR])','([A-Z0-9]{20})'")
    for tag in soup.find_all(onclick=True):
        m = pattern.search(tag.get("onclick", ""))
        if m:
            refs_inmuebles.append({
                "del": m.group(1),
                "mun": m.group(2),
                "tipo": m.group(3),
                "refcat": m.group(4),
                "texto": tag.get_text(strip=True)
            })

    seen = set()
    unique_refs = []
    for r in refs_inmuebles:
        if r["refcat"] not in seen:
            seen.add(r["refcat"])
            unique_refs.append(r)

    return parcela_rc, unique_refs


def descargar_cdg(session, del_cod, mun_cod, refcat, carpeta_salida="."):
    params = {"del": del_cod, "mun": mun_cod, "refcat": refcat}
    url = BASE_URL + "/CYCBienInmueble/SECImprimirCroquisYDatos.aspx"
    resp = session.get(url, params=params)
    resp.raise_for_status()

    os.makedirs(carpeta_salida, exist_ok=True)
    filename = os.path.join(carpeta_salida, f"CDG_{refcat}.html")
    with open(filename, "w", encoding="utf-8") as f:
        f.write(resp.text)
    print(f"  -> Guardado: {filename}")
    return filename


def agente_catastro(direccion: dict, carpeta_salida: str = "catastro_output"):
    print(f"\nBuscando: {direccion}\n")
    session = crear_sesion()

    cod_prov, desc_prov = obtener_provincia(session, direccion["provincia"])
    print(f"[1/5] Provincia: {desc_prov} (cod: {cod_prov})")
    time.sleep(0.5)

    cod_muni, desc_muni = obtener_municipio(session, direccion["municipio"], cod_prov)
    print(f"[2/5] Municipio: {desc_muni} (cod: {cod_muni})")
    time.sleep(0.5)

    via = obtener_via(session, direccion["nombre_via"], cod_prov, cod_muni)
    print(f"[3/5] Via: {via['DenominacionCompleta']} (cod: {via['Codigo']})")
    time.sleep(0.5)

    parcela_rc, inmuebles = obtener_lista_inmuebles(
        session,
        cod_prov, cod_muni, desc_prov, desc_muni,
        via["Codigo"], via["Denominacion"], via["Sigla"],
        direccion["numero"],
        direccion.get("bloque", ""),
        direccion.get("escalera", ""),
        direccion.get("planta", ""),
        direccion.get("puerta", "")
    )
    print(f"[4/5] Parcela: {parcela_rc}")
    print(f"      Inmuebles encontrados: {len(inmuebles)}")
    time.sleep(0.5)

    if not inmuebles:
        print("No se encontraron inmuebles en esa direccion.")
        return []

    print(f"\n[5/5] Descargando CDGs...\n")
    descargados = []
    for inmueble in inmuebles:
        print(f"  -> {inmueble['refcat']} - {inmueble['texto']}")
        try:
            f = descargar_cdg(session, inmueble["del"], inmueble["mun"],
                              inmueble["refcat"], carpeta_salida)
            descargados.append(f)
            time.sleep(1)
        except Exception as e:
            print(f"  X Error: {e}")

    print(f"\nDescargados {len(descargados)} documentos en '{carpeta_salida}/'")
    return descargados


if __name__ == "__main__":
    if len(sys.argv) < 4:
        print("Uso: python catastro_agent.py <provincia> <municipio> <via> <numero> [planta] [puerta]")
        print("Ejemplo: python catastro_agent.py MADRID MADRID 'GRAN VIA' 1")
        sys.exit(1)

    direccion = {
        "provincia": sys.argv[1],
        "municipio": sys.argv[2],
        "nombre_via": sys.argv[3],
        "numero": sys.argv[4],
    }
    if len(sys.argv) > 5:
        direccion["planta"] = sys.argv[5]
    if len(sys.argv) > 6:
        direccion["puerta"] = sys.argv[6]

    agente_catastro(direccion)
