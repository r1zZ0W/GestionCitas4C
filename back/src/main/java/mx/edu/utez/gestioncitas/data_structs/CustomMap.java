package mx.edu.utez.gestioncitas.data_structs;

import org.springframework.lang.NonNull;

import java.util.*;

/**
 * Implementación personalizada de Map usando AbstractMap
 * Utiliza una tabla hash con encadenamiento para manejar colisiones
 */
public class CustomMap<K, V> extends AbstractMap<K, V> {

    // Capacidad inicial por defecto
    private static final int DEFAULT_CAPACITY = 16;

    // Factor de carga por defecto
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    // Tabla hash que contiene los nodos
    private Node<K, V>[] table;

    // Número de entradas en el map
    private int size;

    // Factor de carga
    private final float loadFactor;

    // Umbral para redimensionar
    private int threshold;

    /**
     * Clase interna que representa un nodo en la tabla hash
     */
    static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Map.Entry)) return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return Objects.equals(key, e.getKey()) &&
                    Objects.equals(value, e.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    /**
     * Constructor por defecto
     */
    @SuppressWarnings("unchecked")
    public CustomMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.threshold = (int) (DEFAULT_CAPACITY * loadFactor);
        this.table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
        this.size = 0;
    }

    /**
     * Constructor con capacidad inicial
     */
    @SuppressWarnings("unchecked")
    public CustomMap(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Capacidad inicial no puede ser negativa: " + initialCapacity);
        }
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        int capacity = tableSizeFor(initialCapacity);
        this.threshold = (int) (capacity * loadFactor);
        this.table = (Node<K, V>[]) new Node[capacity];
        this.size = 0;
    }

    /**
     * Constructor con capacidad inicial y factor de carga
     */
    @SuppressWarnings("unchecked")
    public CustomMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Capacidad inicial no puede ser negativa: " + initialCapacity);
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Factor de carga inválido: " + loadFactor);
        }
        this.loadFactor = loadFactor;
        int capacity = tableSizeFor(initialCapacity);
        this.threshold = (int) (capacity * loadFactor);
        this.table = (Node<K, V>[]) new Node[capacity];
        this.size = 0;
    }

    /**
     * Calcula el hash del objeto key
     */
    private int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * Retorna la siguiente potencia de 2 mayor o igual a cap
     */
    private int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : n + 1;
    }

    /**
     * Método requerido por AbstractMap - retorna el conjunto de entradas
     */
    @Override
    @NonNull
    public Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    /**
     * Agrega o actualiza un valor en el map
     */
    @Override
    public V put(K key, V value) {
        return putValue(hash(key), key, value);
    }

    /**
     * Implementación interna de put
     */
    private V putValue(int hash, K key, V value) {
        Node<K, V>[] tab = table;
        int n = tab.length;
        int i = (n - 1) & hash;

        // Si no hay nodo en esa posición, crear uno nuevo
        if (tab[i] == null) {
            tab[i] = new Node<>(hash, key, value, null);
        } else {
            // Hay colisión, buscar en la cadena
            Node<K, V> e = tab[i];

            // Recorrer la cadena
            while (true) {
                // Si la clave ya existe, actualizar el valor
                if (e.hash == hash && Objects.equals(key, e.key)) {
                    V oldValue = e.value;
                    e.value = value;
                    return oldValue;
                }

                // Si llegamos al final de la cadena, agregar nuevo nodo
                if (e.next == null) {
                    e.next = new Node<>(hash, key, value, null);
                    break;
                }

                e = e.next;
            }
        }

        // Incrementar tamaño y verificar si se necesita redimensionar
        if (++size > threshold) {
            resize();
        }

        return null;
    }

    /**
     * Obtiene un valor del map
     */
    @Override
    public V get(Object key) {
        Node<K, V> e = getNode(hash(key), key);
        return e == null ? null : e.value;
    }

    /**
     * Obtiene un nodo del map
     */
    private Node<K, V> getNode(int hash, Object key) {
        Node<K, V>[] tab = table;
        int n = tab.length;
        int i = (n - 1) & hash;
        Node<K, V> first = tab[i];

        if (first != null) {
            Node<K, V> e = first;
            while (e != null) {
                if (e.hash == hash && Objects.equals(key, e.key)) {
                    return e;
                }
                e = e.next;
            }
        }
        return null;
    }

    /**
     * Elimina un elemento del map
     */
    @Override
    public V remove(Object key) {
        Node<K, V> e = removeNode(hash(key), key);
        return e == null ? null : e.value;
    }

    /**
     * Implementación interna de remove
     */
    private Node<K, V> removeNode(int hash, Object key) {
        Node<K, V>[] tab = table;
        int n = tab.length;
        int i = (n - 1) & hash;
        Node<K, V> first = tab[i];

        if (first != null) {
            Node<K, V> e = first;
            Node<K, V> prev = null;

            while (e != null) {
                if (e.hash == hash && Objects.equals(key, e.key)) {
                    if (prev == null) {
                        tab[i] = e.next;
                    } else {
                        prev.next = e.next;
                    }
                    size--;
                    return e;
                }
                prev = e;
                e = e.next;
            }
        }
        return null;
    }

    /**
     * Verifica si el map contiene una clave
     */
    @Override
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }

    /**
     * Retorna el tamaño del map
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Verifica si el map está vacío
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Limpia el map
     */
    @Override
    public void clear() {
        Node<K, V>[] tab = table;
        if (tab != null && size > 0) {
            size = 0;
            Arrays.fill(tab, null);
        }
    }

    /**
     * Redimensiona la tabla hash cuando se alcanza el umbral
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTab = table;
        int oldCap = oldTab.length;
        int newCap = oldCap << 1; // Duplicar capacidad

        if (newCap < 0 || newCap >= (1 << 30)) {
            return; // Overflow o capacidad máxima alcanzada
        }

        Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];

        // Redistribuir todos los nodos
        for (int i = 0; i < oldCap; i++) {
            Node<K, V> e = oldTab[i];
            if (e != null) {
                oldTab[i] = null;

                do {
                    Node<K, V> next = e.next;
                    int newIndex = (newCap - 1) & e.hash;
                    e.next = newTab[newIndex];
                    newTab[newIndex] = e;
                    e = next;
                } while (e != null);
            }
        }

        table = newTab;
        threshold = (int) (newCap * loadFactor);
    }

    /**
     * Clase interna para el conjunto de entradas
     */
    private class EntrySet extends AbstractSet<Entry<K, V>> {

        @Override
        @NonNull
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            CustomMap.this.clear();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Entry<?, ?> e)) {
                return false;
            }
            Object key = e.getKey();
            Node<K, V> candidate = getNode(hash(key), key);
            return candidate != null && candidate.equals(e);
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof Entry<?, ?> e) {
                Object key = e.getKey();
                Object value = e.getValue();
                Node<K, V> node = getNode(hash(key), key);
                if (node != null && Objects.equals(node.getValue(), value)) {
                    CustomMap.this.removeNode(hash(key), key);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Iterador para las entradas del map
     */
    private class EntryIterator implements Iterator<Entry<K, V>> {
        Node<K, V> next;
        Node<K, V> current;
        int index;
        int expectedSize;

        EntryIterator() {
            Node<K, V>[] t = table;
            current = next = null;
            index = 0;
            expectedSize = size;
            if (t != null && size > 0) {
                // Buscar el primer nodo no nulo
                while (index < t.length && (next = t[index++]) == null);
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Entry<K, V> next() {
            if (expectedSize != size) {
                throw new ConcurrentModificationException();
            }

            Node<K, V>[] t = table;
            Node<K, V> e = next;
            if (e == null) {
                throw new NoSuchElementException();
            }

            current = e;
            next = e.next;

            // Si no hay más en la cadena, buscar el siguiente bucket
            if (next == null && t != null) {
                while (index < t.length && (next = t[index++]) == null);
            }

            return e;
        }

        @Override
        public void remove() {
            Node<K, V> p = current;
            if (p == null) {
                throw new IllegalStateException();
            }
            if (expectedSize != size) {
                throw new ConcurrentModificationException();
            }
            current = null;
            K key = p.key;
            CustomMap.this.removeNode(hash(key), key);
            expectedSize = size;
        }
    }
}