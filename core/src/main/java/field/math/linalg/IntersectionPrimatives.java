package field.math.linalg;

import field.core.dispatch.iVisualElement.Rect;
import field.namespace.generic.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author marc created on Jan 22, 2004
 */
public class IntersectionPrimatives {

	static public class LinePointIntersectionInfo {
		public Vector3 closestPoint = new Vector3();

		public Vector3 otherClosestPoint = new Vector3();

		public float distanceAlongLine = 0;

		public float intersectionDistance = 0;

		public float normalizedIntersectionDistance = 0;

		public float otherDistanceAlongLine;

		@Override
		public String toString() {
			return "intersection at <" + closestPoint + "> (" + otherClosestPoint + ")\n" + "  intersection distance =" + intersectionDistance + "\n  normalizedID =" + normalizedIntersectionDistance + "\n  distandalong line =" + distanceAlongLine;
		}
	}

	static public LinePointIntersectionInfo lineSegmentToPoint(Vector3 lineStart, Vector3 lineEnd, Vector3 point) {
		LinePointIntersectionInfo info = new LinePointIntersectionInfo();

		Vector3 to = new Vector3();
		Vector3 lineTangent = new Vector3().sub(lineEnd, lineStart);
		float mag = lineTangent.mag();
		lineTangent.normalize();
		to.sub(point, lineStart);

		info.distanceAlongLine = lineTangent.dot(to);

		if (info.distanceAlongLine < 0)
			info.distanceAlongLine = 0;
		if (info.distanceAlongLine > mag)
			info.distanceAlongLine = mag;

		Vector3.add(lineTangent, info.distanceAlongLine, lineStart, info.closestPoint);

		info.intersectionDistance = info.closestPoint.distanceFrom(point);
		info.normalizedIntersectionDistance = info.intersectionDistance / (lineStart.distanceFrom(point));
		return info;
	}

	static public LinePointIntersectionInfo lineToLine(Vector3 lineOrigin, Vector3 lineTangent, Vector3 lineFrom, Vector3 lineFromTangent) {
		LinePointIntersectionInfo info = new LinePointIntersectionInfo();

		Vector3 r1 = new Vector3(lineOrigin);
		Vector3 r2 = new Vector3(lineFrom);

		Vector3 t1 = new Vector3(lineTangent);
		Vector3 t2 = new Vector3(lineFromTangent);

		t1.normalize();
		t2.normalize();

		float r1r2 = r1.dot(r2);
		float t1t2 = t1.dot(t2);

		float r1t1 = r1.dot(t1);
		float r2t2 = r2.dot(t2);

		float r1t2 = r1.dot(t2);
		float r2t1 = r2.dot(t1);

		float d1 = (r2t1 + r1t2 * t1t2 - r2t2 * t1t2 - r1t1) / (1 - t1t2 * t1t2);
		float d2 = r1t2 + d1 * t1t2 - r2t2;

		Vector3.add(t1, d1, r1, info.closestPoint);
		Vector3.add(t2, d2, r2, info.otherClosestPoint);

		info.distanceAlongLine = d1;
		info.otherDistanceAlongLine = d2;
		info.intersectionDistance = info.otherClosestPoint.distanceFrom(info.closestPoint);
		info.normalizedIntersectionDistance = info.intersectionDistance / d2;

		return info;
	}

	static public LinePointIntersectionInfo lineToLineSegment(Vector3 lineOrigin, Vector3 lineTangent, Vector3 lineSegmentFrom, Vector3 lineSegmentFromTangent) {
		LinePointIntersectionInfo info = new LinePointIntersectionInfo();

		Vector3 r1 = new Vector3(lineOrigin);
		Vector3 r2 = new Vector3(lineSegmentFrom);

		Vector3 t1 = new Vector3(lineTangent);
		Vector3 t2 = new Vector3(lineSegmentFromTangent);

		float mm = t2.mag();

		t1.normalize();
		t2.normalize();

		float r1r2 = r1.dot(r2);
		float t1t2 = t1.dot(t2);

		float r1t1 = r1.dot(t1);
		float r2t2 = r2.dot(t2);

		float r1t2 = r1.dot(t2);
		float r2t1 = r2.dot(t1);

		float d1 = (r2t1 + r1t2 * t1t2 - r2t2 * t1t2 - r1t1) / (1 - t1t2 * t1t2);
		float d2 = r1t2 + d1 * t1t2 - r2t2;

		if (d2 > mm)
			d2 = mm;
		if (d2 < 0)
			d2 = 0;

		Vector3.add(t1, d1, r1, info.closestPoint);
		Vector3.add(t2, d2, r2, info.otherClosestPoint);

		info.distanceAlongLine = d1;
		info.otherDistanceAlongLine = d2;
		info.intersectionDistance = info.otherClosestPoint.distanceFrom(info.closestPoint);
		info.normalizedIntersectionDistance = info.intersectionDistance / d2;

		return info;
	}

	static public LinePointIntersectionInfo lineToLineSegment(Vector2 lineOrigin, Vector2 lineTangent, Vector2 lineSegmentFrom, Vector2 lineSegmentFromTangent) {
		LinePointIntersectionInfo info = new LinePointIntersectionInfo();

		Vector2 r1 = new Vector2(lineOrigin);
		Vector2 r2 = new Vector2(lineSegmentFrom);

		Vector2 t1 = new Vector2(lineTangent);
		Vector2 t2 = new Vector2(lineSegmentFromTangent);

		float mm = t2.mag();

		t1.normalize();
		t2.normalize();

		float r1r2 = r1.dot(r2);
		float t1t2 = t1.dot(t2);

		float r1t1 = r1.dot(t1);
		float r2t2 = r2.dot(t2);

		float r1t2 = r1.dot(t2);
		float r2t1 = r2.dot(t1);

		float d1 = (r2t1 + r1t2 * t1t2 - r2t2 * t1t2 - r1t1) / (1 - t1t2 * t1t2);
		float d2 = r1t2 + d1 * t1t2 - r2t2;

		if (d2 > mm)
			d2 = mm;
		if (d2 < 0)
			d2 = 0;

		Vector2 cp = new Vector2();
		Vector2.add(t1, d1, r1, cp);
		info.closestPoint = cp.toVector3();

		Vector2.add(t2, d2, r2, cp);
		info.otherClosestPoint = cp.toVector3();

		info.distanceAlongLine = d1;
		info.otherDistanceAlongLine = d2;
		info.intersectionDistance = info.otherClosestPoint.distanceFrom(info.closestPoint);
		info.normalizedIntersectionDistance = info.intersectionDistance / d2;

		return info;
	}

