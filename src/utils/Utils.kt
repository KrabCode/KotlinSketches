package utils

import processing.core.PApplet

class Utils {
    companion object {
        fun setupWindowBorderless(p: PApplet, w: Int, h: Int, offset: Int = 0) {
            p.surface.setSize(w, h)
            p.surface.setLocation(p.displayWidth-p.width-offset, offset)
            p.surface.setAlwaysOnTop(true)
        }
    }
}