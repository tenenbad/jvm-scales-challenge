package com.nerdery.jvm

import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequence

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
class ScalesChallenge {
    fun buildScale(note: Note, scale: String): List<RelativeNote> {
        //calculation values
        val maxNote:Int = 8
        val minNote:Int = -4
        val wholeStep = 2
        val halfStep = 1
        val noStep = 0

        val majorScaleSteps = listOf(wholeStep, wholeStep, halfStep, wholeStep, wholeStep, wholeStep, halfStep)
        val minorScaleSteps = listOf(wholeStep, halfStep,  wholeStep, wholeStep, halfStep, wholeStep, wholeStep)
        val twinkleSteps = listOf(noStep, 3*wholeStep+halfStep, noStep, wholeStep, noStep, -wholeStep, noStep, -wholeStep, noStep, -halfStep, noStep, -wholeStep, noStep, -wholeStep)

        var startNote = note.cDistance
        var outputScale : MutableList<RelativeNote> = mutableListOf()
        outputScale.add(RelativeNote(note))

        val chosenScale : List<Int>
        when(scale){
            null -> chosenScale = majorScaleSteps
            "major" -> chosenScale = majorScaleSteps
            "minor" -> chosenScale = minorScaleSteps
            "twinkle" -> chosenScale = twinkleSteps
            else -> {
                chosenScale = majorScaleSteps
            }
        }

        var octave = 0
        var prevNote = startNote
        for(step in chosenScale.indices){
            var newNote = prevNote + chosenScale[step]
            //what if the next note is an octave up
            if(newNote > maxNote){
                octave++
                newNote = minNote + (newNote - maxNote)
            }else if(newNote < minNote){
                octave--
                newNote = maxNote - (minNote-newNote)
            }
            outputScale.add(RelativeNote(getNoteFromCDist(newNote), octave))
            prevNote = newNote
        }

        return outputScale
    }

    fun getNoteFromCDist(cDist: Int):Note{
        when(cDist){
            -4 -> return Note.A_FLAT
            -3 -> return Note.A
            -2 -> return Note.A_SHARP
            -1 -> return Note.B
            0 -> return Note.C
            1 -> return Note.C_SHARP
            2 -> return Note.D
            3 -> return Note.D_SHARP
            4 -> return Note.E
            5 -> return Note.F
            6 -> return Note.F_SHARP
            7 -> return Note.G
            8 -> return Note.G_SHARP
            else -> {
                return Note.C
            }
        }
    }

    fun convertToMidi(notes: List<RelativeNote>): Sequence = NotesMidiGenerator(notes).generateSong()

    fun playMidi(sequence: Sequence): Unit {
        val sequencer = MidiSystem.getSequencer()
        sequencer.sequence = sequence
        sequencer.open()
        Thread.sleep(300L)
        sequencer.start()
        Thread.sleep(5000L)
        sequencer.stop()
        sequencer.close()
    }
}

val usageMessage = "Enter a key signature. For example: C_FLAT, C, C_SHARP"

fun main(args: Array<String>) = println(when (args.size) {
    0 -> usageMessage
    else -> try {
        val challenge = ScalesChallenge()
        var scaleType: String
        if(args.size < 2){
            scaleType = "major"
        }else{
            scaleType = args[1];
        }
        val scale = challenge.buildScale(Note.valueOf(args.first()), scaleType)
        val sequence = challenge.convertToMidi(scale)
        challenge.playMidi(sequence)
        scale.joinToString()
    } catch (e: IllegalArgumentException) {
        usageMessage
    }
})
