document.addEventListener('DOMContentLoaded', () => {
    console.log('Página de historial cargada');
    
    const btnListarHistorial = document.getElementById('btnListarHistorial');
    const btnBuscarPaciente = document.getElementById('btnBuscarPaciente');
    const btnLimpiarBusqueda = document.getElementById('btnLimpiarBusqueda');
    const inpBuscarPaciente = document.getElementById('inpBuscarPaciente');
    
    // Verificar que los elementos existan
    console.log('Elementos encontrados:', {
        btnListarHistorial: !!btnListarHistorial,
        btnBuscarPaciente: !!btnBuscarPaciente,
        btnLimpiarBusqueda: !!btnLimpiarBusqueda,
        inpBuscarPaciente: !!inpBuscarPaciente
    });
    
    if (!btnListarHistorial || !btnBuscarPaciente || !btnLimpiarBusqueda || !inpBuscarPaciente) {
        console.error('Error: No se encontraron todos los elementos necesarios');
        return;
    }
    
    // Cargar historial al iniciar
    cargarHistorial();
    
    // Event listener para actualizar lista
    btnListarHistorial.addEventListener('click', async () => {
        console.log('Botón "Actualizar Lista" clickeado');
        await cargarHistorial();
    });
    
    // Event listener para botón de búsqueda
    btnBuscarPaciente.addEventListener('click', async () => {
        console.log('Botón "Buscar" clickeado');
        const nombre = inpBuscarPaciente.value.trim();
        console.log('Nombre a buscar:', nombre);
        if (nombre) {
            await buscarPorPaciente(nombre);
        } else {
            alert('Por favor ingrese un nombre para buscar');
        }
    });
    
    // Buscar al presionar Enter
    inpBuscarPaciente.addEventListener('keypress', async (e) => {
        if (e.key === 'Enter') {
            console.log('Enter presionado');
            const nombre = inpBuscarPaciente.value.trim();
            if (nombre) {
                await buscarPorPaciente(nombre);
            } else {
                await cargarHistorial();
            }
        }
    });
    
    // Event listener para limpiar búsqueda
    btnLimpiarBusqueda.addEventListener('click', async () => {
        console.log('Botón "Limpiar" clickeado');
        inpBuscarPaciente.value = '';
        await cargarHistorial();
    });
});

