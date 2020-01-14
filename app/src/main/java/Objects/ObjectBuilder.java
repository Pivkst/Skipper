package Objects;

import com.example.skipper.Geometry;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

public class ObjectBuilder {
    private static final int FLOATS_PER_VERTEX = 3;
    private final float[] vertexData;
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();
    private int offset = 0;

    interface DrawCommand {
        void draw();
    }

    class GeneratedData{
        final float[] vertexData;
        final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList){
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    private  ObjectBuilder(int sizeInVertices){
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    private static int sizeOfCircleInVertices(int numPoints){
        return 1 + (numPoints + 1);
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints){
        return (numPoints + 1) * 2;
    }

    private GeneratedData build(){
        return new GeneratedData(vertexData, drawList);
    }

    private void appendCircle(Geometry.Circle circle, int numPoints){
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        //Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        //Top fan
        for(int i = 0; i <= numPoints; i++){
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            vertexData[offset++] = circle.center.x + circle.radius * (float) Math.cos(angleInRadians);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = circle.center.z + circle.radius * (float) Math.sin(angleInRadians);
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    private void appendOpenCylinder(Geometry.Cylinder cylinder, int numPoints){
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float yStart = cylinder.center.y - (cylinder.height/2f);
        final float yEnd = cylinder.center.y + (cylinder.height/2f);
        for(int i=0; i<=numPoints; i++){
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            float xPosition = cylinder.center.x + cylinder.radius * (float)Math.cos(angleInRadians);
            float zPosition = cylinder.center.x - cylinder.radius * (float)Math.sin(angleInRadians);

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints){
        int size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints);

        ObjectBuilder builder = new ObjectBuilder(size);
        Geometry.Circle puckTop = new Geometry.Circle(puck.center.translateY(puck.height/2f), puck.radius);

        builder.appendOpenCylinder(puck, numPoints);
        builder.appendCircle(puckTop, numPoints);

        return builder.build();
    }

    static GeneratedData createMallet(Geometry.Point center, float radius, float height, int numPoints){
        int size = sizeOfCircleInVertices(numPoints) * 2 + sizeOfOpenCylinderInVertices(numPoints) * 2;
        ObjectBuilder builder = new ObjectBuilder(size);
        //Mallet base
        float baseHeight = height*0.25f;
        Geometry.Circle baseCircle = new Geometry.Circle(
                center.translateY(baseHeight),
                radius);
        Geometry.Cylinder baseCylinder = new Geometry.Cylinder(
                baseCircle.center.translateY(-baseHeight/2f),
                radius,
                baseHeight);
        builder.appendOpenCylinder(baseCylinder, numPoints);
        builder.appendCircle(baseCircle, numPoints);
        //Mallet handle
        float handleHeight = height*0.75f;
        float handleRadius = radius/2f;
        Geometry.Circle handleCircle = new Geometry.Circle(
                center.translateY(height),
                handleRadius);
        Geometry.Cylinder handleCylinder = new Geometry.Cylinder(
                handleCircle.center.translateY(-handleHeight/2f),
                handleRadius,
                handleHeight);
        builder.appendOpenCylinder(handleCylinder, numPoints);
        builder.appendCircle(handleCircle, numPoints);

        return builder.build();
    }
}