	static public LinePointIntersectionInfo lineToPlane(Vector3 lineOrigin, Vector3 lineTangent, Vector3 planeOrigin, Vector3 planeLeft, Vector3 planeUp) {
		Vector3 up = new Vector3(planeUp).sub(planeOrigin);
		Vector3 left = new Vector3(planeLeft).sub(planeOrigin);
		Vector3 normal = new Vector3().cross(left, up);

		normal.normalize();

		float lambda = (planeOrigin.dot(normal) - lineOrigin.dot(normal)) / lineTangent.dot(normal);
		if (Float.isNaN(lambda) || Float.isInfinite(lambda))
			return null;

		LinePointIntersectionInfo info = new LinePointIntersectionInfo();
		Vector3.add(lineTangent, lambda, lineOrigin, info.closestPoint);

		info.distanceAlongLine = lambda;
		return info;
	}

	static public LinePointIntersectionInfo lineToPlane(Vector3 lineOrigin, Vector3 lineTangent, Vector3 planeOrigin, Vector3 normal) {

		normal.normalize();

		float lambda = (planeOrigin.dot(normal) - lineOrigin.dot(normal)) / lineTangent.dot(normal);
		if (Float.isNaN(lambda) || Float.isInfinite(lambda))
			return null;

		LinePointIntersectionInfo info = new LinePointIntersectionInfo();
		Vector3.add(lineTangent, lambda, lineOrigin, info.closestPoint);

		info.distanceAlongLine = lambda;
		return info;
	}

	static public LinePointIntersectionInfo lineToPoint(Vector3 lineOrigin, Vector3 lineTangent, Vector3 point) {
		LinePointIntersectionInfo info = new LinePointIntersectionInfo();

		Vector3 to = new Vector3();
		to.sub(point, lineOrigin);

		info.distanceAlongLine = lineTangent.dot(to);

		Vector3.add(lineTangent, info.distanceAlongLine, lineOrigin, info.closestPoint);

		info.intersectionDistance = info.closestPoint.distanceFrom(point);
		info.normalizedIntersectionDistance = info.intersectionDistance / (lineOrigin.distanceFrom(point));
		return info;
	}

	static public Pair<Vector3, Vector3> triangleToTriangleToLineSegment(Vector3 v0, Vector3 v1, Vector3 v2, Vector3 u0, Vector3 u1, Vector3 u2) {
		Vector3 isectpt1 = new Vector3();
		Vector3 isectpt2 = new Vector3();

		Vector3 E1 = new Vector3();
		Vector3 E2 = new Vector3();
		Vector3 N1 = new Vector3();
		Vector3 N2 = new Vector3();
		Vector3 D = new Vector3();

		Vector2 isect1 = new Vector2();
		Vector2 isect2 = new Vector2();

		Vector3 isectpointA1 = new Vector3();
		Vector3 isectpointA2 = new Vector3();
		Vector3 isectpointB1 = new Vector3();
		Vector3 isectpointB2 = new Vector3();

		Vector3 diff = new Vector3();

		float d1, d2;
		float du0, du1, du2, dv0, dv1, dv2;
		float du0du1, du0du2, dv0dv1, dv0dv2;
		short index;
		float vp0, vp1, vp2;
		float up0, up1, up2;
		float b, c, max;
		float tmp;
		int smallest1, smallest2;

		/* compute plane equation of triangle(V0,V1,V2) */
		E1.sub(v1, v0);
		E2.sub(v2, v0);
		N1.cross(E1, E2);
		d1 = N1.dot(v0);
		/* plane equation 1: N1.X+d1=0 */

		/*
		 * put U0,U1,U2 into plane equation 1 to compute signed
		 * distances to the plane
		 */
		du0 = N1.dot(u0) + d1;
		du1 = N1.dot(u1) + d1;
		du2 = N1.dot(u2) + d1;

		/* coplanarity robustness check */
		if (Math.abs(du0) < 1e-10)
			du0 = 0.0f;
		if (Math.abs(du1) < 1e-10)
			du1 = 0.0f;
		if (Math.abs(du2) < 1e-10)
			du2 = 0.0f;

		du0du1 = du0 * du1;
		du0du2 = du0 * du2;

		if (du0du1 > 0.0f && du0du2 > 0.0f) /*
						 * same sign on all of them +
						 * not equal 0 ?
						 */
			return null; /*
				 * no intersection occurs
				 */

		/* compute plane of triangle (U0,U1,U2) */
		E1.sub(u1, u0);
		E2.sub(u2, u0);
		N2.cross(E1, E2);
		d2 = -N2.dot(u0);

		/* plane equation 2: N2.X+d2=0 */

		/* put V0,V1,V2 into plane equation 2 */
		dv0 = N2.dot(v0) + d2;
		dv1 = N2.dot(v1) + d2;
		dv2 = N2.dot(v2) + d2;

		if (Math.abs(dv0) < 1e-10)
			dv0 = 0.0f;
		if (Math.abs(dv1) < 1e-10)
			dv1 = 0.0f;
		if (Math.abs(dv2) < 1e-10)
			dv2 = 0.0f;

		dv0dv1 = dv0 * dv1;
		dv0dv2 = dv0 * dv2;

		if (dv0dv1 > 0.0f && dv0dv2 > 0.0f) /*
						 * same sign on all of them +
						 * not equal 0 ?
						 */
			return null; /*
				 * no intersection occurs
				 */

		/* compute direction of intersection line */
		D.cross(N1, N2);

		/* compute and index to the largest component of D */
		max = Math.abs(D.x);
		index = 0;
		b = Math.abs(D.y);
		c = Math.abs(D.z);
		if (b > max) {
			max = b;
			index = 1;
		}
		if (c > max) {
			max = c;
			index = 2;
		}

		/* this is the simplified projection onto L */
		vp0 = v0.get(index);
		vp1 = v1.get(index);
		vp2 = v2.get(index);

		up0 = u0.get(index);
		up1 = u1.get(index);
		up2 = u2.get(index);

		/* compute interval for triangle 1 */
		boolean coplanar = compute_intervals_isectline(v0, v1, v2, vp0, vp1, vp2, dv0, dv1, dv2, dv0dv1, dv0dv2, isect1, isectpointA1, isectpointA2);
		if (coplanar) {
			return null;
		}

		/* compute interval for triangle 2 */
		compute_intervals_isectline(u0, u1, u2, up0, up1, up2, du0, du1, du2, du0du1, du0du2, isect2, isectpointB1, isectpointB2);

		if (isect1.x > isect1.y) {
			float z = isect1.x;
			isect1.x = isect1.y;
			isect1.y = z;
			smallest1 = 1;
		} else
			smallest1 = 0;
		if (isect2.x > isect2.y) {
			float z = isect2.x;
			isect2.x = isect2.y;
			isect2.y = z;
			smallest2 = 1;
		} else
			smallest2 = 0;

		if (isect1.y < isect2.x || isect2.y < isect1.x) {
			return null;
		}

		/* at this point, we know that the triangles intersect */

		if (isect2.x < isect1.x) {
			if (smallest1 == 0) {
				isectpt1.setValue(isectpointA1);
			} else {
				isectpt1.setValue(isectpointA2);
			}

			if (isect2.y < isect1.y) {
				if (smallest2 == 0) {
					isectpt2.setValue(isectpointB2);
				} else {
					isectpt2.setValue(isectpointB1);
				}
			} else {
				if (smallest1 == 0) {
					isectpt2.setValue(isectpointA2);
				} else {
					isectpt2.setValue(isectpointA1);
				}
			}
		} else {
			if (smallest2 == 0) {
				isectpt1.setValue(isectpointB1);
			} else {
				isectpt1.setValue(isectpointB2);
			}

			if (isect2.x > isect1.y) {
				if (smallest1 == 0) {
					isectpt2.setValue(isectpointA2);
				} else {
					isectpt2.setValue(isectpointA1);
				}
			} else {
				if (smallest2 == 0) {
					isectpt2.setValue(isectpointB2);
				} else {
					isectpt2.setValue(isectpointB1);
				}
			}
		}
		return new Pair<Vector3, Vector3>(isectpt1, isectpt2);

	}

