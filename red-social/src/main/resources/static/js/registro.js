document.addEventListener("DOMContentLoaded", () => {
    const registroForm = document.getElementById("registroForm");

    if (registroForm) {
        registroForm.addEventListener("submit", async function (e) {
            e.preventDefault();

            const nombre = document.getElementById("nombre").value.trim();
            const email = document.getElementById("email").value.trim();
            const password = document.getElementById("password").value.trim();
            const confirmPassword = document.getElementById("confirmPassword").value.trim();

            if (!nombre || !email || !password || !confirmPassword) {
                showAlert("Todos los campos son obligatorios.", "danger");
                return;
            }

            if (!validateEmail(email)) {
                showAlert("El correo electrónico no tiene un formato válido.", "warning");
                return;
            }

            if (password.length < 6) {
                showAlert("La contraseña debe tener al menos 6 caracteres.", "warning");
                return;
            }

            if (password !== confirmPassword) {
                showAlert("Las contraseñas no coinciden.", "danger");
                return;
            }

            const response = await fetch("/api/registro", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ nombre, email, password, confirmPassword })
            });

            const text = await response.text();

            if (response.ok) {
                showAlert(text, "success");
                setTimeout(() => {
                    window.location.href = "/login.html";
                }, 2000);
            } else {
                showAlert(text, "danger");
            }
        });
    }
});

function validateEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

function showAlert(message, type = "info") {
    const existingAlert = document.getElementById("registro-alert");
    if (existingAlert) existingAlert.remove();

    const alert = document.createElement("div");
    alert.id = "registro-alert";
    alert.className = `alert alert-${type} mt-3`;
    alert.textContent = message;

    const formCard = document.querySelector(".card");
    formCard.appendChild(alert);
}

