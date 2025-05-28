package view

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

fun jsonUploader(): File? {
    var file: File? = null
    val chooser = JFileChooser()
    chooser.dialogTitle = "Choose path to save"
    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
        file = chooser.selectedFile
    }
    return file
}

fun jsonDownloader(): File? {
    var file: File? = null
    val chooser = JFileChooser()
    chooser.dialogTitle = "Choose json file"
    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
    chooser.addChoosableFileFilter(FileNameExtensionFilter("JSON file", "json"))
    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        file = chooser.selectedFile
    }
    return file
}
