document.addEventListener('DOMContentLoaded', () => {
    const usuario = localStorage.getItem('usuario');
    if (!usuario) {
        alert('Debes iniciar sesión para acceder.');
        window.location.href = 'login.html';
        return;
    }

    const barraBusqueda = document.getElementById('barraBusqueda');
    const formBusqueda = document.getElementById('formBusqueda');
    const contenedor = document.getElementById('contenedorExplorar');

    // Cerrar sesión
    document.getElementById('cerrarSesion').addEventListener('click', () => {
        localStorage.removeItem('usuario');
        window.location.href = 'login.html';
    });

    // Mostrar contenidos al cargar
    const contenidos = JSON.parse(localStorage.getItem('contenidos') || '[]');
    renderContenidos(contenidos);

    // Buscar por tema, autor o tipo
    formBusqueda.addEventListener('submit', (e) => {
        e.preventDefault();
        const query = barraBusqueda.value.toLowerCase();
        const resultados = contenidos.filter(c =>
            c.titulo.toLowerCase().includes(query) ||
            c.descripcion.toLowerCase().includes(query) ||
            c.tipo.toLowerCase().includes(query) ||
            c.autor.toLowerCase().includes(query)
        );
        renderContenidos(resultados);
    });

    // Mostrar resultados en pantalla
    function renderContenidos(lista) {
        contenedor.innerHTML = '';
        if (lista.length === 0) {
            contenedor.innerHTML = '<p class="text-muted">No se encontraron contenidos.</p>';
            return;
        }

        lista.forEach(c => {
            const card = document.createElement('div');
            card.className = 'col-md-4 mb-4';
            card.innerHTML = `
              <div class="card shadow-sm h-100">
                <div class="card-body">
                  <h5 class="card-title">${c.titulo}</h5>
                  <p class="card-text">${c.descripcion}</p>
                  <p><strong>Tipo:</strong> ${c.tipo}</p>
                  <p><strong>Autor:</strong> ${c.autor}</p>
                  <small class="text-muted">Publicado el ${new Date(c.fecha).toLocaleDateString()}</small>
                  <div class="mt-3">
                    <div class="valoracion" data-id="${c.id}">
                      ${renderStars(c.id)}
                    </div>
                    <small class="text-muted" id="promedio-${c.id}">Valoración: ${calcularPromedio(c.id)} ⭐</small>
                  </div>
                </div>
              </div>
            `;
            contenedor.appendChild(card);
        });
    }

    // Renderizar estrellas clicables
    function renderStars(id) {
        let estrellas = '';
        for (let i = 1; i <= 5; i++) {
            estrellas += `<span class="estrella" data-id="${id}" data-valor="${i}" style="cursor:pointer; color: gold;">★</span>`;
        }
        return estrellas;
    }

    // Calcular promedio de valoraciones
    function calcularPromedio(id) {
        const valoraciones = JSON.parse(localStorage.getItem('valoraciones') || '{}');
        const lista = valoraciones[id] || [];
        if (lista.length === 0) return 'Sin valorar';
        const promedio = lista.reduce((acc, v) => acc + v.valor, 0) / lista.length;
        return promedio.toFixed(1);
    }

    // Manejar clic en estrellas
    contenedor.addEventListener('click', (e) => {
        if (e.target.classList.contains('estrella')) {
            const id = e.target.getAttribute('data-id');
            const valor = parseInt(e.target.getAttribute('data-valor'));

            let valoraciones = JSON.parse(localStorage.getItem('valoraciones') || '{}');
            if (!valoraciones[id]) valoraciones[id] = [];

            valoraciones[id].push({ usuario, valor });
            localStorage.setItem('valoraciones', JSON.stringify(valoraciones));

            // Actualizar promedio
            document.getElementById(`promedio-${id}`).textContent = `Valoración: ${calcularPromedio(id)} ⭐`;
            alert('¡Gracias por tu valoración!');
        }
    });
});
