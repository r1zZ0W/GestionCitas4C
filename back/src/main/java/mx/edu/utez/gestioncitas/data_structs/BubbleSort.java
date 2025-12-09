package mx.edu.utez.gestioncitas.data_structs;

import mx.edu.utez.gestioncitas.model.Medico;

/**
 * Implementación del algoritmo de ordenamiento Bubble Sort
 * para ordenar una lista de Medico por nombre en orden alfabético.
 * @author Tilines Crew
 */
public class BubbleSort {

    /**
     * Ordena una lista de Medico por nombre en orden alfabético ascendente
     * utilizando el algoritmo Bubble Sort.
     * @param medicos ListaSimple de Medico a ordenar
     * @return ListaSimple de Medico ordenada por nombre alfabéticamente
     */
    public static ListaSimple<Medico> sortByNombreAsc(ListaSimple<Medico> medicos) {
        if (medicos == null || medicos.size() <= 1) {
            return medicos;
        }

        // Crear una copia para no modificar la lista original
        ListaSimple<Medico> resultado = new ListaSimple<>();
        for (int i = 0; i < medicos.size(); i++) {
            resultado.add(medicos.get(i));
        }

        int n = resultado.size();
        boolean swapped;

        // Bubble Sort: comparar elementos adyacentes y intercambiar si están en orden incorrecto
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                Medico medico1 = resultado.get(j);
                Medico medico2 = resultado.get(j + 1);
                
                // Comparar por nombre completo (nombre + apellido)
                String nombreCompleto1 = (medico1.getNombre() + " " + medico1.getApellido()).toLowerCase();
                String nombreCompleto2 = (medico2.getNombre() + " " + medico2.getApellido()).toLowerCase();
                
                if (nombreCompleto1.compareTo(nombreCompleto2) > 0) {
                    // Intercambiar elementos
                    resultado.set(j, medico2);
                    resultado.set(j + 1, medico1);
                    swapped = true;
                }
            }
            // Si no hubo intercambios, la lista ya está ordenada
            if (!swapped) {
                break;
            }
        }

        return resultado;
    }

    /**
     * Ordena una lista de Medico por especialidad en orden alfabético ascendente
     * utilizando el algoritmo Bubble Sort.
     * @param medicos ListaSimple de Medico a ordenar
     * @return ListaSimple de Medico ordenada por especialidad alfabéticamente
     */
    public static ListaSimple<Medico> sortByEspecialidadAsc(ListaSimple<Medico> medicos) {
        if (medicos == null || medicos.size() <= 1) {
            return medicos;
        }

        // Crear una copia para no modificar la lista original
        ListaSimple<Medico> resultado = new ListaSimple<>();
        for (int i = 0; i < medicos.size(); i++) {
            resultado.add(medicos.get(i));
        }

        int n = resultado.size();
        boolean swapped;

        // Bubble Sort: comparar elementos adyacentes y intercambiar si están en orden incorrecto
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                Medico medico1 = resultado.get(j);
                Medico medico2 = resultado.get(j + 1);
                
                // Comparar por especialidad
                String especialidad1 = medico1.getEspecialidad() != null ? 
                    medico1.getEspecialidad().toLowerCase() : "";
                String especialidad2 = medico2.getEspecialidad() != null ? 
                    medico2.getEspecialidad().toLowerCase() : "";
                
                if (especialidad1.compareTo(especialidad2) > 0) {
                    // Intercambiar elementos
                    resultado.set(j, medico2);
                    resultado.set(j + 1, medico1);
                    swapped = true;
                }
            }
            // Si no hubo intercambios, la lista ya está ordenada
            if (!swapped) {
                break;
            }
        }

        return resultado;
    }
}

