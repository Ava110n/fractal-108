import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.drawText
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class Complex(var re: Double, var im: Double){
    operator fun plus(c: Complex) : Complex{
        return Complex(this.re + c.re, this.im + c.im)
    }

    operator fun times(c: Complex) : Complex{
        var re1: Double
        var im1: Double

        re1 = this.re * c.re - this.im * c.im
        im1 = this.re * c.im + this.im * c.re
        return Complex(re1, im1)
    }

    fun abs(): Double{
        return Math.sqrt(re*re + im*im)
    }

    override fun toString(): String {
        return "$re + i$im"
    }
}

class Bound(var w: Float, var h: Float, var xMin: Double, var xMax: Double, var yMin: Double, var yMax: Double){
    fun scroll(pos: Offset,k: Double):Bound{
        var xMinP = (pos.x - this.w*k/2).toFloat()
        this.xMin = Pixel(xMinP, 0f).PixToDec(this).x
        var xMaxP = (pos.x + this.w*k/2).toFloat()
        this.xMax = Pixel(xMaxP, 0f).PixToDec(this).x
        var yMinP = (pos.y + this.h*k/2).toFloat()
        this.yMin = Pixel(0f, yMinP).PixToDec(this).y
        var yMaxP = (pos.y + this.h*k/2).toFloat()
        this.yMax = Pixel(0f, yMaxP).PixToDec(this).y
        return Bound(w,h,xMin, xMax, yMin, yMax)
    }
}

class Decart(var x: Double, var y: Double){
    fun DecToPix(b: Bound):Pixel{
        var p_x = (this.x - b.xMin)*b.w/(b.xMax-b.xMin)
        var p_y = (b.yMax - this.y )*b.h/(b.yMax-b.yMin)
        var p = Pixel(p_x.toFloat(), p_y.toFloat())
        return p
    }
}
class Pixel(var x: Float, var y: Float){
    fun PixToDec(b: Bound):Decart{
        var d_x = this.x*(b.xMax-b.xMin)/b.w + b.xMin
        var d_y = b.yMax - this.y*(b.yMax-b.yMin)/b.h
        var p = Decart(d_x, d_y)
        return p
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App(){
    var text by remember { mutableStateOf("Hello, World!") }

    var xMin by remember { mutableStateOf(-2.0) }
    var xMax by remember { mutableStateOf(2.0) }
    var yMin by remember { mutableStateOf(-1.0) }
    var yMax by remember { mutableStateOf(1.0) }
    var bound by remember{ mutableStateOf(Bound(0f,0f,xMin, xMax, yMin, yMax)) }

    MaterialTheme {
        Canvas(modifier = Modifier.fillMaxSize().onPointerEvent(PointerEventType.Scroll){
            xMin = bound.xMin; xMax = bound.xMax; yMin = bound.yMin; yMax = bound.yMax
            bound = Bound(this.size.width.toFloat(), this.size.height.toFloat(), xMin, xMax, yMin, yMax)

            var pos = it.changes.first().position
            bound = bound.scroll(pos, 0.9)
            print(bound.xMin)},
            onDraw = {

                var bound = Bound(this.size.width.toFloat(), this.size.height.toFloat(), xMin, xMax, yMin, yMax)

                for (i in 1..bound.w.toInt()){
                    //coroutineScope.launch {
                    for (j in 1..bound.h.toInt()) {
                        var iterator = 0
                        var z1 = Complex(0.0, 0.0)
                        var c = Complex(
                            xMin + i * (xMax - xMin) / bound.w,
                            yMin + j * (yMax - yMin) / bound.h
                        )

                        while (z1.abs() < 2 && iterator < 1000) {
                            z1 = z1 * z1 + c
                            iterator += 1
                        }

                        var clr: Color
                        //clr = Color.hsv(150f, 0.8f, 0.1f+(iterator/1200).toFloat())
                        if (iterator == 1000) clr = Color.White
                        else clr = Color.Black

                        drawCircle(color = clr, radius = 3f, center = Offset(i.toFloat(), j.toFloat()))
                        //}
                    }
                }
            })
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }

    var a = Complex(1.0, 2.0)
    var b = Complex(3.0, 4.0)

    println(a)
    println(a + b)
    println(a * b)
    println(a.abs())

}