document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    if (!token || !token.startsWith("Bearer ")) {
        console.error("Token no válido o faltante");
        window.location.href = "index.html";
        return;
    }

    const API_BASE_URL = 'http://localhost:8080/api/grupos-estudio';
    let currentUserId = null; // Variable para almacenar el ID del usuario actual

    function showMessage(elementId, message, isError = false) {
        const element = document.getElementById(elementId);
        const messageClass = isError ? 'error' : 'success';
        element.innerHTML = `<p class="${messageClass}">${message}</p>`;
    }

    function showLoading(elementId) {
        const element = document.getElementById(elementId);
        element.innerHTML = '<p class="loading">Procesando...</p>';
    }

    async function generarGrupos() {
        showLoading('resultGenerar');
        try {
            const response = await fetch(`${API_BASE_URL}/generar`, { method: 'POST' });
            if (!response.ok) throw new Error(await response.text() || 'Error al generar grupos');
            showMessage('resultGenerar', '¡Grupos generados exitosamente!');
        } catch (error) {
            showMessage('resultGenerar', error.message, true);
        }
    }
    document.getElementById('cerrarSesion')?.addEventListener('click', () => {
        localStorage.removeItem('token');
        window.location.href = 'index.html';
    });
    window.generarGrupos = generarGrupos;

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

    // Función para cargar usuarios en el comboBox
    async function cargarUsuariosParaAmigos() {
        try {
            const response = await fetch("/api/usuario/listarFiltrados", {
                headers: { "Authorization": token }
            });

            if (!response.ok) throw new Error("Error al cargar usuarios");

            const usuarios = await response.json();
            const combo = document.getElementById("comboUsuarios");

            // Limpiar opciones excepto la primera
            while (combo.options.length > 1) {
                combo.remove(1);
            }

            // Obtener lista de amigos actuales
            const amigosResponse = await fetch("/api/usuario/amigos", {
                headers: { "Authorization": token }
            });
            const amigos = amigosResponse.ok ? await amigosResponse.json() : [];
            const amigosIds = amigos.map(a => a.id);

            // Filtrar usuarios que no son ya amigos y no son el usuario actual
            usuarios.forEach(usuario => {
                if (usuario.id !== currentUserId && !amigosIds.includes(usuario.id)) {
                    const option = document.createElement("option");
                    option.value = usuario.id;
                    option.textContent = usuario.username;
                    combo.appendChild(option);
                }
            });

        } catch (error) {
            console.error("Error al cargar usuarios:", error);
            document.getElementById("mensajeAmigo").textContent = "Error al cargar usuarios";
            document.getElementById("mensajeAmigo").className = "small text-center text-danger";
        }
    }

    // Función para agregar amigo
    async function agregarAmigo() {
        const combo = document.getElementById("comboUsuarios");
        const idAmigo = combo.value;
        const mensajeDiv = document.getElementById("mensajeAmigo");

        if (!idAmigo) {
            mensajeDiv.textContent = "Selecciona un usuario primero";
            mensajeDiv.className = "small text-center text-warning";
            return;
        }

        try {
            const usernameAmigo = combo.options[combo.selectedIndex].text;
            const response = await fetch(`/api/usuario/agregar?userAmigo=${usernameAmigo}`, {
                method: "POST",
                headers: { "Authorization": token }
            });

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || "Error al agregar amigo");
            }

            mensajeDiv.textContent = "¡Amigo agregado con éxito!";
            mensajeDiv.className = "small text-center text-success";

            // Recargar la lista de amigos y el combo
            setTimeout(() => {
                mensajeDiv.textContent = "";
                cargarUsuariosParaAmigos();
            }, 2000);

        } catch (error) {
            console.error("Error al agregar amigo:", error);
            mensajeDiv.textContent = error.message;
            mensajeDiv.className = "small text-center text-danger";
        }
    }

    try {
        const verifyResponse = await fetch("/api/verify", { headers: { "Authorization": token } });
        if (!verifyResponse.ok) throw new Error("Token inválido o expirado");

        const perfilResponse = await fetch("/api/usuario/perfil", { headers: { "Authorization": token } });
        if (!perfilResponse.ok) throw new Error("No se pudo obtener el perfil del usuario");

        const perfil = await perfilResponse.json();
        document.getElementById("nombreUsuario").textContent = perfil.username;
        currentUserId = perfil.id; // Almacenamos el ID del usuario actual

        // Cargar el combo de usuarios para agregar amigos
        await cargarUsuariosParaAmigos();

        // Asignar evento al botón de agregar amigo
        document.getElementById("btnAgregarAmigo").addEventListener("click", agregarAmigo);

        const contenidoResponse = await fetch("/api/contenido/mios", { headers: { "Authorization": token } });
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

                const fileType = contenido.tipoArchivo.split('/')[0];
                const mimeType = contenido.tipoArchivo;
                let mediaContent = '';

                if (mimeType === 'application/pdf') {
                    mediaContent = `
                        <div class="pdf-preview-container" style="height: 300px; background-color: #f5f5f5;">
                            <div class="text-center p-3">
                                <i class="${getFileIconClass(mimeType, contenido.nombreOriginal)} fa-4x text-danger mb-3"></i>
                                <p>${contenido.nombreOriginal}</p>
                                <div class="d-flex gap-2">
                                    <a href="${contenido.url}" class="btn btn-sm btn-outline-primary" target="_blank"><i class="fas fa-external-link-alt"></i> Abrir PDF</a>
                                    <button class="btn btn-sm btn-outline-secondary" onclick="window.open('${contenido.url}#toolbar=0&navpanes=0', '_blank', 'width=800,height=600')"><i class="fas fa-expand"></i> Vista previa</button>
                                </div>
                            </div>
                        </div>
                    `;
                } else if (fileType === 'image') {
                    mediaContent = `
                        <div class="thumbnail-container" style="height: 200px; background-color: #000;">
                            <img src="${contenido.url}" class="img-fluid" style="max-height: 100%; object-fit: contain; cursor: pointer;" onclick="window.open('${contenido.url}', '_blank')">
                        </div>
                    `;
                } else if (fileType === 'video') {
                    mediaContent = `
                        <div class="ratio ratio-16x9 bg-dark">
                            <video controls><source src="${contenido.url}" type="${mimeType}">Tu navegador no soporta el video.</video>
                        </div>
                    `;
                } else {
                    const iconClass = getFileIconClass(mimeType, contenido.nombreOriginal);
                    mediaContent = `
                        <div class="d-flex flex-column align-items-center p-4" style="height: 200px;">
                            <i class="${iconClass} fa-4x text-secondary mb-3"></i>
                            <p>${contenido.nombreOriginal}</p>
                            <a href="${contenido.url}" class="btn btn-outline-primary btn-sm" target="_blank"><i class="fas fa-download"></i> Descargar</a>
                        </div>
                    `;
                }

                card.innerHTML = `
                    ${mediaContent}
                    <div class="card-body">
                        <h5>${contenido.nombreOriginal}</h5>
                        <p>${contenido.descripcion || 'Sin descripción'}</p>
                        <div class="d-flex justify-content-between">
                            <small>${new Date(contenido.fechaPublicacion).toLocaleString()}</small>
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

        const contenedorSolicitudes = document.getElementById("contenedorSolicitudesAyuda");
        if (solicitudesResponse.ok) {
            const solicitudes = await solicitudesResponse.json();
            if (solicitudes.length === 0) {
                contenedorSolicitudes.innerHTML = "<p class='text-muted'>No tienes solicitudes de ayuda urgentes.</p>";
            } else {
                solicitudes.forEach(solicitud => {
                    const card = document.createElement("div");
                    card.className = "border p-3 mb-2 rounded shadow-sm";
                    card.innerHTML = `
                        <h6><i class="bi bi-exclamation-triangle text-danger"></i> ${solicitud.interes}</h6>
                        <p><strong>Fecha de necesidad:</strong> ${new Date(solicitud.fechaNecesidad).toLocaleDateString()}</p>
                        <p><strong>Petición:</strong> ${solicitud.peticion}</p>
                    `;
                    contenedorSolicitudes.appendChild(card);
                });
            }
        }
// Función para cargar y mostrar los amigos
        async function cargarListaAmigos() {
            const token = localStorage.getItem("token");
            const contenedor = document.getElementById("contenedorAmigos");

            try {
                const response = await fetch("/api/usuario/amigos", {
                    headers: { "Authorization": token }
                });

                if (!response.ok) {
                    throw new Error("Error al cargar amigos");
                }

                const amigos = await response.json();

                if (amigos.length === 0) {
                    contenedor.innerHTML = "<p class='text-muted'>No tienes amigos agregados todavía.</p>";
                    return;
                }

                contenedor.innerHTML = "";
                amigos.forEach(amigo => {
                    const amigoDiv = document.createElement("div");
                    amigoDiv.className = "d-flex justify-content-between align-items-center mb-2 p-2 border rounded";
                    amigoDiv.innerHTML = `
                <div>
                    <span class="fw-bold">${amigo.nombre}</span>
                </div>
                <button class="btn btn-sm btn-outline-danger eliminar-amigo" 
                        data-id="${amigo.id}" 
                        title="Eliminar amigo">
                    <i class="fas fa-user-minus"></i>
                </button>
            `;
                    contenedor.appendChild(amigoDiv);
                });

                // Agregar event listeners a los botones de eliminar
                document.querySelectorAll('.eliminar-amigo').forEach(btn => {
                    btn.addEventListener('click', eliminarAmigo);
                });

            } catch (error) {
                console.error("Error:", error);
                contenedor.innerHTML = `<p class="text-danger">Error al cargar amigos: ${error.message}</p>`;
            }
        }

        async function cargarRecomendaciones() {
            const contenedor = document.getElementById("contenedorRecomendaciones");
            try {
                const response = await fetch("/api/grafo/recomendaciones", {
                    headers: { "Authorization": token }
                });
                if (!response.ok) {
                    contenedor.innerHTML = "<p class='text-danger'>No se pudieron cargar las recomendaciones.</p>";
                    return;
                }
                const data = await response.json();
                if (data.length === 0) {
                    contenedor.innerHTML = "<p class='text-muted'>No hay recomendaciones en este momento.</p>";
                    return;
                }
                contenedor.innerHTML = "";
                data.forEach(est => {
                    const card = document.createElement("div");
                    card.className = "border rounded p-2 mb-2 shadow-sm";
                    card.innerHTML = `
                        <h6><i class="bi bi-person-circle"></i> ${est.username}</h6>
                        <p><strong>Email:</strong> ${est.email}</p>
                        <p><strong>Intereses:</strong> ${(est.intereses || []).map(i => i.nombre).join(", ")}</p>
                    `;
                    contenedor.appendChild(card);
                });
            } catch (err) {
                contenedor.innerHTML = "<p class='text-danger'>Error al cargar recomendaciones.</p>";
            }
        }
        async function eliminarAmigo(event) {
            const boton = event.currentTarget;
            const idAmigo = boton.getAttribute('data-id');
            const token = localStorage.getItem("token");

            if (!confirm(`¿Estás seguro de que quieres eliminar a este amigo?`)) {
                return;
            }

            try {
                boton.disabled = true;
                boton.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

                const response = await fetch(`/api/usuario/eliminar?idAmigo=${idAmigo}`, {
                    method: 'DELETE',
                    headers: { "Authorization": token }
                });

                if (!response.ok) {
                    throw new Error(await response.text() || "Error al eliminar amigo");
                }

                // Recargar la lista después de eliminar
                await cargarListaAmigos();

            } catch (error) {
                console.error("Error al eliminar amigo:", error);
                alert(`Error: ${error.message}`);
            } finally {
                boton.disabled = false;
                boton.innerHTML = '<i class="fas fa-user-minus"></i>';
            }
        }

        async function cargarRecomendacionesMultiNivel() {
            const token = localStorage.getItem("token");
            const contenedor = document.getElementById("contenedorRecomendaciones");

            try {
                const response = await fetch("/api/grafo/recomendaciones-multinivel", {
                    headers: { "Authorization": token }
                });

                if (!response.ok) {
                    contenedor.innerHTML = "<p class='text-danger'>No se pudieron cargar las recomendaciones.</p>";
                    return;
                }

                const data = await response.json();

                // Limpiar contenedor
                contenedor.innerHTML = "";

                // Crear sección de primer nivel
                const primerNivelDiv = document.createElement("div");
                primerNivelDiv.className = "mb-4";
                primerNivelDiv.innerHTML = `
            <h5 class="text-primary"><i class="fas fa-user-friends"></i> Personas que quizas conozcas</h5>
            ${data.primerNivel.length === 0 ?
                    '<p class="text-muted">No tienes amigos agregados todavía.</p>' : ''}
        `;

                data.primerNivel.forEach(est => {
                    const card = document.createElement("div");
                    card.className = "border rounded p-2 mb-2 shadow-sm";
                    card.innerHTML = `
                <h6><i class="fas fa-user"></i> ${est.username}</h6>
                <p class="mb-1"><small>${est.email}</small></p>
                <p class="mb-0"><small><strong>Intereses:</strong> ${est.intereses.join(", ")}</small></p>
            `;
                    primerNivelDiv.appendChild(card);
                });
                contenedor.appendChild(primerNivelDiv);

                // Crear sección de segundo nivel
                const segundoNivelDiv = document.createElement("div");
                segundoNivelDiv.innerHTML = `
            <h5 class="text-success"><i class="fas fa-user-plus"></i> Perdido en un tema?, ellos tambien :D</h5>
            ${data.segundoNivel.length === 0 ?
                    '<p class="text-muted">No hay sugerencias en este momento.</p>' : ''}
        `;

                data.segundoNivel.forEach(est => {
                    const card = document.createElement("div");
                    card.className = "border rounded p-2 mb-2 shadow-sm";
                    card.innerHTML = `
                <h6><i class="fas fa-user"></i> ${est.username}</h6>
                <p class="mb-1"><small>${est.email}</small></p>
                <p class="mb-0"><small><strong>Intereses:</strong> ${est.intereses.join(", ")}</small></p>
            `;
                    segundoNivelDiv.appendChild(card);
                });
                contenedor.appendChild(segundoNivelDiv);

            } catch (err) {
                console.error("Error al cargar recomendaciones:", err);
                contenedor.innerHTML = "<p class='text-danger'>Error al cargar recomendaciones.</p>";
            }
        }

        await cargarRecomendacionesMultiNivel();
        await cargarListaAmigos();
    }

    catch (error) {
        console.error("Error en perfil:", error);
        window.location.href = "index.html";
    }
});