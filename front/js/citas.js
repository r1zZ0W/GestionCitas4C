const inpFecha = document.getElementById('inpFecha');
const inpHora = document.getElementById('inpHora');
const selectPaciente = document.getElementById('selectPaciente');
const selectMedico = document.getElementById('selectMedico');
const inpMotivoConsulta = document.getElementById('inpMotivoConsulta');
const selectPrioridad = document.getElementById('selectPrioridad');
const selectEstado = document.getElementById('selectEstado');

const tbodyCitas = document.getElementById('tbodyCitas');
const btnEnviarCita = document.getElementById('btnEnviarCita');
const btnListarCitas = document.getElementById('btnListarCitas');

const btnAtenderPaciente = document.getElementById('btnAtenderPaciente');

// Cargar pacientes y médicos al iniciar
document.addEventListener('DOMContentLoaded', async () => {
    await cargarPacientes();
    await cargarMedicos();
    await listarCitas();
    await cargarPacientesPrioridad();
    
    // Actualización automática cada 3 segundos
    setInterval(async () => {
        await listarCitas();
        await cargarPacientesPrioridad();
    }, 3000); // Actualiza cada 3 segundos
    
    // Función para llenar el formulario con valores de prueba (solo para desarrollo)
    llenarFormularioPrueba();
});

// Función para llenar el formulario con valores de prueba
function llenarFormularioPrueba() {

    // Establecer fecha de hoy
    const hoy = new Date();
    const fechaStr = hoy.toISOString().split('T')[0];
    inpFecha.value = fechaStr;
    
    // Establecer hora actual + 1 hora
    const hora = new Date();
    hora.setHours(hora.getHours() + 1);
    const horaStr = hora.toTimeString().slice(0, 5);
    inpHora.value = horaStr;
    
    // Establecer motivo de consulta de prueba
    inpMotivoConsulta.value = 'Consulta de prueba ' + Date.now();
    
    // Establecer estado por defecto
    selectEstado.value = 'P';
    
    // Seleccionar primer paciente y médico disponibles (si existen)
    setTimeout(() => {
        if (selectPaciente.options.length > 1) {
            selectPaciente.selectedIndex = 1; // Primera opción después de "Seleccione..."
        }
        if (selectMedico.options.length > 1) {
            selectMedico.selectedIndex = 1; // Primera opción después de "Seleccione..."
        }
    }, 500); // Esperar a que se carguen los selects

}

