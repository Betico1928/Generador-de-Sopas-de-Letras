import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

fun main()
{
    val palabras = mutableListOf<String>()
    val scanner = Scanner(System.`in`)

    // Recibir 10 palabras
    println("Ingresa 10 palabras:")
    for (i in 1..10)
    {
        val palabra = scanner.next()
        palabras.add(palabra)
    }



    // Crear la sopa de letras
    val sopaDeLetras = crearSopaDeLetras(palabras)



    // Mostrar la sopa de letras en consola
    println("Sopa de Letras:")
    for (fila in sopaDeLetras)
    {
        for (letra in fila)
        {
            print("$letra ")
        }
        println()
    }

    // Generar imagen de la sopa de letras
    val imagenSopa = generarImagenSopaDeLetras(sopaDeLetras)
    val archivoImagen = File("sopa_de_letras.png")

    ImageIO.write(imagenSopa, "png", archivoImagen)
    println("Se ha generado la imagen 'sopa_de_letras.png'.")
}

fun crearSopaDeLetras(palabras: List<String>): Array<Array<Char>>
{
    val random = Random()
    val sopaDeLetras = Array(20) { Array(20) { ' ' } }

    for (palabra in palabras)
    {
        var fila: Int
        var columna: Int
        var direccion: Int

        do
        {
            fila = random.nextInt(20)
            columna = random.nextInt(20)
            direccion = random.nextInt(8)
        }

        while (!esValida(fila, columna, direccion, palabra, sopaDeLetras))
        // Colocar la palabra en la sopa de letras
        colocarPalabra(fila, columna, direccion, palabra, sopaDeLetras)
    }

    // Rellenar espacios vacíos con letras aleatorias
    for (i in 0 until 20)
    {
        for (j in 0 until 20)
        {
            if (sopaDeLetras[i][j] == ' ')
            {
                sopaDeLetras[i][j] = obtenerLetraAleatoria()
            }
        }
    }
    return sopaDeLetras
}

fun esValida(fila: Int, columna: Int, direccion: Int, palabra: String, sopaDeLetras: Array<Array<Char>>): Boolean
{
    val longitud = palabra.length

    // Verificar si la palabra cabe en la dirección seleccionada
    when (direccion)
    {
        0 -> if (fila - longitud < 0) return false // Arriba
        1 -> if (fila + longitud >= 20) return false // Abajo
        2 -> if (columna - longitud < 0) return false // Izquierda
        3 -> if (columna + longitud >= 20) return false // Derecha
        4 -> if (fila - longitud < 0 || columna - longitud < 0) return false // Arriba-Izquierda
        5 -> if (fila - longitud < 0 || columna + longitud >= 20) return false // Arriba-Derecha
        6 -> if (fila + longitud >= 20 || columna - longitud < 0) return false // Abajo-Izquierda
        7 -> if (fila + longitud >= 20 || columna + longitud >= 20) return false // Abajo-Derecha
    }

    // Verificar si la palabra se superpone con alguna otra palabra
    for (i in 0 until longitud)
    {
        val letra = palabra[i]
        val nuevaFila = obtenerNuevaFila(fila, direccion, i)
        val nuevaColumna = obtenerNuevaColumna(columna, direccion, i)

        if (sopaDeLetras[nuevaFila][nuevaColumna] != ' ' && sopaDeLetras[nuevaFila][nuevaColumna] != letra)
        {
            return false
        }
    }
    return true
}

fun colocarPalabra(fila: Int, columna: Int, direccion: Int, palabra: String, sopaDeLetras: Array<Array<Char>>)
{
    val longitud = palabra.length

    for (i in 0 until longitud)
    {
        val letra = palabra[i]
        val nuevaFila = obtenerNuevaFila(fila, direccion, i)
        val nuevaColumna = obtenerNuevaColumna(columna, direccion, i)

        sopaDeLetras[nuevaFila][nuevaColumna] = letra
    }
}

fun obtenerNuevaFila(fila: Int, direccion: Int, paso: Int): Int
{
    return when (direccion)
    {
        0 -> fila - paso // Arriba
        1 -> fila + paso // Abajo
        4, 5 -> fila - paso // Arriba-Izquierda, Arriba-Derecha
        6, 7 -> fila + paso // Abajo-Izquierda, Abajo-Derecha
        else -> fila // Izquierda, Derecha
    }
}

fun obtenerNuevaColumna(columna: Int, direccion: Int, paso: Int): Int
{
    return when (direccion)
    {
        2 -> columna - paso // Izquierda
        3 -> columna + paso // Derecha
        4, 6 -> columna - paso // Arriba-Izquierda, Abajo-Izquierda
        5, 7 -> columna + paso // Arriba-Derecha, Abajo-Derecha
        else -> columna // Arriba, Abajo
    }
}

fun obtenerLetraAleatoria(): Char
{
    val letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    return letras.random()
}



fun generarImagenSopaDeLetras(sopaDeLetras: Array<Array<Char>>): BufferedImage
{
    val anchoCelda = 30
    val altoCelda = 30

    val anchoImagen = sopaDeLetras.size * anchoCelda
    val altoImagen = sopaDeLetras[0].size * altoCelda

    val imagen = BufferedImage(anchoImagen, altoImagen, BufferedImage.TYPE_INT_RGB)
    val g2d = imagen.createGraphics()

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)


    // Rellenar fondo blanco
    g2d.color = Color.WHITE
    g2d.fillRect(0, 0, anchoImagen, altoImagen)



    // Dibujar letras en celdas
    for (i in sopaDeLetras.indices)
    {
        for (j in sopaDeLetras[i].indices)
        {
            val letra = sopaDeLetras[i][j]

            val x = j * anchoCelda
            val y = i * altoCelda

            g2d.color = Color.BLACK
            g2d.drawRect(x, y, anchoCelda, altoCelda)

            g2d.font = Font("Arial", Font.BOLD, 14)

            val metrics = g2d.fontMetrics
            val anchoLetra = metrics.stringWidth(letra.toString())
            val altoLetra = metrics.height

            val xTexto = x + (anchoCelda - anchoLetra) / 2
            val yTexto = y + (altoCelda + altoLetra) / 2

            g2d.color = Color.BLACK
            g2d.drawString(letra.toString(), xTexto, yTexto)
        }
    }

    g2d.dispose()

    return imagen
}