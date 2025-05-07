package viewmodel

import algo.planar.ForceAtlas2
import algo.planar.Planar
import algo.planar.YifanHu
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.graphs.EmptyGraph
import view.ColorList
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.collections.forEach

class MainScreenViewModel<K, V>(graphViewModel: GraphViewModel<K, V>) {

    var viewModel by mutableStateOf(graphViewModel)

    var pathDialog = mutableStateOf(false)
    var warning = mutableStateOf(false)
    var error = mutableStateOf(false)
    var errorText = mutableStateOf("")


    val openNeo4j=mutableStateOf(false)
    val readyNeo4j=mutableStateOf(true)
    val set= mutableStateOf(false)


    val uriNeo4j = mutableStateOf("")
    val passwordNeo4j = mutableStateOf("")
    val loginNeo4j = mutableStateOf("")

    val showAddVertexDialog = mutableStateOf(false) 
    val newVertexKey = mutableStateOf("") 
    val newVertexValue =  mutableStateOf("") 
    val addVertexError = mutableStateOf<String?>(null)

    var showAddEdgesDialog = mutableStateOf(false)
    var edgeWeightInput = "1"
    var isAllToAllMode = mutableStateOf(true)
    val edgeWeight = mutableStateOf(edgeWeightInput.toIntOrNull())
    val isWeightValid = mutableStateOf(edgeWeightInput.toIntOrNull() != null )
    val edgeError =  mutableStateOf(false)

    val showDeleteConfirmation =  mutableStateOf(false)
    val showNoSelectionWarning =  mutableStateOf(false)
    

    val buttonEdgeLabel=mutableStateOf(false)

    val path=mutableStateOf(0)

    val planarAlgos: (Planar) -> Unit = {
        clean()
        val temp= it.apply(viewModel.graph)
        temp.forEach { v, c ->
            viewModel.vertices[v]?.x?.value=c.first.toDouble()
            viewModel.vertices[v]?.y?.value=c.second.toDouble()
        }
    }
    val clean = {
        viewModel.vertices.values.forEach {
            if (it.color.value!=Color.Red)
                it.color.value=Color.Cyan
        }
        viewModel.edges.values.forEach {
            it.color.value=Color.Black
        }
    }


