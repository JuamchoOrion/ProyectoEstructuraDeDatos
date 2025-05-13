
const usuarioActual = "juan";
const contactoActual = "carlos";

function cargarMensajes() {
    fetch(`/api/mensajes/${usuarioActual}/${contactoActual}`)
        .then(res => res.json())
        .then(mensajes => {
            const lista = document.querySelector(".message-list");
            lista.innerHTML = "";

            mensajes.forEach(m => {
                const div = document.createElement("div");
                div.classList.add("message");
                div.classList.add(m.emisor === usuarioActual ? "sent" : "received");
                div.textContent = m.contenido;
                lista.appendChild(div);
            });

            lista.scrollTop = lista.scrollHeight;
        })
        .catch(err => console.error("Error al cargar mensajes:", err));
}

function enviarMensaje() {
    const input = document.getElementById("mensajeInput");
    const contenido = input.value.trim();

    if (contenido === "") return;

    const mensaje = {
        emisor: usuarioActual,
        receptor: contactoActual,
        contenido: contenido
    };

    fetch("/api/mensajes/enviar", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(mensaje)
    })
        .then(res => res.json())
        .then(() => {
            input.value = "";
            cargarMensajes();
        })
        .catch(err => console.error("Error al enviar mensaje:", err));
}

document.addEventListener("DOMContentLoaded", () => {
    cargarMensajes();

    const input = document.getElementById("mensajeInput");
    input.addEventListener("keydown", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            enviarMensaje();
        }
    });
});
