package ann;

import com.google.common.base.CharMatcher;

import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;

class StringSrc extends SimpleJavaFileObject {

    final String source;
    final long lastModified;


    StringSrc(String fullyQualifiedName, String source) {
        super(createUri(fullyQualifiedName), Kind.SOURCE);
        this.source = source;
        this.lastModified = System.currentTimeMillis();
    }

    static URI createUri(String fullyQualifiedClassName) {
        return URI.create(CharMatcher.is('.').replaceFrom(fullyQualifiedClassName, '/') + Kind.SOURCE.extension);
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return this.source;
    }

    public OutputStream openOutputStream() {
        throw new IllegalStateException();
    }

    public InputStream openInputStream() {
        return new ByteArrayInputStream(this.source.getBytes(Charset.defaultCharset()));
    }

    public Writer openWriter() {
        throw new IllegalStateException();
    }

    public Reader openReader(boolean ignoreEncodingErrors) {
        return new StringReader(this.source);
    }

    public long getLastModified() {
        return this.lastModified;
    }
}