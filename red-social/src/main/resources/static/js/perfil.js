document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem("token");
    console.log("Token obtenido del storage:", token);

    if (!token || !token.startsWith("Bearer ")) {
        console.error("Token no válido o faltante");
        window.location.href = "index.html";
        return;
    }

    // Función para obtener el icono según el tipo de archivo
    function getFileIconClass(mimeType, fileName) {
        const extension = fileName.split('.').pop().toLowerCase();
        const type = mimeType.split('/')[0];

        if (type === 'image') return 'fas fa-file-image';
        if (type === 'video') return 'fas fa-file-video';
        if (type === 'audio') return 'fas fa-file-audio';
        if (mimeType === 'application/pdf') return 'fas fa-file-pdf';
        if (mimeType.includes('msword') || mimeType.includes('wordprocessingml') || extension === 'doc' || extension === 'docx') return 'fas fa-file-word';
        if (mimeType.includes('spreadsheetml') || mimeType.includes('excel') || extension === 'xls' || extension === 'xlsx') return 'fas fa-file-excel';
        if (mimeType.includes('presentationml') || mimeType.includes('powerpoint') || extension === 'ppt' || extension === 'pptx') return 'fas fa-file-powerpoint';
        if (mimeType.includes('zip') || ['zip', 'rar', '7z', 'tar', 'gz'].includes(extension)) return 'fas fa-file-archive';
        if (mimeType.includes('text') || ['txt', 'csv', 'json', 'xml', 'html', 'css', 'js'].includes(extension)) return 'fas fa-file-code';

        return 'fas fa-file'; // Icono por defecto
    }

    try {
        // Verificación del token
        const verifyResponse = await fetch("/api/verify", {
            headers: { "Authorization": token }
        });

        if (!verifyResponse.ok) throw new Error("Token inválido o expirado");

        // Obtener el perfil del usuario
        const perfilResponse = await fetch("/api/usuario/perfil", {
            headers: { "Authorization": token }
        });

        if (!perfilResponse.ok) {
            throw new Error("No se pudo obtener el perfil del usuario");
        }

        const perfil = await perfilResponse.json();
        document.getElementById("nombreUsuario").textContent = perfil.username;

        // Cargar contenidos del usuario
        const contenidoResponse = await fetch("/api/contenido/mios", {
            headers: { "Authorization": token }
        });

        if (!contenidoResponse.ok) throw new Error("Error al obtener contenidos");

        const contenidos = await contenidoResponse.json();
        localStorage.setItem('contenidos', JSON.stringify(contenidos));

        const contenedor = document.getElementById('contenedorContenidos');
        const mostrarContenidos = (lista) => {
            contenedor.innerHTML = "";

            if (lista.length === 0) {
                contenedor.innerHTML = "<p class='text-muted'>No hay contenidos disponibles.</p>";
                return;
            }

            lista.forEach(contenido => {
                const card = document.createElement("div");
                card.className = "card mb-3 shadow-sm";
                card.style.maxWidth = "100%";

                let mediaContent = '';
                const fileType = contenido.tipoArchivo.split('/')[0];
                const mimeType = contenido.tipoArchivo;

                // Manejo especial para PDFs
                if (mimeType === 'application/pdf') {
                    mediaContent = `
                        <div class="pdf-preview-container" style="height: 300px; background-color: #f5f5f5; display: flex; flex-direction: column; align-items: center; justify-content: center;">
                            <div class="text-center p-3">
                                <i class="${getFileIconClass(mimeType, contenido.nombreOriginal)} fa-4x text-danger mb-3"></i>
                                <p class="mb-2">${contenido.nombreOriginal}</p>
                                <div class="d-flex gap-2">
                                    <a href="${contenido.url}" class="btn btn-sm btn-outline-primary" target="_blank">
                                        <i class="fas fa-external-link-alt"></i> Abrir PDF
                                    </a>
                                    <button class="btn btn-sm btn-outline-secondary" onclick="window.open('${contenido.url}#toolbar=0&navpanes=0', '_blank', 'width=800,height=600')">
                                        <i class="fas fa-expand"></i> Vista previa
                                    </button>
                                </div>
                            </div>
                        </div>
                    `;
                }
                // Manejo para imágenes
                else if (fileType === 'image') {
                    mediaContent = `
                        <div class="thumbnail-container" style="height: 200px; overflow: hidden; display: flex; align-items: center; justify-content: center; background-color: #000;">
                            <img src="${contenido.url}" class="img-fluid" alt="${contenido.nombreOriginal}" 
                                 style="max-height: 100%; max-width: 100%; object-fit: contain; cursor: pointer;"
                                 onclick="window.open('${contenido.url}', '_blank')">
                        </div>
                    `;
                }
                // Manejo para videos
                else if (fileType === 'video') {
                    mediaContent = `
                        <div class="ratio ratio-16x9 bg-dark">
                            <video controls style="max-height: 300px;">
                                <source src="${contenido.url}" type="${mimeType}">
                                Tu navegador no soporta el elemento de video.
                            </video>
                        </div>
                    `;
                }
                // Manejo para otros tipos de archivo
                else {
                    const iconClass = getFileIconClass(mimeType, contenido.nombreOriginal);
                    mediaContent = `
                        <div class="d-flex flex-column align-items-center p-4" style="height: 200px; justify-content: center; background-color: #f8f9fa;">
                            <i class="${iconClass} fa-4x text-secondary mb-3"></i>
                            <p class="text-center mb-3">${contenido.nombreOriginal}</p>
                            <a href="${contenido.url}" class="btn btn-outline-primary btn-sm" target="_blank">
                                <i class="fas fa-download"></i> Descargar archivo
                            </a>
                        </div>
                    `;
                }

                card.innerHTML = `
                    ${mediaContent}
                    <div class="card-body">
                        <h5 class="card-title">${contenido.nombreOriginal}</h5>
                        <p class="card-text">${contenido.descripcion || 'Sin descripción'}</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <small class="text-muted">${new Date(contenido.fechaPublicacion).toLocaleString()}</small>
                            <div>
                                <span class="badge bg-success me-2">${contenido.likes} <i class="fas fa-thumbs-up"></i></span>
                                <span class="badge bg-info">${contenido.descargas || 0} <i class="fas fa-download"></i></span>
                            </div>
                        </div>
                    </div>
                `;
                contenedor.appendChild(card);
            });
        };

        mostrarContenidos(contenidos);

        const solicitudesResponse = await fetch("/api/solicitudes/urgentes", {
            headers: { "Authorization": token }
        });

        if (!solicitudesResponse.ok) throw new Error("Error al obtener solicitudes de ayuda");

        const solicitudes = await solicitudesResponse.json();
        const contenedorSolicitudes = document.getElementById("contenedorSolicitudesAyuda");

        if (solicitudes.length === 0) {
            contenedorSolicitudes.innerHTML = "<p class='text-muted'>No tienes solicitudes de ayuda urgentes.</p>";
        } else {
            solicitudes.forEach(solicitud => {
                const card = document.createElement("div");
                card.className = "border p-3 mb-2 rounded shadow-sm";

                const fecha = new Date(solicitud.fechaNecesidad).toLocaleDateString();

                card.innerHTML = `
            <h6><i class="bi bi-exclamation-triangle text-danger"></i> ${solicitud.interes}</h6>
            <p><strong>Fecha de necesidad:</strong> ${fecha}</p>
            <p><strong>Petición:</strong> ${solicitud.peticion}</p>
        `;

                contenedorSolicitudes.appendChild(card);
            });
        }


    } catch (error) {
        console.error("Error:", error);
        window.location.href = "index.html";
    }
});
