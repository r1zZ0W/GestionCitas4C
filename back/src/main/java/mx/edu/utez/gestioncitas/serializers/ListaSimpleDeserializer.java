package mx.edu.utez.gestioncitas.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import mx.edu.utez.gestioncitas.data_structs.ListaSimple;

import java.io.IOException;

/**
 * Deserializador personalizado para la clase ListaSimple.
 * Para que funcione correctamente con Jackson, debe estar anotada con:
 * @JsonDeserialize(using = ListaSimpleDeserializer.class)
 *
 */
public class ListaSimpleDeserializer extends StdDeserializer<ListaSimple<?>> {

    /**
     * Constructor por defecto.
     */
    public ListaSimpleDeserializer() {
        super(ListaSimple.class);
    }

    /**
     * Deserializa un JSON en una instancia de ListaSimple.
     * @param p el parser JSON
     * @param ctxt el contexto de deserialización
     * @return una instancia de ListaSimple con los datos deserializados
     * @throws IOException si ocurre un error durante la deserialización
     */
    @Override
    public ListaSimple<?> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {

        ListaSimple<Object> lista = new ListaSimple<>();
        JsonNode arrayNode = p.getCodec().readTree(p);

        if (!arrayNode.isArray()) {
            return lista;
        }

        ObjectMapper mapper = (ObjectMapper) p.getCodec();

        // Intentar obtener el tipo del contexto
        JavaType valueType = ctxt.getContextualType();
        Class<?> elementClass = Object.class;

        if (valueType != null && valueType.containedTypeCount() > 0)
            elementClass = valueType.containedType(0).getRawClass();

        for (JsonNode itemNode : arrayNode) {
            Object item = mapper.treeToValue(itemNode, elementClass);
            lista.add(item);
        }

        return lista;
    }
}