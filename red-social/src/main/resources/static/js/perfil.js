document.addEventListener('DOMContentLoaded', async () => {
    // Obtener token de localStorage
    const token = localStorage.getItem("token");
    console.log("Token obtenido del storage:", token);

    // Validación básica del token
    if (!token || !token.startsWith("Bearer ")) {
        console.error("Token no válido o faltante");
        window.location.href = "login.html";
        return;
    }

    try {
        // Verificar token con el backend
        const verifyResponse = await fetch("/api/verify", {
            headers: {
                "Authorization": token // Enviamos el token completo con "Bearer"
            }
        });

        if (!verifyResponse.ok) {
            throw new Error("Token inválido o expirado");
        }

        // Obtener datos del perfil
        const profileResponse = await fetch("/api/usuario/perfil", {
            headers: {
                "Authorization": token
            }
        });

        if (!profileResponse.ok) {
            throw new Error("Error al cargar perfil");
        }

        const usuario = await profileResponse.json();
        document.getElementById('nombreUsuario').textContent = usuario.username;

        // Configurar cerrar sesión
        document.getElementById('cerrarSesion').addEventListener('click', () => {
            localStorage.removeItem('token');
            window.location.href = 'login.html';
        });

        // Cargar y mostrar contenidos
        const contenedor = document.getElementById('contenedorContenidos');
        let contenidos = JSON.parse(localStorage.getItem('contenidos') || '[]');

        const mostrarContenidos = (lista) => {
            contenedor.innerHTML = lista.length === 0
                ? '<p class="text-muted">No hay contenidos disponibles.</p>'
                : lista.map(c => `
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5 class="card-title">${c.titulo}</h5>
                            <p class="card-text">${c.descripcion}</p>
                            <p class="card-text"><small class="text-muted">Tipo: ${c.tipo} | Autor: ${c.autor}</small></p>
                        </div>
                    </div>
                `).join('');
        };

        mostrarContenidos(contenidos);

        // Funcionalidad de búsqueda
        document.getElementById('formBusqueda').addEventListener('submit', (e) => {
            e.preventDefault();
            const termino = document.getElementById('barraBusqueda').value.toLowerCase().trim();
            const filtrados = contenidos.filter(c =>
                c.titulo.toLowerCase().includes(termino) ||
                c.autor.toLowerCase().includes(termino) ||
                c.tipo.toLowerCase().includes(termino)
            );
            mostrarContenidos(filtrados);
        });

    } catch (error) {
        console.error("Error:", error);
        localStorage.removeItem("token");
        window.location.href = "login.html";
    }
});