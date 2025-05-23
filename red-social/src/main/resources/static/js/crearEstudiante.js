// registro.js - Manejo del formulario de creación de estudiantes con autenticación JWT

// Token Manager (puede moverse a un archivo separado si se usa en múltiples lugares)
const tokenManager = {
    getToken: () => localStorage.getItem('moderadorToken'),
    setToken: (token) => localStorage.setItem('moderadorToken', token),
    clearToken: () => localStorage.removeItem('moderadorToken'),
    getUsername: () => {
        const token = tokenManager.getToken();
        if (!token) return null;
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.sub;
        } catch (e) {
            console.error("Error decodificando token:", e);
            return null;
        }
    },
    fetchWithAuth: async (url, options = {}) => {
        const token = tokenManager.getToken();
        if (!token) {
            showAlert("Debes iniciar sesión como moderador primero", "danger");
            throw new Error("No autenticado");
        }

        const headers = {
            ...options.headers,
            "Authorization": `Bearer ${token}`
        };
        console.log("Token actual:", tokenManager.getToken());
        if (!tokenManager.getToken()) {
            console.error("No hay token disponible");
            return;
        }

        const response = await fetch("/api/moderadores/crear-estudiante", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${tokenManager.getToken()}` // ← ESTA LÍNEA ES CRUCIAL
            },
            body: JSON.stringify(formData)
        });



        if (response.status === 401) {
            tokenManager.clearToken();
            showAlert("Sesión expirada. Por favor inicia sesión nuevamente", "danger");
            // Redirigir a login o recargar
            setTimeout(() => window.location.href = '/login', 2000);
            throw new Error("Token expirado");
        }

        return response;
    }
};

// Mostrar alertas
function showAlert(message, type = "info") {
    const alertContainer = document.getElementById("registro-alert-container");
    alertContainer.innerHTML = `
        <div class="alert alert-${type} mt-3 animate__animated animate__fadeIn">
            ${message}
        </div>
    `;

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

// Validar email
function validateEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

// Validar formulario
function validarFormulario(nombre, email, password, confirmPassword, intereses) {
    const errores = [];

    if (!nombre) errores.push("El nombre del estudiante es obligatorio");
    if (!email) errores.push("El correo electrónico es obligatorio");
    if (!password) errores.push("La contraseña es obligatoria");
    if (!confirmPassword) errores.push("Debes confirmar la contraseña");

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

// Manejar el envío del formulario
document.addEventListener("DOMContentLoaded", () => {
    const registroForm = document.getElementById("registroForm");

    if (registroForm) {
        // Verificar si el usuario es moderador
        const username = tokenManager.getUsername();
        if (!username) {
            showAlert("Acceso restringido. Solo moderadores pueden crear estudiantes.", "danger");
            registroForm.querySelector('button[type="submit"]').disabled = true;
            return;
        }

        registroForm.addEventListener("submit", async function(e) {
            e.preventDefault();

            // Obtener valores del formulario
            const nombre = document.getElementById("nombre").value.trim();
            const email = document.getElementById("email").value.trim();
            const password = document.getElementById("password").value.trim();
            const confirmPassword = document.getElementById("confirmPassword").value.trim();

            // Obtener intereses seleccionados
            const intereses = Array.from(
                document.querySelectorAll('input[type="checkbox"]:checked')
            ).map(checkbox => checkbox.value);

            // Validaciones
            const errores = validarFormulario(nombre, email, password, confirmPassword, intereses);
            if (errores.length > 0) {
                showAlert(errores.join("<br>"), "danger");
                return;
            }

            const submitBtn = registroForm.querySelector('button[type="submit"]');
            const originalBtnText = submitBtn.innerHTML;

            try {
                // Mostrar estado de carga
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i> Creando...';

                // Enviar datos al servidor con autenticación
                const response = await tokenManager.fetchWithAuth("/api/moderadores/crear-estudiante", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        nombre,
                        email,
                        password,
                        intereses
                    })
                });

                const data = await response.json();

                if (!response.ok) {
                    throw new Error(data.message || "Error al crear el estudiante");
                }

                // Éxito
                showAlert(`¡Estudiante ${nombre} creado exitosamente por ${username}!`, "success");
                registroForm.reset();

            } catch (error) {
                console.error("Error:", error);
                showAlert(error.message || "Ocurrió un error durante la creación", "danger");
            } finally {
                // Restaurar botón
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalBtnText;
            }
        });
    }
});