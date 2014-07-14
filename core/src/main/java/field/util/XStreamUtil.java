package field.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.SunUnsafeReflectionProvider;
import com.thoughtworks.xstream.core.ReferenceByIdMarshallingStrategy;
import field.core.plugins.drawing.opengl.CachedLineCompression;

/**
 * Created by jason on 7/14/14.
 */
public class XStreamUtil {
    //TODO cache?
       public static XStream newDefaultXStream() {
            return new XStream(new SunUnsafeReflectionProvider()) {{
                registerConverter(new ChannelSerializer());
                registerConverter(new MarkerSerializer(getMapper()));
                registerConverter(new FloatBufferSerializer());
                registerConverter(CachedLineCompression.converter);
                setMarshallingStrategy(new ReferenceByIdMarshallingStrategy());
            }};
        }
}
