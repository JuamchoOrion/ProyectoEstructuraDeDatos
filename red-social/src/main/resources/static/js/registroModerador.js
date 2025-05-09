document.getElementById("registroModeradorForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    // Obtenemos los valores de los campos del formulario
    const nombre = document.getElementById("nombre").value;
    const correo = document.getElementById("correo").value;
    const contrasena = document.getElementById("contrasena").value;
    const confirmarContrasena = document.getElementById("confirmarContrasena").value;

    // Validación básica del lado del cliente
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
            alert("Moderador registrado correctamente");
            window.location.href = "login.html"; // Redirigir al login
        } else {
            alert("Error: " + result);
        }
    } catch (error) {
        console.error("Error en la solicitud:", error);
        alert("Error del servidor");
    }
});