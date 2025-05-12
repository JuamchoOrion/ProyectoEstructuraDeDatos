document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem("token");
    console.log("Token obtenido del storage:", token);

    if (!token || !token.startsWith("Bearer ")) {
        console.error("Token no válido o faltante");
        window.location.href = "index.html";
        return;
    }

    try {
        // Verificación del token
        const verifyResponse = await fetch("/api/verify", {
            headers: { "Authorization": token }
        });

        if (!verifyResponse.ok) throw new Error("Token inválido o expirado");


        // ✅ Obtener el perfil del usuario
        const perfilResponse = await fetch("/api/usuario/perfil", {
            headers: { "Authorization": token }
        });

        if (!perfilResponse.ok) {
            throw new Error("No se pudo obtener el perfil del usuario");
        }

        const perfil = await perfilResponse.json();

        // Mostrar nombre de usuario en el DOM
        document.getElementById("nombreUsuario").textContent = perfil.username;


        // Cargar contenidos públicos (explorar)
        const contenidoResponse = await fetch("/api/contenido/mios", {
            headers: { "Authorization": token }
        });

        if (!contenidoResponse.ok) throw new Error("Error al obtener contenidos");

        const contenidos = await contenidoResponse.json();
        localStorage.setItem('contenidos', JSON.stringify(contenidos)); // Guardar en caché

        const contenedor = document.getElementById('contenedorContenidos');
        const mostrarContenidos = (lista) => {
            contenedor.innerHTML = "";

            if (lista.length === 0) {
                contenedor.innerHTML = "<p class='text-muted'>No hay contenidos disponibles para explorar.</p>";
                return;
            }

            lista.forEach(contenido => {
                const card = document.createElement("div");
                card.className = "card mb-3 shadow-sm";

                // Determinar cómo mostrar el contenido según su tipo
                let mediaContent = '';
                const fileType = contenido.tipoArchivo.split('/')[0]; // Obtener el tipo principal (image, video, etc.)

                if (fileType === 'image') {
                    // Mostrar miniatura de imagen
                    mediaContent = `
                        <div class="thumbnail-container" style="height: 200px; overflow: hidden; display: flex; align-items: center; justify-content: center;">
                            <img src="${contenido.url}" class="img-thumbnail" alt="${contenido.nombreOriginal}" 
                                 style="max-height: 100%; max-width: 100%; object-fit: contain; cursor: pointer;"
                                 onclick="window.open('${contenido.url}', '_blank')">
                        </div>
                    `;
                } else {
                    // Mostrar icono según tipo de archivo para no-imágenes
                    const iconClass = getFileIconClass(contenido.tipoArchivo, contenido.nombreOriginal);
                    mediaContent = `
                        <div class="d-flex flex-column align-items-center p-4">
                            <i class="${iconClass} fa-4x text-secondary mb-2"></i>
                            <a href="${contenido.url}" class="btn btn-outline-primary btn-sm" target="_blank">Ver archivo</a>
                        </div>
                    `;
                }

                card.innerHTML = `
                    ${mediaContent}
                    <div class="card-body">
                        <h5 class="card-title">${contenido.nombreOriginal}</h5>
                        <p class="card-text">${contenido.descripcion}</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <small class="text-muted">${new Date(contenido.fechaPublicacion).toLocaleString()}</small>
                            <span class="badge bg-success">${contenido.likes} <i class="fas fa-thumbs-up"></i></span>
                        </div>
                    </div>
                `;
                contenedor.appendChild(card);
            });
        };

        mostrarContenidos(contenidos);

    } catch (error) {
        console.error(error);
        window.location.href = "index.html"; // Redirige en caso de error
    }
});
