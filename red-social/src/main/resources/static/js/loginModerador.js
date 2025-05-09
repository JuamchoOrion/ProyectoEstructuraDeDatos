function showAlert(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} fixed-top text-center`;
    alertDiv.textContent = message;
    document.body.prepend(alertDiv);

    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
}
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.querySelector("form");

    if (loginForm) {
        loginForm.addEventListener("submit", async function (e) {
            e.preventDefault();

            const username = document.getElementById("username").value.trim();
            const password = document.getElementById("password").value.trim();

            // Validaciones básicas
            if (!username || !password) {
                showAlert("Por favor, completa todos los campos.", "danger");
                return;
            }

            try {
                const response = await fetch("/api/moderadores/auth/login", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ username, password })
                });

                const data = await response.json();

                if (response.ok) {
                    console.log("Login exitoso. Token recibido:", data.jwt);
                    localStorage.setItem("token", "Bearer " + data.jwt);
                    console.log(localStorage.getItem("token"));

                    // Redirección después de login exitoso
                    window.location.href = "/moderador.html";
                } else {
                    throw new Error(data.message || "Error en el login");
                }
            } catch (error) {
                console.error("Error:", error);
                showAlert(error.message || "Error en el inicio de sesión", "danger");
            }
        });
    }
});