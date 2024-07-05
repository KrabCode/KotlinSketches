package utils

import com.krab.lazy.Input
import com.krab.lazy.LazyGui
import com.krab.lazy.LazyGuiSettings
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import java.util.*

class Utils {
    companion object {

        private var recStarted: Int = -1
        private var saveIndex: Int = 1
        private var recordingId: String = generateRandomShortId()

        private fun generateRandomShortId(): String {
            return UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        }

        fun setupWindowBorderless(p: PApplet, w: Int, h: Int, offset: Int = 0) {
            p.surface.setSize(w, h)
            p.surface.setLocation(p.displayWidth-p.width-offset, offset)
            p.surface.setAlwaysOnTop(true)
        }

        fun record(pApplet: PApplet, gui: LazyGui) {
            gui.sliderInt("rec/frame")
            gui.sliderSet("rec/frame", saveIndex.toFloat())
            val recLengthDefault = 360
            val recLength = gui.sliderInt("rec/length", recLengthDefault)

            val recordingInProgress =
                recStarted != -1 && pApplet.frameCount < recStarted + recLength
            var recordingJustEnded =
                recStarted != -1 && pApplet.frameCount == recStarted + recLength
            if (gui.button("rec/start (ctrl + k)") ||
                (Input.getCode(PConstants.CONTROL).down && Input.getChar('k').pressed)
            ) {
                recordingId = generateRandomShortId()
                recStarted = pApplet.frameCount
                saveIndex = 1
                gui.sliderSet("rec/frame", saveIndex.toFloat())
            }
            val stopCommand = gui.button("rec/stop  (ctrl + l)")
            if (stopCommand ||
                (Input.getCode(PConstants.CONTROL).down && Input.getChar('l').pressed)
            ) {
                recStarted = -1
                saveIndex = 1
                recordingJustEnded = true
                gui.sliderSet("rec/frame", saveIndex.toFloat())
            }
            val sketchMainClassName = pApplet.javaClass.simpleName
            val recDir = pApplet.dataPath("video/" + sketchMainClassName + "_" + recordingId)
            val recDirAbsolute = Paths.get(recDir).toAbsolutePath().toString()
            if (gui.button("rec/open folder")) {
                val desktop = Desktop.getDesktop()
                val dir = File(pApplet.dataPath("video"))
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                try {
                    desktop.open(dir)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val recordRectPos =
                PVector.add(gui.plotXY("rec/rect pos"), PVector(pApplet.width / 2f, pApplet.height / 2f))
            val recordRectSize = gui.plotXY("rec/rect size", pApplet.width.toFloat(), pApplet.height.toFloat())
            var recordRectSizeX = PApplet.floor(recordRectSize.x)
            var recordRectSizeY = PApplet.floor(recordRectSize.y)
            // prevent resolutions odd numbers because ffmpeg can't work with them
            if (recordRectSizeX % 2 != 0) {
                recordRectSizeX += 1
            }
            if (recordRectSizeY % 2 != 0) {
                recordRectSizeY += 1
            }
            val recImageFormat = ".jpg"
            if (recordingInProgress) {
                PApplet.println("saved $saveIndex / $recLength")
                val cutout =
                    pApplet[PApplet.floor(recordRectPos.x) - recordRectSizeX / 2, PApplet.floor(recordRectPos.y) - recordRectSizeY / 2, recordRectSizeX, recordRectSizeY]
                cutout.save(recDir + "/" + saveIndex++ + recImageFormat)
            }
            if (gui.toggle("rec/show rect")) {
                pApplet.pushStyle()
                pApplet.stroke(pApplet.color(-0x5f000001))
                pApplet.noFill()
                pApplet.rectMode(PConstants.CENTER)
                pApplet.rect(recordRectPos.x, recordRectPos.y, recordRectSizeX.toFloat(), recordRectSizeY.toFloat())
                pApplet.popStyle()
            }

            val ffmpegFramerate = gui.sliderInt("rec/ffmpeg fps", 60, 1, Int.MAX_VALUE)
            if (gui.toggle("rec/ffmpeg", true) && recordingJustEnded) {
                val outMovieFilename =
                    pApplet.dataPath("video/" + sketchMainClassName + "_" + recordingId)
                val inputFormat = "$recDirAbsolute/%01d$recImageFormat"
                val command = String.format(
                    "ffmpeg  -r %s -i %s -start_number_range 100 -an %s.mp4",
                    ffmpegFramerate, inputFormat, outMovieFilename
                )
                PApplet.println("running ffmpeg: $command")
                try {
                    val proc = Runtime.getRuntime().exec(command)
                    Thread {
                        val sc = Scanner(proc.errorStream)
                        while (sc.hasNextLine()) {
                            PApplet.println(sc.nextLine())
                        }
                        PApplet.println("finished recording $outMovieFilename")
                    }.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
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
//                    .setStartGuiHidden(true)
            )
        }
    }


}