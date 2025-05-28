$(document).ready(function () {
    console.log("Documento listo, iniciando carga de datos...");
    inicializarTablas();
    cargarDatos();
});

function inicializarTablas() {
    console.log("Inicializando tablas DataTables...");

    const configBase = {
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json'
        },
        responsive: true,
        autoWidth: false
    };

    $('#tablaValorados').DataTable({
        ...configBase,
        order: [[3, 'desc']],
        columns: [
            { title: "ID" },
            { title: "Título" },
            { title: "Autor" },
            { title: "Valoración Promedio" },
            { title: "Número de Votos" }
        ]
    });

    $('#tablaConexiones').DataTable({
        ...configBase,
        order: [[3, 'desc']],
        columns: [
            { title: "ID" },
            { title: "Nombre" },
            { title: "Email" },
            { title: "Conexiones" }
        ]
    });

    $('#tablaCaminos').DataTable({
        ...configBase,
        order: [[3, 'asc']],
        columns: [
            { title: "Estudiante A" },
            { title: "Estudiante B" },
            { title: "Camino" },
            { title: "Longitud" }
        ]
    });

    $('#tablaComunidades').DataTable({
        ...configBase,
        columns: [
            { title: "Grupo" },
            { title: "Miembros" }
        ]
    });

    $('#tablaParticipacion').DataTable({
        ...configBase,
        order: [[1, 'desc']],
        columns: [
            { title: "Estudiante" },
            { title: "Publicaciones" },

        ]
    });
}

async function cargarDatos() {
    console.log("Comenzando carga de datos...");
    $('.card-body').prepend('<div class="alert alert-info">Cargando datos...</div>');

    try {
        await Promise.all([
            cargarContenidosValorados(),
            cargarParticipacion(),
            cargarCaminosCortos(),
            cargarEstudiantesConexiones(),
            cargarComunidades(),
        ]);
        console.log("Todos los datos cargados correctamente");
    } catch (error) {
        console.error("Error al cargar datos:", error);
        $('.card-body').prepend(
            '<div class="alert alert-danger">Error al cargar datos. Por favor recarga la página.</div>'
        );
    } finally {
        $('.alert-info').remove();
    }
}

async function cargarContenidosValorados() {
    try {
        console.log("Cargando contenidos valorados...");
        const response = await $.ajax({
            url: '/api/contenido/listar',
            method: 'GET'
        });

        console.log("Datos recibidos para contenidos:", response);

        const table = $('#tablaValorados').DataTable();
        table.clear();

        if (response && response.length > 0) {
            response.forEach(item => {
                table.row.add([
                    item.id || 'N/A',
                    item.nombre || item.titulo || item.nombreOriginal || 'Sin título',
                    item.autor || 'Desconocido',
                    item.promedioValoracion !== undefined ? item.promedioValoracion.toFixed(2) : '0.00',
                    item.likes || item.numeroVotos || 0
                ]);
            });
        } else {
            table.row.add(['No hay datos disponibles', '', '', '', '']).draw();
        }

        table.draw();
    } catch (error) {
        console.error("Error en cargarContenidosValorados:", error);
        console.error("Detalles del error:", error.responseText || error.statusText);
        mostrarErrorEnTabla('#tablaValorados', "Error al cargar contenidos valorados");
        throw error;
    }
}
async function cargarCaminosCortos() {
    try {
        const response = await $.ajax({
            url: '/api/grafo/caminoCorto',
            method: 'GET',
            dataType: 'json'
        });

        const tabla = $('#tablaCaminos').DataTable();
        tabla.clear();

        const paresUnicos = new Set();

        response.forEach(item => {
            const parOrdenado = [item.estudianteA, item.estudianteB].sort().join("-");
            if (!paresUnicos.has(parOrdenado)) {
                paresUnicos.add(parOrdenado);
                tabla.row.add([
                    item.estudianteA,
                    item.estudianteB,
                    item.camino.join(" → "),
                    item.longitud
                ]);
            }
        });

        tabla.draw();
    } catch (error) {
        console.error("Error al cargar caminos cortos:", error);
        mostrarErrorEnTabla("#tablaCaminos", "Error al cargar caminos más cortos");
    }
}
async function cargarEstudiantesConexiones() {
    try {
        console.log("Cargando estudiantes con más conexiones...");
        const response = await $.ajax({
            url: '/api/grafo/conexiones',
            method: 'GET',
            dataType: 'json'
        });

        const tabla = $('#tablaConexiones').DataTable();
        tabla.clear();

        if (response && response.length > 0) {
            response.forEach(estudiante => {
                tabla.row.add([
                    estudiante.id || 'N/A',
                    estudiante.username || 'N/A',
                    estudiante.email || 'N/A',
                    estudiante.conexiones || 0
                ]);
            });
        } else {
            tabla.row.add(['No hay datos disponibles', '', '', '']).draw();
        }

        tabla.draw();
    } catch (error) {
        console.error("Error al cargar estudiantes con conexiones:", error);
        mostrarErrorEnTabla("#tablaConexiones", "Error al cargar estudiantes con más conexiones");
    }
}
async function cargarComunidades() {
    try {
        console.log("Cargando comunidades de estudio...");
        const response = await $.ajax({
            url: '/api/grafo/comunidades',
            method: 'GET',
            dataType: 'json'
        });

        console.log("Comunidades recibidas:", response);

        const tabla = $('#tablaComunidades').DataTable();
        tabla.clear();

        if (response && response.length > 0) {
            response.forEach((grupo, index) => {
                const nombreGrupo = `Clúster ${index + 1}`;
                const miembros = grupo.map(est => est.username || 'N/A').join('<br>');

                tabla.row.add([
                    nombreGrupo,
                    miembros
                ]);
            });
        } else {
            tabla.row.add(['No hay comunidades detectadas', '']).draw();
        }

        tabla.draw();
    } catch (error) {
        console.error("Error al cargar comunidades:", error);
        mostrarErrorEnTabla("#tablaComunidades", "Error al cargar comunidades");
    }
}



async function cargarParticipacion() {
    try {
        console.log("Cargando datos de participación...");
        const response = await $.ajax({
            url: '/api/contenido/contenidosContados',
            method: 'GET',
            dataType: 'json',
            timeout: 10000
        });

        console.log("Datos de participación recibidos:", response);

        const table = $('#tablaParticipacion').DataTable();
        table.clear();

        if (response && response.length > 0) {
            response.forEach(item => {
                table.row.add([
                    item.username || 'N/A',
                    item.publicaciones || 0,
                ]);
            });
        } else {
            table.row.add(['No hay datos disponibles', '']).draw();
        }

        table.draw();
    } catch (error) {
        console.error("Error en cargarParticipacion:", error);
        console.error("Detalles del error:", error.responseText || error.statusText);
        mostrarErrorEnTabla('#tablaParticipacion', "Error al cargar participación");
        throw error;
    }
}



function mostrarErrorEnTabla(selector, mensaje) {
    $(selector).html(`
        <thead>
            <tr>
                <th>Autor</th>
                <th>Publicaciones</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td colspan="2" class="text-danger">${mensaje}</td>
            </tr>
        </tbody>
    `);
}