async function cargarHistorial() {
    try {
        const URL = 'http://localhost:8080/api/cita/pila/historial';
        console.log('Intentando conectar a:', URL);
        
        const response = await fetch(
            URL, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            }
        );

        console.log('Respuesta recibida:', response.status, response.statusText);

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status} - ${response.statusText}`);
        }

        const data = await response.json();
        console.log('=== DATOS DEL HISTORIAL ===');
        console.log('Objeto completo:', JSON.stringify(data, null, 2));
        console.log('Tamaño del historial:', data.tamaño);
        console.log('Está vacío?', data.isEmpty);
        console.log('historialCitas existe?', !!data.historialCitas);
        console.log('Tipo de historialCitas:', typeof data.historialCitas);
        if (data.historialCitas) {
            console.log('Es array?', Array.isArray(data.historialCitas));
            console.log('Longitud:', data.historialCitas.length);
        }

        const tbodyHistorial = document.getElementById('tbodyHistorial');
        tbodyHistorial.innerHTML = ''; // Limpiar tabla primero

        // Verificar si hay datos
        if (!data.historialCitas || data.historialCitas.length === 0) {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td colspan="6" class="text-center">No hay citas en el historial</td>
            `;
            tbodyHistorial.appendChild(row);
            return;
        }

        const historialCitas = data.historialCitas;
        console.log('Citas en historial:', historialCitas);
        console.log('Número de citas:', historialCitas.length);

    for (let i = 0; i < historialCitas.length; i++) {
        const cita = historialCitas[i];
        
        // Obtener datos de paciente y médico
        const pacienteNombre = cita.paciente ? `${cita.paciente.nombre} ${cita.paciente.apellido}` : 'N/A';
        const medicoNombre = cita.medicoAsignado ? `${cita.medicoAsignado.nombre} ${cita.medicoAsignado.apellido}` : 'N/A';
        
        // Obtener prioridad del paciente
        let prioridadTexto = 'N/A';
        if (cita.paciente && cita.paciente.prioridad) {
            if (cita.paciente.prioridad === 1) prioridadTexto = 'Alta';
            else if (cita.paciente.prioridad === 2) prioridadTexto = 'Media';
            else if (cita.paciente.prioridad === 3) prioridadTexto = 'Baja';
        }
        
        // Formatear fecha
        const fecha = cita.fecha || 'N/A';
        const motivoConsulta = cita.motivoConsulta || 'N/A';

        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${i + 1}</td>
            <td>${fecha}</td>
            <td>${pacienteNombre}</td>
            <td>${medicoNombre}</td>
            <td>${prioridadTexto}</td>
            <td>${motivoConsulta}</td>
        `;
        tbodyHistorial.appendChild(row);
    }
    } catch (error) {
        console.error('Error al cargar historial:', error);
        const tbodyHistorial = document.getElementById('tbodyHistorial');
        tbodyHistorial.innerHTML = `
            <tr>
                <td colspan="6" class="text-center text-danger">Error al cargar el historial: ${error.message}</td>
            </tr>
        `;
    }
}

async function buscarPorPaciente(nombre) {
    try {
        const URL = `http://localhost:8080/api/cita/historial/buscar/paciente?nombre=${encodeURIComponent(nombre)}`;
        console.log('Buscando paciente:', nombre);
        
        const response = await fetch(URL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const data = await response.json();
        console.log('Resultados de búsqueda:', data);

        const tbodyHistorial = document.getElementById('tbodyHistorial');
        tbodyHistorial.innerHTML = ''; // Limpiar tabla primero

        if (!data.citasEncontradas || data.citasEncontradas.length === 0) {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td colspan="6" class="text-center">No se encontraron citas para "${nombre}"</td>
            `;
            tbodyHistorial.appendChild(row);
            return;
        }

        const citasEncontradas = data.citasEncontradas;
        console.log('Citas encontradas:', citasEncontradas.length);

        for (let i = 0; i < citasEncontradas.length; i++) {
            const cita = citasEncontradas[i];
            
            // Obtener datos de paciente y médico
            const pacienteNombre = cita.paciente ? `${cita.paciente.nombre} ${cita.paciente.apellido}` : 'N/A';
            const medicoNombre = cita.medicoAsignado ? `${cita.medicoAsignado.nombre} ${cita.medicoAsignado.apellido}` : 'N/A';
            
            // Obtener prioridad del paciente
            let prioridadTexto = 'N/A';
            if (cita.paciente && cita.paciente.prioridad) {
                if (cita.paciente.prioridad === 1) prioridadTexto = 'Alta';
                else if (cita.paciente.prioridad === 2) prioridadTexto = 'Media';
                else if (cita.paciente.prioridad === 3) prioridadTexto = 'Baja';
            }
            
            // Formatear fecha
            const fecha = cita.fecha || 'N/A';
            const motivoConsulta = cita.motivoConsulta || 'N/A';

            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${i + 1}</td>
                <td>${fecha}</td>
                <td>${pacienteNombre}</td>
                <td>${medicoNombre}</td>
                <td>${prioridadTexto}</td>
                <td>${motivoConsulta}</td>
            `;
            tbodyHistorial.appendChild(row);
        }
    } catch (error) {
        console.error('Error al buscar:', error);
        const tbodyHistorial = document.getElementById('tbodyHistorial');
        tbodyHistorial.innerHTML = `
            <tr>
                <td colspan="6" class="text-center text-danger">Error al buscar: ${error.message}</td>
            </tr>
        `;
    }
}
