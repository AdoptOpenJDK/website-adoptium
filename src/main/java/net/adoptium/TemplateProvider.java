package net.adoptium;

import io.quarkus.qute.TemplateInstance;

import java.util.function.Function;

public class TemplateProvider<T> {
    private Function<T, TemplateInstance> function;

    public TemplateProvider(Function<T, TemplateInstance> function) {
        this.function = function;
    }

    public void setFunction(Function<T, TemplateInstance> function) {
        this.function = function;
    }

    public TemplateInstance get(T template) {
        return function.apply(template);
    }
}
