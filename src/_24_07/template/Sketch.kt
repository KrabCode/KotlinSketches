package _24_07.template

import com.krab.lazy.*;
import processing.core.PApplet
import processing.core.PGraphics
import utils.Utils

fun main() {
    Sketch.run()
}

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
        initCanvas()
        colorMode(HSB, 1f, 1f, 1f, 1f)
        gui = Utils.setupGui(this)
    }

    private fun initCanvas() {
        can = createGraphics(width, height, P2D)
        can.colorMode(HSB, 1f, 1f, 1f, 1f)
    }

    override fun draw() {
        fullScreenToggle()
        drawOnCanvas()
        background(0)
        image(can, 0f, 0f)
        Utils.record(this, gui)
    }

    private fun fullScreenToggle() {
        if (gui.button("fullScreen")) {
            if (width != displayWidth) {
                surface.setSize(displayWidth, displayHeight)
                surface.setLocation(0, 0)
                initCanvas()
            } else {
                Utils.setupWindowBorderless(this, 800, 800, 10)
                initCanvas()
            }
        }
    }

    private fun drawOnCanvas() {
        can.beginDraw()
        can.blendMode(BLEND)
        can.noStroke()
        can.fill(gui.colorPicker("bg", color(0.5f, 1f, 1f)).hex)
        can.rect(0f, 0f, can.width.toFloat(), can.height.toFloat())
        can.blendMode(SUBTRACT)
        can.fill(gui.colorPicker("sub", color(0.5f, 1f, 1f)).hex)
        can.rect(0f, 0f, can.width.toFloat(), can.height.toFloat())
        can.blendMode(ADD)
        gui.pushFolder("ring")
        val count = gui.slider("count", 10f);
        val size = gui.slider("size", 10f);
        val radius = gui.slider("radius", 10f);
        val amp = gui.slider("amp", 10f);
        val freq = gui.slider("freq", 10f);
        val time = gui.slider("time", 1f);
        can.translate(width / 2f, height / 2f)
        can.fill(gui.colorPicker("fill", color(0.5f, 1f, 1f)).hex)
        can.stroke(gui.colorPicker("stroke", color(0.0f, 0f, 1f)).hex)
        for (i in 0..count.toInt()) {
            val n = norm(i.toFloat(), 0f, count)
            val theta = n * TWO_PI
            val localRadius = radius + sin(theta * freq + time * radians(frameCount.toFloat())) * amp
            val x = localRadius * cos(theta)
            val y = localRadius * sin(theta)
            can.ellipse(x, y, size, size)
        }
        can.endDraw()
        gui.popFolder()
    }
}
