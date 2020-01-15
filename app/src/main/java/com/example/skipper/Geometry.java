package com.example.skipper;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

public class Geometry {
    public static class Point{
        public final float x, y, z;
        public Point(float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point translateY(float distance){
            return new Point(x, y+distance, z);
        }
        Point translate(Vector vector){
            return new Point(
                    x + vector.x,
                    y + vector.y,
                    z + vector.z
            );
        }
    }

    public static class Circle {
        public final Point center;
        public final float radius;

        public Circle(Point center, float radius){
            this.center = center;
            this.radius = radius;
        }

        @SuppressWarnings("unused")
        public Circle scale(float scale){
            return new Circle(center, radius*scale);
        }
    }

    public static class Cylinder{
        public final Point center;
        public final float radius;
        public final float height;

        public Cylinder(Point center, float radius, float height){
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }

    public static class Sphere{
        final Point center;
        final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    static class Ray{
        final Point point;
        final Vector vector;

        Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }
    }

    static class Vector{
        final float x, y, z;

        Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @NotNull
        @Override
        public String toString(){
            return "x: "+x+" y: "+y+" z: "+z+" length:" +length();
        }

        float length(){
            return (float) Math.sqrt(x*x + y*y + z*z);
        }

        Vector crossProduct(Vector otherVector){
            return new Vector(
                    (y*otherVector.z) - (z*otherVector.y),
                    (z*otherVector.x) - (x*otherVector.z),
                    (x*otherVector.y) - (y*otherVector.x)
            );
        }

        float dot(Vector other){
            return x * other.x + y * other.y + z * other.z;
        }

        Vector scale(float f){
            return new Vector(x*f, y*f, z*f);
        }

        Vector add(Vector a){
            return new Vector(x+a.x, y+a.y, z+a.z);
        }

        private Float scalarProjection(Vector b){
            return this.dot(b) / b.length();
        }

        private Vector vectorProjection(Vector b){
            return b.scale((this.dot(b) / b.dot(b)));
        }

        Vector rebound(Vector n){
            //Rebounds this vector off a plane with a normal of n if it's pointing towards it
            //If the projection of this on the normal and the normal are not codirectional(if the
            // the scalar projection of this onto b is negative)
            // https://en.wikipedia.org/wiki/Vector_projection
            if(scalarProjection(n) < 0){
                //Reverse the component of this vector which is colinear with b
                Log.v("REBOUND", "Occurred");
                return this.add(vectorProjection(n).scale(-2));
            } else{
                Log.v("REBOUND", "Ignored");
                return this;
            }
        }
    }

    static Vector vectorBetween(Point from, Point to){
        return new Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }

    static void divideByW(float[] vector){
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    @SuppressWarnings("unused")
    public static boolean intersects(Sphere sphere, Ray ray){
        float distance = distanceBetween(sphere.center, ray);
        return  distance < sphere.radius;
    }

    private static float distanceBetween(Point point, Ray ray){
        //Basically math magic that I could not give less of a shit about right now
        // see: http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point);
        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();
        return areaOfTriangleTimesTwo / lengthOfBase;
    }

    static class Plane{
        final Point point;
        final Vector normal;

        Plane(Point point, Vector normal) {
            this.point = point;
            this.normal = normal;
        }
    }

    static Point intersectionPoint(Ray ray, Plane plane){
        // https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection
        Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);
        float scaleFactor = rayToPlaneVector.dot(plane.normal) / ray.vector.dot(plane.normal);
        return ray.point.translate(ray.vector.scale(scaleFactor));
    }
}
