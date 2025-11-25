package mx.edu.utez.gestioncitas.data_structs;

public class Pila<T> {

    private Nodo<T> top; // el último que llegó (el más reciente)
    private int size;

    public Pila() {
        this.top = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    public void push(T data) { // LIFO: último en entrar, primero en salir

        Nodo<T> newNode = new Nodo<>(data);

        if (isEmpty()) {
            top = newNode;
        } else {
            newNode.setNext(top);
            top = newNode; // se pone arriba de la pila
        }
        size++;
    }

    public T pop() { // saca al último que llegó 

        if (isEmpty()) {
            return null;
        }

        T data = top.getData();
        top = top.getNext();
        size--;

        return data;
    }

    public T peek() {

        if (isEmpty()) {
            return null;
        }
        return top.getData();
    }

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

    public ListaSimple<T> toList() {

        ListaSimple<T> result = new ListaSimple<>();
        Nodo<T> current = top;

        while (current != null) {
            result.add(current.getData());
            current = current.getNext();
        }

        return result;
    }

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

    public void clear() {
        top = null;
        size = 0;
    }

}
