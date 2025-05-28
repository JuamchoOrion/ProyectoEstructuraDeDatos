$(document).ready(function() {

    // Verificar autenticación
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'registro.html';
        return;
    }

    // Configuración de DataTable
    const tablaUsuarios = $('#tablaUsuarios').DataTable({
        ajax: {
            url: 'http://localhost:8080/api/usuario/listar',
            dataSrc: '',
            error: function(xhr) {
                console.error('Error cargando datos:', xhr.statusText);

                // if (xhr.status === 401) {
                //     localStorage.removeItem('token');
                //   window.location.href = 'registro.html';
                //} else if (xhr.status === 403) {
                //  alert('No tienes permisos de moderador');
                //window.location.href = 'index.html';
                //}
            }
        },
        columns: [
            {
                data: 'id',
                className: 'text-center',
                width: '5%'
            },
            {
                data: 'username',
                width: '25%'
            },
            {
                data: 'email',
                width: '30%'
            },
            {
                data: 'rol',
                className: 'text-center',
                width: '15%',
                render: function(data) {
                    const badgeClass = data === 'Estudiante' ? 'badge-estudiante' : 'badge-moderador';
                    return `<span class="badge ${badgeClass}">${data}</span>`;
                }
            },
            {
                data: null,
                className: 'text-center',
                width: '25%',
                render: function(data) {
                    return `
                        <button class="btn btn-primary btn-action btn-editar" data-id="${data.id}">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-danger btn-action btn-eliminar" data-id="${data.id}">
                            <i class="fas fa-trash"></i>
                        </button>
                        ${data.tipoUsuario === 'Estudiante' ? `
                        <button class="btn btn-warning btn-action btn-intereses" data-id="${data.id}">
                            <i class="fas fa-tags"></i>
                        </button>` : ''}
                    `;
                }
            }
        ],
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json'
        },
        responsive: true,
        autoWidth: false
    });

    // Botón de búsqueda
    $('#btnBuscar').click(function() {
        tablaUsuarios.search($('#buscarUsuario').val()).draw();
    });

    // Botón cerrar sesión
    $('#cerrarSesion').click(function() {
        localStorage.removeItem('token');
        window.location.href = 'index.html';
    });

    // Evento para editar usuario con validación del rol del usuario objetivo
    $('#tablaUsuarios tbody').on('click', '.btn-editar', function() {
        const id = $(this).data('id');
        const $btn = $(this);
        const $row = $btn.closest('tr');

        // Deshabilitar botón y mostrar spinner
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i>');
        $row.addClass('table-warning');

        // Primero verificar el rol del usuario a editar
        fetch(`http://localhost:8080/api/usuario/${id}/verificarRol`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al verificar el rol del usuario');
                }
                return response.json();
            })
            .then(data => {
                if (data.rol === 'MODERADOR') {
                    throw new Error('No está permitido editar usuarios moderadores');
                }
                else if (data.rol === 'ESTUDIANTE') {
                    window.location.href = `editarEstudiante.html?id=${id}`;
                }
                else {
                    throw new Error('Rol de usuario no reconocido');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                mostrarAlerta(error.message || 'Error al preparar la edición', 'danger');
            })
            .finally(() => {
                // Restaurar botón
                $btn.prop('disabled', false).html('<i class="fas fa-edit"></i>');
                $row.removeClass('table-warning');
            });
    });

// Evento para eliminar usuario (sin token)
    $('#tablaUsuarios tbody').on('click', '.btn-eliminar', function() {
        const id = $(this).data('id');
        const $btn = $(this);
        const $row = $btn.closest('tr');

        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i>');
        $row.addClass('table-warning');

        if (confirm('¿Está seguro de eliminar este usuario?')) {
            fetch(`http://localhost:8080/api/usuario/${id}/eliminar`, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        tablaUsuarios.row($row).remove().draw();
                        mostrarAlerta('Usuario eliminado correctamente', 'success');
                    } else {
                        return response.text().then(error => {
                            throw new Error(error || 'Error desconocido');
                        });
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    mostrarAlerta(error.message || 'Error al eliminar el usuario', 'danger');
                })
                .finally(() => {
                    $btn.prop('disabled', false).html('<i class="fas fa-trash"></i>');
                    $row.removeClass('table-warning');
                });
        } else {
            $btn.prop('disabled', false).html('<i class="fas fa-trash"></i>');
            $row.removeClass('table-warning');
        }
    });


    // Función para mostrar alertas
    function mostrarAlerta(mensaje, tipo) {
        const alerta = $(`
            <div class="alert alert-${tipo} alert-dismissible fade show position-fixed top-0 end-0 m-3" role="alert">
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `);
        $('body').append(alerta);
        setTimeout(() => alerta.alert('close'), 5000);
    }
    function showLoading(elementId) {
        const element = document.getElementById(elementId);
        element.innerHTML = '<p class="loading">Procesando...</p>';
    }

    function showMessage(message, type = "info") {
        const div = document.createElement("div");
        div.innerText = message;
        div.className = `message ${type}`;
        document.body.appendChild(div);

        setTimeout(() => div.remove(), 3000);
    }

    async function generarGruposMod() {
        const resultElement = document.getElementById('resultGenerar');
        showLoading('resultGenerar');

        try {
            const response = await fetch(`/api/grupos-estudio/generar`, { method: 'POST' });
            if (!response.ok) throw new Error(await response.text() || 'Error al generar grupos');

            resultElement.innerHTML = ''; // Quitar mensaje de carga
            showMessage('¡Grupos generados exitosamente!', 'success');
        } catch (error) {
            resultElement.innerHTML = ''; // Quitar mensaje de carga en caso de error
            showMessage(error.message, 'error');
        }
    }

    document.getElementById('cerrarSesion')?.addEventListener('click', () => {
        localStorage.removeItem('token');
        window.location.href = 'index.html';
    });

// Esto es correcto para exponer la función al HTML
    window.generarGruposMod = generarGruposMod;


    function getFileIconClass(mimeType, fileName) {
        const extension = fileName.split('.').pop().toLowerCase();
        const type = mimeType.split('/')[0];
        if (type === 'image') return 'fas fa-file-image';
        if (type === 'video') return 'fas fa-file-video';
        if (type === 'audio') return 'fas fa-file-audio';
        if (mimeType === 'application/pdf') return 'fas fa-file-pdf';
        if (mimeType.includes('msword') || mimeType.includes('wordprocessingml') || ['doc', 'docx'].includes(extension)) return 'fas fa-file-word';
        if (mimeType.includes('spreadsheetml') || mimeType.includes('excel') || ['xls', 'xlsx'].includes(extension)) return 'fas fa-file-excel';
        if (mimeType.includes('presentationml') || mimeType.includes('powerpoint') || ['ppt', 'pptx'].includes(extension)) return 'fas fa-file-powerpoint';
        if (mimeType.includes('zip') || ['zip', 'rar', '7z', 'tar', 'gz'].includes(extension)) return 'fas fa-file-archive';
        if (mimeType.includes('text') || ['txt', 'csv', 'json', 'xml', 'html', 'css', 'js'].includes(extension)) return 'fas fa-file-code';
        return 'fas fa-file';
    }
});

