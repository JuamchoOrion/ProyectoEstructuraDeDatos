document.addEventListener("DOMContentLoaded", () => {
    // Lógica del formulario (no se modifica)
    document.getElementById("formContenido").addEventListener("submit", async function (e) {
        e.preventDefault();
        const form = e.target;
        const formData = new FormData(form);

        const token = localStorage.getItem("token");
        if (!token) {
            alert("No hay token disponible. Por favor inicia sesión.");
            return;
        }

        const response = await fetch("/api/contenido/subir", {
            method: "POST",
            headers: {
                "Authorization": token, // o "Bearer " + token según tu caso
            },
            body: formData
        });

        const text = await response.text();
        alert(text);
    });

    // Agrega navegabilidad con la navbar
    const cerrarSesionBtn = document.getElementById('cerrarSesion');
    if (cerrarSesionBtn) {
        cerrarSesionBtn.addEventListener('click', () => {
            localStorage.removeItem('usuario');
            localStorage.removeItem('token'); // también eliminamos el token por seguridad
            window.location.href = 'index.html';
        });
    }

    const navLinks = {
        navInicio: 'homePage.html',
        navPerfil: 'perfil.html',
        navGrupos: 'grupos.html',
        navMensajes: 'mensajes.html'
    };

    Object.entries(navLinks).forEach(([id, href]) => {
        const link = document.getElementById(id);
        if (link) {
            link.addEventListener('click', () => window.location.href = href);
        }
    });
});
