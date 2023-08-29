#version 300 es
precision highp float;
precision highp int;
in vec4 a_position;
out vec2 v_coord;

void main(void) {
  gl_Position = a_position;
  v_coord = a_position.xy * vec2(1, -1);
}
