package mx.edu.utez.gestioncitas.data_structs;

/**
 * Nodo del Trie (árbol prefijo) que almacena un carácter,
 * sus hijos y un indicador de si es el final de una palabra.
 */
public class TrieNode {

    // Atributos del nodo
    private char character;
    private CustomMap<Character, TrieNode> children;
    private boolean isEndOfWord;

    // Constructor con parámetro de carácter
    public TrieNode(char character) {
        this.character = character;
        this.children = new CustomMap<>();
        this.isEndOfWord = false;
    }

    // Getters y Setters
    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public CustomMap<Character, TrieNode> getChildren() {
        return children;
    }

    public void setChildren(CustomMap<Character, TrieNode> children) {
        this.children = children;
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }
}