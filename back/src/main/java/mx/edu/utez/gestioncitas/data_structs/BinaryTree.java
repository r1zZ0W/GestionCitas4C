package mx.edu.utez.gestioncitas.data_structs;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Implementación de un árbol binario de búsqueda genérico.
 * Permite insertar, buscar y recorrer elementos de manera ordenada.
 * @param <T> Tipo de elementos almacenados en el árbol
 */
public class BinaryTree<T> {

    private BinaryTreeNode<T> root;
    private int size;
    private Comparator<T> comparator;

    /**
     * Constructor del árbol binario con comparador
     * @param comparator Comparador para ordenar los elementos
     */
    public BinaryTree(Comparator<T> comparator) {
        this.root = null;
        this.size = 0;
        this.comparator = comparator;
    }

    /**
     * Constructor del árbol binario sin comparador (requiere que T implemente Comparable)
     */
    @SuppressWarnings("unchecked")
    public BinaryTree() {
        this.root = null;
        this.size = 0;
        this.comparator = (a, b) -> {
            if (a instanceof Comparable && b instanceof Comparable) {
                return ((Comparable<T>) a).compareTo(b);
            }
            throw new IllegalArgumentException("Los elementos deben implementar Comparable o proporcionar un Comparator");
        };
    }

    /**
     * Verifica si el árbol está vacío
     * @return true si el árbol está vacío, false en caso contrario
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Obtiene el tamaño del árbol
     * @return el número de elementos en el árbol
     */
    public int size() {
        return size;
    }

    /**
     * Inserta un elemento en el árbol
     * @param data el elemento a insertar
     * @return true si se insertó correctamente
     */
    public boolean insert(T data) {
        if (data == null) {
            throw new NullPointerException("No se puede insertar un elemento nulo");
        }

        root = insertRecursive(root, data);
        size++;
        return true;
    }

    /**
     * Método recursivo para insertar un elemento
     * @param node nodo actual
     * @param data dato a insertar
     * @return el nodo actualizado
     */
    private BinaryTreeNode<T> insertRecursive(BinaryTreeNode<T> node, T data) {
        if (node == null) {
            return new BinaryTreeNode<>(data);
        }

        int comparison = comparator.compare(data, node.getData());
        if (comparison < 0) {
            node.setLeft(insertRecursive(node.getLeft(), data));
        } else if (comparison > 0) {
            node.setRight(insertRecursive(node.getRight(), data));
        } else {
            // Si es igual, lo insertamos a la derecha (permite duplicados)
            node.setRight(insertRecursive(node.getRight(), data));
        }

        return node;
    }

    /**
     * Busca un elemento en el árbol
     * @param data el elemento a buscar
     * @return el elemento encontrado o null si no existe
     */
    public T search(T data) {
        if (data == null) {
            return null;
        }
        return searchRecursive(root, data);
    }

    /**
     * Método recursivo para buscar un elemento
     * @param node nodo actual
     * @param data dato a buscar
     * @return el dato encontrado o null
     */
    private T searchRecursive(BinaryTreeNode<T> node, T data) {
        if (node == null) {
            return null;
        }

        int comparison = comparator.compare(data, node.getData());
        if (comparison == 0) {
            return node.getData();
        } else if (comparison < 0) {
            return searchRecursive(node.getLeft(), data);
        } else {
            return searchRecursive(node.getRight(), data);
        }
    }

    /**
     * Convierte el árbol en una lista simple usando recorrido in-order
     * @return ListaSimple con los elementos ordenados
     */
    public ListaSimple<T> toList() {
        ListaSimple<T> result = new ListaSimple<>();
        inOrderTraversal(root, result);
        return result;
    }

    /**
     * Recorrido in-order del árbol (izquierda, raíz, derecha)
     * @param node nodo actual
     * @param result lista donde se almacenan los resultados
     */
    private void inOrderTraversal(BinaryTreeNode<T> node, ListaSimple<T> result) {
        if (node != null) {
            inOrderTraversal(node.getLeft(), result);
            result.add(node.getData());
            inOrderTraversal(node.getRight(), result);
        }
    }

    /**
     * Convierte el árbol en una lista simple usando recorrido pre-order
     * @return ListaSimple con los elementos en pre-order
     */
    public ListaSimple<T> toListPreOrder() {
        ListaSimple<T> result = new ListaSimple<>();
        preOrderTraversal(root, result);
        return result;
    }

    /**
     * Recorrido pre-order del árbol (raíz, izquierda, derecha)
     * @param node nodo actual
     * @param result lista donde se almacenan los resultados
     */
    private void preOrderTraversal(BinaryTreeNode<T> node, ListaSimple<T> result) {
        if (node != null) {
            result.add(node.getData());
            preOrderTraversal(node.getLeft(), result);
            preOrderTraversal(node.getRight(), result);
        }
    }

    /**
     * Convierte el árbol en una lista simple usando recorrido post-order
     * @return ListaSimple con los elementos en post-order
     */
    public ListaSimple<T> toListPostOrder() {
        ListaSimple<T> result = new ListaSimple<>();
        postOrderTraversal(root, result);
        return result;
    }

    /**
     * Recorrido post-order del árbol (izquierda, derecha, raíz)
     * @param node nodo actual
     * @param result lista donde se almacenan los resultados
     */
    private void postOrderTraversal(BinaryTreeNode<T> node, ListaSimple<T> result) {
        if (node != null) {
            postOrderTraversal(node.getLeft(), result);
            postOrderTraversal(node.getRight(), result);
            result.add(node.getData());
        }
    }

    /**
     * Limpia el árbol, removiendo todos sus elementos
     */
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Busca un elemento por su ID utilizando una función para obtener el ID
     * @param id el ID a buscar
     * @param idGetter función que obtiene el ID del elemento
     * @return el elemento encontrado o null si no se encuentra
     */
    public T findById(Integer id, Function<T, Integer> idGetter) {
        return findByIdRecursive(root, id, idGetter);
    }

    /**
     * Método recursivo para buscar por ID
     * @param node nodo actual
     * @param id ID a buscar
     * @param idGetter función que obtiene el ID
     * @return el elemento encontrado o null
     */
    private T findByIdRecursive(BinaryTreeNode<T> node, Integer id, Function<T, Integer> idGetter) {
        if (node == null) {
            return null;
        }

        Integer currentId = idGetter.apply(node.getData());
        if (currentId != null && currentId.equals(id)) {
            return node.getData();
        }

        T leftResult = findByIdRecursive(node.getLeft(), id, idGetter);
        if (leftResult != null) {
            return leftResult;
        }

        return findByIdRecursive(node.getRight(), id, idGetter);
    }
}

