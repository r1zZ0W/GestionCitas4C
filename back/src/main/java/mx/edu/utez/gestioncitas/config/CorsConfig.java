package mx.edu.utez.gestioncitas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * Configuración de CORS para la aplicación.
 * Permite solicitudes desde cualquier origen, con cualquier metodo y cabecera.
 * Esto es útil para permitir que aplicaciones frontend accedan a la API sin restricciones.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configura las reglas de CORS.
     * @param registry El registro de CORS donde se añaden las configuraciones.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Aplica CORS a todas las rutas (/**)
        registry.addMapping("/**")
                // Permitimos cualquier origen (*)
                .allowedOrigins("*")
                // Permitimos cualquier metodo (GET, POST, PUT, DELETE, y muy importante: OPTIONS)
                .allowedMethods("*")
                // Permitimos cualquier cabecera
                .allowedHeaders("*");
    }
}