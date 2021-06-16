package com.wesing.module_partylive_common.im.publicscreen.ui.widget

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView

/**
 * create by zlonghuang on 2020/4/9
 *
 * 增加用户等级， member标签 span
 **/

class MsgRichTextView@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : TextView(context, attrs, defStyle) {

    var mSpanable: SpannableString? = null

    private var mUserLevel: Long = 0
    private var mNamePlateIconUrl: String ? = null
    private var mMemberTagTxt: String? = null
    private var mAirborneTxt: String? = null
    private var mUserUid: Long = 0

    companion object {
        const val LEVEL_TAG = "level_tag"
        const val MEMBER_TAG = "member_tag"
        const val NAMEPLATE_TAG = "Plate"
        const val AIRBORNE_TAG = "Airborne"  //空降歌手
        const val TAG = "MsgRichTextView"
    }

    fun setAirborneText(text: String?) {
        mAirborneTxt = text
    }

    fun setUserLevel(level: Long, uid: Long) {
        mUserUid = uid
        mUserLevel = level
    }

    fun setMemberTagTxt(text: String?) {
        mMemberTagTxt = text
    }

    fun setNameplate(nameplateIconUrl : String?){
        mNamePlateIconUrl = nameplateIconUrl
    }


    override fun setText(text: CharSequence?, type: BufferType?) {
        var msgText = text
        //空降和member只能同能存在一个，空降优先级高
        if (!TextUtils.isEmpty(mAirborneTxt)) {
            //空降歌手标签
            val text = msgText.toString().replace(AIRBORNE_TAG, "") //避免重复，先清除member标志
            msgText = "$AIRBORNE_TAG $text"
        } else {
            //member标签
            val text = msgText.toString().replace(MEMBER_TAG, "") //避免重复，先清除member标志
            if (!TextUtils.isEmpty(mMemberTagTxt)) {
                msgText = "$MEMBER_TAG $text"
            }
        }
        if (mUserLevel >= 0 && !TextUtils.isEmpty(msgText)) {
            var text = msgText.toString().replace(NAMEPLATE_TAG,"")
            text = text.replace(LEVEL_TAG, "")
            msgText = "$LEVEL_TAG $text"
            if(!mNamePlateIconUrl.isNullOrEmpty()){
                msgText = "$NAMEPLATE_TAG $msgText"
            }
        }
        super.setText(msgText, type)
    }

    fun doFormatParse(s: CharSequence?) {
        if (s == null || s.isEmpty() || mSpanable == null || mSpanable!!.isEmpty()) {
            return
        }
        val namePlateIndex = mSpanable?.indexOf(NAMEPLATE_TAG)
        val levelIndex = mSpanable?.indexOf(LEVEL_TAG)
        val memberIndex = mSpanable?.indexOf(MEMBER_TAG)
        val airborneIndex =mSpanable?.indexOf(AIRBORNE_TAG)
        if (airborneIndex != null) {
            if (mSpanable != null && airborneIndex >= 0 && !TextUtils.isEmpty(mAirborneTxt)) {
                val airborneSpan = AirborneSpan(mAirborneTxt!!)
                mSpanable?.setSpan(airborneSpan, airborneIndex, airborneIndex + AIRBORNE_TAG.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        }
    }

}