	private static boolean compute_intervals_isectline(Vector3 VERT0, Vector3 VERT1, Vector3 VERT2, float VV0, float VV1, float VV2, float D0, float D1, float D2, float D0D1, float D0D2, Vector2 isect0, Vector3 isectpoint0, Vector3 isectpoint1) {
		{
			if (D0D1 > 0.0f) {
				/* here we know that D0D2<=0.0 */
				/*
				 * that is D0, D1 are on the same side, D2 on
				 * the other or on the plane
				 */
				isect2(VERT2, VERT0, VERT1, VV2, VV0, VV1, D2, D0, D1, isect0, isectpoint0, isectpoint1);
			} else if (D0D2 > 0.0f) {
				/* here we know that d0d1<=0.0 */
				isect2(VERT1, VERT0, VERT2, VV1, VV0, VV2, D1, D0, D2, isect0, isectpoint0, isectpoint1);
			} else if (D1 * D2 > 0.0f || D0 != 0.0f) {
				/* here we know that d0d1<=0.0 or that D0!=0.0 */
				isect2(VERT0, VERT1, VERT2, VV0, VV1, VV2, D0, D1, D2, isect0, isectpoint0, isectpoint1);
			} else if (D1 != 0.0f) {
				isect2(VERT1, VERT0, VERT2, VV1, VV0, VV2, D1, D0, D2, isect0, isectpoint0, isectpoint1);
			} else if (D2 != 0.0f) {
				isect2(VERT2, VERT0, VERT1, VV2, VV0, VV1, D2, D0, D1, isect0, isectpoint0, isectpoint1);
			} else {
				return true;
			}
			return false;
		}
	}

	private static void isect2(Vector3 VTX0, Vector3 VTX1, Vector3 VTX2, float VV0, float VV1, float VV2, float D0, float D1, float D2, Vector2 isect0, Vector3 isectpoint0, Vector3 isectpoint1) {
		float tmp = D0 / (D0 - D1);
		Vector3 diff = new Vector3();
		isect0.x = VV0 + (VV1 - VV0) * tmp;

		diff.sub(VTX1, VTX0);
		diff.scale(tmp);
		isectpoint0.add(diff, VTX0);

		tmp = D0 / (D0 - D2);
		isect0.y = VV0 + (VV2 - VV0) * tmp;

		diff.sub(VTX2, VTX0);
		diff.scale(tmp);
		isectpoint1.add(VTX0, diff);

	}

