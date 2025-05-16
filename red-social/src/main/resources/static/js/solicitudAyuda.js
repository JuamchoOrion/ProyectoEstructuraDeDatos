document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('solicitudForm');
    const token = localStorage.getItem('token');

    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const solicitudData = {
            fechaNecesidad: document.getElementById('fechaNecesidad').value,
            peticion: document.getElementById('peticion').value,
            interes: document.getElementById('interes').value
        };

        try {
            const response = await fetch('/api/solicitudes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    "Authorization": token
                },
                body: JSON.stringify(solicitudData)
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Error al crear solicitud');
            }

            const data = await response.json();
            alert(`Solicitud creada con ID: ${data.id}`);
            form.reset();

        } catch (error) {
            console.error('Error:', error);
            alert(`Error: ${error.message}`);

            if (error.message.includes('token') || error.message.includes('autenticación')) {
                localStorage.removeItem('token');
                window.location.href = 'perfil.html';
            }
        }
    });
});