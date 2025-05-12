document.addEventListener('DOMContentLoaded', async () => {
    // Verificación de autenticación mediante JWT
    const token = localStorage.getItem('token');

    if (!token) {
        alert('Debes iniciar sesión para acceder.');
        window.location.href = 'index.html';
        return;
    }

    // Variables globales
    let contenidos = [];
    const barraBusqueda = document.getElementById('barraBusqueda');
    const formBusqueda = document.getElementById('formBusqueda');
    const contenedor = document.getElementById('contenedorExplorar');

    // Cerrar sesión
    document.getElementById('cerrarSesion')?.addEventListener('click', () => {
        localStorage.removeItem('token');
        window.location.href = 'index.html';
    });

    // Mostrar contenidos al cargar
    fetchContenidos();

    // Buscar contenidos
    formBusqueda?.addEventListener('submit', (e) => {
        e.preventDefault();
        buscarContenidos();
    });

    // Función para buscar contenidos
    function buscarContenidos() {
        const query = barraBusqueda?.value.trim().toLowerCase() || '';

        if (!query) {
            renderContenidos(contenidos);
            return;
        }

        const resultados = contenidos.filter(c => {
            return (c.nombreOriginal?.toLowerCase().includes(query)) ||
                (c.descripcion?.toLowerCase().includes(query)) ||
                (c.tipoArchivo?.toLowerCase().includes(query)) ||
                (c.autor?.toLowerCase().includes(query));
        });

        renderContenidos(resultados);
    }

    // Obtener contenidos desde la API
    async function fetchContenidos() {
        try {
            const response = await fetch('/api/contenido/explorar', {
                headers: {
                    "Authorization": token,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                if (response.status === 401) {
                    // Token inválido o expirado
                    localStorage.removeItem('token');
                    window.location.href = 'index.html';
                    return;
                }
                throw new Error(`Error HTTP: ${response.status}`);
            }

            contenidos = await response.json();
            renderContenidos(contenidos);
        } catch (error) {
            console.error('Error al cargar los contenidos:', error);
            mostrarError('Error al cargar los contenidos. Por favor, intenta nuevamente.');
        }
    }

    // Función para determinar el icono según tipo de archivo
    function getFileIconClass(fileType, fileName) {
        const extension = fileName?.split('.').pop()?.toLowerCase() || '';
        const mainType = fileType?.split('/')[0] || '';

        if (mainType === 'image') return 'fas fa-image';
        if (mainType === 'video') return 'fas fa-video';
        if (mainType === 'audio') return 'fas fa-music';

        switch(extension) {
            case 'pdf': return 'fas fa-file-pdf';
            case 'doc':
            case 'docx': return 'fas fa-file-word';
            case 'xls':
            case 'xlsx': return 'fas fa-file-excel';
            case 'ppt':
            case 'pptx': return 'fas fa-file-powerpoint';
            case 'zip':
            case 'rar': return 'fas fa-file-archive';
            case 'txt': return 'fas fa-file-alt';
            default: return 'fas fa-file';
        }
    }

    // Mostrar resultados en pantalla con miniaturas
    function renderContenidos(lista) {
        if (!contenedor) return;

        contenedor.innerHTML = '';

        if (!lista || lista.length === 0) {
            contenedor.innerHTML = '<p class="text-muted">No se encontraron contenidos.</p>';
            return;
        }

        lista.forEach(c => {
            try {
                // Determinar cómo mostrar el contenido según su tipo
                let mediaContent = '';
                const fileType = c.tipoArchivo?.split('/')[0] || ''; // Obtener el tipo principal
                const iconClass = getFileIconClass(c.tipoArchivo, c.nombreOriginal);

                if (fileType === 'image') {
                    // Mostrar miniatura de imagen
                    mediaContent = `
                        <div class="thumbnail-container" style="height: 200px; overflow: hidden; display: flex; align-items: center; justify-content: center; background: #f8f9fa;">
                            <img src="${c.url}" class="img-thumbnail" alt="${c.nombreOriginal || 'Imagen'}" 
                                 style="max-height: 100%; max-width: 100%; object-fit: contain; cursor: pointer;"
                                 onclick="window.open('${c.url}', '_blank')">
                        </div>
                    `;
                } else {
                    // Mostrar icono para otros tipos de archivo
                    mediaContent = `
                        <div class="d-flex flex-column align-items-center p-4" style="height: 200px; background: #f8f9fa;">
                            <i class="${iconClass} fa-4x text-secondary mb-3"></i>
                            <a href="${c.url}" class="btn btn-outline-primary btn-sm" target="_blank">Ver archivo</a>
                        </div>
                    `;
                }

                const card = document.createElement('div');
                card.className = 'col-md-4 mb-4';
                card.innerHTML = `
                  <div class="card shadow-sm h-100">
                    ${mediaContent}
                    <div class="card-body">
                      <h5 class="card-title">${c.nombreOriginal || 'Sin título'}</h5>
                      <p class="card-text">${c.descripcion || 'Sin descripción'}</p>
                      <div class="d-flex justify-content-between">
                        <p class="mb-1"><strong>Tipo:</strong> ${c.tipoArchivo || 'Desconocido'}</p>
                        <p class="mb-1"><strong>Autor:</strong> ${c.autor || 'Anónimo'}</p>
                      </div>
                      <small class="text-muted">Publicado el ${c.fechaPublicacion ? new Date(c.fechaPublicacion).toLocaleDateString() : 'fecha desconocida'}</small>
                      <div class="mt-3">
                        <div class="valoracion" data-id="${c.id}">
                          ${renderStars(c.id)}
                        </div>
                        <small class="text-muted" id="promedio-${c.id}">Valoración: ${calcularPromedio(c.id)} ⭐</small>
                      </div>
                    </div>
                  </div>
                `;
                contenedor.appendChild(card);
            } catch (error) {
                console.error('Error al renderizar contenido:', c, error);
            }
        });
    }

    // Renderizar estrellas clicables
    function renderStars(id) {
        let estrellas = '';
        for (let i = 1; i <= 5; i++) {
            estrellas += `<span class="estrella" data-id="${id}" data-valor="${i}" style="cursor:pointer; color: gold;">★</span>`;
        }
        return estrellas;
    }

    // Calcular promedio de valoraciones
    function calcularPromedio(id) {
        try {
            const valoraciones = JSON.parse(localStorage.getItem('valoraciones') || '{}');
            const lista = valoraciones[id] || [];

            if (lista.length === 0) return 'Sin valorar';

            const promedio = lista.reduce((acc, v) => acc + (v.valor || 0), 0) / lista.length;
            return isNaN(promedio) ? 'Error' : promedio.toFixed(1);
        } catch (error) {
            console.error('Error calculando promedio:', error);
            return 'Error';
        }
    }

    // Manejar clic en estrellas
    contenedor?.addEventListener('click', async (e) => {
        if (e.target.classList.contains('estrella')) {
            try {
                const id = e.target.getAttribute('data-id');
                const valor = parseInt(e.target.getAttribute('data-valor'));

                if (!id || isNaN(valor) || valor < 1 || valor > 5) {
                    console.error('Valoración inválida');
                    return;
                }

                // Enviar valoración al backend
                const response = await fetch('/api/contenido/valorar', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        contenidoId: id,
                        valoracion: valor
                    })
                });

                if (!response.ok) {
                    throw new Error('Error al enviar valoración');
                }

                // Actualizar UI
                const promedioElement = document.getElementById(`promedio-${id}`);
                if (promedioElement) {
                    const data = await response.json();
                    promedioElement.textContent = `Valoración: ${data.promedio.toFixed(1)} ⭐`;
                    mostrarFeedback('¡Valoración registrada!', 'success');
                }
            } catch (error) {
                console.error('Error en valoración:', error);
                mostrarFeedback('Error al registrar valoración', 'error');
            }
        }
    });

    // Función para mostrar mensajes de feedback
    function mostrarFeedback(mensaje, tipo = 'success') {
        const feedback = document.createElement('div');
        feedback.className = `alert alert-${tipo} fixed-top mx-auto mt-3`;
        feedback.style.width = 'fit-content';
        feedback.style.maxWidth = '90%';
        feedback.style.zIndex = '1000';
        feedback.textContent = mensaje;
        document.body.appendChild(feedback);

        setTimeout(() => {
            feedback.style.opacity = '0';
            feedback.style.transition = 'opacity 0.5s';
            setTimeout(() => feedback.remove(), 500);
        }, 3000);
    }

    // Función para mostrar errores
    function mostrarError(mensaje) {
        const errorContainer = document.getElementById('error-container') || document.createElement('div');
        errorContainer.id = 'error-container';
        errorContainer.className = 'alert alert-danger mt-3';
        errorContainer.style.maxWidth = '600px';
        errorContainer.style.margin = '0 auto';
        errorContainer.textContent = mensaje;

        if (!document.getElementById('error-container')) {
            document.querySelector('main')?.prepend(errorContainer);
        }

        setTimeout(() => {
            errorContainer.style.opacity = '0';
            errorContainer.style.transition = 'opacity 0.5s';
            setTimeout(() => errorContainer.remove(), 500);
        }, 5000);
    }
});