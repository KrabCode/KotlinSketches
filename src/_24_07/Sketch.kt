import com.krab.lazy.*;
import processing.core.PApplet
import utils.Utils

class Sketch : PApplet() {
    companion object {
        fun run() {
            val sketch = Sketch()
            sketch.runSketch()
        }
    }

    private lateinit var gui: LazyGui

    override fun settings() {
        fullScreen(P2D)
    }

    override fun setup() {
        Utils.setupWindowBorderless(this, 800, 800, 10)
        gui = LazyGui(this)
    }

    override fun draw() {
        background(gui.colorPicker("bg").hex)
    }
}

fun main() {
    Sketch.run()
}