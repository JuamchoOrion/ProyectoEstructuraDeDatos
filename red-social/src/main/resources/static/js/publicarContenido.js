document.addEventListener('DOMContentLoaded', () => {
    // Verificar que el usuario esté autenticado
    const usuario = localStorage.getItem('usuario');
    if (!usuario) {
        alert('Debes iniciar sesión para publicar contenido.');
        window.location.href = 'index.html';
        return;
    }

    // Cerrar sesión
    const cerrarSesionBtn = document.getElementById('cerrarSesion');
    cerrarSesionBtn.addEventListener('click', () => {
        localStorage.removeItem('usuario');
        window.location.href = 'index.html';
    });

    // Navegación desde la navbar
    document.getElementById('navInicio').addEventListener('click', () => window.location.href = 'homePage.html');
    document.getElementById('navPerfil').addEventListener('click', () => window.location.href = 'perfil.html');
    document.getElementById('navGrupos').addEventListener('click', () => window.location.href = 'grupos.html');
    document.getElementById('navMensajes').addEventListener('click', () => window.location.href = 'mensajes.html');

    // Manejador del formulario de publicación
    const form = document.getElementById('formContenido');
    form.addEventListener('submit', (e) => {
        e.preventDefault();

        const titulo = document.getElementById('titulo').value.trim();
        const descripcion = document.getElementById('descripcion').value.trim();
        const tipo = document.getElementById('tipo').value;

        if (!titulo || !descripcion || !tipo) {
            alert('Por favor, completa todos los campos.');
            return;
        }

        const nuevoContenido = {
            titulo,
            descripcion,
            tipo,
            autor: usuario,
            fecha: new Date().toISOString()
        };

        const contenidos = JSON.parse(localStorage.getItem('contenidos') || '[]');
        contenidos.push(nuevoContenido);
        localStorage.setItem('contenidos', JSON.stringify(contenidos));

        alert('Contenido publicado exitosamente.');
        form.reset();
    });
});
