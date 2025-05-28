document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');

    if (!token) {
        alert('Debes iniciar sesión para acceder.');
        window.location.href = 'index.html';
        return;
    }

    let contenidos = [];
    let filtroTipo = 'ALL';
    let filtroAutor = '';
    let filtroTipoArchivo = 'ALL';
    let filtroInteres ='ALL';

    const barraBusqueda = document.getElementById('barraBusqueda');
    const formBusqueda = document.getElementById('formBusqueda');
    const contenedor = document.getElementById('contenedorExplorar');

    document.getElementById('cerrarSesion')?.addEventListener('click', () => {
        localStorage.removeItem('token');
        window.location.href = 'index.html';
    });
    // Event listeners para los nuevos filtros
    document.getElementById('filtroAutor')?.addEventListener('input', (e) => {
        filtroAutor = e.target.value.trim().toLowerCase();
        aplicarFiltros();
    });
    document.getElementById('filtroInteres')?.addEventListener('change', (e) => {
        filtroInteres = e.target.value;
        aplicarFiltros();
    });

    document.getElementById('filtroTipoArchivo')?.addEventListener('change', (e) => {
        filtroTipoArchivo = e.target.value;
        aplicarFiltros();
    });

    fetchContenidos();
    document.getElementById('filtroTipo')?.addEventListener('change', (e) => {
        filtroTipo = e.target.value;
        aplicarFiltros();
    });

    formBusqueda?.addEventListener('submit', (e) => {
        e.preventDefault();
        buscarContenidos();
    });

