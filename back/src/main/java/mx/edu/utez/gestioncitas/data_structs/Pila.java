package mx.edu.utez.gestioncitas.data_structs;

/**
 * Implementación de una pila genérica utilizando nodos enlazados.
 * Sigue el principio LIFO (Last In, First Out).
 * @param <T> El tipo de datos que almacenará la pila
 */
public class Pila<T> {

    private Nodo<T> top; // el último que llegó (el más reciente)
    private int size;

    /**
     * Constructor de la pila
     */
    public Pila() {
        this.top = null;
        this.size = 0;
    }

    /**
     * Verifica si la pila está vacía
     * @return true si la pila está vacía, false en caso contrario
     */
    public boolean isEmpty() {
        return top == null;
    }

    /**
     * Obtiene el tamaño actual de la pila
     * @return el número de elementos en la pila
     */
    public int size() {
        return size;
    }
    /**
     * Agrega un nuevo elemento a la cima de la pila
     * @param data el dato a agregar
     */
    public void push(T data) { // LIFO: último en entrar, primero en salir

        Nodo<T> newNode = new Nodo<>(data);

        // se pone arriba de la pila
        if (!isEmpty())
            newNode.setNext(top);

        top = newNode;
        size++;
    }

    /**
     * Remueve y devuelve el elemento en la cima de la pila
     * @return el dato removido, o null si la pila está vacía
     */
    public T pop() {

        if (isEmpty())
            return null;


        T data = top.getData();
        top = top.getNext();
        size--;

        return data;
    }

    /**
     * Devuelve el elemento en la cima de la pila sin removerlo
     * @return el dato en la cima, o null si la pila está vacía
     */
    public T peek() {

        if (isEmpty())
            return null;

        return top.getData();
    }

    /**
     * Muestra el contenido de la pila en la consola
     */
    public void display() {

        if (isEmpty()) {
            System.out.println("Pila vacía");
            return;
        }

        System.out.print("Pila (LIFO - de arriba hacia abajo): ");
        Nodo<T> current = top;
        while (current != null) {
            System.out.print(current.getData() + " | ");
            current = current.getNext();
        }
        System.out.println();
    }

    /**
     * Convierte la pila en una lista simple
     * @return una ListaSimple con los elementos de la pila
     */
    public ListaSimple<T> toList() {

        ListaSimple<T> result = new ListaSimple<>();
        Nodo<T> current = top;

        while (current != null) {
            result.add(current.getData());
            current = current.getNext();
        }

        return result;
    }

    /**
     * Busca un elemento en la pila
     * @param data el dato a buscar
     * @return true si se encuentra el dato, false en caso contrario
     */
    public boolean search(T data) {

        Nodo<T> current = top;
        while (current != null) {

            if (current.getData().equals(data)) {
                return true;
            }

            current = current.getNext();
        }

        return false;
    }

    /**
     * Limpia la pila, removiendo todos sus elementos
     */
    public void clear() {
        top = null;
        size = 0;
    }

}
