// Cargar lista al iniciar
document.addEventListener('DOMContentLoaded', async () => {
    await listarPacientes();
});

const inpNombre = document.getElementById('inpNombre');
const inApellidos = document.getElementById('inpApellidos');
const inpFechaNacimiento = document.getElementById('inpFechaNacimiento');
const inpNumeroTelefono = document.getElementById('inpNumeroTelefono');
const inpDireccion = document.getElementById('inpDireccion');
const inpCorreoElectronico = document.getElementById('inpCorreoElectronico');

const tbodyPacientes = document.getElementById('tbodyPacientes');
const btnEnviar = document.getElementById('btnEnviarPaciente');
const btnListar = document.getElementById('btnListarPacientes');
const inpPrioridad = document.getElementById('inpPrioridad');

document.getElementById('formPaciente').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const radioSexo = document.querySelector('input[name="inpSexo"]:checked');
    
    const obj = {
        nombre: inpNombre.value,
        apellido: inApellidos.value,
        fechaNacimiento: inpFechaNacimiento.value,
        numeroTelefono: inpNumeroTelefono.value,
        direccion: inpDireccion.value,
        correoElectronico: inpCorreoElectronico.value,
        sexo: radioSexo ? radioSexo.value : null,
        prioridad: inpPrioridad.value,
        enAtencion: false
    };

    const URL = 'http://localhost:8080/api/paciente';

    const response = await fetch(URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(obj)
    });

    if (response.ok) {
        alert('Paciente registrado correctamente');
        document.getElementById('formPaciente').reset();
        await listarPacientes();
    }
});

btnListar.addEventListener('click', async () => {
    await listarPacientes();
});

async function listarPacientes() {
    const URL = 'http://localhost:8080/api/paciente';
    const response = await fetch(URL, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    const jsonResponse = await response.json();
    const listaPacientes = jsonResponse.listPacientes;

    let htmlTable = '';

    for (let i = 0; i < listaPacientes.length; i++) {
        const persona = listaPacientes[i];
        htmlTable += `
        <tr>
            <td>${i + 1}</td>
            <td>${persona.nombre}</td>
            <td>${persona.apellido}</td>
            <td class="text-center">
                <button class="btn btn-sm btnEditarPaciente me-1" style="background-color: #8DBCC7; border-color: #8DBCC7; color: #333; padding: 0.25rem 0.5rem;" data-id="${persona.id}" title="Editar">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-danger btn-sm btnEliminarPaciente" style="padding: 0.25rem 0.5rem;" data-id="${persona.id}" title="Eliminar">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
        `;
    }

    tbodyPacientes.innerHTML = htmlTable;
}

// Función para cargar datos de un paciente en el modal
async function cargarDatosPaciente(id) {
    try {
        const URL = `http://localhost:8080/api/paciente/${id}`;
        const response = await fetch(URL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const result = await response.json();
            const paciente = result.paciente;

            document.getElementById('editPacienteId').value = paciente.id;
            document.getElementById('editNombre').value = paciente.nombre || '';
            document.getElementById('editApellidos').value = paciente.apellido || '';
            document.getElementById('editFechaNacimiento').value = paciente.fechaNacimiento || '';
            document.getElementById('editTelefono').value = paciente.numeroTelefono || '';
            document.getElementById('editCorreo').value = paciente.correoElectronico || '';
            document.getElementById('editDireccion').value = paciente.direccion || '';

            if (paciente.sexo === 'M') {
                document.getElementById('editSexoM').checked = true;
            } else if (paciente.sexo === 'F') {
                document.getElementById('editSexoF').checked = true;
            }

            const modal = new bootstrap.Modal(document.getElementById('modalEditarPaciente'));
            modal.show();
        } else {
            alert('Error al cargar los datos del paciente');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
}

// Event listener para guardar edición
document.getElementById('formEditarPaciente').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    try {
        const id = document.getElementById('editPacienteId').value;
        const obj = {
            nombre: document.getElementById('editNombre').value.trim(),
            apellido: document.getElementById('editApellidos').value.trim(),
            fechaNacimiento: document.getElementById('editFechaNacimiento').value,
            numeroTelefono: document.getElementById('editTelefono').value.trim(),
            direccion: document.getElementById('editDireccion').value.trim(),
            correoElectronico: document.getElementById('editCorreo').value.trim(),
            sexo: document.querySelector('input[name="editSexo"]:checked')?.value || null
        };

        const URL = `http://localhost:8080/api/paciente/${id}`;
        const response = await fetch(URL, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(obj)
        });

        if (response.ok) {
            alert('Paciente actualizado correctamente');
            const modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarPaciente'));
            modal.hide();
            await listarPacientes();
        } else {
            alert('Error al actualizar el paciente');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
});

// Función para eliminar un paciente
async function eliminarPaciente(id) {
    if (!confirm('¿Está seguro de que desea eliminar este paciente?')) {
        return;
    }

    try {
        const URL = `http://localhost:8080/api/paciente/${id}`;
        const response = await fetch(URL, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            alert('Paciente eliminado correctamente');
            await listarPacientes();
        } else {
            alert('Error al eliminar el paciente');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
}

// Event listener para acciones en la tabla
document.addEventListener('click', async (event) => {
    const target = event.target;
    const btnPaciente = target.closest('.btnEditarPaciente') || target.closest('.btnEliminarPaciente');
    
    if (btnPaciente) {
        const id = btnPaciente.getAttribute('data-id');
        if (btnPaciente.classList.contains('btnEditarPaciente')) {
            await cargarDatosPaciente(id);
        } else if (btnPaciente.classList.contains('btnEliminarPaciente')) {
            await eliminarPaciente(id);
        }
    }
});
