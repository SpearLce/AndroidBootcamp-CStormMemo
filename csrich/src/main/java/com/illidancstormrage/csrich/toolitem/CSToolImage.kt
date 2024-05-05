package com.illidancstormrage.csrich.toolitem

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.text.Spannable
import android.text.style.ImageSpan
import android.view.View
import androidx.core.text.buildSpannedString
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.illidancstormrage.csrich.R
import com.illidancstormrage.csrich.richeditor.otherui.CSImageButton
import com.illidancstormrage.csrich.span.CSImageSpan
import com.illidancstormrage.csrich.utils.extensions.getScreenSizeTotalApi30Plus
import com.illidancstormrage.utils.log.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CSToolImage : CSToolItem() {

    companion object {
        private const val TAG = "CSToolImage"
        private const val PACKAGE_NAME = "com.illidancstormrage.spandemo"
        const val FROM_ALBUM = 0
    }

    //将当前editText关联
    private var context: Context? = null

    //private val fragment: Fragment? = null


    override fun applyStyle(start: Int, end: Int) {
    }

    override fun setStyle(start: Int, end: Int) {
    }

    override fun removeStyle(start: Int, end: Int) {
    }

    override fun getView(context: Context): List<View> {
        //1 保存传入 context
        this.context = context


        //2 新建工具项view - imageButton:CSImageButton(正方形)
        val imageButton = CSImageButton(context)
        imageButton.setImageResource(R.drawable.icon_text_picture)

        //3
        //保存操作对象 - view
        //view 继承自 CSToolItem
        view = imageButton
        (view as CSImageButton).setOnClickListener {
            //获取图片的URI，并插入到imageSpan中
            //context是Editor组件的context : linearLayout

            LogUtil.e(TAG,"tool_image 被点击")

            //从相册中选一张图片 - ACTION_OPEN_DOCUMENT
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            //指定只显示图片
            //action ACTION_OPEN_DOCUMENT，所以指定type为image/*，图片
            intent.type = "image/*"
            //context - LinearLayout
            LogUtil.e(TAG,"tool_image context = $context")
            //LogUtil.e(TAG,"tool_image context as Activity = ${context as Activity}")
            (context as Activity).startActivityForResult(intent, FROM_ALBUM)
        }
        val views: MutableList<View> = ArrayList()
        views.add(view as CSImageButton)
        return views
    }

    private suspend fun insertImage(src: Any, type: CSImageSpan.ImageType) {
        var targetWidth: Int? = null
        var targetHeight: Int? = null
        context?.let {
            val screenSize = it.getScreenSizeTotalApi30Plus() // 或 getScreenSizeInAppWindow() 根据您的选择
            targetWidth = screenSize.x / 3 * 2  //x 270
            targetHeight = screenSize.y / 3 * 2 //y 600
        }
        LogUtil.e(TAG, "$targetWidth $targetHeight")

        val futureTarget: FutureTarget<Bitmap> = Glide.with(context!!)
            .asBitmap()
            .load(src as Uri)
            .override(targetWidth!!, targetHeight!!) //覆盖固定宽高 - 但是竖屏横屏图片宽高是逆的
            .fitCenter() //缩放适应view
            .submit()



        val scope = CoroutineScope(Dispatchers.IO).async {
            val bitmap = futureTarget.get() //get：后台线程执行
            val imageSpan: ImageSpan?
            imageSpan = CSImageSpan(context!!, bitmap, src)
            imageSpan
        }
        //main线程
        insertSpan(scope.await()) //await：需要同步获取图片内容

    }

    private fun insertSpan(imageSpan: ImageSpan) {

        getEditText()?.let {
            val editable = it.editableText
            val start = it.selectionStart
            val end = it.selectionEnd

            val text = buildSpannedString {
                append("\n[image]\n\n")
                //[image]1,8 - 换成图片
                setSpan(imageSpan, 1, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            editable.replace(start, end, text)
        }
    }


    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
    }

    override fun onCheckStateUpdate() {
    }

    fun onActivityResult(data: Intent?) {
        data?.data.let { uri ->
            if (uri != null) {
                val scope = CoroutineScope(Dispatchers.Main).launch {

                    //提供永久读取权限
                    context!!.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    LogUtil.e(TAG, "触发次数 -- ")
                    insertImage(uri, CSImageSpan.ImageType.URI)
                }

            } else {
                LogUtil.e(TAG, "传递图片URI为空")
            }
        }
    }

}
