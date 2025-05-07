document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.querySelector("form");

    if (loginForm) {
        loginForm.addEventListener("submit", async function (e) {
            e.preventDefault();

            const username = document.getElementById("username").value.trim();
            const password = document.getElementById("password").value.trim();

            // Validaciones
            if (!username || !password) {
                showAlert("Por favor, completa todos los campos.", "danger");
                return;
            }

            if (username.length < 4) {
                showAlert("El nombre de usuario debe tener al menos 4 caracteres", "warning");
                return;
            }

            try {
                const response = await fetch("/api/login", {
                    method: "POST",
                    credentials: 'include', // Importante para cookies
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ username, password })
                });

                if (!response.ok) {
                    const error = await response.text();
                    throw new Error(error || "Credenciales incorrectas");
                }

                const data = await response.json();
                console.log("Login exitoso. Token recibido:", data.jwt);  // Log aquí
                localStorage.setItem("token", "Bearer " + data.jwt);
                console.log(localStorage.getItem("token"));
                showAlert("Inicio de sesión exitoso. Redirigiendo...", "success");

                setTimeout(() => {
                    window.location.href = "/perfil.html";
                }, 1500);
            } catch (error) {
                showAlert(error.message || "Error en el inicio de sesión", "danger");
                console.error("Error:", error);
            }
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