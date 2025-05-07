document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.querySelector("form");

    if (loginForm) {
        loginForm.addEventListener("submit", function (e) {
            e.preventDefault();

            const usernameInput = document.getElementById("username");
            const passwordInput = document.getElementById("password");

            const username = usernameInput.value.trim();
            const password = passwordInput.value.trim();

            // Validaciones
            if (!username || !password) {
                showAlert("Por favor, completa todos los campos.", "danger");
                return;
            }

            if (username.length < 4) {
                showAlert("El nombre de usuario debe tener al menos 4 caracteres", "warning");
                return;
            }

            // Llamada al backend
            fetch("/api/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: username,
                    password: password
                })
            })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => { throw new Error(text || 'Credenciales incorrectas') });
                    }
                    return response.json();
                })
                .then(data => {
                    localStorage.setItem("token", data.token);
                    showAlert("Inicio de sesión exitoso. Redirigiendo...", "success");

                    setTimeout(() => {
                        window.location.href = "/homePage.html";
                    }, 1500);
                })
                .catch(error => {
                    showAlert(error.message || "Error en el inicio de sesión", "danger");
                    console.error("Error:", error);
                });
        });
    }
});

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