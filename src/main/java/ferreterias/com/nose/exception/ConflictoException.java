package ferreterias.com.nose.exception;

/** 409 – la operación genera un conflicto (duplicado, datos asociados). */
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
