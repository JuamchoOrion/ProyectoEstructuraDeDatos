function updateTokenDisplay() {
    const token = localStorage.getItem('token');
    document.getElementById('tokenInfo').textContent = token || 'No hay token almacenado';
}

document.getElementById('setTokenBtn').addEventListener('click', () => {
    localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqdWFuY2hvIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c');
    updateTokenDisplay();
    alert('Token de prueba establecido');
});

document.getElementById('clearTokenBtn').addEventListener('click', () => {
    localStorage.removeItem('token');
    updateTokenDisplay();
    alert('Token eliminado');
});

document.addEventListener("DOMContentLoaded", () => {
    updateTokenDisplay();

    const form = document.getElementById("formContenido");
    const responseDiv = document.getElementById("response");
    const submitBtn = form.querySelector('button[type="submit"]');

    form.addEventListener("submit", async function (e) {
        e.preventDefault();
        submitBtn.disabled = true;
        responseDiv.innerHTML = '<div class="text-info">Subiendo contenido...</div>';

        const token = localStorage.getItem("token");
        if (!token) {
            responseDiv.innerHTML = '<div class="text-danger">Error: No hay token disponible. Por favor inicia sesión.</div>';
            submitBtn.disabled = false;
            return;
        }

        try {
            const formData = new FormData(form);
            const response = await fetch("/api/contenido/subir", {
                method: "POST",
                headers: {
                    "Authorization": token
                },
                body: formData
            });

            if (response.ok) {
                const data = await response.json();
                responseDiv.innerHTML = `
                        <div class="alert alert-success">
                            <strong>¡Contenido subido con éxito!</strong><br>
                            <strong>ID:</strong> ${data.contentId}<br>
                            <strong>Archivo:</strong> ${data.nombreOriginal}<br>
                            <a href="${data.url}" class="btn btn-sm btn-primary mt-2" target="_blank">
                                <i class="bi bi-box-arrow-up-right"></i> Ver contenido
                            </a>
                        </div>
                    `;
                form.reset();
            } else {
                const errorText = await response.text();
                responseDiv.innerHTML = `<div class="alert alert-danger">Error (${response.status}): ${errorText}</div>`;
            }
        } catch (error) {
            responseDiv.innerHTML = `<div class="alert alert-danger">Error de conexión: ${error.message}</div>`;
            console.error(error);
        } finally {
            submitBtn.disabled = false;
        }
    });
});