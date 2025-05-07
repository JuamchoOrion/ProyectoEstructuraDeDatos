document.addEventListener('DOMContentLoaded', () => {
    // Verificar si el usuario está autenticado
    const usuario = localStorage.getItem('usuario');
    if (!usuario) {
        alert('Debes iniciar sesión.');
        window.location.href = 'login.html';
        return;
    }

    // Mostrar nombre de usuario
    document.getElementById('nombreUsuario').textContent = usuario;

    // Cerrar sesión
    document.getElementById('cerrarSesion').addEventListener('click', () => {
        localStorage.removeItem('usuario');
        window.location.href = 'index.html';
    });

    // Cargar contenidos publicados

    const contenedor = document.getElementById('contenedorContenidos');
    let contenidos = JSON.parse(localStorage.getItem('contenidos') || '[]');

    const mostrarContenidos = (lista) => {
        contenedor.innerHTML = '';
        if (lista.length === 0) {
            contenedor.innerHTML = '<p class="text-muted">No hay contenidos disponibles.</p>';
            return;
        }

        lista.forEach((c) => {
            const card = document.createElement('div');
            card.className = 'card mb-3';
            card.innerHTML = `
        <div class="card-body">
          <h5 class="card-title">${c.titulo}</h5>
          <p class="card-text">${c.descripcion}</p>
          <p class="card-text"><small class="text-muted">Tipo: ${c.tipo} | Autor: ${c.autor}</small></p>
        </div>
      `;
            contenedor.appendChild(card);
        });
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

    // Enlaces de navegación desde la navbar
    document.getElementById('navInicio').addEventListener('click', () => window.location.href = 'homePage.html');
    document.getElementById('navPerfil').addEventListener('click', () => window.location.href = 'perfil.html');
    document.getElementById('navGrupos').addEventListener('click', () => window.location.href = 'grupos.html');
    document.getElementById('navMensajes').addEventListener('click', () => window.location.href = 'mensajes.html');
});
