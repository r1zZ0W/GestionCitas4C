package mx.edu.utez.gestioncitas.data_structs;

import mx.edu.utez.gestioncitas.model.Paciente;


/**
 * Implementación del algoritmo de ordenamiento Merge Sort
 * para ordenar una lista de Paciente por su prioridad en orden ascendente.
 * @author Tilines Crew
 */
public class MergeSort {

    /**
     * Ordena una lista de Paciente por su prioridad en orden ascendente
     * utilizando el algoritmo Merge Sort.
     * @param p ListaSimple de Paciente a ordenar
     * @return ListaSimple de Paciente ordenada por prioridad ascendente
     */
    public static ListaSimple<Paciente> sortByPrioridadAsc(ListaSimple<Paciente> p) {

        if (p == null || p.size() <= 1) {
            return p;
        }

        // 1. Dividir la lista en dos mitades
        int mid = p.size() / 2;

        ListaSimple<Paciente> left = new ListaSimple<>();
        ListaSimple<Paciente> right = new ListaSimple<>();

        for (int i = 0; i < mid; i++)
            left.add(p.get(i));

        for (int i = mid; i < p.size(); i++)
            right.add(p.get(i));


        // 2. Llamada recursiva para ordenar cada mitad
        left = sortByPrioridadAsc(left);
        right = sortByPrioridadAsc(right);

        // 3. Mezclar resultados ordenados
        return merge(left, right);
    }

    /**
     * Mezcla dos listas ordenadas de Paciente en una sola lista ordenada.
     * @param left ListaSimple de Paciente ordenada
     * @param right ListaSimple de Paciente ordenada
     * @return ListaSimple de Paciente resultante de la mezcla ordenada
     */
    private static ListaSimple<Paciente> merge(ListaSimple<Paciente> left,
                                               ListaSimple<Paciente> right) {

        ListaSimple<Paciente> result = new ListaSimple<>();

        int i = 0, j = 0;

        // Mezcla principal
        while (i < left.size() && j < right.size()) {

            if (left.get(i).getPrioridad() <= right.get(j).getPrioridad()) {
                result.add(left.get(i));
                i++;
            } else {
                result.add(right.get(j));
                j++;
            }
        }

        // Agregar sobrantes
        while (i < left.size()) {
            result.add(left.get(i));
            i++;
        }

        while (j < right.size()) {
            result.add(right.get(j));
            j++;
        }

        return result;
    }

}