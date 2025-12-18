package com.winlator.cmod.renderer.effects;

import com.winlator.cmod.renderer.material.ScreenMaterial;
import com.winlator.cmod.renderer.material.ShaderMaterial;

public class FakeReflectionsEffect extends Effect {
    // Constructor
    public FakeReflectionsEffect() {
        super(); // Calls the constructor of the superclass Effect
    }

    // Creates and returns the ShaderMaterial for this effect
    @Override
    protected ShaderMaterial createMaterial() {
        // Returns an instance of the inner class which extends ScreenMaterial and implements the FakeReflections shader
        return new FakeReflectionsMaterial();
    }

    // Inner class implementing the FXAA shader material
    private class FakeReflectionsMaterial extends ScreenMaterial {
        // Constructor for the inner class, calls the superclass constructor
        public FakeReflectionsMaterial() {
            super();
        }

        @Override
        protected String getFragmentShader() {
            // Returns the GLSL fragment shader as a string.
            return String.join("\n", new CharSequence[]{
                    "precision highp float;",
                    "uniform sampler2D screenTexture;",
                    "uniform vec2 resolution;",
                    "void main() {",
                    "    vec2 params = vec2(0.5, 0.5);",
                    "    vec2 uv = gl_FragCoord.xy / resolution;",
                    "    gl_FragColor = texture2D(screenTexture, uv);",
                    "    vec3 color = gl_FragColor.rgb;",
                    "    float gray = (color.r + color.g + color.b) / 3.0;",
                    "    float saturation = (abs(color.r - gray) + abs(color.g - gray) + abs(color.b - gray)) / 3.0;",
                    "    float scale = 1.5;",
                    "    float rndx = mod(uv.x + gray, 0.03 * scale) + mod(uv.y + saturation, 0.05 * scale);",
                    "    float rndy = mod(uv.y + saturation, 0.03 * scale) + mod(uv.x + gray, 0.05 * scale);",
                    "    float step = (max(gray, saturation) + 0.1) * (1.0 - uv.y);",
                    "    vec3 reflection = texture2D(screenTexture, uv + vec2(rndx, rndy + min((1.0 - uv.y), 0.25) * scale) * step).rgb;",
                    "    reflection *= 4.0 * (1.0 - gray) * params.x;",
                    "    reflection *= reflection * step * params.y;",
                    "    gl_FragColor.rgb += reflection;",
                    "}"
            });
        }
    }
}
