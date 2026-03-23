package io.github.jespersm.unselectinator.core;

/**
 * Caller location captured from the current stack.
 */
public record SourceLocation(String className, String methodName, String fileName, int lineNumber) {

    @Override
    public String toString() {
        return className + "#" + methodName + "(" + fileName + ":" + lineNumber + ")";
    }
}

