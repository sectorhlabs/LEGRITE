const { chromium } = require('playwright');
const path = require('path');

const BASE_URL = 'https://www1.sedecatastro.gob.es';
const OUTPUT_DIR = path.join(__dirname, 'catastro_output');

async function descargarCDG(refcat, del, mun) {
    const browser = await chromium.launch({ headless: true });
    const context = await browser.newContext({ acceptDownloads: true });
    const page = await context.newPage();

    // Iniciar sesion visitando la pagina principal
    console.log('Iniciando sesion...');
    await page.goto(BASE_URL + '/CYCBienInmueble/OVCBusqueda.aspx', { waitUntil: 'networkidle' });
    await page.waitForTimeout(2000);

    // Visitar la ficha del inmueble (establece contexto)
    console.log('Accediendo a ficha del inmueble...');
    const fichaUrl = `${BASE_URL}/CYCBienInmueble/OVCConCiud.aspx?del=${del}&mun=${mun}&UrbRus=U&RefC=${refcat}&from=OVCBusqueda&ZV=NO&anyoZV=`;
    await page.goto(fichaUrl, { waitUntil: 'networkidle' });
    await page.waitForTimeout(2000);

    // Descargar el CDG capturando la descarga
    console.log('Descargando CDG...');
    const cdgUrl = `${BASE_URL}/CYCBienInmueble/SECImprimirCroquisYDatos.aspx?del=${del}&mun=${mun}&refcat=${refcat}`;

    const [download] = await Promise.all([
        page.waitForEvent('download'),
        page.goto(cdgUrl).catch(() => {}),
    ]);

    const outputPath = path.join(OUTPUT_DIR, `CDG_${refcat}_ok.pdf`);
    await download.saveAs(outputPath);
    console.log(`Guardado: ${outputPath}`);

    await browser.close();
    return outputPath;
}

const refcat = process.argv[2] || '9335001TG3493N0005UI';
const del = process.argv[3] || '41';
const mun = process.argv[4] || '900';

descargarCDG(refcat, del, mun).catch(console.error);
