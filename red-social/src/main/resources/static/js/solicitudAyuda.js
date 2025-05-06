// js/solicitud.js
// AGREGAR EVENTOS DEL RESTO DE ELEMENTOS EN NAV BAR que falten
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('solicitudForm');

    // Proteger acceso
    const usuario = localStorage.getItem('usuario');
    if (!usuario) {
        alert('Debes iniciar sesión para publicar una solicitud.');
        window.location.href = 'index.html';
        return;
    }

    // Evento para cerrar sesión (todas las vistas tienen este evento)
    const cerrarSesionBtn = document.getElementById('cerrarSesion');
    cerrarSesionBtn.addEventListener('click', () => {
        localStorage.removeItem('usuario');
        window.location.href = 'index.html';
    });

    // Eventos de la barra de navegación
    const navInicio = document.getElementById('navHomePage');
    const navPerfil = document.getElementById('navPerfil');
    const navGrupos = document.getElementById('navGrupos');
    const navMensajes = document.getElementById('navMensajes');

    navInicio.addEventListener('click', (e) => {
        e.preventDefault();
        window.location.href = 'homePage.html';
    });

    navPerfil.addEventListener('click', (e) => {
        e.preventDefault();
        window.location.href = 'perfil.html';
    });

    navGrupos.addEventListener('click', (e) => {
        e.preventDefault();
        window.location.href = 'grupos.html';
    });

    navMensajes.addEventListener('click', (e) => {
        e.preventDefault();
        window.location.href = 'mensajes.html';
    });

    // Manejo del formulario, se maneja el botón submit o publicar solicitud y guarda datos para crear elemento de tipo solicitud
    form.addEventListener('submit', (e) => {
        e.preventDefault();

        const tema = document.getElementById('tema').value.trim();
        const descripcion = document.getElementById('descripcion').value.trim();
        const urgencia = document.getElementById('urgencia').value;

        if (!tema || !descripcion || !urgencia) {
            alert('Por favor, completa todos los campos.');
            return;
        }

        const solicitud = {
            tema,
            descripcion,
            urgencia,
            autor: usuario,
            fecha: new Date().toISOString()
        };

        // Simulamos la "cola de prioridad" con localStorage (agregar su lógica de priority queue. En la interfaz se manejan 3: ALTA - MEDIA - BAJA y es una lista desplegable)
        const solicitudes = JSON.parse(localStorage.getItem('solicitudes') || '[]');
        solicitudes.push(solicitud);
        localStorage.setItem('solicitudes', JSON.stringify(solicitudes));

        alert('Solicitud publicada exitosamente.');
        form.reset();
    });
});
