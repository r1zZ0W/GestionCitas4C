// Cargar lista al iniciar
document.addEventListener('DOMContentLoaded', async () => {
    await listarMedicos();
});

const inpNombreMedico = document.getElementById('inpNombreMedico');
const inpApellidosMedico = document.getElementById('inpApellidosMedico');
const inpEspecialidad = document.getElementById('inpEspecialidad');
const inpNumeroConsultorio = document.getElementById('inpNumeroConsultorio');

const tbodyMedicos = document.getElementById('tbodyMedicos');
const btnEnviarMedico = document.getElementById('btnEnviarMedico');
const btnListarMedicos = document.getElementById('btnListarMedicos');

document.getElementById('formMedico').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const obj = {
        nombre: inpNombreMedico.value,
        apellido: inpApellidosMedico.value,
        especialidad: inpEspecialidad.value,
        numeroConsultorio: parseInt(inpNumeroConsultorio.value),
        ocupado: false
    };

    const URL = 'http://localhost:8080/api/medico';

    const response = await fetch(URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(obj)
    });

    if (response.ok) {
        alert('Médico registrado correctamente');
        document.getElementById('formMedico').reset();
        await listarMedicos();
    }
});

btnListarMedicos.addEventListener('click', async () => {
    await listarMedicos();
});

async function listarMedicos() {
    const URL = 'http://localhost:8080/api/medico';
    const response = await fetch(URL, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    const jsonResponse = await response.json();
    const listaMedicos = jsonResponse.listMedicos;

    let htmlTable = '';

    for (let i = 0; i < listaMedicos.length; i++) {
        const medico = listaMedicos[i];
        htmlTable += `
        <tr>
            <td>${i + 1}</td>
            <td>${medico.nombre}</td>
            <td>${medico.apellido}</td>
            <td>${medico.especialidad}</td>
            <td>${medico.numeroConsultorio}</td>
            <td class="text-center">
                <button class="btn btn-sm btnEditarMedico me-1" style="background-color: #A4CCD9; border-color: #A4CCD9; color: #333; padding: 0.25rem 0.5rem;" data-id="${medico.id}" title="Editar">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-danger btn-sm btnEliminarMedico" style="padding: 0.25rem 0.5rem;" data-id="${medico.id}" title="Eliminar">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
        `;
    }

    tbodyMedicos.innerHTML = htmlTable;
}

// Función para cargar datos de un médico en el modal
async function cargarDatosMedico(id) {
    try {
        const URL = `http://localhost:8080/api/medico/${id}`;
        const response = await fetch(URL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const result = await response.json();
            const medico = result.medico;

            document.getElementById('editMedicoId').value = medico.id;
            document.getElementById('editNombreMedico').value = medico.nombre || '';
            document.getElementById('editApellidosMedico').value = medico.apellido || '';
            document.getElementById('editEspecialidad').value = medico.especialidad || '';
            document.getElementById('editNumeroConsultorio').value = medico.numeroConsultorio || '';

            const modal = new bootstrap.Modal(document.getElementById('modalEditarMedico'));
            modal.show();
        } else {
            alert('Error al cargar los datos del médico');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
}

// Event listener para guardar edición
document.getElementById('formEditarMedico').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    try {
        const id = document.getElementById('editMedicoId').value;
        const obj = {
            nombre: document.getElementById('editNombreMedico').value.trim(),
            apellido: document.getElementById('editApellidosMedico').value.trim(),
            especialidad: document.getElementById('editEspecialidad').value.trim(),
            numeroConsultorio: parseInt(document.getElementById('editNumeroConsultorio').value)
        };

        const URL = `http://localhost:8080/api/medico/${id}`;
        const response = await fetch(URL, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(obj)
        });

        if (response.ok) {
            alert('Médico actualizado correctamente');
            const modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarMedico'));
            modal.hide();
            await listarMedicos();
        } else {
            alert('Error al actualizar el médico');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
});

// Función para eliminar un médico
async function eliminarMedico(id) {
    if (!confirm('¿Está seguro de que desea eliminar este médico?')) {
        return;
    }

    try {
        const URL = `http://localhost:8080/api/medico/${id}`;
        const response = await fetch(URL, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            alert('Médico eliminado correctamente');
            await listarMedicos();
        } else {
            alert('Error al eliminar el médico');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
}

// Event listener para acciones en la tabla
document.addEventListener('click', async (event) => {
    const target = event.target;
    const btnMedico = target.closest('.btnEditarMedico') || target.closest('.btnEliminarMedico');
    
    if (btnMedico) {
        const id = btnMedico.getAttribute('data-id');
        if (btnMedico.classList.contains('btnEditarMedico')) {
            await cargarDatosMedico(id);
        } else if (btnMedico.classList.contains('btnEliminarMedico')) {
            await eliminarMedico(id);
        }
    }
});
