package io.github.jespersm.jpa.tripwire.unselectinator.core;

/**
 * Caller location captured from the current stack.
 *
 * @param className declaring class name
 * @param methodName method name
 * @param fileName source file name
 * @param lineNumber source line number
 */
public record SourceLocation(String className, String methodName, String fileName, int lineNumber) {

    @Override
    public String toString() {
        return className + "#" + methodName + "(" + fileName + ":" + lineNumber + ")";
    }
}

