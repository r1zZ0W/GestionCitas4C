package mx.edu.utez.gestioncitas.data_structs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ListaSimple<T> {

    private Nodo<T> head;

    public ListaSimple() {
        this.head = null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void append(T data) {

        Nodo<T> newNode = new Nodo<>(data);

        if (isEmpty()) {
            head = newNode;
            return;
        }

        Nodo<T> current = head;

        while (current.getNext() != null)
            current = current.getNext();

        current.setNext(newNode);
    }

    public void delete(T data) {

        if (isEmpty()) return;

        if (head.getData().equals(data)) {
            head = head.getNext();
            return;
        }

        Nodo<T> current = head;
        while (current.getNext() != null) {

            if (current.getNext().getData().equals(data)) {

                current.setNext(current.getNext().getNext());
                return;

            }

            current = current.getNext();

        }

    }

    public boolean search(T data) {

        Nodo<T> current = head;
        while (current != null) {

            if (current.getData().equals(data)) return true;

            current = current.getNext();

        }

        return false;

    }

    public void display() {

        if (isEmpty()) {

            System.out.println("Lista vacía");
            return;

        }

        Nodo<T> current = head;
        while (current != null) {
            System.out.print(current.getData() + " -> ");
            current = current.getNext();
        }

        System.out.println("null");
    }

    public T findById(Integer id, Function<T, Integer> idGetter) {
        Nodo<T> current = head;
        while (current != null) {

            if (idGetter.apply(current.getData()).equals(id))
                return current.getData();

            current = current.getNext();
        }
        return null;
    }

    public List<T> toList() {
        List<T> result = new ArrayList<>();
        Nodo<T> current = head;

        while (current != null) {
            result.add(current.getData());
            current = current.getNext();
        }

        return result;
    }

}
