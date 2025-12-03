const inpFecha = document.getElementById('inpFecha');
const inpHora = document.getElementById('inpHora');
const selectPaciente = document.getElementById('selectPaciente');
const selectMedico = document.getElementById('selectMedico');
const inpMotivoConsulta = document.getElementById('inpMotivoConsulta');
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

    

});

async function atenderPaciente() {
    


}

async function cargarPacientesPrioridad() {

    const URL = 'http://localhost:8080/api/paciente/prioridad/asc';
    const response = await fetch(URL, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    console.log(response);

    const jsonResponse = await response.json();
    const listaPacientes = jsonResponse.listPacientes;

    let htmlTable = '';

    for (let i = 0; i < listaPacientes.length; i++) {
        const paciente = listaPacientes[i];
        
        let prioridad = '';
        
        if (paciente.prioridad === 1)
            prioridad = 'Alta';
        
        if (paciente.prioridad === 2)
            prioridad = 'Media';
        
        if(paciente.prioridad === 3)
             prioridad = 'Baja';

        htmlTable += `
        <tr>
            <td>${i + 1}</td>
            <td>${paciente.nombre} ${paciente.apellido}</td>
            <td>${prioridad}</td>
            <td>
                En espera
            </td> 
        </tr>
        `;
    }

    tbodyPrioridad.innerHTML = htmlTable;
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

    console.log(response);

    if (response.ok) {
        alert('Cita registrada correctamente');
        document.getElementById('formCita').reset();
        await listarCitas();
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
        switch (estado) {
            case 'P': estadoTexto = 'Programada'; break;
            case 'C': estadoTexto = 'Cancelada'; break;
            case 'F': estadoTexto = 'Finalizada'; break;
            case 'R': estadoTexto = 'Reagendada'; break;
            default: estadoTexto = estado;
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
            <td>${estadoTexto}</td>
            <td class="text-center">
                <button class="btn btn-sm btnEditarCita me-1" style="background-color: #A4CCD9; border-color: #A4CCD9; color: #333; padding: 0.25rem 0.5rem;" data-id="${cita.id}" title="Editar">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-danger btn-sm btnEliminarCita" style="padding: 0.25rem 0.5rem;" data-id="${cita.id}" title="Eliminar">
                    <i class="bi bi-trash"></i>
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