btnAtenderPaciente.addEventListener('click', async () => {

    try {
        // Usar el endpoint que maneja todo: marca en atención, crea cita, y después de 30-60 seg la finaliza
        const response = await fetch('http://localhost:8080/api/cita/atender/prioridad', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        const data = await response.json();
        
        if (!response.ok || data.error) {
            alert('Error: ' + (data.error || 'No se pudo atender al paciente'));
            return;
        }
        
        const cita = data.cita;
        const paciente = data.paciente;
        const tiempoEspera = data.tiempoEspera || 30;
        
        // Mostrar mensaje de éxito
        alert(`Paciente ${paciente.nombre} ${paciente.apellido} está en atención.\nSerá finalizado automáticamente en ${tiempoEspera} segundos y agregado al historial.`);
        
        // Actualizar las listas
        await cargarPacientesPrioridad();
        await listarCitas();
        
    } catch (error) {
        console.error('Error:', error);
        alert('Error al atender paciente: ' + error.message);
    }
});

// Función para finalizar consulta y liberar médico
async function finalizarConsultaYLiberarMedico(citaId, medicoId) {
    try {
        // 1. Atender la cita (marcarla como finalizada)
        const responseAtender = await fetch('http://localhost:8080/api/cita/cola/atender', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (responseAtender.ok) {
            // 2. Liberar al médico
            await fetch(`http://localhost:8080/api/medico/${medicoId}/disponible`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            alert('Consulta finalizada y médico liberado');
            await listarCitas();
            await cargarPacientesPrioridad();
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al finalizar consulta');
    }
}

async function cargarPacientesPrioridad() {
    try {
        const URL = 'http://localhost:8080/api/paciente/prioridad/asc';
        const response = await fetch(URL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            console.error('Error al cargar pacientes por prioridad:', response.status, response.statusText);
            tbodyPrioridad.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center text-danger">Error al cargar la lista de prioridad</td>
                </tr>
            `;
            return;
        }

        const jsonResponse = await response.json();
        const listaPacientes = jsonResponse.listPacientes || [];

    let htmlTable = '';

    if (listaPacientes.length === 0) {
        htmlTable = `
        <tr>
            <td colspan="4" class="text-center">No hay pacientes disponibles en espera</td>
        </tr>
        `;
    } else {
        for (let i = 0; i < listaPacientes.length; i++) {
            const paciente = listaPacientes[i];
            
            let prioridad = '';
            
            if (paciente.prioridad === 1)
                prioridad = 'Alta';
            else if (paciente.prioridad === 2)
                prioridad = 'Media';
            else if (paciente.prioridad === 3)
                prioridad = 'Baja';

            // Verificar estado
            let estado = 'En espera';
            let estadoClass = 'text-info';
            if (paciente.enAtencion) {
                estado = 'En atención';
                estadoClass = 'text-warning fw-bold';
            }

            htmlTable += `
            <tr>
                <td>${i + 1}</td>
                <td>${paciente.nombre} ${paciente.apellido}</td>
                <td>${prioridad}</td>
                <td class="${estadoClass}">
                    ${estado}
                </td> 
            </tr>
            `;
        }
    }

    tbodyPrioridad.innerHTML = htmlTable;
    } catch (error) {
        console.error('Error en cargarPacientesPrioridad:', error);
        tbodyPrioridad.innerHTML = `
            <tr>
                <td colspan="4" class="text-center text-danger">Error al cargar la lista de prioridad: ${error.message}</td>
            </tr>
        `;
    }
}

// Función para cargar pacientes
async function cargarPacientes() {
    const URL = 'http://localhost:8080/api/paciente';
    const response = await fetch(URL, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    const jsonResponse = await response.json();
    const listaPacientes = jsonResponse.listPacientes;

    selectPaciente.innerHTML = '<option value="">Seleccione un paciente</option>';

    for (let i = 0; i < listaPacientes.length; i++) {
        const paciente = listaPacientes[i];
        selectPaciente.innerHTML += `<option value="${paciente.id}">${paciente.nombre} ${paciente.apellido}</option>`;
    }
}

// Función para cargar médicos 
async function cargarMedicos() {
    const URL = 'http://localhost:8080/api/medico';
    const response = await fetch(URL, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    const jsonResponse = await response.json();
    const listaMedicos = jsonResponse.listMedicos;

    selectMedico.innerHTML = '<option value="">Seleccione un médico</option>';

    for (let i = 0; i < listaMedicos.length; i++) {
        const medico = listaMedicos[i];
        selectMedico.innerHTML += `<option value="${medico.id}">${medico.nombre} ${medico.apellido} - ${medico.especialidad}</option>`;
    }
}

document.getElementById('formCita').addEventListener('submit', async (e) => {
    e.preventDefault();

    // Obtener los objetos completos de paciente y médico
    const pacienteId = parseInt(selectPaciente.value);
    const medicoId = parseInt(selectMedico.value);

    // Obtener paciente completo
    const responsePaciente = await fetch(`http://localhost:8080/api/paciente/${pacienteId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });
    const pacienteData = await responsePaciente.json();
    const paciente = pacienteData.paciente;
    
    // Actualizar paciente con la prioridad asignada ANTES de crear la cita
    if (selectPrioridad.value) {
        try {
            const updateResponse = await fetch(`http://localhost:8080/api/paciente/${pacienteId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    nombre: paciente.nombre,
                    apellido: paciente.apellido,
                    prioridad: parseInt(selectPrioridad.value)
                })
            });
            if (updateResponse.ok) {
                const updatedPacienteData = await updateResponse.json();
                paciente.prioridad = updatedPacienteData.paciente.prioridad;
                console.log('Paciente actualizado con prioridad antes de crear cita');
            }
        } catch (error) {
            console.error('Error al actualizar paciente antes de crear cita:', error);
        }
    }

    // Obtener médico completo
    const responseMedico = await fetch(`http://localhost:8080/api/medico/${medicoId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });
    const medicoData = await responseMedico.json();
    const medico = medicoData.medico;

    const obj = {};

    obj.fecha = inpFecha.value;
    obj.hora = inpHora.value;
    obj.paciente = paciente;
    obj.medicoAsignado = medico;
    obj.motivoConsulta = inpMotivoConsulta.value;
    obj.estado = selectEstado.value;

    const URL = 'http://localhost:8080/api/cita';

    const response = await fetch(URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(obj)
    });

    console.log('Response status:', response.status);

    if (response.ok) {
        const responseData = await response.json();
        console.log('Cita registrada:', responseData);
        alert('Cita registrada correctamente');
        document.getElementById('formCita').reset();
        try {
            await listarCitas();
            // Esperar un poco para que el backend procese la cita
            await new Promise(resolve => setTimeout(resolve, 500));
            await cargarPacientesPrioridad();
            console.log('Listas actualizadas correctamente');
        } catch (error) {
            console.error('Error al actualizar listas:', error);
            alert('Cita registrada pero hubo un error al actualizar las listas. Por favor, recarga la página.');
        }
    } else {
        const error = await response.json();
        alert('Error al registrar la cita: ' + (error.error || 'Error desconocido'));
        console.error('Error:', error);
    }

});

btnListarCitas.addEventListener('click', async () => {
    await listarCitas();
});

async function listarCitas() {
    const URL = 'http://localhost:8080/api/cita';
    const response = await fetch(URL, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    const jsonResponse = await response.json();
    const listaCitas = jsonResponse.listCitas;

    let htmlTable = '';

    for (let i = 0; i < listaCitas.length; i++) {
        const cita = listaCitas[i];
        const fecha = cita.fecha || 'N/A';
        const fechaHora = cita.hora ? cita.hora.substring(0, 5) : 'N/A';
        const motivoConsulta = cita.motivoConsulta || 'N/A';
        const estado = cita.estado || 'N/A';
        
        let estadoTexto = '';
        let estadoClass = '';
        switch (estado) {
            case 'P': 
                estadoTexto = 'Programada'; 
                estadoClass = 'text-info';
                break;
            case 'C': 
                estadoTexto = 'Cancelada'; 
                estadoClass = 'text-danger';
                break;
            case 'F': 
                estadoTexto = 'Finalizada'; 
                estadoClass = 'text-success fw-bold';
                break;
            case 'R': 
                estadoTexto = 'Reagendada'; 
                estadoClass = 'text-warning';
                break;
            case 'E': 
                estadoTexto = 'En Atención'; 
                estadoClass = 'text-warning fw-bold';
                break;
            default: 
                estadoTexto = estado; 
                estadoClass = '';
        }
        const pacienteNombre = cita.paciente ? `${cita.paciente.nombre} ${cita.paciente.apellido}` : 'N/A';
        const medicoNombre = cita.medicoAsignado ? `${cita.medicoAsignado.nombre} ${cita.medicoAsignado.apellido}` : 'N/A';

        htmlTable += `
        <tr>
            <td>${i + 1}</td>
            <td>${fecha}</td>
            <td>${fechaHora}</td>
            <td>${pacienteNombre}</td>
            <td>${medicoNombre}</td>
            <td>${motivoConsulta}</td>
            <td class="${estadoClass}">${estadoTexto}</td>
            <td class="text-center">
                <button class="btn btn-sm btn-primary btnEditarCita me-1" style="padding: 0.375rem 0.75rem; min-width: 38px; min-height: 38px; display: inline-flex; align-items: center; justify-content: center;" data-id="${cita.id}" title="Editar">
                    <i class="fas fa-edit" style="font-size: 14px; display: inline-block; font-family: 'Font Awesome 6 Free'; font-weight: 900; opacity: 1; visibility: visible;"></i>
                </button>
                <button class="btn btn-danger btn-sm btnEliminarCita" style="padding: 0.375rem 0.75rem; min-width: 38px; min-height: 38px; display: inline-flex; align-items: center; justify-content: center;" data-id="${cita.id}" title="Eliminar">
                    <i class="fas fa-trash" style="font-size: 14px; display: inline-block; font-family: 'Font Awesome 6 Free'; font-weight: 900; opacity: 1; visibility: visible;"></i>
                </button>
            </td>
        </tr>
        `;

    }

    tbodyCitas.innerHTML = htmlTable;

}

// Función para cargar datos de una cita en el modal de edición
async function cargarDatosCita(id) {
    try {
        const URL = `http://localhost:8080/api/cita/${id}`;
        const response = await fetch(URL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const result = await response.json();
            const cita = result.cita;

            document.getElementById('editCitaId').value = cita.id;
            document.getElementById('editFecha').value = cita.fecha || '';

            // Convertir hora a formato time
            if (cita.hora) {
                document.getElementById('editHora').value = cita.hora.substring(0, 5);
            }

            document.getElementById('editMotivoConsulta').value = cita.motivoConsulta || '';

            // Cargar pacientes y médicos en los selects del modal
            await cargarPacientesEnModal();
            await cargarMedicosEnModal();

            // Seleccionar paciente y médico actuales
            if (cita.paciente) {
                document.getElementById('editSelectPaciente').value = cita.paciente.id;
            }
            if (cita.medicoAsignado) {
                document.getElementById('editSelectMedico').value = cita.medicoAsignado.id;
            }

            // Seleccionar estado
            document.getElementById('editSelectEstado').value = cita.estado || '';

            const modal = new bootstrap.Modal(document.getElementById('modalEditarCita'));
            modal.show();
        } else {
            alert('Error al cargar los datos de la cita');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
}

// Función para cargar pacientes en el select del modal
async function cargarPacientesEnModal() {
    const URL = 'http://localhost:8080/api/paciente';
    const response = await fetch(URL, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    const jsonResponse = await response.json();
    const listaPacientes = jsonResponse.listPacientes;

    const selectEditPaciente = document.getElementById('editSelectPaciente');
    const currentValue = selectEditPaciente.value;

    selectEditPaciente.innerHTML = '<option value="">Seleccione un paciente</option>';

    for (let i = 0; i < listaPacientes.length; i++) {
        const paciente = listaPacientes[i];
        selectEditPaciente.innerHTML += `<option value="${paciente.id}">${paciente.nombre} ${paciente.apellido}</option>`;
    }

    // Restaurar valor seleccionado
    if (currentValue) {
        selectEditPaciente.value = currentValue;
    }
}

// Función para cargar médicos en el select del modal
async function cargarMedicosEnModal() {
    const URL = 'http://localhost:8080/api/medico';
    const response = await fetch(URL, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    const jsonResponse = await response.json();
    const listaMedicos = jsonResponse.listMedicos;

    const selectEditMedico = document.getElementById('editSelectMedico');
    const currentValue = selectEditMedico.value;

    selectEditMedico.innerHTML = '<option value="">Seleccione un médico</option>';

    for (let i = 0; i < listaMedicos.length; i++) {
        const medico = listaMedicos[i];
        selectEditMedico.innerHTML += `<option value="${medico.id}">${medico.nombre} ${medico.apellido} - ${medico.especialidad}</option>`;
    }

    // Restaurar valor seleccionado
    if (currentValue) {
        selectEditMedico.value = currentValue;
    }
}

// Event listener para guardar edición de cita
document.getElementById('formEditarCita').addEventListener('submit', async (e) => {
    e.preventDefault();

    try {
        const id = document.getElementById('editCitaId').value;
        const pacienteId = parseInt(document.getElementById('editSelectPaciente').value);
        const medicoId = parseInt(document.getElementById('editSelectMedico').value);

        // Obtener paciente completo
        const responsePaciente = await fetch(`http://localhost:8080/api/paciente/${pacienteId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const pacienteData = await responsePaciente.json();
        const paciente = pacienteData.paciente;

        // Obtener médico completo
        const responseMedico = await fetch(`http://localhost:8080/api/medico/${medicoId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const medicoData = await responseMedico.json();
        const medico = medicoData.medico;

        const obj = {
            fecha: document.getElementById('editFecha').value,
            hora: document.getElementById('editHora').value,
            paciente: paciente,
            medicoAsignado: medico,
            motivoConsulta: document.getElementById('editMotivoConsulta').value.trim(),
            estado: document.getElementById('editSelectEstado').value
        };

        const URL = `http://localhost:8080/api/cita/${id}`;
        const response = await fetch(URL, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(obj)
        });

        if (response.ok) {
            alert('Cita actualizada correctamente');
            const modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarCita'));
            modal.hide();
            await listarCitas();
        } else {
            const error = await response.json();
            alert('Error al actualizar la cita: ' + (error.error || 'Error desconocido'));
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
});

// Función para eliminar una cita
async function eliminarCita(id) {
    if (!confirm('¿Está seguro de que desea eliminar esta cita?')) {
        return;
    }

    try {
        const URL = `http://localhost:8080/api/cita/${id}`;
        const response = await fetch(URL, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            alert('Cita eliminada correctamente');
            await listarCitas();
        } else {
            alert('Error al eliminar la cita');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
}

// Event listener para acciones en la tabla (editar/eliminar)
document.addEventListener('click', async (event) => {
    const target = event.target;

    // Manejo de botones de citas
    const btnCita = target.closest('.btnEditarCita') || target.closest('.btnEliminarCita');
    if (btnCita) {
        const id = btnCita.getAttribute('data-id');
        if (btnCita.classList.contains('btnEditarCita')) {
            await cargarDatosCita(id);
        } else if (btnCita.classList.contains('btnEliminarCita')) {
            await eliminarCita(id);
        }
    }
});
