package com.d479.xpenses.analyzer

import android.graphics.Point
import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text



class TextRecognitionAnalyzer(
    private val onTextDetected: (String) -> Unit,
    private val onTextDetailsDetected: (List<TextLineDetails>) -> Unit
) : ImageAnalysis.Analyzer {

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    data class TextLineDetails(
        val text: String,
        val cornerPoints: Array<Point>?,
        val boundingBox: Rect?
    )

    fun processImage(image: InputImage) {
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val textElements = mutableListOf<Text.Element>()

                // Collect all text elements
                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        textElements.addAll(line.elements)
                    }
                }

                // Sort text elements based on position
                textElements.sortWith(compareBy({ it.boundingBox?.top }, { it.boundingBox?.left }))

                // Group elements into lines
                val lines = groupTextElementsIntoLines(textElements)

                val detectedText = StringBuilder()
                lines.forEach { line ->
                    detectedText.append(line.joinToString(" ") { it.text }).append("\n")
                }

                onTextDetected(detectedText.toString().trim())
                onTextDetailsDetected(lines.map { line ->
                    TextLineDetails(
                        text = line.joinToString(" ") { it.text },
                        cornerPoints = line.first().cornerPoints,
                        boundingBox = line.first().boundingBox
                    )
                })
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onTextDetected("Text recognition failed: ${e.message}")
            }
    }

    override fun analyze(imageProxy: ImageProxy) {
        // No need to implement for single image processing
    }

    private fun groupTextElementsIntoLines(textElements: List<Text.Element>): List<List<Text.Element>> {
        val lines = mutableListOf<MutableList<Text.Element>>()

        textElements.forEach { element ->
            if (lines.isEmpty() || !isSameLine(lines.last().last(), element)) {
                lines.add(mutableListOf(element))
            } else {
                lines.last().add(element)
            }
        }

        lines.dropLast(1).forEach { line ->
            line.add(Text.Element(" ", Rect(), mutableListOf<Any>(), "", null, 0f, 0f, mutableListOf<Any>())) // Adicione o espaço como um elemento fictício
        }

        return lines
    }


    private fun isSameLine(t1: Text.Element, t2: Text.Element): Boolean {
        val diffOfTops = t1.boundingBox?.top?.minus(t2.boundingBox?.top ?: 0) ?: 0
        val avgHeight = ((t1.boundingBox?.height() ?: 0) + (t2.boundingBox?.height() ?: 0)) / 2
        val verticalThreshold = (avgHeight * 0.35).toInt()

        return Math.abs(diffOfTops) <= verticalThreshold
    }
}