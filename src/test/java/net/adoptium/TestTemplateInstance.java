package net.adoptium;

import io.quarkus.qute.TemplateInstanceBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

public class TestTemplateInstance extends TemplateInstanceBase {
    @Override
    public String render() {
        return null;
    }

    @Override
    public CompletionStage<String> renderAsync() {
        return null;
    }

    @Override
    public Multi<String> createMulti() {
        return null;
    }

    @Override
    public Uni<String> createUni() {
        return null;
    }

    @Override
    public CompletionStage<Void> consume(Consumer<String> consumer) {
        return null;
    }

   public Object getData() {
        return this.data;
   }
}
