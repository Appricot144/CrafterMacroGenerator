package com.cafterMacroGenerator.exception;

public class MacroGenerationException extends RuntimeException {
    
    public MacroGenerationException(String message) {
        super(message);
    }
    
    public MacroGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
