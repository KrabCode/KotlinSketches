package utils

import com.krab.lazy.LazyGui
import com.krab.lazy.LazyGuiSettings
import processing.core.PApplet
import java.nio.file.Paths

class Utils {
    companion object {
        fun setupWindowBorderless(p: PApplet, w: Int, h: Int, offset: Int = 0) {
            p.surface.setSize(w, h)
            p.surface.setLocation(p.displayWidth-p.width-offset, offset)
            p.surface.setAlwaysOnTop(true)
        }

        /**
         * Loads a resource from the sketch's data folder.
         * Not from the global data folder, but from the data folder directly next to the _24_07.TemplateSketch.main sketch java file.
         * @param where path to the resource inside {current_sketch_folder}/data
         * @return the absolute path to the resource
         */
        fun sketchPath(where: String): String? {
            try {
                val callingClassName = Exception().stackTrace[2].className
                val callingClass = Class.forName(callingClassName)
                val packageName = callingClass.getPackage().name.replace("\\.".toRegex(), "\\\\")
                val sketchPath = Paths.get("").toAbsolutePath().toString()
                return "$sketchPath\\src\\$packageName\\$where"
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                return null
            }
        }

        fun setupGui(p: PApplet): LazyGui {
            return LazyGui(
                p,
                LazyGuiSettings()
                    .setCustomGuiDataFolder(sketchPath("gui"))
                    .setStartGuiHidden(true)
            )
        }
    }


}