	// the giant I'm standing on the shoulder of (without the aid of
	// operator overloading) here is David Eberly \u2014 geometrictools.com
	public static LinePointIntersectionInfo lineSegmentToLineSegment(Vector3 pkSegment0_Origin, Vector3 pkSegment0_Direction, float pkSegment0_Extent, Vector3 pkSegment1_Origin, Vector3 pkSegment1_Direction, float pkSegment1_Extent) {
		Vector3 kDiff = new Vector3().sub(pkSegment0_Origin, pkSegment1_Origin);
		float fA01 = -pkSegment0_Direction.dot(pkSegment1_Direction);
		float fB0 = kDiff.dot(pkSegment0_Direction);
		float fB1 = -kDiff.dot(pkSegment1_Direction);
		float fC = kDiff.magSquared();
		float fDet = Math.abs((float) 1.0 - fA01 * fA01);
		float fS0, fS1, fSqrDist, fExtDet0, fExtDet1, fTmpS0, fTmpS1;

		if (fDet >= 1e-10) {
			// segments are not parallel
			fS0 = fA01 * fB1 - fB0;
			fS1 = fA01 * fB0 - fB1;
			fExtDet0 = pkSegment0_Extent * fDet;
			fExtDet1 = pkSegment1_Extent * fDet;

			if (fS0 >= -fExtDet0) {
				if (fS0 <= fExtDet0) {
					if (fS1 >= -fExtDet1) {
						if (fS1 <= fExtDet1) // region 0
						// (interior)
						{
							// minimum at two
							// interior points of 3D
							// lines
							float fInvDet = ((float) 1.0) / fDet;
							fS0 *= fInvDet;
							fS1 *= fInvDet;
							fSqrDist = fS0 * (fS0 + fA01 * fS1 + ((float) 2.0) * fB0) + fS1 * (fA01 * fS0 + fS1 + ((float) 2.0) * fB1) + fC;
						} else // region 3 (side)
						{
							fS1 = pkSegment1_Extent;
							fTmpS0 = -(fA01 * fS1 + fB0);
							if (fTmpS0 < -pkSegment0_Extent) {
								fS0 = -pkSegment0_Extent;
								fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
							} else if (fTmpS0 <= pkSegment0_Extent) {
								fS0 = fTmpS0;
								fSqrDist = -fS0 * fS0 + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
							} else {
								fS0 = pkSegment0_Extent;
								fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
							}
						}
					} else // region 7 (side)
					{
						fS1 = -pkSegment1_Extent;
						fTmpS0 = -(fA01 * fS1 + fB0);
						if (fTmpS0 < -pkSegment0_Extent) {
							fS0 = -pkSegment0_Extent;
							fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						} else if (fTmpS0 <= pkSegment0_Extent) {
							fS0 = fTmpS0;
							fSqrDist = -fS0 * fS0 + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						} else {
							fS0 = pkSegment0_Extent;
							fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						}
					}
				} else {
					if (fS1 >= -fExtDet1) {
						if (fS1 <= fExtDet1) // region 1
						// (side)
						{
							fS0 = pkSegment0_Extent;
							fTmpS1 = -(fA01 * fS0 + fB1);
							if (fTmpS1 < -pkSegment1_Extent) {
								fS1 = -pkSegment1_Extent;
								fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							} else if (fTmpS1 <= pkSegment1_Extent) {
								fS1 = fTmpS1;
								fSqrDist = -fS1 * fS1 + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							} else {
								fS1 = pkSegment1_Extent;
								fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							}
						} else // region 2 (corner)
						{
							fS1 = pkSegment1_Extent;
							fTmpS0 = -(fA01 * fS1 + fB0);
							if (fTmpS0 < -pkSegment0_Extent) {
								fS0 = -pkSegment0_Extent;
								fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
							} else if (fTmpS0 <= pkSegment0_Extent) {
								fS0 = fTmpS0;
								fSqrDist = -fS0 * fS0 + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
							} else {
								fS0 = pkSegment0_Extent;
								fTmpS1 = -(fA01 * fS0 + fB1);
								if (fTmpS1 < -pkSegment1_Extent) {
									fS1 = -pkSegment1_Extent;
									fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
								} else if (fTmpS1 <= pkSegment1_Extent) {
									fS1 = fTmpS1;
									fSqrDist = -fS1 * fS1 + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
								} else {
									fS1 = pkSegment1_Extent;
									fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
								}
							}
						}
					} else // region 8 (corner)
					{
						fS1 = -pkSegment1_Extent;
						fTmpS0 = -(fA01 * fS1 + fB0);
						if (fTmpS0 < -pkSegment0_Extent) {
							fS0 = -pkSegment0_Extent;
							fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						} else if (fTmpS0 <= pkSegment0_Extent) {
							fS0 = fTmpS0;
							fSqrDist = -fS0 * fS0 + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						} else {
							fS0 = pkSegment0_Extent;
							fTmpS1 = -(fA01 * fS0 + fB1);
							if (fTmpS1 > pkSegment1_Extent) {
								fS1 = pkSegment1_Extent;
								fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							} else if (fTmpS1 >= -pkSegment1_Extent) {
								fS1 = fTmpS1;
								fSqrDist = -fS1 * fS1 + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							} else {
								fS1 = -pkSegment1_Extent;
								fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							}
						}
					}
				}
			} else {
				if (fS1 >= -fExtDet1) {
					if (fS1 <= fExtDet1) // region 5 (side)
					{
						fS0 = -pkSegment0_Extent;
						fTmpS1 = -(fA01 * fS0 + fB1);
						if (fTmpS1 < -pkSegment1_Extent) {
							fS1 = -pkSegment1_Extent;
							fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
						} else if (fTmpS1 <= pkSegment1_Extent) {
							fS1 = fTmpS1;
							fSqrDist = -fS1 * fS1 + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
						} else {
							fS1 = pkSegment1_Extent;
							fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
						}
					} else // region 4 (corner)
					{
						fS1 = pkSegment1_Extent;
						fTmpS0 = -(fA01 * fS1 + fB0);
						if (fTmpS0 > pkSegment0_Extent) {
							fS0 = pkSegment0_Extent;
							fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						} else if (fTmpS0 >= -pkSegment0_Extent) {
							fS0 = fTmpS0;
							fSqrDist = -fS0 * fS0 + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						} else {
							fS0 = -pkSegment0_Extent;
							fTmpS1 = -(fA01 * fS0 + fB1);
							if (fTmpS1 < -pkSegment1_Extent) {
								fS1 = -pkSegment1_Extent;
								fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							} else if (fTmpS1 <= pkSegment1_Extent) {
								fS1 = fTmpS1;
								fSqrDist = -fS1 * fS1 + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							} else {
								fS1 = pkSegment1_Extent;
								fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							}
						}
					}
				} else // region 6 (corner)
				{
					fS1 = -pkSegment1_Extent;
					fTmpS0 = -(fA01 * fS1 + fB0);
					if (fTmpS0 > pkSegment0_Extent) {
						fS0 = pkSegment0_Extent;
						fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
					} else if (fTmpS0 >= -pkSegment0_Extent) {
						fS0 = fTmpS0;
						fSqrDist = -fS0 * fS0 + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
					} else {
						fS0 = -pkSegment0_Extent;
						fTmpS1 = -(fA01 * fS0 + fB1);
						if (fTmpS1 < -pkSegment1_Extent) {
							fS1 = -pkSegment1_Extent;
							fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
						} else if (fTmpS1 <= pkSegment1_Extent) {
							fS1 = fTmpS1;
							fSqrDist = -fS1 * fS1 + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
						} else {
							fS1 = pkSegment1_Extent;
							fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
						}
					}
				}
			}
		} else {
			// The segments are parallel. The average b0 term is
			// designed to
			// ensure symmetry of the function. That is,
			// dist(seg0,seg1) and
			// dist(seg1,seg0) should produce the same number.
			float fE0pE1 = pkSegment0_Extent + pkSegment1_Extent;
			float fSign = (fA01 > (float) 0.0 ? (float) -1.0 : (float) 1.0);
			float fB0Avr = ((float) 0.5) * (fB0 - fSign * fB1);
			float fLambda = -fB0Avr;
			if (fLambda < -fE0pE1) {
				fLambda = -fE0pE1;
			} else if (fLambda > fE0pE1) {
				fLambda = fE0pE1;
			}

			fS1 = -fSign * fLambda * pkSegment1_Extent / fE0pE1;
			fS0 = fLambda + fSign * fS1;
			fSqrDist = fLambda * (fLambda + ((float) 2.0) * fB0Avr) + fC;
		}

		LinePointIntersectionInfo info = new LinePointIntersectionInfo();
		info.closestPoint = Vector3.add(pkSegment0_Direction, fS0, pkSegment0_Origin, null);
		info.otherClosestPoint = Vector3.add(pkSegment1_Direction, fS1, pkSegment1_Origin, null);
		info.distanceAlongLine = fS0;
		info.otherDistanceAlongLine = fS1;
		info.intersectionDistance = (float) Math.abs(Math.sqrt(fSqrDist));
		return info;
	}
	
	
	public static LinePointIntersectionInfo lineSegmentToLineSegment(Vector2 pkSegment0_Origin, Vector2 pkSegment0_Direction, float pkSegment0_Extent, Vector2 pkSegment1_Origin, Vector2 pkSegment1_Direction, float pkSegment1_Extent) {
		Vector2 kDiff = new Vector2().sub(pkSegment0_Origin, pkSegment1_Origin);
		float fA01 = -pkSegment0_Direction.dot(pkSegment1_Direction);
		float fB0 = kDiff.dot(pkSegment0_Direction);
		float fB1 = -kDiff.dot(pkSegment1_Direction);
		float fC = kDiff.lengthSquared();
		float fDet = Math.abs((float) 1.0 - fA01 * fA01);
		float fS0, fS1, fSqrDist, fExtDet0, fExtDet1, fTmpS0, fTmpS1;

		if (fDet >= 1e-10) {
			// segments are not parallel
			fS0 = fA01 * fB1 - fB0;
			fS1 = fA01 * fB0 - fB1;
			fExtDet0 = pkSegment0_Extent * fDet;
			fExtDet1 = pkSegment1_Extent * fDet;

			if (fS0 >= -fExtDet0) {
				if (fS0 <= fExtDet0) {
					if (fS1 >= -fExtDet1) {
						if (fS1 <= fExtDet1) // region 0
						// (interior)
						{
							// minimum at two
							// interior points of 3D
							// lines
							float fInvDet = ((float) 1.0) / fDet;
							fS0 *= fInvDet;
							fS1 *= fInvDet;
							fSqrDist = fS0 * (fS0 + fA01 * fS1 + ((float) 2.0) * fB0) + fS1 * (fA01 * fS0 + fS1 + ((float) 2.0) * fB1) + fC;
						} else // region 3 (side)
						{
							fS1 = pkSegment1_Extent;
							fTmpS0 = -(fA01 * fS1 + fB0);
							if (fTmpS0 < -pkSegment0_Extent) {
								fS0 = -pkSegment0_Extent;
								fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
							} else if (fTmpS0 <= pkSegment0_Extent) {
								fS0 = fTmpS0;
								fSqrDist = -fS0 * fS0 + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
							} else {
								fS0 = pkSegment0_Extent;
								fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
							}
						}
					} else // region 7 (side)
					{
						fS1 = -pkSegment1_Extent;
						fTmpS0 = -(fA01 * fS1 + fB0);
						if (fTmpS0 < -pkSegment0_Extent) {
							fS0 = -pkSegment0_Extent;
							fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						} else if (fTmpS0 <= pkSegment0_Extent) {
							fS0 = fTmpS0;
							fSqrDist = -fS0 * fS0 + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						} else {
							fS0 = pkSegment0_Extent;
							fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						}
					}
				} else {
					if (fS1 >= -fExtDet1) {
						if (fS1 <= fExtDet1) // region 1
						// (side)
						{
							fS0 = pkSegment0_Extent;
							fTmpS1 = -(fA01 * fS0 + fB1);
							if (fTmpS1 < -pkSegment1_Extent) {
								fS1 = -pkSegment1_Extent;
								fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							} else if (fTmpS1 <= pkSegment1_Extent) {
								fS1 = fTmpS1;
								fSqrDist = -fS1 * fS1 + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							} else {
								fS1 = pkSegment1_Extent;
								fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							}
						} else // region 2 (corner)
						{
							fS1 = pkSegment1_Extent;
							fTmpS0 = -(fA01 * fS1 + fB0);
							if (fTmpS0 < -pkSegment0_Extent) {
								fS0 = -pkSegment0_Extent;
								fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
							} else if (fTmpS0 <= pkSegment0_Extent) {
								fS0 = fTmpS0;
								fSqrDist = -fS0 * fS0 + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
							} else {
								fS0 = pkSegment0_Extent;
								fTmpS1 = -(fA01 * fS0 + fB1);
								if (fTmpS1 < -pkSegment1_Extent) {
									fS1 = -pkSegment1_Extent;
									fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
								} else if (fTmpS1 <= pkSegment1_Extent) {
									fS1 = fTmpS1;
									fSqrDist = -fS1 * fS1 + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
								} else {
									fS1 = pkSegment1_Extent;
									fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
								}
							}
						}
					} else // region 8 (corner)
					{
						fS1 = -pkSegment1_Extent;
						fTmpS0 = -(fA01 * fS1 + fB0);
						if (fTmpS0 < -pkSegment0_Extent) {
							fS0 = -pkSegment0_Extent;
							fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						} else if (fTmpS0 <= pkSegment0_Extent) {
							fS0 = fTmpS0;
							fSqrDist = -fS0 * fS0 + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						} else {
							fS0 = pkSegment0_Extent;
							fTmpS1 = -(fA01 * fS0 + fB1);
							if (fTmpS1 > pkSegment1_Extent) {
								fS1 = pkSegment1_Extent;
								fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							} else if (fTmpS1 >= -pkSegment1_Extent) {
								fS1 = fTmpS1;
								fSqrDist = -fS1 * fS1 + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							} else {
								fS1 = -pkSegment1_Extent;
								fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							}
						}
					}
				}
			} else {
				if (fS1 >= -fExtDet1) {
					if (fS1 <= fExtDet1) // region 5 (side)
					{
						fS0 = -pkSegment0_Extent;
						fTmpS1 = -(fA01 * fS0 + fB1);
						if (fTmpS1 < -pkSegment1_Extent) {
							fS1 = -pkSegment1_Extent;
							fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
						} else if (fTmpS1 <= pkSegment1_Extent) {
							fS1 = fTmpS1;
							fSqrDist = -fS1 * fS1 + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
						} else {
							fS1 = pkSegment1_Extent;
							fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
						}
					} else // region 4 (corner)
					{
						fS1 = pkSegment1_Extent;
						fTmpS0 = -(fA01 * fS1 + fB0);
						if (fTmpS0 > pkSegment0_Extent) {
							fS0 = pkSegment0_Extent;
							fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						} else if (fTmpS0 >= -pkSegment0_Extent) {
							fS0 = fTmpS0;
							fSqrDist = -fS0 * fS0 + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
						} else {
							fS0 = -pkSegment0_Extent;
							fTmpS1 = -(fA01 * fS0 + fB1);
							if (fTmpS1 < -pkSegment1_Extent) {
								fS1 = -pkSegment1_Extent;
								fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							} else if (fTmpS1 <= pkSegment1_Extent) {
								fS1 = fTmpS1;
								fSqrDist = -fS1 * fS1 + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							} else {
								fS1 = pkSegment1_Extent;
								fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
							}
						}
					}
				} else // region 6 (corner)
				{
					fS1 = -pkSegment1_Extent;
					fTmpS0 = -(fA01 * fS1 + fB0);
					if (fTmpS0 > pkSegment0_Extent) {
						fS0 = pkSegment0_Extent;
						fSqrDist = fS0 * (fS0 - ((float) 2.0) * fTmpS0) + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
					} else if (fTmpS0 >= -pkSegment0_Extent) {
						fS0 = fTmpS0;
						fSqrDist = -fS0 * fS0 + fS1 * (fS1 + ((float) 2.0) * fB1) + fC;
					} else {
						fS0 = -pkSegment0_Extent;
						fTmpS1 = -(fA01 * fS0 + fB1);
						if (fTmpS1 < -pkSegment1_Extent) {
							fS1 = -pkSegment1_Extent;
							fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
						} else if (fTmpS1 <= pkSegment1_Extent) {
							fS1 = fTmpS1;
							fSqrDist = -fS1 * fS1 + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
						} else {
							fS1 = pkSegment1_Extent;
							fSqrDist = fS1 * (fS1 - ((float) 2.0) * fTmpS1) + fS0 * (fS0 + ((float) 2.0) * fB0) + fC;
						}
					}
				}
			}
		} else {
			// The segments are parallel. The average b0 term is
			// designed to
			// ensure symmetry of the function. That is,
			// dist(seg0,seg1) and
			// dist(seg1,seg0) should produce the same number.
			float fE0pE1 = pkSegment0_Extent + pkSegment1_Extent;
			float fSign = (fA01 > (float) 0.0 ? (float) -1.0 : (float) 1.0);
			float fB0Avr = ((float) 0.5) * (fB0 - fSign * fB1);
			float fLambda = -fB0Avr;
			if (fLambda < -fE0pE1) {
				fLambda = -fE0pE1;
			} else if (fLambda > fE0pE1) {
				fLambda = fE0pE1;
			}

			fS1 = -fSign * fLambda * pkSegment1_Extent / fE0pE1;
			fS0 = fLambda + fSign * fS1;
			fSqrDist = fLambda * (fLambda + ((float) 2.0) * fB0Avr) + fC;
			
		}

		LinePointIntersectionInfo info = new LinePointIntersectionInfo();
		info.closestPoint = Vector2.add(pkSegment0_Direction, fS0, pkSegment0_Origin, null).toVector3();
		info.otherClosestPoint = Vector2.add(pkSegment1_Direction, fS1, pkSegment1_Origin, null).toVector3();
		info.distanceAlongLine = fS0;
		info.otherDistanceAlongLine = fS1;
		info.intersectionDistance = (float) Math.abs(Math.sqrt(fSqrDist));
		return info;
	}

