document.addEventListener("DOMContentLoaded", () => {
    const registroForm = document.getElementById("registroForm");

    if (registroForm) {
        registroForm.addEventListener("submit", async function (e) {
            e.preventDefault();

            // Obtener valores del formulario
            const nombre = document.getElementById("nombre").value.trim();
            const email = document.getElementById("email").value.trim();
            const password = document.getElementById("password").value.trim();
            const confirmPassword = document.getElementById("confirmPassword").value.trim();

            // Obtener intereses seleccionados (convertir a mayúsculas)
            const intereses = Array.from(
                document.querySelectorAll('input[type="checkbox"]:checked')
            ).map(checkbox => checkbox.value);

            // Validaciones
            const errores = validarFormulario(nombre, email, password, confirmPassword, intereses);
            if (errores.length > 0) {
                showAlert(errores.join("<br>"), "danger");
                return;
            }

            try {
                // Mostrar estado de carga
                const submitBtn = registroForm.querySelector('button[type="submit"]');
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i> Registrando...';

                const response = await fetch("/api/registro", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        nombre,
                        email,
                        password,
                        confirmPassword,
                        intereses
                    })
                });

                const data = await response.json();

                if (!response.ok) {
                    throw new Error(data.message || "Error en el registro");
                }

                // Registro exitoso
                showAlert(`¡Registro exitoso, ${nombre}! Serás redirigido...`, "success");

                // Redirigir después de 2 segundos
                setTimeout(() => {
                    window.location.href = "index.html";
                }, 2000);

            } catch (error) {
                console.error("Error en el registro:", error);
                showAlert(error.message || "Ocurrió un error durante el registro", "danger");
            } finally {
                // Restaurar botón
                const submitBtn = registroForm.querySelector('button[type="submit"]');
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<i class="fas fa-user-plus me-2"></i>Registrarse';
            }
        });
    }
});

/**
 * Valida los datos del formulario
 */
function validarFormulario(nombre, email, password, confirmPassword, intereses) {
    const errores = [];

    if (!nombre) errores.push("El nombre de usuario es obligatorio");
    if (!email) errores.push("El correo electrónico es obligatorio");
    if (!password) errores.push("La contraseña es obligatoria");
    if (!confirmPassword) errores.push("Debes confirmar tu contraseña");

    if (nombre && nombre.length < 4) {
        errores.push("El nombre debe tener al menos 4 caracteres");
    }

    if (email && !validateEmail(email)) {
        errores.push("El correo electrónico no es válido");
    }

    if (password && password.length < 6) {
        errores.push("La contraseña debe tener al menos 6 caracteres");
    }

    if (password && confirmPassword && password !== confirmPassword) {
        errores.push("Las contraseñas no coinciden");
    }

    if (intereses.length === 0) {
        errores.push("Debes seleccionar al menos un interés");
    }

    return errores;
}

/**
 * Valida formato de email
 */
function validateEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

/**
 * Muestra alertas en el formulario
 */
function showAlert(message, type = "info") {
    const alertContainer = document.getElementById("registro-alert-container");
    alertContainer.innerHTML = `
        <div class="alert alert-${type} mt-3 animate__animated animate__fadeIn">
            ${message}
        </div>
    `;

    // Eliminar alerta después de 5 segundos (excepto para success)
    if (type !== "success") {
        setTimeout(() => {
            const alert = alertContainer.querySelector(".alert");
            if (alert) {
                alert.classList.add("animate__fadeOut");
                alert.addEventListener("animationend", () => {
                    alert.remove();
                });
            }
        }, 5000);
    }
}