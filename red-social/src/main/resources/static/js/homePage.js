document.addEventListener("DOMContentLoaded", () => {
    const user = localStorage.getItem("userEmail");

    // Verificación de sesión activa
    if (!user) {
        alert("Por favor, inicia sesión para acceder a esta sección.");
        window.location.href = "/login.html";
        return;
    }

    // Personaliza el saludo si lo deseas
    const welcomeTitle = document.querySelector("h2");
    if (welcomeTitle) {
        welcomeTitle.textContent = `Bienvenido, ${user.split('@')[0]} 👋`;
    }

    // Redirecciones de botones principales
    const setupRedirect = (selector, url) => {
        const btn = document.querySelector(selector);
        if (btn) {
            btn.addEventListener("click", (e) => {
                e.preventDefault();
                window.location.href = url;
            });
        }
    };
// faltan todas esas inetrfaces xd
    //pero los botones redigiran a esas

    setupRedirect(".btn-primary", "/explorar.html");
    setupRedirect(".btn-success", "/grupos.html");
    setupRedirect(".btn-warning", "/solicitud.html");
    setupRedirect(".btn-info", "/sugerencias.html");
    setupRedirect(".btn-dark", "/grafo.html");

    // Navbar redirecciones
    setupRedirect('.nav-link[href="#"]', '/index.html'); // Inicio
    setupRedirect('.nav-link[href="#"]:nth-of-type(2)', '/perfil.html'); // Perfil
    setupRedirect('.nav-link[href="#"]:nth-of-type(3)', '/grupos.html'); // Grupos
    setupRedirect('.nav-link[href="#"]:nth-of-type(4)', '/mensajes.html'); // Mensajes

    // Cerrar sesión
    const logoutBtn = document.querySelector('.nav-link[href="#"]:nth-of-type(5)');
    if (logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            localStorage.removeItem("userEmail");
            window.location.href = "/login.html";
        });
    }
});