// Función buscarContenidos actualizada
    function buscarContenidos() {
        const query = barraBusqueda?.value.trim().toLowerCase() || '';

        const resultados = contenidos.filter(c => {
            // Filtro por tipo de contenido (nuevo)
            const tipoContenidoCoincide = filtroTipo === 'ALL' ||
                (c.tipoContenido && c.tipoContenido.toUpperCase() === filtroTipo.toUpperCase());
            const interesCoincide = filtroInteres === 'ALL' ||
                (c.interes && c.interes.toUpperCase() === filtroInteres.toUpperCase()) ||
                (filtroInteres === 'OTROS' && (!c.interes || c.interes === ''));

            // Filtro por tipo de archivo (nuevo)
            const tipoArchivoCoincide = filtroTipoArchivo === 'ALL' ||
                (filtroTipoArchivo === 'image' && c.tipoArchivo?.startsWith('image')) ||
                (filtroTipoArchivo === 'video' && c.tipoArchivo?.startsWith('video')) ||
                (c.tipoArchivo === filtroTipoArchivo);

            // Filtro por autor (nuevo)
            const autorCoincide = !filtroAutor ||
                (c.autor && c.autor.toLowerCase().includes(filtroAutor));

            // Filtro por búsqueda general
            const textoCoincide = !query ||
                (c.nombreOriginal?.toLowerCase().includes(query)) ||
                (c.descripcion?.toLowerCase().includes(query));

            return tipoContenidoCoincide && tipoArchivoCoincide && autorCoincide &&
                interesCoincide && textoCoincide;
        });

        renderContenidos(resultados);
    }

    function aplicarFiltros() {
        buscarContenidos();
    }


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
                    localStorage.removeItem('token');
                    window.location.href = 'index.html';
                    return;
                }
                throw new Error(`Error HTTP: ${response.status}`);
            }

            contenidos = await response.json();
            aplicarFiltros();
            //renderContenidos(contenidos);
        } catch (error) {
            console.error('Error al cargar los contenidos:', error);
            mostrarError('Error al cargar los contenidos. Por favor, intenta nuevamente.');
        }
    }

    function getFileIconClass(fileType, fileName) {
        const extension = fileName?.split('.').pop()?.toLowerCase() || '';
        const mainType = fileType?.split('/')[0] || '';

        if (mainType === 'image') return 'fas fa-image';
        if (mainType === 'video') return 'fas fa-video';
        if (mainType === 'audio') return 'fas fa-music';

        switch (extension) {
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

    function renderContenidos(lista) {
        if (!contenedor) return;
        contenedor.innerHTML = '';

        if (!lista || lista.length === 0) {
            contenedor.innerHTML = '<p class="text-muted">No se encontraron contenidos.</p>';
            return;
        }

        lista.forEach(c => {
            try {
                let mediaContent = '';
                const fileType = c.tipoArchivo?.split('/')[0] || '';
                const iconClass = getFileIconClass(c.tipoArchivo, c.nombreOriginal);

                if (fileType === 'image') {
                    mediaContent = `
                        <div class="thumbnail-container" style="height: 200px; overflow: hidden; display: flex; align-items: center; justify-content: center; background: #f8f9fa;">
                            <img src="${c.url}" class="img-thumbnail" alt="${c.nombreOriginal || 'Imagen'}" 
                                 style="max-height: 100%; max-width: 100%; object-fit: contain; cursor: pointer;"
                                 onclick="window.open('${c.url}', '_blank')">
                        </div>
                    `;
                } else {
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
                  <div class="contenedor-mensajes">
                    <p class="card-text">${c.descripcion || 'Sin descripción'}</p>
                  </div>
                  <div class="info-adicional">
                    <div class="d-flex justify-content-between small">
                      <span><strong>Tipo:</strong> ${c.tipoArchivo || 'Desconocido'}</span>
                      <span><strong>Autor:</strong> ${c.autor || 'Anónimo'}</span>
                    </div>
                    <div class="d-flex justify-content-between small mt-1">
                      <span><strong>Categoría:</strong> ${c.tipoContenido || 'No especificado'}</span>
                      ${c.interes ? `<span><strong>Interés:</strong> ${c.interes ||'No especificado'}</span>` : ''}
                    </div>
                    <small class="text-muted d-block mt-2">Publicado: ${c.fechaPublicacion ? new Date(c.fechaPublicacion).toLocaleDateString() : 'fecha desconocida'}</small>
                    <div class="mt-2">
                      <div class="valoracion" data-id="${c.id}">
                        ${renderStars(c.id, c.promedioValoracion || 0)}
                      </div>
                      <small class="text-muted" id="promedio-${c.id}">Valoración: ${calcularPromedio(c)} ⭐</small>
                    </div>
                  </div>
                </div>
              </div>
            `;
                const cardText = card.querySelector('.card-text');
                if (cardText && cardText.scrollHeight > cardText.clientHeight) {
                    const toggleBtn = document.createElement('button');
                    toggleBtn.className = 'btn btn-link btn-sm p-0 text-primary';
                    toggleBtn.textContent = 'Ver más';
                    toggleBtn.addEventListener('click', () => {
                        cardText.classList.toggle('scrollable');
                        toggleBtn.textContent = cardText.classList.contains('scrollable') ? 'Ver menos' : 'Ver más';
                    });
                    cardText.parentNode.insertBefore(toggleBtn, cardText.nextSibling);
                }
                contenedor.appendChild(card);
            } catch (error) {
                console.error('Error al renderizar contenido:', c, error);
            }
        });
    }

    function renderStars(id, promedio = 0, valoracionUsuario = 0) {
        let estrellas = '';
        for (let i = 1; i <= 5; i++) {
            const isUserRating = valoracionUsuario >= i;
            const isAverageRating = promedio >= i;
            const color = isUserRating ? 'gold' : (isAverageRating ? 'lightgoldenrodyellow' : 'gray');

            estrellas += `<span class="estrella" data-id="${id}" data-valor="${i}" 
                      style="cursor:pointer; font-size: 1.5em; color: ${color}">★</span>`;
        }
        return estrellas;
    }
    function calcularPromedio(contenido) {
        if (contenido.promedioValoracion === undefined || contenido.promedioValoracion === null) {
            return 'Sin valorar';
        }
        return contenido.promedioValoracion.toFixed(1);
    }

    contenedor?.addEventListener('click', async (e) => {
        if (e.target.classList.contains('estrella')) {
            try {
                const starElement = e.target;
                const id = starElement.getAttribute('data-id');
                const valor = parseInt(starElement.getAttribute('data-valor'));

                // Resaltar estrellas seleccionadas
                const starsContainer = starElement.parentElement;
                const stars = starsContainer.querySelectorAll('.estrella');
                stars.forEach((s, index) => {
                    s.style.color = (index < valor) ? 'gold' : 'gray';
                });

                const response = await fetch(`/api/contenido/${id}/valorar`, {
                    method: 'POST',
                    headers: {
                        "Authorization": token,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ puntuacion: valor }) // Cambiado de valoracion a puntuacion
                });

                if (!response.ok) throw new Error('Error al valorar');

                // Actualizar solo el contenido valorado en lugar de recargar todo
                const data = await response.json();
                const promedioElement = document.getElementById(`promedio-${id}`);
                if (promedioElement) {
                    promedioElement.textContent = `Valoración: ${data.promedioValoracion?.toFixed(1) || '0.0'} ⭐`;
                }

                mostrarFeedback('Valoración registrada correctamente', 'success');
            } catch (error) {
                console.error('Error:', error);
                mostrarFeedback(error.message || 'Error al valorar', 'danger');
            }
        }
    });

    function mostrarFeedback(mensaje, tipo = 'info') {
        const feedbackDiv = document.createElement('div');
        feedbackDiv.className = `alert alert-${tipo}`;
        feedbackDiv.textContent = mensaje;
        document.body.appendChild(feedbackDiv);
        setTimeout(() => feedbackDiv.remove(), 3000);
    }

    function mostrarError(mensaje) {
        contenedor.innerHTML = `<div class="alert alert-danger">${mensaje}</div>`;
    }
});
