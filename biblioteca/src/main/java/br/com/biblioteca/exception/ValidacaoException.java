package br.com.biblioteca.exception;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValidacaoException extends RuntimeException {
    private final Map<String, String> erros;

    public ValidacaoException(Map<String, String> erros) {
        super("Existem dados inválidos no formulário");
        this.erros = Collections.unmodifiableMap(new LinkedHashMap<>(erros));
    }

    public Map<String, String> getErros() {
        return erros;
    }
}
