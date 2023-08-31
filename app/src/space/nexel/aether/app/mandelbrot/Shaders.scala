package space.nexel.aether.app.mandelbrot

object Shaders {

  val vertex =
    """#version 300 es
precision highp float;
precision highp int;
in vec4 a_position;
out vec2 v_coord;

void main(void)
{
  gl_Position = a_position;
  v_coord = a_position.xy * vec2(1,-1);
}"""

  val fragment =
    """#version 300 es
precision highp float;
precision highp int;

uniform vec2 center;
uniform float scale;
uniform int iter;

in vec2 v_coord;
out vec4 o_color;

float hue2rgb(float f1, float f2, float hue) {
    if (hue < 0.0)
        hue += 1.0;
    else if (hue > 1.0)
        hue -= 1.0;
    float res;
    if ((6.0 * hue) < 1.0)
        res = f1 + (f2 - f1) * 6.0 * hue;
    else if ((2.0 * hue) < 1.0)
        res = f2;
    else if ((3.0 * hue) < 2.0)
        res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;
    else
        res = f1;
    return res;
}

vec3 hsl2rgb(vec3 hsl) {
    vec3 rgb;

    if (hsl.y == 0.0) {
        rgb = vec3(hsl.z); // Luminance
    } else {
        float f2;

        if (hsl.z < 0.5)
            f2 = hsl.z * (1.0 + hsl.y);
        else
            f2 = hsl.z + hsl.y - hsl.y * hsl.z;

        float f1 = 2.0 * hsl.z - f2;

        rgb.r = hue2rgb(f1, f2, mod(hsl.x + (1.0/3.0), 1.0));
        rgb.g = hue2rgb(f1, f2, mod(hsl.x, 1.0));
        rgb.b = hue2rgb(f1, f2, mod(hsl.x - (1.0/3.0), 1.0));
    }
    return rgb;
}

void main() {
    vec2 c = v_coord * scale + center;
    vec2 z = c;
    vec2 z1;
    int i;
    for(i = 0; z.x*z.x+z.y*z.y <= 4.0 && i < iter; i++) {
        z1.x = z.x * z.x - z.y * z.y + c.x;
        z1.y = z.y * z.x + z.x * z.y + c.y;
        z = z1;
    }
    float col = float(i) / float(iter);
    float ce = 1.0 - exp(-col * 20.0);
    o_color.rgb = hsl2rgb(vec3(col * 10.0, 1.0, i == iter ? 0.0 : 0.5));
    o_color.a = 1.0;
}
"""


}
