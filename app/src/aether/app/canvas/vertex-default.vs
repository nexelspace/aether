#version 300 es
// precision highp float;
// precision highp int;

uniform vec2 iResolution;
uniform vec4 iViewport;

in vec4 a_position;
in vec4 a_texCoord;

out vec2 vScreenCoord;
out vec2 vPixelCoord;

vec2 viewport(vec2 vp) {
  vec2 v = vp / iResolution * 2.f - 1.f;
  return vec2(v.x, -v.y); // flip y, origo in top-left
} 

void main() {
  vec2 v0 = viewport(iViewport.xy);     // top-left
  vec2 v1 = viewport(iViewport.zw);     // bottom-right
  vec2 f = (a_position.xy + 1.f) / 2.f; // fraction
  vec2 p = mix(v0, v1, f);
  gl_Position.xy = p;
  gl_Position.w = 1.f;
  vScreenCoord = f;
  vPixelCoord = f * iViewport.zw;
}
