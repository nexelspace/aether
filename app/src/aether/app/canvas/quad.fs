#version 300 es

precision highp float;
precision highp int;
precision lowp usampler2D;

uniform vec2 iResolution;
uniform float iTime;
uniform int iFrame;

uniform usampler2D iQuadS;
uniform int iQuadSize;

uniform int iIterations;

uniform vec4 iViewport;
uniform mat3 iTransform;

in vec2 vScreenCoord;
in vec2 vPixelCoord;

out vec4 oColorOut;

vec4 toColor(uint id) {
  return vec4(ivec4(id>>24, id>>16, id>>8, id) & 0xff)/255.0;
}

uint data(uint ptr) {
  ivec2 p = ivec2(ptr & (uint(iQuadSize)-1u), ptr / uint(iQuadSize));
  uvec4 d = texelFetch(iQuadS, p, 0);
  return uint((d.r << 24u) | (d.g << 16u) | (d.b << 8u) | d.a);
}

vec4 renderQuad(in vec2 coord) {

  uint ptr = 0u;
  uint id;
  if (coord.x < -1.0 || coord.x >= 1.0 || coord.y < -1.0 || coord.y >= 1.0) discard;
  vec2 pos = coord;
  for (int i = 0; i < iIterations; ++i) {
    id = data(ptr);
  	if (id != 0x100u) break;
  	uint offset = uint(step(0.f, pos.x)) + uint(step(0.f, pos.y)) * 2u;
  	ptr = offset==0u ? ptr + 4u : data(ptr + offset);
  	pos = pos * 2.0f - (step(0.f, pos)* 2.f - 1.f);
  }

  return toColor(uint(id));
}

void main() {
  vec2 size = iViewport.zw;
  mat3 trans = inverse(iTransform);
  vec2 p = (trans * vec3(vPixelCoord, 1)).xy;
  oColorOut = renderQuad(p);
}
