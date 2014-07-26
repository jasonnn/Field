package field.util;

import field.math.abstraction.iMetric;
import field.math.util.Hungarian;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;


/**
 *
 * now with configurable sources and sinks
 *
 * @author marc Created on Feb 10, 2005 \u2014 lab
 */
public class HungarianConvenience3<t_element> {

	public FloatBuffer distanceMatrix;

	public int sourceLength;

	public int targetLength;

	public int sources;

	public int sinks;

	private float distanceToSource;

	private float distanceToSink;

	private float sourceDistanceToSink;

	t_element[] sourceBuffer;

	t_element[] targetBuffer;

	IntBuffer outputVector;

	Hungarian h = new Hungarian();

	public HungarianConvenience3(int max) {
//		sourceBuffer = ByteBuffer.allocateDirect(max * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		targetBuffer = ByteBuffer.allocateDirect(max * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		sourceBuffer = (t_element[]) new Object[max];
		targetBuffer = (t_element[]) new Object[max];
		distanceMatrix = ByteBuffer.allocateDirect(max * max * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		outputVector = ByteBuffer.allocateDirect(max * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
	}

	public void computeDistanceMatrix(iMetric<t_element, t_element> e) {
		// this should be accelerated!
		for (int x = 0; x < (sourceLength + sources); x++) {

			t_element x1 = null;
			if (x < sourceLength) {
				x1 = sourceBuffer[x];
			}
			for (int y = 0; y < (targetLength + sinks); y++) {

				t_element x2 = null;
				if (y < targetLength) {
					x2 = targetBuffer[y];
				}

				if ((x < sourceLength) && (y < targetLength)) {
					float d = e.distance(x1, x2);
					d = (float) Math.sqrt(d);
					distanceMatrix.put((y * (sourceLength + sources)) + x, d);
				} else if (x < sourceLength) {
					distanceMatrix.put((y * (sourceLength + sources)) + x, distanceToSource);
				} else if (y < targetLength) {
					distanceMatrix.put((y * (sourceLength + sources)) + x, distanceToSink);
				} else {
					distanceMatrix.put((y * (sourceLength + sources)) + x, sourceDistanceToSink);
				}
			}
		}
	}

	public void computeHungarianMatch() {
		// outputVector.rewind();
		// for(int i=0;i<outputVector.limit();i++)
		// {
		// outputVector.put(i, -1);
		// }
		// outputVector.rewind();
		//
		new MiscNative().performFloat(distanceMatrix, targetLength + sinks, sourceLength + sources, outputVector);

		// the i-th element of outputVector tells us what the i-th element of target maps to in sourceLength
	}

	public float getDistance(int source, int target) {
		return distanceMatrix.get((target * (sourceLength + sources)) + source);
	}

	public int mapTargetToSource(int targetElement) {
		// assert targetElement<targetLength : targetElement+" "+targetLength;
		int r = outputVector.get(targetElement);

		// assert r<sourceLength : r+" "+sourceLength;

		if (r >= (sourceLength + sinks)) return -1;
		return r;
	}

	public float modeAssignmentDistance() {

		float d = 0;
		int targetLength2 = targetLength;
		for (int i = 0; i < targetLength; i++) {
			int to = mapTargetToSource(i);
			if (to == -1) targetLength2--;
		}

		float[] distance = new float[targetLength2];

		int c = 0;
		for (int i = 0; i < targetLength; i++) {
			int to = mapTargetToSource(i);
			if (to != -1) {
				distance[c++] = distanceMatrix.get((i * (sourceLength + sources)) + to);
			}

		}
		if (targetLength2 == 0) return -1;
		if (targetLength2 == 1) return distance[0];

		Arrays.sort(distance);
		if ((distance.length % 2) == 1)
			return distance[distance.length / 2];
		else
			return (distance[distance.length / 2] + distance[((distance.length / 2) + 1)]) / 2;
	}

	public String printDetailedMatchResults() {

		String r = "";
		float[] distance = new float[targetLength];
		float total = 0;

		for (int i = 0; i < targetLength; i++) {
			int to = mapTargetToSource(i);
			r += ("  map <" + i + ((i >= targetLength) ? "(sink)" : "") + "> -> <" + to + ((to >= sourceLength)
                                                                                           ? "(source)"
                                                                                           : "") + '>');
			if (to != -1)
				r += (" d = <" + distanceMatrix.get((i * (sourceLength + sources)) + to) + ">\n");
			else
				r += "\n";
			if (to != -1) {
				total += distanceMatrix.get((i * (sourceLength + sources)) + to);
				distance[i] = distanceMatrix.get((i * (sourceLength + sources)) + to);
			}
		}

		Arrays.sort(distance);
        r += ("  min <"
              + distance[0]
              + "> mode <"
              + distance[distance.length / 2]
              + "> max <"
              + distance[distance.length - 1]
              + "> max2 <"
              + ((distance.length > 2) ? String.valueOf(distance[distance.length - 2]) : "n/a")
              + ">\n");

		r += ("  distance <" + totalAssignmentDistanceSinkless() + " sinkless or " + totalAssignmentDistance() + " including sinks>");
		return r;
	}

	public void setDefaultDistances(float d) {
		for (int x = 0; x < (sourceLength + sources); x++) {
			for (int y = 0; y < (targetLength + sinks); y++) {
				if ((x < sourceLength) && (y < targetLength)) {
					distanceMatrix.put((y * (sourceLength + sources)) + x, d);
				}
				if (x < sourceLength) {
					distanceMatrix.put((y * (sourceLength + sources)) + x, distanceToSource);
				} else if (y < targetLength) {
					distanceMatrix.put((y * (sourceLength + sources)) + x, distanceToSink);
				} else {
					distanceMatrix.put((y * (sourceLength + sources)) + x, sourceDistanceToSink);
				}
			}
		}
	}

	public void setDistance(int source, int target, float d) {
		distanceMatrix.put((target * (sourceLength + sources)) + source, d);
	}

	public void setSource(int num, t_element to) {
		assert num < sourceLength : num + " " + sourceLength;
//		assert num * 4 + 3 < sourceBuffer.capacity() : num + " " + sourceBuffer.capacity();
//		sourceBuffer.put(num * 4, to.x);
//		sourceBuffer.put(num * 4 + 1, to.y);
//		sourceBuffer.put(num * 4 + 2, to.z);
//		sourceBuffer.put(num * 4 + 3, 0);
		
		sourceBuffer[num]=to;
		
	}

	public void setSourceLength(int sourceLength) {
		this.sourceLength = sourceLength;
	}

	public void setSourcesAndSinks(int sources, int sinks, float distanceToSource, float distanceToSink, float sourceDistanceToSink) {
		this.sources = sources;
		this.sinks = sinks;
		this.distanceToSource = distanceToSource;
		this.distanceToSink = distanceToSink;
		this.sourceDistanceToSink = sourceDistanceToSink;
	}

	public void setTarget(int num, t_element to) {
		assert num < targetLength : num + " " + targetLength;
//		assert num * 4 + 3 < targetBuffer.capacity() : num + " " + targetBuffer.capacity();
//
//		targetBuffer.put(num * 4, to.x);
//		targetBuffer.put(num * 4 + 1, to.y);
//		targetBuffer.put(num * 4 + 2, to.z);
//		targetBuffer.put(num * 4 + 3, 0);
		targetBuffer[num] = to;
	}

	public void setTargetLength(int targetLength) {
		this.targetLength = targetLength;
	}

	public float totalAssignmentDistance() {
		float d = 0;
		for (int i = 0; i < (targetLength + sinks); i++) {
			int to = mapTargetToSource(i);
			if (to != -1) d += distanceMatrix.get((i * (sourceLength + sources)) + to);

		}
		return d;
	}

	public float totalAssignmentDistanceSinkless() {
		float d = 0;
		for (int i = 0; i < targetLength; i++) {
			int to = mapTargetToSource(i);
			if (to < sourceLength) {
				if (to != -1) {
					d += distanceMatrix.get((i * (sourceLength + sources)) + to);
				}
			}
		}
		return d;
	}
}