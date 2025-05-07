document.addEventListener('DOMContentLoaded', function () {
    // Cerrar sesión
    const cerrarSesionBtn = document.getElementById('cerrarSesion');
    cerrarSesionBtn.addEventListener('click', () => {
        localStorage.removeItem('usuario');
        window.location.href = 'index.html';
    });

    // Obtener usuarios del localStorage
    const usuarios = JSON.parse(localStorage.getItem('usuarios')) || [];

    const tbody = document.querySelector('#usuarios tbody');

    usuarios.forEach((usuario, index) => {
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${index + 1}</td>
            <td>${usuario.nombre}</td>
            <td>${usuario.email}</td>
            <td>${usuario.telefono || '-'}</td>
            <td>
                <button class="btn btn-sm btn-warning me-2 btn-editar" data-index="${index}">Editar</button>
                <button class="btn btn-sm btn-danger btn-eliminar" data-index="${index}">Eliminar</button>
            </td>
        `;
        tbody.appendChild(fila);
    });

    // Activar DataTable
    $('#usuarios').DataTable();

    // Función Eliminar
    document.querySelectorAll('.btn-eliminar').forEach(btn => {
        btn.addEventListener('click', function () {
            const index = this.dataset.index;
            if (confirm('¿Estás seguro de eliminar este usuario?')) {
                usuarios.splice(index, 1);
                localStorage.setItem('usuarios', JSON.stringify(usuarios));
                location.reload(); // Refresca para actualizar la tabla
            }
        });
    });

    // Función Editar (solo demo: alerta con los datos)
    document.querySelectorAll('.btn-editar').forEach(btn => {
        btn.addEventListener('click', function () {
            const index = this.dataset.index;
            const usuario = usuarios[index];
            alert(`Editar usuario:\nNombre: ${usuario.nombre}\nEmail: ${usuario.email}`);
            // Aquí podrías abrir un modal o redirigir a una página de edición
        });
    });
});
