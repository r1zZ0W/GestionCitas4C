package mx.edu.utez.gestioncitas.data_structs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import mx.edu.utez.gestioncitas.serializers.ListaSimpleDeserializer;
import mx.edu.utez.gestioncitas.serializers.ListaSimpleSerializer;

import java.util.AbstractList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Clase que representa una lista enlazada simple genérica. Utiliza Serialización y Deserialización personalizada para JSON.
 * @param <T> el tipo de elementos en la lista
 */
@JsonSerialize(using = ListaSimpleSerializer.class)
@JsonDeserialize(using = ListaSimpleDeserializer.class)
public class ListaSimple<T> extends AbstractList<T> {

    /**
     * Nodo cabeza de la lista y la cola, además del tamaño de la lista.
     */
    private Nodo<T> head;
    private Nodo<T> tail;
    private int size;

    /**
     * Constructor de la lista simple.
     */
    public ListaSimple() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }


    /**
     * Obtiene el elemento en la posición idx.
     * @param idx índice del elemento a obtener
     * @return el elemento en la posición idx
     */
    @Override
    public T get(int idx) {

        checkIndex(idx);

        Nodo<T> current = head;

        for(int i = 0; i < idx; i++)
            current = current.getNext();

        return current.getData();
    }

    /**
     * Obtiene el tamaño de la lista.
     * @return el tamaño de la lista
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Agrega un elemento al final de la lista.
     * @param data el elemento a agregar
     * @return true si el elemento fue agregado exitosamente
     */
    @Override
    public boolean add(T data) {
        Nodo<T> newNode = new Nodo<>(data);

        if (isEmpty())
            head = newNode;
        else
             tail.setNext(newNode);

        tail = newNode;
        size++;
        modCount++;
        return true;
    }

    /**
     * Reemplaza el elemento en la posición idx con el nuevo elemento.
     * @param idx índice del elemento a reemplazar
     * @param element nuevo elemento
     * @return el elemento anterior en esa posición
     */
    @Override
    public T set(int idx, T element) {
        checkIndex(idx);
        
        Nodo<T> current = head;
        for (int i = 0; i < idx; i++)
            current = current.getNext();
        
        T oldData = current.getData();
        current.setData(element);
        modCount++;
        return oldData;
    }

    /**
     * Remueve el elemento en la posición idx.
     * @param idx índice del elemento a remover
     * @return el elemento removido
     */
    @Override
    public T remove(int idx) {
        checkIndex(idx);

        T removedData;

        if (idx == 0) {
            removedData = head.getData();
            head = head.getNext();
        } else {

            Nodo<T> current = head;

            for (int i = 0; i < idx - 1; i++)
                current = current.getNext();

            removedData = current.getNext().getData();
            current.setNext(current.getNext().getNext());

            if (idx == size - 1)
                tail = current;

        }

        size--;
        modCount++;
        return removedData;
    }

    /**
     * Limpia la lista.
     */
    @Override
    public void clear() {
        head = null;
        size = 0;
        modCount++;
    }

    /**
     * Verifica si la lista está vacía.
     * @return true si la lista está vacía, false en caso contrario
     */
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Verifica si el índice es válido.
     * @param idx índice a verificar
     */
    private void checkIndex(int idx) {
        if (idx < 0 || idx >= size) {
            throw new IndexOutOfBoundsException("Index: " + idx + ", Size: " + size);
        }
    }

    /**
     * Agrega todos los elementos de una colección a la lista.
     * @param c la colección de elementos a agregar
     * @return true si los elementos fueron agregados exitosamente
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {

        if (c == null)
            // Estándar en Java para argumentos de colección nulos
            throw new NullPointerException("La colección a añadir no puede ser nula.");

        boolean modified = false;

        for (T item : c)
            // Llama a add(item)
            if (add(item))
                modified = true;

        // Devuelve true si se añadió al menos un elemento
        return modified;

    }

    /**
     * Busca un elemento por su ID utilizando una función para obtener el ID del elemento.
     * @param id el ID a buscar
     * @param idGetter función que obtiene el ID del elemento
     * @return el elemento encontrado o null si no se encuentra
     */
    public T findById(Integer id, Function<T, Integer> idGetter) {
        Nodo<T> current = head;
        while (current != null) {
            Integer currentId = idGetter.apply(current.getData());
            if (currentId != null && currentId.equals(id)) {
                return current.getData();
            }
            current = current.getNext();
        }
        return null;
    }

    /**
     * Retorna el primer elemento (dato) de la lista.
     * * @return El elemento de tipo T en la cabeza (head) de la lista.
     * @throws NoSuchElementException Si la lista está vacía.
     */
    @Override
    public T getFirst() {

        // 1. Verificar si la lista está vacía
        if (head == null) {
            // O si size == 0, dependiendo de cómo manejes la lista.
            // Es crucial para evitar un NullPointerException.
            throw new NoSuchElementException("La lista está vacía.");
        }

        // 2. Retornar el dato del nodo apuntado por 'head'
        return head.getData();
    }

    /**
     * Remueve todos los elementos que cumplen con el predicado dado.
     * @param filter el predicado que determina qué elementos eliminar
     * @return true si se eliminó al menos un elemento, false en caso contrario
     * @throws NullPointerException si el predicado es nulo
     */
    @Override
    public boolean removeIf(Predicate<? super T> filter) {

        if (filter == null)
            throw new NullPointerException("El filtro no puede ser nulo");


        boolean removed = false;
        Nodo<T> current = head;
        Nodo<T> prev = null;

        while (current != null) {
            // Guardamos referencia al siguiente antes de (posiblemente) eliminar
            Nodo<T> nextNode = current.getNext();

            if (filter.test(current.getData())) {
                // --- CASO: ELIMINAR NODO ---
                removed = true;
                size--;

                if (prev == null) {
                    // Caso 1: Estamos eliminando la cabeza (head)
                    head = nextNode;

                    // Si la lista se vacía, tail también debe ser null
                    if (head == null)
                        tail = null;

                } else {
                    // Caso 2: Estamos eliminando un nodo intermedio o final
                    prev.setNext(nextNode);

                    // Si eliminamos el último nodo, actualizamos tail
                    if (current == tail)
                        tail = prev;

                }
                // Importante: No avanzamos 'prev', porque el actual fue borrado
                // y el 'prev' sigue siendo el anterior del nuevo 'current'.
            } else {
                // --- CASO: CONSERVAR NODO ---
                // Solo avanzamos 'prev' si no eliminamos el nodo actual
                prev = current;
            }

            // Avanzamos al siguiente nodo para la próxima iteración
            current = nextNode;
        }

        return removed;
    }
}