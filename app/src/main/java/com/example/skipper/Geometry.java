package com.example.skipper;

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
        public Point translate(Vector vector){
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
        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    public static class Ray{
        public final Point point;
        public final Vector vector;

        public Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }
    }

    public static class Vector{
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float length(){
            return (float) Math.sqrt(x*x + y*y + z*z);
        }

        public Vector crossProduct(Vector otherVector){
            return new Vector(
                    (y*otherVector.z) - (z*otherVector.y),
                    (z*otherVector.x) - (x*otherVector.z),
                    (x*otherVector.y) - (y*otherVector.x)
            );
        }

        public float dotProduct(Vector other){
            return x * other.x + y * other.y + z * other.z;
        }

        public Vector scale(float f){
            return new Vector(x*f, y*f, z*f);
        }
    }

    public static Vector vectorBetween(Point from, Point to){
        return new Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }

    public static boolean intersects(Sphere sphere, Ray ray){
        float distance = distanceBetween(sphere.center, ray);
        return  distance < sphere.radius;
    }

    public static float distanceBetween(Point point, Ray ray){
        //Basically math magic that I could not give less of a shit about right now
        // see: http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point);
        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();
        return areaOfTriangleTimesTwo / lengthOfBase;
    }

    public static class Plane{
        public final Point point;
        public final Vector normal;

        public Plane(Point point, Vector normal) {
            this.point = point;
            this.normal = normal;
        }
    }

    public static Point intersectionPoint(Ray ray, Plane plane){
        // https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection
        Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);
        float scaleFactor = rayToPlaneVector.dotProduct(plane.normal) / ray.vector.dotProduct(plane.normal);
        Point interSectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));
        return interSectionPoint;
    }
}
