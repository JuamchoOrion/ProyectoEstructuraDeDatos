document.getElementById("formContenido").addEventListener("submit", async function (e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);

    const token = localStorage.getItem("token");
    if (!token) {
        alert("No hay token disponible. Por favor inicia sesión.");
        return;
    }

    const response = await fetch("/api/contenido/subir", {
        method: "POST",
        headers: {
            "Authorization": token, // o "Bearer " + token según tu caso
        },
        body: formData
    });

    const text = await response.text();
    alert(text);
});