package ru.sbrf.cache.impl;

public class UnsupportedCacheTypeException extends RuntimeException {

    private static final long serialVersionUID = -5799261736212445407L;

    public UnsupportedCacheTypeException(String message) {
        super(message);
    }
}
