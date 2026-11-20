package ferreterias.com.nose.exception;

/** 401 – correo/contraseña incorrectos o token inválido. */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
