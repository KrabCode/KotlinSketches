package _24_07.template

import com.krab.lazy.*;
import processing.core.PApplet
import processing.core.PGraphics
import utils.Utils

class Sketch : PApplet() {
    companion object {
        fun run() {
            val sketch = Sketch()
            sketch.runSketch()
        }
    }

    private lateinit var gui: LazyGui
    private lateinit var can: PGraphics

    override fun settings() {
        fullScreen(P2D)
    }

    override fun setup() {
        Utils.setupWindowBorderless(this, 800, 800, 10)
        colorMode(HSB,1f,1f,1f,1f)
        can = createGraphics(width, height, P2D)
        can.colorMode(HSB,1f,1f,1f,1f)
        gui = Utils.setupGui(this)
    }

    override fun draw() {
        background(gui.colorPicker("bg", color(0.5f, 1f, 1f)).hex)
    }
}

fun main() {
    Sketch.run()
}