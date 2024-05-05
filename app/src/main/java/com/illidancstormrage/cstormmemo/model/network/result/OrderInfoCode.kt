package com.illidancstormrage.cstormmemo.model.network.result

object OrderInfoCode {
    enum class FailType(val code: Int, val msg: String) {
        AUDIO_NORMAL(0, "音频正常执行"),
        UPLOAD_FAILED(1, "音频上传失败"),
        TRANSCODING_FAILED(2, "音频转码失败"),
        RECOGNITION_FAILED(3, "音频识别失败"),
        DURATION_EXCEEDED(4, "音频时长超限（最大音频时长为 5 小时）"),
        INTEGRITY_CHECK_FAILED(5, "音频校验失败（duration 对应的值与真实音频时长不符合要求）"),
        SILENT_FILE(6, "静音文件"),
        TRANSLATION_FAILED(7, "翻译失败"),
        NO_TRANSLATION_PERMISSION(8, "账号无翻译权限"),
        TRANSCRIPTION_QUALITY_FAILED(9, "转写质检失败"),
        KEYWORD_MATCHING_FAILED(10, "转写质检未匹配出关键词"),
        FEATURE_NOT_ENABLED(11, "upload接口创建任务时，未开启质检或者翻译能力；"),
        OTHER_ERRORS(99, "其他");

        companion object {
            fun fromCode(code: Int): String? {
                return entries.firstOrNull { it.code == code }?.msg
            }
        }
    }
    enum class Status(val code: Int, val msg: String){
        ORDER_CREATED(0,"订单已创建"),
        ORDER_PROCESSING(3,"订单处理中"),
        ORDER_COMPLETED(4,"订单已完成"),
        ORDER_FAILED(-1,"订单失败");
    }
}