    fun dijkstra() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            val temp = viewModel.dijkstra()
            path.value = temp.first
            temp.second.forEach {
                if (viewModel.vertices[it]?.selected?.value==false)
                    viewModel.vertices[it]?.color?.value = Color.Green
            }
            for (i in 1..temp.second.size - 1)
                viewModel.edges.forEach {
                    if (it.key.link.first === temp.second[i - 1] && it.key.link.second === temp.second[i] ||
                        it.key.link.second === temp.second[i - 1] && it.key.link.first === temp.second[i])
                        it.value.color.value = Color.Red
                }
            pathDialog.value = true
        } catch (e: IndexOutOfBoundsException) {
            warning.value =true
        } catch (e: IllegalStateException) {
            errorText.value="Choose graph type first"
            error.value =true
        } catch (e: Exception) {
            errorText.value=e.message.toString()
            error.value=true
        }
    }

    fun fordBellman() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            val temp =
                viewModel.fordBellman()
            path.value = temp.first
            temp.second?.forEach {
                if (viewModel.vertices[it]?.selected?.value==false)
                    viewModel.vertices[it]?.color?.value = Color.Green
            }
            temp.third?.forEach {
                if (viewModel.vertices[it]?.selected?.value==false)
                    viewModel.vertices[it]?.color?.value = Color.Yellow
            }
            for (i in 1..(temp.second?.size?.minus(1) ?: 0))
                viewModel.edges.forEach {
                    if (it.key.link.first === temp.second?.get(i - 1) && it.key.link.second === temp.second?.get(i) ||
                        it.key.link.second === temp.second?.get(i - 1) && it.key.link.first === temp.second?.get(i))
                        it.value.color.value = Color.Green
                }
            for (i in 1..(temp.third?.size?.minus(1) ?: 0))
                viewModel.edges.forEach {
                    if (it.key.link.first === temp.second?.get(i - 1) && it.key.link.second === temp.second?.get(i) ||
                        it.key.link.second === temp.second?.get(i - 1) && it.key.link.first === temp.second?.get(i))
                        it.value.color.value = Color.Yellow
                }
            pathDialog.value = true
        } catch (e: IllegalStateException) {
            errorText.value="Choose graph type first"
            error.value=true
        } catch (e: IndexOutOfBoundsException) {
            warning.value=true
        } catch (e: Exception) {
            errorText.value=e.message.toString()
            error.value=true
        }
    }

    fun cycles() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            val temp = viewModel.cycles()
            temp.forEach { cycle ->
                for (i in 0..cycle.size - 1) {
                    viewModel.vertices[cycle[i]]?.color?.value = Color.Magenta
                    if (i > 0) {
                        viewModel.edges.forEach { p0, p1 ->
                            if (p0.link.first === cycle[i - 1] && p0.link.second === cycle[i] ||
                                p0.link.second === cycle[i - 1] && p0.link.first === cycle[i])
                                p1.color.value = Color.Magenta
                        }
                    }
                }
            }
        }  catch (e: NoSuchElementException) {
            warning.value=true
        } catch (e: IllegalStateException) {
            errorText.value="Choose graph type first"
            error.value=true
        } catch (e: Exception) {
            errorText.value=e.message.toString()
            error.value=true
        }
    }

    fun kosajuruSharir() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            val colors = ColorList().iterator()
            val temp =
                viewModel.kosarujuSharir()
            temp.forEach { component ->
                colors.hasNext()
                val color = colors.next().toInt(16)
                val red = color / (16 * 16 * 16 * 16)
                val green = (color - red) / (16 * 16)
                val blue = color - green
                component.forEach { v ->
                    viewModel.vertices[v]?.color?.value = Color(red, green, blue)
                    viewModel.edges.values.forEach { e ->
                        if (e.from.vertex === v)
                            e.color.value = Color(red, green, blue)
                    }
                }

            }
        } catch (e: IllegalStateException) {
            errorText.value="Choose graph type first"
            error.value=true
        } catch (e: Exception) {
            errorText.value=e.message.toString()
            error.value=true
        }
    }

    fun forceAtlas2() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            planarAlgos(ForceAtlas2())
        } catch (e: IllegalStateException) {
            errorText.value="Choose graph type first"
            error.value=true
        } catch (e: Exception) {
            errorText.value=e.message.toString()
            error.value=true
        }
    }

    fun yuifanHu() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            planarAlgos(YifanHu())
        } catch (e: IllegalStateException) {
            errorText.value="Choose graph type first"
            error.value=true
        } catch (e: Exception) {
            errorText.value=e.message.toString()
            error.value=true
        }
    }

    fun downloadJson() {
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            var file: File? = null
            val chooser = JFileChooser()
            chooser.dialogTitle = "Choose json file"
            chooser.fileSelectionMode = JFileChooser.FILES_ONLY
            chooser.addChoosableFileFilter(FileNameExtensionFilter("JSON file", "json"))
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                file = chooser.selectedFile
            val result = viewModel.downloadJson(file)
        } catch (e: IllegalStateException) {
            errorText.value="Choose graph type first"
            error.value=true
        } catch (e: Exception) {
            errorText.value=e.message.toString()
            error.value=true
        }
    }

    fun downloadNeo4j() {
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            openNeo4j.value=true
        } catch (e: IllegalStateException) {
            openNeo4j.value = false
            errorText.value = "Choose graph type first"
            error.value = true
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun downloadNeo4jBasic() {
        GlobalScope.launch(
            block = {
                    try {
                        readyNeo4j.value = false
                        val result = viewModel.downloadNeo4j(uriNeo4j.value,
                            loginNeo4j.value, passwordNeo4j.value)
                    } catch (e: Exception) {
                        openNeo4j.value = false
                        errorText.value = e.message.toString()
                        error.value = true
                    } finally {
                        set.value = false
                        readyNeo4j.value = true
                    }

            }
        )
    }

    fun uploadJson() {
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            val chooser = JFileChooser()
            chooser.dialogTitle = "Choose path to save"
            chooser.showSaveDialog(null)
            val file = File(chooser.selectedFile.toString())
            file.writeText(viewModel.uploadJson())
        } catch (e: IllegalStateException) {
            errorText.value="Choose graph type first"
            error.value=true
        } catch (e: Exception) {
            errorText.value= e.message.toString()
            error.value=true
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun uploadNeo4jBasic() {
        GlobalScope.launch(
            block = {
                try {
                    readyNeo4j.value = false
                    viewModel.uploadNeo4j(uriNeo4j.value,
                        loginNeo4j.value, passwordNeo4j.value)
                } catch (e: Exception) {
                    openNeo4j.value = false
                    errorText.value = e.message.toString()
                    error.value = true
                } finally {
                    readyNeo4j.value = true
                    set.value = false
                }
            }
        )
    }

    fun uploadNeo4j() {
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            openNeo4j.value=true
        } catch (e: IllegalStateException) {
            openNeo4j.value = false
            errorText.value = "Choose graph type first"
            error.value = true
        }
    }

    fun resetSelected() {
        viewModel.vertices.values.forEach {
            it.color.value=Color.Cyan
        }
        viewModel.edges.values.forEach {
            it.color.value=Color.Black
        }
        viewModel.selected.clear()
    }

    fun visibleEdges() {
        viewModel.edges.values.forEach {
            it.isVisible.value=!it.isVisible.value
            buttonEdgeLabel.value=!buttonEdgeLabel.value
        }
    }


}

