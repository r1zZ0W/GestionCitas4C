package mx.edu.utez.gestioncitas.data_structs;

import org.springframework.lang.NonNull;

import java.util.*;

/**
 * Implementación personalizada de Map usando AbstractMap
 * Utiliza una tabla hash con encadenamiento para manejar colisiones
 * @param <K> Tipo de clave
 * @param <V> Tipo de valor
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
     * @param <K> Tipo de clave
     * @param <V> Tipo de valor
     */
    static class Node<K, V> implements Map.Entry<K, V> {

        // Atributos del nodo
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        /** Constructor del nodo
         * @param hash Hash de la clave
         * @param key Clave
         * @param value Valor
         * @param next Siguiente nodo en la cadena
         */
        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        /**
         * Obtiene la clave del nodo
         * @return Clave del nodo
         */
        @Override
        public K getKey() {
            return key;
        }

        /**
         * Obtiene el valor del nodo
         * @return Valor del nodo
         */
        @Override
        public V getValue() {
            return value;
        }

        /**
         * Establece un nuevo valor para el nodo
         * @param newValue Nuevo valor a establecer
         * @return Valor antiguo del nodo
         */
        @Override
        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        /**
         * Compara este nodo con otro objeto para igualdad
         * @param o Objeto a comparar
         * @return True si son iguales, false en caso contrario
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Entry<?, ?> e)) return false;
            return Objects.equals(key, e.getKey()) &&
                    Objects.equals(value, e.getValue());
        }

        /**|
         * Calcula el hash code del nodo
         * @return Hash code del nodo
         */
        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        /**
         * Representación en cadena del nodo
         * @return Cadena en formato "key=value"
         */
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
     * @param initialCapacity Capacidad inicial del mapa
     * @throws IllegalArgumentException si la capacidad inicial es negativa
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
     * @param initialCapacity Capacidad inicial del mapa
     * @param loadFactor Factor de carga del mapa
     * @throws IllegalArgumentException si la capacidad inicial es negativa o el factor de carga es inválido
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
     * @param key Clave a hashear
     * @return Hash de la clave
     */
    private int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * Retorna la siguiente potencia de 2 mayor o igual a cap
     * @param cap Capacidad deseada
     * @return Siguiente potencia de 2 mayor o igual a cap
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
     * @return Conjunto de entradas del mapa
     */
    @Override
    @NonNull
    public Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    /**
     * Agrega o actualiza un valor en el map
     * @param key Clave del valor
     * @param value Valor a asociar con la clave
     * @return Valor antiguo asociado con la clave, o null si no existía
     */
    @Override
    public V put(K key, V value) {
        return putValue(hash(key), key, value);
    }

    /**
     * Implementación interna de put
     * @param hash Hash de la clave
     * @param key Clave del valor
     * @param value Valor a asociar con la clave
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
     * @param key Clave del valor a obtener
     * @return Valor asociado con la clave, o null si no existe
     */
    @Override
    public V get(Object key) {
        Node<K, V> e = getNode(hash(key), key);
        return e == null ? null : e.value;
    }

    /**
     * Obtiene un nodo del map
     * @param hash Hash de la clave
     * @param key Clave del valor a obtener
     * @return Nodo asociado con la clave, o null si no existe
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
     * @param key Clave del valor a eliminar
     * @return Valor eliminado, o null si no existía
     */
    @Override
    public V remove(Object key) {
        Node<K, V> e = removeNode(hash(key), key);
        return e == null ? null : e.value;
    }

    /**
     * Implementación interna de remove
     * @param hash Hash de la clave
     * @param key Clave del valor a eliminar
     * @return Nodo eliminado, o null si no existía
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
     * @param key Clave a verificar
     * @return True si la clave existe, false en caso contrario
     */
    @Override
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }

    /**
     * Retorna el tamaño del map
     * @return Número de entradas en el map
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Verifica si el map está vacío
     * @return True si el map está vacío, false en caso contrario
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
     * Inserta el valor solo si la clave no está presente
     * @param key Clave a insertar
     * @param value Valor a insertar
     * @return El valor existente si la clave ya estaba presente, null si se insertó el nuevo valor.
     */
    @Override
    public V putIfAbsent(K key, V value) {
        int hash = hash(key);
        Node<K, V>[] tab = table;
        int n = tab.length;
        int i = (n - 1) & hash;

        Node<K, V> e = tab[i];

        // Si no existe nodo en el bucket, lo insertamos directamente
        if (e == null) {
            tab[i] = new Node<>(hash, key, value, null);
            if (++size > threshold) {
                resize();
            }
            return null;
        }

        // Recorrer la cadena
        Node<K, V> current = e;
        while (true) {

            // Si la clave ya existe, retornamos su valor sin modificarlo
            if (current.hash == hash && Objects.equals(key, current.key))
                return current.value;

            // Llegamos al final de la cadena: insertar nuevo nodo
            if (current.next == null) {
                current.next = new Node<>(hash, key, value, null);
                if (++size > threshold) {
                    resize();
                }
                return null;
            }

            current = current.next;
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

        /**
         *  Retorna un iterador para las entradas del map
         * @return Iterador de entradas
         */
        @Override
        @NonNull
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        /**
         * Retorna el tamaño del conjunto de entradas
         * @return Tamaño del conjunto de entradas
         */
        @Override
        public int size() {
            return size;
        }

        /**
         * Limpia el conjunto de entradas
         */
        @Override
        public void clear() {
            CustomMap.this.clear();
        }

        /**
         * Verifica si el conjunto de entradas contiene una entrada específica
         * @param o Entrada a verificar
         * @return True si la entrada existe, false en caso contrario
         */
        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Entry<?, ?> e)) {
                return false;
            }
            Object key = e.getKey();
            Node<K, V> candidate = getNode(hash(key), key);
            return candidate != null && candidate.equals(e);
        }

        /**
         * Elimina una entrada específica del conjunto
         * @param o Entrada a eliminar
         * @return True si la entrada fue eliminada, false en caso contrario
         */
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

        // Atributos del iterador
        Node<K, V> next;
        Node<K, V> current;
        int index;
        int expectedSize;

        // Constructor del iterador
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

        /**
         * Verifica si hay más entradas para iterar
         * @return True si hay más entradas, false en caso contrario
         */
        @Override
        public boolean hasNext() {
            return next != null;
        }

        /**
         * Retorna la siguiente entrada en la iteración
         * @return Siguiente entrada
         * @throws ConcurrentModificationException si el mapa ha sido modificado durante la iteración
         * @throws NoSuchElementException si no hay más elementos para iterar
         */
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

        /**
         * Elimina la entrada actual de la iteración
         * @throws IllegalStateException si next() no ha sido llamado antes de remove()
         * @throws ConcurrentModificationException si el mapa ha sido modificado durante la iteración
         */
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