package field.graphics.core;

import field.graphics.core.MeshBlast.Frame;
import field.graphics.core.MeshBlast.Header;
import field.util.PythonUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.*;
import java.nio.channels.FileChannel.MapMode;

public
class MeshBlastLoader {

    public Header header;
    public RandomAccessFile file;
    public MappedByteBuffer source;

    FloatBuffer vertex;
    IntBuffer topology;
    private final ByteBuffer vertexb;
    private final ByteBuffer topologyb;

    public
    MeshBlastLoader(String filename) throws IOException {
        header = (Header) PythonUtils.loadAsXML(filename + ".xmlHeader");
        file = new RandomAccessFile(filename, "r");
        source = file.getChannel().map(MapMode.READ_ONLY, 0, file.length());
        source.order(ByteOrder.nativeOrder());

        vertexb = ByteBuffer.allocateDirect(4 * header.maximumDimensions.get(0)).order(ByteOrder.nativeOrder());
        vertex = vertexb.asFloatBuffer();
        topologyb = ByteBuffer.allocateDirect(4 * header.maximumDimensions.get(-1)).order(ByteOrder.nativeOrder());
        topology = topologyb.asIntBuffer();
    }

    public
    void goToFrame(int i) {
        vertexb.rewind();
        topologyb.rewind();

        {
            Frame frame = header.frames.getList(0).get(i);
            source.limit(source.capacity());
            source.position((int) frame.offset * 4);
            source.limit((int) (frame.offset * 4 + frame.length * 4));
            vertexb.rewind();
            vertexb.limit(vertexb.capacity());
            vertexb.put(source);
            vertexb.limit(vertexb.position());
            vertex.limit(vertexb.position() / 4);
            vertexb.rewind();
            vertex.rewind();
        }
        {
            Frame frame = header.frames.getList(-1).get(i);
            source.limit(source.capacity());
            source.position((int) frame.offset * 4);
            source.limit((int) (frame.offset * 4 + frame.length * 4));
            topologyb.rewind();
            topologyb.limit(topologyb.capacity());
            topologyb.put(source);
            topologyb.limit(topologyb.position());
            topology.limit(topology.position() / 4);
            topologyb.rewind();
        }
    }


    public
    int numVertex() {
        return vertex.limit() / 3;
    }

    public
    FloatBuffer getVertex() {
        return vertex;
    }

}
