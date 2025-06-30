package com.mshdabiola.model

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import kotlin.random.Random

// Helper function to create a random past date
fun getRandomPastDate(): Long {
    val currentTime = System.currentTimeMillis()
    val oneYearInMillis = 365 * 24 * 60 * 60 * 1000L
    return currentTime - Random.nextLong(oneYearInMillis)
}

// Helper function to create a random future date
fun getRandomFutureDate(): Long {
    val currentTime = System.currentTimeMillis()
    val oneMonthInMillis = 30 * 24 * 60 * 60 * 1000L
    return currentTime + Random.nextLong(oneMonthInMillis)
}

fun createFakeNotePads(range: IntRange): List<NotePad> {
    val fakeNotePads = mutableListOf<NotePad>()
    val sampleLabels = listOf(
        Label(id = 1L, label = "Work"),
        Label(id = 2L, label = "Personal"),
        Label(id = 3L, label = "Ideas"),
        Label(id = 4L, label = "Urgent"),
        Label(id = 5L, label = "Shopping"),
    )

    for (i in range) {
        val noteId = i.toLong()
        val creationDate = getRandomPastDate()
        val editDate =
            creationDate + Random.nextLong(1000 * 60 * 60 * 24 * 7) // Edited within a week

        // Create Fake Note
        val fakeNote = Note(
            id = noteId,
            title = "Note Title $i",
            detail = "This is the detailed content for note number $i. It might contain some interesting ideas or reminders. Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            editDate = editDate,
            noteType = NoteType.entries.toTypedArray().random(),
            isCheck = Random.nextBoolean(),
            isPin = Random.nextBoolean(),
        )

        // Create Fake Notification (optional)
        val fakeNotification = if (Random.nextBoolean() && i % 2 == 0) {
            NotificationUiState(
                noteId = noteId,
                currentDateTime = Instant.fromEpochMilliseconds(
                    getRandomFutureDate(),
                ).toLocalDateTime(
                    TimeZone.currentSystemDefault(),
                ),
                currentPlace = null,
                currentInterval = NotificationInterval.Monthly(
                    sameDay = true,
                    interval = "1",
                    intervalEnd = IntervalEnd.Forever,
                ),
            )
        } else {
            null
        }

        // Create Fake Drawings
        val fakeDrawings = if (Random.nextBoolean() && i % 3 == 0) {
            List(Random.nextInt(1, 3)) { drawingIndex ->
                NoteDrawing(
                    id = (i * 100 + drawingIndex).toLong(),
                    noteId = noteId,
                    drawingPaths = listOf(),
                )
            }
        } else {
            emptyList()
        }

        // Create Fake Images
        val fakeImages = if (Random.nextBoolean() && i % 2 != 0) {
            List(Random.nextInt(1, 4)) { imageIndex ->
                NoteImage(
                    id = (i * 1000 + imageIndex).toLong(),
                    noteId = noteId,
                    path = "content://media/external/images/media/${Random.nextInt(1000, 9999)}",

                )
            }
        } else {
            emptyList()
        }

        // Create Fake Voices
        val fakeVoices = if (Random.nextBoolean() && i % 4 == 0) {
            List(Random.nextInt(1, 2)) { voiceIndex ->
                NoteVoice(
                    id = (i * 10000 + voiceIndex).toLong(),
                    noteId = noteId,
                    filePath = "/storage/emulated/0/Recordings/voice_note_${UUID.randomUUID()}.mp3",
                )
            }
        } else {
            emptyList()
        }

        // Create Fake Checks
        val fakeChecks = if (fakeNote.isCheck && Random.nextBoolean()) {
            List(Random.nextInt(2, 6)) { checkIndex ->
                NoteCheck(
                    id = (i * 10 + checkIndex).toLong(),
                    noteId = noteId,
                    content = "Checklist item ${checkIndex + 1} for note $i",
                    isCheck = Random.nextBoolean(),
                )
            }
        } else {
            emptyList()
        }

        // Assign Fake Labels (subset of sampleLabels)
        val fakeLabels = if (Random.nextBoolean()) {
            sampleLabels.shuffled().take(Random.nextInt(0, sampleLabels.size / 2 + 1))
        } else {
            emptyList()
        }

        // Create Fake Uris
        val fakeUris = if (Random.nextBoolean() && i % 5 == 0) {
            List(Random.nextInt(1, 3)) { uriIndex ->
                NoteUri(
                    id = uriIndex,
                    uri = "https://www.example.com/resource/${UUID.randomUUID()}",
                    icon = "",
                    path = "",
                )
            }
        } else {
            emptyList()
        }

        fakeNotePads.add(
            NotePad(
                note = fakeNote,
                notification = fakeNotification,
                drawings = fakeDrawings,
                images = fakeImages,
                voices = fakeVoices,
                checks = fakeChecks,
                labels = fakeLabels,
                uris = fakeUris,
            ),
        )
    }
    return fakeNotePads
}

