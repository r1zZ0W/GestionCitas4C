package mx.edu.utez.gestioncitas.data_structs;

/**
 * Clase que representa un nodo en una lista enlazada genérica.
 * @param <T> el tipo de dato almacenado en el nodo
 */
public class Nodo<T> {

    // Atributos del nodo
    private T data;
    private Nodo<T> next;
    private Nodo<T> previous;

    // Constructor del nodo
    public Nodo(T data) {
        this.data = data;
        this.next = null;
        this.previous = null;
    }

    // Getters y setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Nodo<T> getNext() {
        return next;
    }

    public void setNext(Nodo<T> next) {
        this.next = next;
    }

    public Nodo<T> getPrevious() {
        return previous;
    }

    public void setPrevious(Nodo<T> previous) {
        this.previous = previous;
    }

}