	public static LinePointIntersectionInfo lineSegmentToPoint(Vector2 lineStart, Vector2 lineEnd, Vector2 point) {
		LinePointIntersectionInfo info = new LinePointIntersectionInfo();

		Vector2 to = new Vector2();
		Vector2 lineTangent = new Vector2().sub(lineEnd, lineStart);
		float mag = lineTangent.mag();
		lineTangent.normalize();
		to.sub(point, lineStart);

		info.distanceAlongLine = lineTangent.dot(to);

		if (info.distanceAlongLine < 0)
			info.distanceAlongLine = 0;
		if (info.distanceAlongLine > mag)
			info.distanceAlongLine = mag;

		Vector2 add = Vector2.add(lineTangent, info.distanceAlongLine, lineStart, null);
		info.closestPoint = add.toVector3();

		info.intersectionDistance = add.distanceFrom(point);
		info.normalizedIntersectionDistance = info.intersectionDistance / (lineStart.distanceFrom(point));
		return info;
	}

	public static LinePointIntersectionInfo lineToTriangle(Vector2 start, Vector2 dir, Vector2 a, Vector2 b, Vector2 c) {
		LinePointIntersectionInfo s1 = lineToLineSegment(start, dir, a, new Vector2().sub(b, a));
		LinePointIntersectionInfo s2 = lineToLineSegment(start, dir, a, new Vector2().sub(c, a));
		LinePointIntersectionInfo s3 = lineToLineSegment(start, dir, b, new Vector2().sub(c, b));
		if (s1.intersectionDistance < s2.intersectionDistance) {
			if (s3.intersectionDistance < s1.intersectionDistance) {
				return s3;
			} else {
				return s1;
			}
		} else {
			if (s3.intersectionDistance < s2.intersectionDistance) {
				return s3;
			} else {
				return s2;
			}
		}
	}

