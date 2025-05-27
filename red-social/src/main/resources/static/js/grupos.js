const API_BASE_URL = 'http://localhost:8080/api/grupos-estudio';

// Función para mostrar mensajes
function showMessage(elementId, message, isError = false) {
    const element = document.getElementById(elementId);
    element.innerHTML = `<div class="alert ${isError ? 'alert-danger' : 'alert-success'}">${message}</div>`;
}

// Función para mostrar estado de carga
function showLoading(elementId) {
    const element = document.getElementById(elementId);
    element.innerHTML = '<div class="text-center py-3"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Cargando...</span></div><p class="mt-2">Cargando...</p></div>';
}

// Obtener token de localStorage
function getToken() {
    return localStorage.getItem('token');
}

// Función para redirigir a la vista de detalles del grupo
function verDetallesGrupo(grupoId) {
    window.location.href = `grupo-detalle.html?id=${grupoId}`;
}

// Función principal para obtener y mostrar los grupos
async function obtenerGrupos() {
    showLoading('gruposContainer');

    try {
        const response = await fetch(`${API_BASE_URL}/mis-grupos`, {
            headers: {
                'Authorization': getToken()
            }
        });

        if (!response.ok) {
            throw new Error('Error al obtener grupos');
        }

        const grupos = await response.json();
        mostrarGrupos(grupos);
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('gruposContainer').innerHTML = `
            <div class="col-12">
                <div class="alert alert-danger">${error.message}</div>
            </div>
        `;
    }
}

// Función para mostrar los grupos en cards
function mostrarGrupos(grupos) {
    const container = document.getElementById('gruposContainer');

    if (grupos.length === 0) {
        container.innerHTML = `
            <div class="col-12 text-center py-4">
                <i class="bi bi-people fs-1 text-muted"></i>
                <h5 class="mt-3">No perteneces a ningún grupo</h5>
                <p class="text-muted">Únete o crea un grupo para empezar</p>
            </div>
        `;
        return;
    }

    container.innerHTML = grupos.map(grupo => `
        <div class="col-md-6 col-lg-4 mb-3">
            <div class="card grupo-card h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <h5 class="card-title mb-0">${grupo.interes || 'Grupo'}</h5>
                        <span class="badge bg-primary">${grupo.estudiantes ? grupo.estudiantes.length : 0} <i class="bi bi-people-fill"></i></span>
                    </div>
                    <p class="card-text text-muted small">${grupo.contenidos ? grupo.contenidos.length : 0} contenidos compartidos</p>
                    <div class="d-flex justify-content-between mt-3">
                        <button class="btn btn-sm btn-outline-primary" onclick="verDetallesGrupo(${grupo.id})">
                            <i class="bi bi-eye"></i> Ver detalles
                        </button>
                        <button class="btn btn-sm btn-outline-success" onclick="location.href='grupo-detalle.html?id=${grupo.id}'">
                            <i class="bi bi-box-arrow-in-right"></i> Entrar
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

// Función para generar grupos automáticamente
async function generarGrupos() {
    showLoading('resultGenerar');
    try {
        const response = await fetch(`${API_BASE_URL}/generar`, {
            method: 'POST',
            headers: {
                'Authorization': getToken()
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Error al generar grupos');
        }

        showMessage('resultGenerar', '¡Grupos generados exitosamente!');
        // Actualizar la lista de grupos después de generarlos
        setTimeout(obtenerGrupos, 1000);
    } catch (error) {
        showMessage('resultGenerar', error.message, true);
    }
}

// Cargar grupos al iniciar la página
document.addEventListener('DOMContentLoaded', () => {
    // Verificar autenticación
    const token = getToken();
    if (!token) {
        window.location.href = 'index.html';
        return;
    }

    // Cargar nombre de usuario
    fetch('/api/usuario/me', {
        headers: {
            'Authorization': token
        }
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('nombreUsuario').textContent = data.username;
        })
        .catch(error => console.error('Error al cargar perfil:', error));

    // Configurar botón de cerrar sesión
    document.getElementById('cerrarSesion').addEventListener('click', () => {
        localStorage.removeItem('token');
        window.location.href = 'index.html';
    });

    // Cargar grupos
    obtenerGrupos();
});