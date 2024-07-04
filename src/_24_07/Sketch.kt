import com.krab.lazy.*;
import processing.core.PApplet

class Sketch : PApplet() {
    companion object {
        fun run() {
            val art = Sketch()
            art.runSketch()
        }
    }

    lateinit var gui: LazyGui

    override fun settings() {
        size(800,800,P2D)
    }

    override fun setup() {
        this.gui = LazyGui(this)
    }

    override fun draw() {
        background(gui.colorPicker("bg").hex);
    }
}

fun main(args: Array<String>) {
    Sketch.run()
}