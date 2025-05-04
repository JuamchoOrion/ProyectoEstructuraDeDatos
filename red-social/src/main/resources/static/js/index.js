document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.querySelector("form");

    if (loginForm) {
        loginForm.addEventListener("submit", function (e) {
            e.preventDefault();

            const emailInput = document.getElementById("email");
            const passwordInput = document.getElementById("password");

            const email = emailInput.value.trim();
            const password = passwordInput.value.trim();

            // Validaciones
            if (!email || !password) {
                showAlert("Por favor, completa todos los campos.", "danger");
                return;
            }

            if (!validateEmail(email)) {
                showAlert("Por favor, introduce un correo válido.", "warning");
                return;
            }

            // Aquí podrías hacer una llamada al backend con fetch/AJAX

            // Simulación de inicio de sesión exitoso
            showAlert("Inicio de sesión exitoso. Redirigiendo...", "success");
            setTimeout(() => {
                window.location.href = "/perfil.html";
            }, 1500);
        });
    }
});

// Validación básica de correo
function validateEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

// Función para mostrar mensajes tipo alerta de Bootstrap
function showAlert(message, type = "info") {
    const existingAlert = document.getElementById("login-alert");
    if (existingAlert) existingAlert.remove();

    const alert = document.createElement("div");
    alert.id = "login-alert";
    alert.className = `alert alert-${type} mt-3`;
    alert.textContent = message;

    const formContainer = document.querySelector(".card");
    formContainer.appendChild(alert);
}
