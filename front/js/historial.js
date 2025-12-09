const btnListarHistorial = document.getElementById('btnListarHistorial');
const tbodyHistorial = document.getElementById('tbodyHistorial');

document.addEventListener('DOMContentLoaded', () => {
    cargarHistorial();
});

btnListarHistorial.addEventListener('click', async () => {

    const URL = 'http://localhost:8080/api/cita/pila/historial';
    
    const response = await fetch(
        URL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }
    );

    const data = await response.json();
    console.log(data);


});

async function cargarHistorial() {

    const URL = 'http://localhost:8080/api/cita/pila/historial';
    
    const response = await fetch(
        URL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }
    );

    const data = await response.json();
    console.log(data);

    const tbodyHistorial = document.getElementById('tbodyHistorial');

    for (let i = 0; i < data.length; i++) {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${i + 1}</td>
            <td>${data[i].fecha}</td>
            <td>${data[i].paciente}</td>
            <td>${data[i].medicoAsignado}</td>
            <td>${data[i].motivoConsulta}</td>
        `;
        tbodyHistorial.appendChild(row);
    }

    tbodyHistorial.innerHTML = '';

}