	public static LinePointIntersectionInfo lineToTriangle(Vector3 start, Vector3 dir, Vector3 a, Vector3 b, Vector3 c) {

		// Test if line intersects triangle. If so, the squared distance
		// is zero.
		Vector3 kEdge0 = new Vector3().sub(b, a);
		Vector3 kEdge1 = new Vector3().sub(c, a);
		Vector3 kNormal = new Vector3().cross(kEdge0, kEdge1).normalize();
		float fNdD = Math.abs(kNormal.dot(dir));

		if (fNdD > 1e-8) {
			// The line and triangle are not parallel, so the line
			// intersects
			// the plane of the triangle.
			Vector3 kDiff = new Vector3().sub(start, a);
            Vector3 rkD = dir;
            Vector3 kU = new Vector3();
			Vector3 kV = new Vector3();

			rkD.generateComplementBasis(kU, kV);
			float fUdE0 = kU.dot(kEdge0);
			float fUdE1 = kU.dot(kEdge1);
			float fUdDiff = kU.dot(kDiff);
			float fVdE0 = kV.dot(kEdge0);
			float fVdE1 = kV.dot(kEdge1);
			float fVdDiff = kV.dot(kDiff);
			float fInvDet = ((float) 1.0) / (fUdE0 * fVdE1 - fUdE1 * fVdE0);

			// Barycentric coordinates for the point of
			// intersection.
			float fB1 = (fVdE1 * fUdDiff - fUdE1 * fVdDiff) * fInvDet;
			float fB2 = (fUdE0 * fVdDiff - fVdE0 * fUdDiff) * fInvDet;
			float fB0 = (float) 1.0 - fB1 - fB2;

			if (fB0 >= (float) 0.0 && fB1 >= (float) 0.0 && fB2 >= (float) 0.0) {
				// ;//System.out.println(" is barycentric ");
				// Line parameter for the point of intersection.
				float fDdE0 = rkD.dot(kEdge0);
				float fDdE1 = rkD.dot(kEdge1);
				float fDdDiff = dir.dot(kDiff);
				float m_fLineParameter = fB1 * fDdE0 + fB2 * fDdE1 - fDdDiff;

				// Barycentric coordinates for the point of
				// intersection.
				// m_afTriangleBary[0] = fB0;
				// m_afTriangleBary[1] = fB1;
				// m_afTriangleBary[2] = fB2;

				// The intersection point is inside or on the
				// triangle.
				Vector3 m_kClosestPoint0 = Vector3.add(dir, m_fLineParameter, start, new Vector3());
				Vector3 m_kClosestPoint1 = Vector3.add(kEdge0, fB1, a, new Vector3()).add(kEdge1, fB2);

				LinePointIntersectionInfo p = new LinePointIntersectionInfo();
				p.closestPoint = m_kClosestPoint0;
				p.otherClosestPoint = m_kClosestPoint1;
				p.distanceAlongLine = m_fLineParameter;
				p.intersectionDistance = 0;

				return p;
			}
		}

		LinePointIntersectionInfo s1 = lineToLineSegment(start, dir, a, new Vector3().sub(b, a));
		LinePointIntersectionInfo s2 = lineToLineSegment(start, dir, a, new Vector3().sub(c, a));
		LinePointIntersectionInfo s3 = lineToLineSegment(start, dir, b, new Vector3().sub(c, b));

		//
		// projectOntoPlane(s1.closestPoint, kNormal, a);
		// projectOntoPlane(s2.closestPoint, kNormal, a);
		// projectOntoPlane(s3.closestPoint, kNormal, a);
		// s1.intersectionDistance =
		// s1.closestPoint.distanceFrom(s1.otherClosestPoint);
		// s2.intersectionDistance =
		// s2.closestPoint.distanceFrom(s2.otherClosestPoint);
		// s3.intersectionDistance =
		// s3.closestPoint.distanceFrom(s3.otherClosestPoint);
		//
		if (s1.intersectionDistance < s2.intersectionDistance) {
			if (s3.intersectionDistance < s1.intersectionDistance) {
				return s3;
			} else {
				return s1;
			}
		} else {
			if (s3.intersectionDistance < s2.intersectionDistance) {
				return s3;
			} else {
				return s2;
			}
		}

	}

