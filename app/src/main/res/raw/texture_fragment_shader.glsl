precision mediump float;

uniform sampler2D u_TextureUnit;
varying vec2 v_TextureCoordinates;

void main() {
    //vec2 coord = vec2(gl_FragCoord) - vec2(0.5);
    //if(length(coord) > 0.5) discard;
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}
