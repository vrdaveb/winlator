package com.winlator.cmod.renderer.material;

public class BacklightMaterial extends ShaderMaterial {
    public BacklightMaterial() {
        setUniformNames("xform", "viewSize", "texture");
    }

    @Override
    protected String getVertexShader() {
        return
            "uniform float xform[6];\n" +
            "uniform vec2 viewSize;\n" +
            "attribute vec2 position;\n" +
            "varying vec2 vUV;\n" +

            "void main() {\n" +
                "vUV = position;\n" +
                "vec2 transformedPos = applyXForm(position, xform);\n" +
                "gl_Position = vec4(2.0 * transformedPos.x / viewSize.x - 1.0, 1.0 - 2.0 * transformedPos.y / viewSize.y, 0.0, 1.0);\n" +
            "}"
        ;
    }

    @Override
    protected String getFragmentShader() {
        return
            "precision mediump float;\n" +

            "uniform sampler2D texture;\n" +
            "varying vec2 vUV;\n" +


            "vec3 getcolor(float i, float j, float step) {\n" +
                "float count = 0.0;\n" +
                "float samples = 4.0;\n" +
                "vec3 color = vec3(0.0, 0.0, 0.0);\n" +
                "vec2 o = vec2(i * step, j * step);\n" +
                "vec2 m = vec2(mod(vUV.x, step), mod(vUV.y, step));\n" +
                "for (float x = -step; x <= step; x += step / samples) {\n" +
                    "color += texture2D(texture, vUV + o - m + vec2(x, 0.0)).rgb;\n" +
                    "count += 1.0;\n" +
                "}\n" +
                "for (float y = -step; y <= step; y += step / samples) {\n" +
                    "color += texture2D(texture, vUV + o - m + vec2(0.0, y)).rgb;\n" +
                    "count += 1.0;\n" +
                "}\n" +
                "return color / count;\n" +
            "}\n" +

            "void main() {\n" +
                "float step = 0.05;\n" +
                "vec2 m = vec2(mod(vUV.x, step), mod(vUV.y, step));\n" +
                "vec3 a = mix(getcolor(0.0, 0.0, step), getcolor(1.0, 0.0, step), m.x / step);\n" +
                "vec3 b = mix(getcolor(0.0, 1.0, step), getcolor(1.0, 1.0, step), m.x / step);\n" +
                "gl_FragColor.rgb = mix(a, b, m.y / step);\n" +
                "gl_FragColor.a = 2.0 - 4.0 * length(vUV - vec2(0.5, 0.5));\n" +
            "}"
        ;
    }
}
