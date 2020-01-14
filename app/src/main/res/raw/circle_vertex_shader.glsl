attribute vec4 a_Position;
uniform mat4 u_Matrix;
uniform float u_Size;

void main(){
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = u_Size / gl_Position.w;
}