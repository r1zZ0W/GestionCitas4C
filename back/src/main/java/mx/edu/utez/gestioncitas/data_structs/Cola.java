package mx.edu.utez.gestioncitas.data_structs;

import org.springframework.lang.NonNull;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementación de una cola genérica utilizando una lista enlazada.
 * Permite operaciones de encolado, desencolado y visualización del primer elemento.
 * @param <T> Tipo de elementos almacenados en la cola.
 */
public class Cola<T> extends AbstractQueue<T> {

    private Nodo<T> front; // el primero en llegar
    private Nodo<T> rear;  // el último en llegar
    private int size;

    public Cola() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    /**
     * Inserta un elemento a la cola
     * @param e elemento a insertar
     * @return true si se insertó correctamente
    */
    @Override
    public boolean offer(T e) {
        if (e == null) throw new NullPointerException();

        Nodo<T> newNode = new Nodo<>(e);
        if (size == 0) {
            front = newNode;
            rear = newNode;
        } else {
            rear.setNext(newNode);
            rear = newNode;
        }
        size++;
        return true;
    }

    /**
     * Desencola el primer elemento de la cola y lo devuelve
     * @return el primer elemento de la cola o null si está vacía
     */
    @Override
    public T poll() {
        if (size == 0) return null;

        T data = front.getData();
        front = front.getNext();

        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }

    /**
     * Ve el elemento sin sacarlo de la cola
     * @return el primer elemento de la cola o null si está vacía
     */
    @Override
    public T peek() {
        if (size == 0) return null;
        return front.getData();
    }

    /**
     *  Devuelve el tamaño de la cola para saber si está vacia o no
     *  @return tamaño de la cola
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Devuelve un iterador para recorrer la cola y así obtener métodos como toString y así xD
     * @return iterador de la cola
     */
    @Override
    @NonNull
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> current = front;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) throw new NoSuchElementException();
                T data = current.getData();
                current = current.getNext();
                return data;
            }
        };
    }
}
