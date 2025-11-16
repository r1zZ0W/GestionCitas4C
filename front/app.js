const inpNombre = document.getElementById('inpNombre');
const inApellidos = document.getElementById('inpApellidos');
const inpFechaNacimiento = document.getElementById('inpFechaNacimiento');
const inpNumeroTelefono = document.getElementById('inpNumeroTelefono');
const inpDireccion = document.getElementById('inpDireccion');
const inpCorreoElectronico = document.getElementById('inpCorreoElectronico');
const radioSexo = document.querySelector('input[name="inpSexo"]:checked');

const tblPersonas = document.getElementById('tblPersonas');
const tbodyDataTable = document.getElementById('tbodyDataTable');
let dataTablePersonas = null;

const btnEnviar = document.getElementById('btnEnviar');
const btnListar = document.getElementById('btnListar');

btnEnviar.addEventListener('click', async () => {
    
    const obj={};

    obj.nombre = inpNombre.value;
    obj.apellido = inApellidos.value;
    obj.fechaNacimiento = inpFechaNacimiento.value;
    obj.numeroTelefono = inpNumeroTelefono.value;
    obj.direccion = inpDireccion.value;
    obj.correoElectronico = inpCorreoElectronico.value;
    obj.sexo = radioSexo ? radioSexo.value : null;

    const URL = 'http://localhost:8080/api/paciente';

    const response = await fetch(
        URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(obj)
    });

    console.log(response);

    if (response.ok) {
        alert('Paciente registrado correctamente');
        dataTablePersonas.ajax.reload();
    }

});

btnListar.addEventListener('click', async () => {
    
    const URL = 'http://localhost:8080/api/paciente';
    const response = await fetch(
        URL, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    const jsonResponse = await response.json();
    const listaPacientes = jsonResponse.listPacientes;

    listaPacientes.forEach(element => {
        console.log(`Nombre: ${element.nombre} Apellidos: ${element.apellido}`);

    });

    let htmlTable = '';

    for (let i = 0; i < listaPacientes.length; i++) {
    
        const persona = listaPacientes[i];
    
        htmlTable += `
        <tr>
            <td>${i + 1}</td>
            <td>${persona.nombre}</td>
            <td>${persona.apellido}</td>
        </tr>
        `;
    
    }

    tblPersonas.innerHTML = htmlTable;  
    
});

document.addEventListener('DOMContentLoaded', async () => {
    dataTablePersonas = new DataTable('#dataTablePersonas', {
        ajax: {
            url: 'http://localhost:8080/api/paciente',
        dataSrc: 'listPacientes'
        },
        columns: [
            { 
                data: null,
                render: (data, type, row, meta) => { return meta.row + 1; }
            },
            { data: 'nombre' },
            { data: 'apellido' },
            { 
                data: null,
                render: (data, type, row, meta) => { // row es el objeto en el que estamos actualmente
                    return `
                    <button class="btn btn-primary btn-sm btnEditar" data-id="${row.id}">Editar</button>
                    <button class="btn btn-danger btn-sm btnEliminar" data-id="${row.id}">Eliminar</button>`;
                }
            }
        ]
    });
});

tbodyDataTable.addEventListener('click', async (event) => {
    
    console.log(event);
    const target = event.target;

    if (target.classList.contains('btnEliminar')) {
     
        const id = target.getAttribute('data-id');
     
        console.log(`Eliminar el id: ${id}`);
     
        const URL = `http://localhost:8080/api/persona/${id}`;
        const response = await fetch(
            URL, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });
     
        console.log(response);
        dataTablePersonas.ajax.reload();
    
    }


});