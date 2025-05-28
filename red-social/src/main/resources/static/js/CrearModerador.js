document.getElementById("crearModeradorForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    // Obtener los valores del formulario
    const nombre = document.getElementById("nombre").value.trim();
    const correo = document.getElementById("correo").value.trim();
    const contrasena = document.getElementById("contrasena").value;
    const confirmarContrasena = document.getElementById("confirmarContrasena").value;

    // Validación básica
    if (contrasena !== confirmarContrasena) {
        alert("Las contraseñas no coinciden");
        return;
    }

    try {
        const response = await fetch("/api/moderadores/registro", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                nombre: nombre,
                correo: correo,
                contrasena: contrasena
            })
        });

        const result = await response.text();

        if (response.ok) {
            alert("Moderador creado exitosamente");
            document.getElementById("crearModeradorForm").reset(); // Limpiar el formulario
        } else {
            alert("Error al crear moderador: " + result);
        }
    } catch (error) {
        console.error("Error en la solicitud:", error);
        alert("Error del servidor. Inténtalo más tarde.");
    }
});