	private static void projectOntoPlane(Vector3 o, Vector3 normal, Vector3 z) {
		float c = z.dot(normal);

		float c2 = o.dot(normal);

		o.add(normal, c - c2);
	}

	static public Vector4 toQuadBarycentric(Vector3 v0, Vector3 v1, Vector3 v2, Vector3 v3, Vector3 v, Vector3 normal) {
		Vector3 s0 = new Vector3().sub(v0, v);
		Vector3 s1 = new Vector3().sub(v1, v);
		Vector3 s2 = new Vector3().sub(v2, v);
		Vector3 s3 = new Vector3().sub(v3, v);

		if (s0.mag() < 1e-6)
			return new Vector4(1, 0, 0, 0);
		if (s1.mag() < 1e-6)
			return new Vector4(0, 1, 0, 0);
		if (s2.mag() < 1e-6)
			return new Vector4(0, 0, 1, 0);
		if (s3.mag() < 1e-6)
			return new Vector4(0, 0, 0, 1);

		Vector3 a0 = new Vector3().cross(s0, s1);
		Vector3 a1 = new Vector3().cross(s1, s2);
		Vector3 a2 = new Vector3().cross(s2, s3);
		Vector3 a3 = new Vector3().cross(s3, s0);

		float d0 = s0.dot(s1);
		float d1 = s1.dot(s2);
		float d2 = s2.dot(s3);
		float d3 = s3.dot(s0);

		float r0 = s0.mag();
		float r1 = s1.mag();
		float r2 = s2.mag();
		float r3 = s3.mag();

		float sa0 = a0.mag() * Math.signum(normal.dot(a0));
		float sa1 = a1.mag() * Math.signum(normal.dot(a1));
		float sa2 = a2.mag() * Math.signum(normal.dot(a2));
		float sa3 = a3.mag() * Math.signum(normal.dot(a3));

		float t0 = (r0 * r1 - d0) * sa1 * sa2 * sa3;
		float t1 = (r1 * r2 - d1) * sa0 * sa2 * sa3;
		float t2 = (r2 * r3 - d2) * sa0 * sa1 * sa3;
		float t3 = (r3 * r0 - d3) * sa0 * sa1 * sa2;

		float m0 = (t3 + t0) * r1 * r2 * r3;
		float m1 = (t0 + t1) * r0 * r2 * r3;
		float m2 = (t1 + t2) * r0 * r1 * r3;
		float m3 = (t2 + t3) * r0 * r1 * r2;

		float s = m0 + m1 + m2 + m3;

		// ;//System.out.println(d0+" "+d1+" "+d2+" "+d3+"\n"+r0+" "+r1+" "+r2+" "+r3+"\n"+t0+" "+t1+" "+t2+" "+t3+"\n"+m0+" "+m1+" "+m2+" "+m3+"\n"+s);

		// can happen for the occasional point outside the quad
		if (s == 0)
			return null;

		return new Vector4(m0 / s, m1 / s, m2 / s, m3 / s);
	}

