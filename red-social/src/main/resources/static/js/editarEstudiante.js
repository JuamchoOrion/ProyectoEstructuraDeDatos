// editar.js - Manejo del formulario de edición de estudiantes (sin autenticación)

// Mostrar alertas
function showAlert(message, type = "info") {
    const alertContainer = document.getElementById("editar-alert-container");
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
function validarFormularioEdicion(nuevoUsername, nuevoEmail, nuevaPassword, intereses) {
    const errores = [];

    if (nuevoUsername && nuevoUsername.length < 4) {
        errores.push("El username debe tener al menos 4 caracteres");
    }

    if (nuevoEmail && !validateEmail(nuevoEmail)) {
        errores.push("El correo electrónico no es válido");
    }

    if (nuevaPassword && nuevaPassword.length < 6) {
        errores.push("La contraseña debe tener al menos 6 caracteres");
    }

    if (intereses.length === 0) {
        errores.push("Debes seleccionar al menos un interés");
    }

    return errores;
}

// Cargar datos del estudiante a editar
async function cargarDatosEstudiante() {
    try {
        const id = obtenerIdEstudiante();
        const response = await fetch(`/api/usuario/cargarDatos/${id}`);
        const data = await response.json();

        if (!response.ok) throw new Error(data.message || "Error al cargar datos");

        // 1. Actualizar campos estáticos
        document.getElementById("usernameActual").textContent = data.username || "No disponible";
        document.getElementById("emailActual").textContent = data.email || "No disponible";

        // 2. Procesar intereses (texto + checkboxes)
        const intereses = data.intereses || [];
        const interesesTexto = intereses
            .map(interes => {
                // Convertir a formato legible (ej: "MATEMATICAS" -> "Matemáticas")
                switch (interes.toUpperCase()) {
                    case "MATEMATICAS": return "Matemáticas";
                    case "FISICA": return "Física";
                    case "BIOLOGIA": return "Biología";
                    case "ETICA": return "Ética";
                    case "SOCIALES": return "Sociales";
                    default: return interes;
                }
            })
            .join(", ");

        document.getElementById("interesesActual").textContent = interesesTexto || "No especificado";

        // 3. Marcar checkboxes (comparando valores en MAYÚSCULAS)
        intereses.forEach(interes => {
            const checkbox = document.querySelector(`input[type="checkbox"][value="${interes.toUpperCase()}"]`);
            if (checkbox) checkbox.checked = true;
        });

    } catch (error) {
        console.error("Error:", error);
        showAlert(error.message, "danger");
    }
}

// Ejecutar al cargar la página
document.addEventListener("DOMContentLoaded", cargarDatosEstudiante);

// Obtener ID del estudiante de la URL
function obtenerIdEstudiante() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id');
}

// Manejar el envío del formulario de edición
document.addEventListener("DOMContentLoaded", () => {
    const editarForm = document.getElementById("editarForm");

    if (editarForm) {
        // Cargar datos del estudiante al cargar la página
        cargarDatosEstudiante()
            .then(() => {
                console.log("Datos cargados correctamente.");
            })
            .catch((error) => {
                console.error("Error capturado externamente:", error);
            });

        editarForm.addEventListener("submit", async function(e) {
            e.preventDefault();

            // Obtener valores del formulario
            const nuevoUsername = document.getElementById("nuevoUsername").value.trim();
            const nuevoEmail = document.getElementById("nuevoEmail").value.trim();
            const nuevaPassword = document.getElementById("nuevaPassword").value.trim();
            const intereses = Array.from(
                document.querySelectorAll('#editarForm input[type="checkbox"]:checked')
            ).map(checkbox => checkbox.value);

            // Validaciones
            const errores = validarFormularioEdicion(nuevoUsername, nuevoEmail, nuevaPassword, intereses);
            if (errores.length > 0) {
                showAlert(errores.join("<br>"), "danger");
                return;
            }

            const submitBtn = editarForm.querySelector('button[type="submit"]');
            const originalBtnText = submitBtn.innerHTML;

            try {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i> Guardando...';

                // Preparar datos para enviar
                const datosActualizados = {};
                if (nuevoUsername) datosActualizados.username = nuevoUsername;
                if (nuevoEmail) datosActualizados.email = nuevoEmail;
                if (nuevaPassword) datosActualizados.password = nuevaPassword;
                datosActualizados.intereses = intereses;

                const estudianteId = obtenerIdEstudiante();
                const response = await fetch(`/api/usuario/editar/${estudianteId}`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(datosActualizados)
                });

                if (!response.ok) {
                    throw new Error("Error al actualizar el estudiante");
                }

                showAlert("¡Estudiante actualizado exitosamente!", "success");
                setTimeout(() => {
                    window.location.href = "moderador.html?updated=" + Date.now();
                }, 1500);
                setTimeout(() => cargarDatosEstudiante(), 1000);

            } catch (error) {
                console.error("Error:", error);
                showAlert(error.message || "Ocurrió un error durante la actualización", "danger");
            } finally {
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalBtnText;
            }
        });
    }
});