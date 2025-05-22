$(document).ready(function() {
    // Configuración de DataTable
    const tablaUsuarios = $('#tablaUsuarios').DataTable({
        ajax: {
            url: 'http://localhost:8080/api/usuarios',
            dataSrc: '',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        },
        columns: [
            { data: 'id' },
            { data: 'nombre' },
            { data: 'email' },
            {
                data: 'tipoUsuario',
                render: function(data) {
                    return data === 'Estudiante'
                        ? '<span class="badge bg-success">Estudiante</span>'
                        : '<span class="badge bg-info text-dark">Moderador</span>';
                }
            },
            { data: 'telefono', defaultContent: 'N/A' },
            {
                data: null,
                render: function(data) {
                    return `
                        <div class="btn-group" role="group">
                            <button class="btn btn-sm btn-primary btn-editar" data-id="${data.id}">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-danger btn-eliminar" data-id="${data.id}">
                                <i class="fas fa-trash"></i>
                            </button>
                            ${data.tipoUsuario === 'Estudiante'
                        ? `<button class="btn btn-sm btn-warning btn-intereses" data-id="${data.id}">
                                    <i class="fas fa-tags"></i>
                                   </button>`
                        : ''}
                        </div>
                    `;
                }
            }
        ],
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json'
        }
    });

    // Variables de estado
    let tipoUsuarioActual = '';
    const interesesDisponibles = ['PROGRAMACION', 'MATEMATICAS', 'CIENCIAS', 'HISTORIA', 'ARTE'];

    // Modal
    const usuarioModal = new bootstrap.Modal('#usuarioModal');
    const usuarioForm = $('#usuarioForm');
    const modalTitle = $('#modalTitle');

    // Botón nuevo estudiante
    $('#btnNuevoEstudiante').click(function() {
        tipoUsuarioActual = 'ESTUDIANTE';
        resetForm();
        modalTitle.text('Nuevo Estudiante');
        $('#interesesSection').removeClass('d-none');
        renderizarIntereses();
        usuarioModal.show();
    });

    // Botón nuevo moderador
    $('#btnNuevoModerador').click(function() {
        tipoUsuarioActual = 'MODERADOR';
        resetForm();
        modalTitle.text('Nuevo Moderador');
        $('#interesesSection').addClass('d-none');
        usuarioModal.show();
    });
    // Botón reportes moderador
    $('#btnReportesModerador').click(function () {
        // Aquí defines qué quieres hacer
        // Ejemplo: redirigir a otra página
        window.location.href = 'reportes.html';

        // O si quieres mostrar un modal, puedes usar:
        // $('#modalReportesModerador').modal('show');
    });

    // Editar usuario
    $('#tablaUsuarios tbody').on('click', '.btn-editar', function() {
        const id = $(this).data('id');

        fetch(`http://localhost:8080/api/usuarios/${id}`, {
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        })
            .then(response => response.json())
            .then(usuario => {
                tipoUsuarioActual = usuario.tipoUsuario;
                modalTitle.text(`Editar ${usuario.tipoUsuario}`);
                $('#usuarioId').val(usuario.id);
                $('#nombre').val(usuario.nombre);
                $('#username').val(usuario.username);
                $('#email').val(usuario.email);
                $('#telefono').val(usuario.telefono || '');

                // Ocultar campos de contraseña en edición
                $('#passwordFields').addClass('d-none');

                // Mostrar intereses si es estudiante
                if (usuario.tipoUsuario === 'Estudiante') {
                    $('#interesesSection').removeClass('d-none');
                    renderizarIntereses(usuario.intereses || []);
                } else {
                    $('#interesesSection').addClass('d-none');
                }

                usuarioModal.show();
            });
    });

    // Eliminar usuario
    $('#tablaUsuarios tbody').on('click', '.btn-eliminar', function() {
        const id = $(this).data('id');

        if (confirm('¿Estás seguro de eliminar este usuario?')) {
            fetch(`http://localhost:8080/api/usuarios/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                }
            })
                .then(response => {
                    if (response.ok) {
                        tablaUsuarios.ajax.reload();
                        mostrarAlerta('Usuario eliminado correctamente', 'success');
                    } else {
                        mostrarAlerta('Error al eliminar el usuario', 'danger');
                    }
                });
        }
    });

    // Gestionar intereses
    $('#tablaUsuarios tbody').on('click', '.btn-intereses', function() {
        const id = $(this).data('id');
        // Implementar lógica para gestionar intereses
        alert(`Gestión de intereses para usuario ID: ${id}`);
    });

    // Buscar usuarios
    $('#btnBuscar').click(function() {
        const termino = $('#buscarUsuario').val();
        tablaUsuarios.search(termino).draw();
    });

    // Enviar formulario
    usuarioForm.on('submit', function(e) {
        e.preventDefault();

        const id = $('#usuarioId').val();
        const usuario = {
            nombre: $('#nombre').val(),
            username: $('#username').val(),
            email: $('#email').val(),
            telefono: $('#telefono').val(),
            password: $('#password').val()
        };

        // Validar contraseña si es nuevo usuario
        if (!id && $('#password').val() !== $('#confirmPassword').val()) {
            mostrarAlerta('Las contraseñas no coinciden', 'danger');
            return;
        }

        // Obtener intereses seleccionados si es estudiante
        if (tipoUsuarioActual === 'ESTUDIANTE') {
            usuario.intereses = [];
            $('.interes-checkbox:checked').each(function() {
                usuario.intereses.push($(this).val());
            });
        }

        const url = id
            ? `http://localhost:8080/api/usuarios/${id}`
            : `http://localhost:8080/api/usuarios?tipo=${tipoUsuarioActual}`;
        const method = id ? 'PUT' : 'POST';

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            body: JSON.stringify(usuario)
        })
            .then(response => {
                if (!response.ok) throw response;
                return response.json();
            })
            .then(() => {
                tablaUsuarios.ajax.reload();
                usuarioModal.hide();
                mostrarAlerta(`Usuario ${id ? 'actualizado' : 'creado'} correctamente`, 'success');
            })
            .catch(error => {
                error.json().then(err => {
                    mostrarAlerta(err.message || 'Error al procesar la solicitud', 'danger');
                });
            });
    });

    // Funciones auxiliares
    function resetForm() {
        usuarioForm[0].reset();
        $('#usuarioId').val('');
        $('#passwordFields').removeClass('d-none');
    }

    function renderizarIntereses(interesesSeleccionados = []) {
        const container = $('#interesesContainer');
        container.empty();

        interesesDisponibles.forEach(interes => {
            const isChecked = interesesSeleccionados.includes(interes);
            container.append(`
                <div class="form-check form-check-inline">
                    <input class="form-check-input interes-checkbox" type="checkbox" 
                           id="interes-${interes}" value="${interes}" 
                           ${isChecked ? 'checked' : ''}>
                    <label class="form-check-label" for="interes-${interes}">
                        ${interes.toLowerCase().replace('_', ' ')}
                    </label>
                </div>
            `);
        });
    }

    function mostrarAlerta(mensaje, tipo) {
        const alerta = $(`
            <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `);
        $('.container').prepend(alerta);
        setTimeout(() => alerta.alert('close'), 5000);
    }
});