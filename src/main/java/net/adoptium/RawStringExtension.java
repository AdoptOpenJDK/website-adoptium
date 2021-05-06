package net.adoptium;

import io.quarkus.qute.RawString;
import io.quarkus.qute.TemplateExtension;

@TemplateExtension
class RawStringExtension {

    static RawString toRaw(String val) {
        return new RawString(val);
    }
}
