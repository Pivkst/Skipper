attribute vec4 a_Position;

uniform float u_Size;
uniform mat4 u_Matrix;

void main(){
    gl_PointSize = u_Size;
    gl_Position = u_Matrix * a_Position;
}