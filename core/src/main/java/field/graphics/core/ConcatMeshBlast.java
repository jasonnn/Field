package field.graphics.core;

import field.graphics.core.MeshBlast.Frame;
import field.graphics.core.MeshBlast.Header;
import field.util.PythonUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.Map.Entry;

public class ConcatMeshBlast {

	private final ArrayList<Header> headers;
	private final FileChannel channelOut;

	public ConcatMeshBlast(String[] names, String out) throws IOException {
		headers = new ArrayList<Header>();
		HashMap<Integer, Integer> newMaxes = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> newStrides = new HashMap<Integer, Integer>();
		int totalFrames = 0;
		for (String n : names) {
			Header h = (Header) PythonUtils.loadAsXML(n + ".xmlHeader");
			headers.add(h);
			for (Map.Entry<Integer, Integer> ii : h.maximumDimensions.entrySet()) {
				Integer q = newMaxes.get(ii.getKey());
				if (q == null)
					newMaxes.put(ii.getKey(), ii.getValue());
				else
					newMaxes.put(ii.getKey(), Math.max(ii.getValue(), q));
			}
			for (Map.Entry<Integer, Integer> ii : h.strides.entrySet()) {
				Integer q = newStrides.get(ii.getKey());
				if (q == null)
					newStrides.put(ii.getKey(), ii.getValue());
				else if (!q.equals(ii.getValue()))
					throw new IllegalArgumentException(" strides for <" + ii.getKey() + "> not compatible <" + newStrides + "> <" + h.strides + '>');
			}
			totalFrames += h.numFrames;
		}

		Header newHeader = new Header();
		newHeader.numFrames = totalFrames;
		newHeader.maximumDimensions = newMaxes;
		newHeader.strides = newStrides;

		int offset = 0;

		channelOut = new RandomAccessFile(out, "rw").getChannel();
		for (Header h : headers) {
			Set<Entry<Integer, Collection<Frame>>> es = h.frames.entrySet();
			for (Entry<Integer, Collection<Frame>> e : es) {
				newHeader.frames.addAllToList(e.getKey(), e.getValue());
				for (Frame f : e.getValue()) {
					f.offset += offset;
				}
			}
			RandomAccessFile ff = new RandomAccessFile(out, "r");
			long ll = ff.length();
			channelOut.transferFrom(ff.getChannel(), 0, ll);
			ff.close();

			offset += ll;
		}

		channelOut.close();

	}

	static public void concatSavedMeshSetIndex(String[] names, String out) {
		List l2 = new ArrayList();
		for (String n : names) {
			List ll = (List) PythonUtils.loadAsXML(n);
			l2.addAll(ll);
		}
		new PythonUtils().persistAsXML(l2, out);
	}
}
