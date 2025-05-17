package view

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

fun JsonUploader(): File {
    val chooser = JFileChooser()
    chooser.dialogTitle = "Choose path to save"
    chooser.showSaveDialog(null)
    val file = File(chooser.selectedFile.toString())
    return file
}

fun JsonDownloader(): File? {
    var file: File? = null
    val chooser = JFileChooser()
    chooser.dialogTitle = "Choose json file"
    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
    chooser.addChoosableFileFilter(FileNameExtensionFilter("JSON file", "json"))
    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        file = chooser.selectedFile
    return file
}