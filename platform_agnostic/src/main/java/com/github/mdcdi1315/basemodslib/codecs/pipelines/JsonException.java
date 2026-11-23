package com.github.mdcdi1315.basemodslib.codecs.pipelines;

import com.google.gson.JsonParseException;

final class JsonException
        extends SerializationException
{
    private final JsonParseException original;

    public JsonException(JsonParseException original) { this.original = original; }

    @Override
    public String getMessage() { return original.getMessage(); }

    @Override
    public String getLocalizedMessage() { return original.getLocalizedMessage(); }

    @Override
    public StackTraceElement[] getStackTrace() { return original.getStackTrace(); }

    @Override
    public synchronized Throwable fillInStackTrace()
    {
        original.fillInStackTrace();
        return super.fillInStackTrace();
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace)
    {
        super.setStackTrace(stackTrace);
        original.setStackTrace(stackTrace);
    }
}
