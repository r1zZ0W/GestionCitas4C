package mx.edu.utez.gestioncitas.util;

import org.springframework.http.HttpStatus;

public class Status {

    /**
     * HELPER -
     * Convierte un código entero a HttpStatus rescatado del mapa de respuesta a un HttpStatus
     * @param code Código entero rescatado del mapa de respuesta
     * @return HttpStatus correspondiente al código entero
     */
    public static HttpStatus getStatus(int code) {
        return switch (code) {
            case 200 -> HttpStatus.OK;
            case 201 -> HttpStatus.CREATED;
            case 400 -> HttpStatus.BAD_REQUEST;
            case 404 -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

}
