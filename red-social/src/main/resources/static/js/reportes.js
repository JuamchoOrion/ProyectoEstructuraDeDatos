$(document).ready(function () {
    // Inicializar DataTables
    $('#tablaValorados, #tablaConexiones, #tablaCaminos, #tablaComunidades, #tablaParticipacion').DataTable();

    // Cargar datos
    cargarContenidosValorados();
    cargarEstudiantesConexiones();
    cargarCaminosCortos();
    cargarComunidades();
    cargarParticipacion();
});
//HAY QUE HACER LAS API PARA CADA UNA DE LAS TABLAS
//NOTAS D EGPT:
// Este script espera que tengas rutas como /api/reportes/participacion, etc., que devuelvan datos en formato JSON.
//
// Los campos como item.titulo, camino.ruta, comunidad.miembros, etc., deben coincidir con la estructura de tu backend.
function cargarContenidosValorados() {
    $.ajax({
        url: '/api/reportes/contenidos-valorados',
        method: 'GET',
        success: function (data) {
            const tbody = $('#tablaValorados tbody');
            data.forEach(item => {
                tbody.append(`
          <tr>
            <td>${item.id}</td>
            <td>${item.titulo}</td>
            <td>${item.autor}</td>
            <td>${item.valoracion_promedio}</td>
            <td>${item.total_votos}</td>
          </tr>
        `);
            });
        }
    });
}

function cargarEstudiantesConexiones() {
    $.ajax({
        url: '/api/reportes/estudiantes-conectados',
        method: 'GET',
        success: function (data) {
            const tbody = $('#tablaConexiones tbody');
            data.forEach(est => {
                tbody.append(`
          <tr>
            <td>${est.id}</td>
            <td>${est.nombre}</td>
            <td>${est.email}</td>
            <td>${est.conexiones}</td>
          </tr>
        `);
            });
        }
    });
}

function cargarCaminosCortos() {
    $.ajax({
        url: '/api/reportes/caminos-cortos',
        method: 'GET',
        success: function (data) {
            const tbody = $('#tablaCaminos tbody');
            data.forEach(camino => {
                tbody.append(`
          <tr>
            <td>${camino.estudianteA}</td>
            <td>${camino.estudianteB}</td>
            <td>${camino.ruta.join(' → ')}</td>
            <td>${camino.longitud}</td>
          </tr>
        `);
            });
        }
    });
}

function cargarComunidades() {
    $.ajax({
        url: '/api/reportes/comunidades',
        method: 'GET',
        success: function (data) {
            const tbody = $('#tablaComunidades tbody');
            data.forEach(comunidad => {
                tbody.append(`
          <tr>
            <td>${comunidad.grupo}</td>
            <td>${comunidad.miembros.join(', ')}</td>
          </tr>
        `);
            });
        }
    });
}

function cargarParticipacion() {
    $.ajax({
        url: '/api/reportes/participacion',
        method: 'GET',
        success: function (data) {
            const tbody = $('#tablaParticipacion tbody');
            data.forEach(p => {
                tbody.append(`
          <tr>
            <td>${p.nombre}</td>
            <td>${p.publicaciones}</td>
            <td>${p.comentarios}</td>
            <td>${p.reacciones}</td>
            <td>${p.total}</td>
          </tr>
        `);
            });
        }
    });
}
