package mx.edu.utez.gestioncitas.data_structs;

/**
 * Implementación de un Trie (árbol prefijo) para almacenamiento y búsqueda de palabras.
 * Permite insertar palabras y obtener sugerencias basadas en un prefijo dado.
 */
public class Trie {

    private final TrieNode root;

    public Trie() {
        root = new TrieNode('\0'); // Nodo raíz vacío
    }

    /**
     * Inserta una palabra en el trie.
     * @param word la palabra a insertar.
     */
    public void insert(String word) {

        TrieNode current = root;
        for (char ch : word.toCharArray()) {

            current.getChildren().putIfAbsent(ch, new TrieNode(ch));
            current = current.getChildren().get(ch);

        }

        current.setEndOfWord(true);

    }

    /**
     * Obtiene una lista de sugerencias de palabras que comienzan con el prefijo dado.
     * @param prefix el prefijo para buscar sugerencias.
     * @return una lista simple de sugerencias de palabras.
     */
    public ListaSimple<String> getSuggestions(String prefix) {

        ListaSimple<String> suggestions = new ListaSimple<>();
        TrieNode current = root;

        for (char ch : prefix.toCharArray()) {
            if (!current.getChildren().containsKey(ch))
                return suggestions; // No hay palabras con este prefijo

            current = current.getChildren().get(ch);
        }

        findAllWords(current, prefix, suggestions);
        return suggestions;

    }

    /**
     * Encuentra todas las palabras en el trie que comienzan desde el nodo dado.
     * @param node El nodo actual en el trie.
     * @param currentPrefix El prefijo actual construido hasta este nodo.
     * @param suggestions La lista para almacenar las sugerencias encontradas.
     */
    private void findAllWords(TrieNode node, String currentPrefix, ListaSimple<String> suggestions) {

        if (node.isEndOfWord())
            suggestions.add(currentPrefix);

        for (char ch : node.getChildren().keySet())
            findAllWords(node.getChildren().get(ch), currentPrefix + ch, suggestions);

    }
}