	static private boolean SAME_SIGNS(double a, double b) {
		return Math.signum(a) == Math.signum(b);
	}

	public static Vector2 lineToRect(final Vector2 a, Vector2 b, Rect r) {
		Vector2 a1 = lines_intersect(a.x, a.y, b.x, b.y, r.x, r.y, r.x + r.w, r.y);
		Vector2 a2 = lines_intersect(a.x, a.y, b.x, b.y, r.x, r.y, r.x, r.y + r.h);
		Vector2 a3 = lines_intersect(a.x, a.y, b.x, b.y, r.x + r.w, r.y, r.x + r.w, r.y + r.h);
		Vector2 a4 = lines_intersect(a.x, a.y, b.x, b.y, r.x, r.y + r.h, r.x + r.w, r.y + r.h);

		List<Vector2> aa = new ArrayList<Vector2>();
		if (a1 != null)
			aa.add(a1);
		if (a2 != null)
			aa.add(a2);
		if (a3 != null)
			aa.add(a3);
		if (a4 != null)
			aa.add(a4);

		if (aa.size() == 0)
			return null;

		Collections.sort(aa, new Comparator<Vector2>() {

			@Override
			public int compare(Vector2 o1, Vector2 o2) {
				return Double.compare(o1.distanceFrom(a), o2.distanceFrom(a));
			}
		});

        //System.out.println(" sorted: " + aa);

		return aa.get(0);

	}

	static public Vector2 lines_intersect(double x1, double y1, /*
								 * First line
								 * segment
								 */
			double x2, double y2,

			double x3, double y3, /* Second line segment */
			double x4, double y4

	) {
		double a1, a2, b1, b2, c1, c2; /* Coefficients of line eqns. */
		double r1, r2, r3, r4; /* 'Sign' values */
		double denom, offset, num; /* doubleermediate values */

		/*
		 * Compute a1, b1, c1, where line joining podoubles 1 and 2 is
		 * "a1 x  +  b1 y  +  c1  =  0".
		 */

		a1 = y2 - y1;
		b1 = x1 - x2;
		c1 = x2 * y1 - x1 * y2;

		/*
		 * Compute r3 and r4.
		 */

		r3 = a1 * x3 + b1 * y3 + c1;
		r4 = a1 * x4 + b1 * y4 + c1;

		/*
		 * Check signs of r3 and r4. If both podouble 3 and podouble 4
		 * lie on same side of line 1, the line segments do not
		 * doubleersect.
		 */

		if (r3 != 0 && r4 != 0 && SAME_SIGNS(r3, r4))
			return null;

		/* Compute a2, b2, c2 */

		a2 = y4 - y3;
		b2 = x3 - x4;
		c2 = x4 * y3 - x3 * y4;

		/* Compute r1 and r2 */

		r1 = a2 * x1 + b2 * y1 + c2;
		r2 = a2 * x2 + b2 * y2 + c2;

		/*
		 * Check signs of r1 and r2. If both podouble 1 and podouble 2
		 * lie on same side of second line segment, the line segments do
		 * not doubleersect.
		 */

		if (r1 != 0 && r2 != 0 && SAME_SIGNS(r1, r2))
			return null;

		/*
		 * Line segments doubleersect: compute doubleersection podouble.
		 */

		denom = a1 * b2 - a2 * b1;
		if (denom == 0)
			return null;
		offset = denom < 0 ? -denom / 2 : denom / 2;

		/*
		 * The denom/2 is to get rounding instead of truncating. It is
		 * added or subtracted to the numerator, depending upon the sign
		 * of the numerator.
		 */

		num = b1 * c2 - b2 * c1;
		double xx = (num < 0 ? num - offset : num + offset) / denom;

		num = a2 * c1 - a1 * c2;
		double yy = (num < 0 ? num - offset : num + offset) / denom;

		return new Vector2(xx, yy);
	} /* lines_doubleersect */

	

}
