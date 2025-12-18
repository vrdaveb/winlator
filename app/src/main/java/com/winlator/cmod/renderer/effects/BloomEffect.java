package com.winlator.cmod.renderer.effects;

import com.winlator.cmod.renderer.material.ScreenMaterial;
import com.winlator.cmod.renderer.material.ShaderMaterial;

public class BloomEffect extends Effect {
    // Constructor
    public BloomEffect() {
        super(); // Calls the constructor of the superclass Effect
    }

    // Creates and returns the ShaderMaterial for this effect
    @Override
    protected ShaderMaterial createMaterial() {
        // Returns an instance of the inner class which extends ScreenMaterial and implements the Bloom shader
        return new BloomMaterial();
    }

    // Inner class implementing the Bloom shader material
    private class BloomMaterial extends ScreenMaterial {
        // Constructor for the inner class, calls the superclass constructor
        public BloomMaterial() {
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
                    "    vec2 invResolution = 1.0 / resolution;",
                    "    gl_FragColor = texture2D(screenTexture, gl_FragCoord.xy * invResolution);",
                    "    vec3 color = gl_FragColor.rgb;",
                    "    float gray = (color.r + color.g + color.b) / 3.0;",
                    "    float saturation = (abs(color.r - gray) + abs(color.g - gray) + abs(color.b - gray)) / 3.0;",
                    "    float step = 0.001 * gray / max(saturation, 0.25) * params.x;",
                    "    vec3 sum = vec3(0);",
                    "    for (int x = -3; x <= 3; x += 2) {",
                    "        for (int y = -3; y <= 3; y += 2) {",
                    "             color = texture2D(screenTexture, gl_FragCoord.xy * invResolution + vec2(x, y)*step).xyz;",
                    "             gray = (color.r + color.g + color.b) / 3.0;",
                    "             saturation = (abs(color.r - gray) + abs(color.g - gray) + abs(color.b - gray)) / 3.0;",
                    "             sum += color * gray * gray / max(saturation, 0.25);",
                    "        }",
                    "    }",
                    "    sum /= 16.0;",
                    "    gl_FragColor.rgb += sum * params.y;",
                    "}"
            });
        }
    }
}
