package mx.edu.utez.gestioncitas.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import mx.edu.utez.gestioncitas.data_structs.ListaSimple;

import java.io.IOException;

/**
 * Serializador personalizado para la clase ListaSimple.
 * Para que funcione correctamente con Jackson, debe estar anotada con:
 * @JsonSerialize(using = ListaSimpleSerializer.class)
 *
 */
public class ListaSimpleSerializer extends StdSerializer<ListaSimple<?>> {

    /**
     * Constructor por defecto.
     */
    public ListaSimpleSerializer() {
        super(ListaSimple.class, true);
    }

    /**
     * Serializa una instancia de ListaSimple en JSON.
     * @param lista la instancia de ListaSimple a serializar
     * @param gen el generador JSON
     * @param provider el proveedor de serialización
     * @throws IOException si ocurre un error durante la serialización
     */
    @Override
    public void serialize(ListaSimple<?> lista, JsonGenerator gen, SerializerProvider provider)
            throws IOException {

        gen.writeStartArray();
        for (Object item : lista)
            gen.writeObject(item);

        gen.writeEndArray();
    }

}

