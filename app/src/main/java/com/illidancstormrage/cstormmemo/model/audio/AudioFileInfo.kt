package com.illidancstormrage.cstormmemo.model.audio

import android.net.Uri

import java.io.File
data class AudioFileInfo(
    var fileName: String,
    var fileSize: Long,
    var durationMs: Long,
    var tmpFile: File,
    var audioUri: Uri?
)