fun getDefinedNotePads(): List<NotePad> {
    val definedNotePads = mutableListOf<NotePad>()
    val referenceTime = 1672531200000L // Approx Jan 1, 2023, for base timestamps

    // --- NotePad 1: Simple Text Note with a Label ---
    definedNotePads.add(
        NotePad(
            note = Note(
                id = 1L,
                title = "Meeting Agenda",
                detail = "1. Introduction\n2. Review Q4 Results\n3. Q1 Planning\n4. Q&A",
                editDate = referenceTime + (60 * 60 * 1000L), // +1 hour
                noteType = NoteType.NOTE,
                isCheck = false,
            ),
            labels = listOf(
                Label(id = 101L, label = "Work"),
            ),
        ),
    )

    // --- NotePad 2: Checklist Note with Some Items Checked ---
    definedNotePads.add(
        NotePad(
            note = Note(
                id = 2L,
                title = "Project Phoenix - Phase 1 Tasks",
                detail = "Key deliverables for the first phase.",
                editDate = referenceTime + (24 * 60 * 60 * 1000L) + (2 * 60 * 60 * 1000L), // +1 day, +2 hours
                noteType = NoteType.NOTE,
                isCheck = true,
            ),
            checks = listOf(
                NoteCheck(id = 201L, noteId = 2L, content = "Define scope", isCheck = true),
                NoteCheck(id = 202L, noteId = 2L, content = "Setup environment", isCheck = true),
                NoteCheck(
                    id = 203L,
                    noteId = 2L,
                    content = "Initial design mockups",
                    isCheck = false,
                ),
                NoteCheck(
                    id = 204L,
                    noteId = 2L,
                    content = "User stories documentation",
                    isCheck = false,
                ),
            ),
            labels = listOf(
                Label(id = 101L, label = "Work"),
                Label(id = 102L, label = "Project Phoenix"),
            ),
        ),
    )

    // --- NotePad 3: Note with an Image and a Short Description ---
    definedNotePads.add(
        NotePad(
            note = Note(
                id = 3L,
                title = "Inspiration - Landscape",
                detail = "Beautiful mountain range, possible color palette source.",
                editDate = referenceTime + (2 * 24 * 60 * 60 * 1000L),
                noteType = NoteType.NOTE,
                isCheck = false,
            ),
            images = listOf(
                NoteImage(
                    id = 301L,
                    noteId = 3L,
                    path = "content://media/external/images/media/landscape_001.jpg",

                ),
            ),
            labels = listOf(Label(id = 103L, label = "Ideas")),
        ),
    )

    // --- NotePad 4: Archived Note - Old Recipe ---
    definedNotePads.add(
        NotePad(
            note = Note(
                id = 4L,
                title = "Grandma's Cookie Recipe (Old)",
                detail = "The classic recipe, moved to archive as I have a new version.",
                editDate = referenceTime - (29 * 24 * 60 * 60 * 1000L), // Edited a day after creation
                noteType = NoteType.ARCHIVE,
                isCheck = false,
            ),
        ),
    )

    // --- NotePad 5: Note with an Active Reminder/Notification ---
    definedNotePads.add(
        NotePad(
            note = Note(
                id = 5L,
                title = "Submit Expense Report",
                detail = "Deadline is EOD Friday. Include all receipts from the conference.",
                editDate = referenceTime + (3 * 24 * 60 * 60 * 1000L) + (30 * 60 * 1000L), // +3 days, +30 mins
                noteType = NoteType.NOTE,
                isCheck = false,
            ),
            notification = NotificationUiState(
                noteId = 5,
                currentDateTime = Instant.fromEpochMilliseconds(
                    referenceTime,
                ).toLocalDateTime(
                    TimeZone.currentSystemDefault(),
                ),
                currentPlace = null,
                currentInterval = NotificationInterval.Monthly(
                    sameDay = true,
                    interval = "1",
                    intervalEnd = IntervalEnd.Forever,
                ),
            ),
            labels = listOf(
                Label(id = 101L, label = "Work"),
                Label(id = 104L, label = "Urgent"),
            ),
        ),
    )

    // --- NotePad 6: Note with a Sketch/Drawing ---
    definedNotePads.add(
        NotePad(
            note = Note(
                id = 6L,
                title = "App UI Flow Idea",
                detail = "Rough sketch of the navigation between screens.",
                editDate = referenceTime + (4 * 24 * 60 * 60 * 1000L),
                noteType = NoteType.NOTE,
                isCheck = false,
            ),
            drawings = listOf(
                NoteDrawing(
                    id = 601L,
                    noteId = 6L,
                    drawingPaths = listOf(),
                ),
            ),
            labels = listOf(
                Label(id = 103L, label = "Ideas"),
                Label(id = 105L, label = "UX/UI"),
            ),
        ),
    )

    // --- NotePad 7: Note with a Voice Memo ---
    definedNotePads.add(
        NotePad(
            note = Note(
                id = 7L,
                title = "Quick Voice Note to Self",
                detail = "Reminder about the book I wanted to read.",
                editDate = referenceTime + (5 * 24 * 60 * 60 * 1000L),
                noteType = NoteType.NOTE,
                isCheck = false,
            ),
            voices = listOf(
                NoteVoice(
                    id = 701L,
                    noteId = 7L,
                    filePath = "/storage/emulated/0/Recordings/book_reminder_01.mp3",
                ),
            ),
            labels = listOf(Label(id = 106L, label = "Personal")),
        ),
    )

    // --- NotePad 8: Note in Trash (Accidentally Deleted) ---
    definedNotePads.add(
        NotePad(
            note = Note(
                id = 8L,
                title = "Draft Blog Post - To Recover",
                detail = "Started writing something, then decided against it, but might need it later.",
                editDate = referenceTime + (6 * 24 * 60 * 60 * 1000L),
                noteType = NoteType.TRASH,
                isCheck = false,
            ),
        ),
    )

    // --- NotePad 9: Complex Note with Multiple Content Types (Text, URI, Labels) ---
    definedNotePads.add(
        NotePad(
            note = Note(
                id = 9L,
                title = "Research: Quantum Computing Basics",
                detail = "Need to understand qubits, superposition, and entanglement. See linked article.",
                editDate = referenceTime + (7 * 24 * 60 * 60 * 1000L) + (5 * 60 * 60 * 1000L), // +7 days, +5 hours
                noteType = NoteType.NOTE,
                isCheck = false,
            ),
            labels = listOf(
                Label(id = 101L, label = "Work"),
                Label(id = 107L, label = "Learning"),
                Label(id = 108L, label = "Tech Deep Dive"),
            ),
            uris = listOf(
                NoteUri(
                    id = 1,
                    uri = "https://en.wikipedia.org/wiki/Quantum_computing",
                    icon = "",
                    path = "",
                ),

            ),
        ),
    )

    // --- NotePad 10: Very Simple Note - Title Only (but not empty by NotePad.isEmpty() definition) ---
    definedNotePads.add(
        NotePad(
            note = Note(
                id = 10L,
                title = "A Fleeting Thought",
                detail = "", // Explicitly empty detail
                editDate = referenceTime + (8 * 24 * 60 * 60 * 1000L),
                noteType = NoteType.NOTE,
                isCheck = false,
            ),
            // No other components for this one to keep it minimal
        ),
    )

    return definedNotePads
}
