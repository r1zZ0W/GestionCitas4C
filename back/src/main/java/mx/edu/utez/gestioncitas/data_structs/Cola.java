package mx.edu.utez.gestioncitas.data_structs;

import java.util.ArrayList;
import java.util.List;

public class Cola<T> {

    private Nodo<T> front; // el primero en llegar
    private Nodo<T> rear;  // el último en llegar
    private int size;

    public Cola() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    public void enqueue(T data) { // primero en llegar, primero en salir

        Nodo<T> newNode = new Nodo<>(data);

        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.setNext(newNode);
            rear = newNode; // se va al final de la cola
        }
        size++;
    }

    public T dequeue() { // saca al primero de la cola 

        if (isEmpty()) {
            return null;
        }

        T data = front.getData();
        front = front.getNext();

        if (front == null) {
            rear = null; // si no hay nadie, la cola está vacía
        }

        size--;
        return data;
    }

    public T peek() {

        if (isEmpty()) {
            return null;
        }
        return front.getData();
    }

    public void display() {

        if (isEmpty()) {
            System.out.println("Cola vacía");
            return;
        }

        Nodo<T> current = front;
        System.out.print("Cola (FIFO): ");
        while (current != null) {
            System.out.print(current.getData() + " <- ");
            current = current.getNext();
        }
        System.out.println("null");
    }

    public List<T> toList() {
        List<T> result = new ArrayList<>();
        Nodo<T> current = front;

        while (current != null) {
            result.add(current.getData());
            current = current.getNext();
        }

        return result;
    }

    public boolean search(T data) {

        Nodo<T> current = front;
        while (current != null) {

            if (current.getData().equals(data)) {
                return true;
            }

            current = current.getNext();
        }

        return false;
    }

    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }

}
