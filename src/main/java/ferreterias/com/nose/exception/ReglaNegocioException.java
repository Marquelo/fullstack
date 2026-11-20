package ferreterias.com.nose.exception;

/** 400 – se incumple una regla de negocio. */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
