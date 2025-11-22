package mx.edu.utez.gestioncitas.data_structs;

import java.util.AbstractList;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ListaSimple<T> extends AbstractList<T> {

    private Nodo<T> head;
    private int size;

    public ListaSimple() {
        this.head = null;
        this.size = 0;
    }

    // ========== MÉTODOS REQUERIDOS POR AbstractList ==========

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice: " + index + ", Tamaño: " + size);
        }

        Nodo<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }

        return current.getData();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(T data) {
        Nodo<T> newNode = new Nodo<>(data);

        if (isEmpty()) {
            head = newNode;
        } else {
            Nodo<T> current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newNode);
        }

        size++;
        modCount++;
        return true;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice: " + index + ", Tamaño: " + size);
        }

        T removedData;

        if (index == 0) {
            removedData = head.getData();
            head = head.getNext();
        } else {
            Nodo<T> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.getNext();
            }
            removedData = current.getNext().getData();
            current.setNext(current.getNext().getNext());
        }

        size--;
        modCount++;
        return removedData;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
        modCount++;
    }

    // ========== MÉTODOS ADICIONALES ÚTILES ==========

    public boolean isEmpty() {
        return head == null;
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
            Integer currentId = idGetter.apply(current.getData());
            if (currentId != null && currentId.equals(id)) {
                return current.getData();
            }
            current = current.getNext();
        }
        return null;
    }
}