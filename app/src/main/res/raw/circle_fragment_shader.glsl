precision mediump float;

uniform vec4 u_Color;
uniform float u_Size;

void main(){
    vec2 coord = gl_PointCoord - vec2(0.5);
    if(length(coord) > 0.5) discard;
    gl_FragColor = u_Color;
}