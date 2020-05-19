package com.example.project.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.project.DBHelper
import com.example.project.R
import com.example.project.viewmodel.SharedViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class YesterdayFragment : Fragment(R.layout.fragment_yesterday) {
    private lateinit var model: SharedViewModel

    // Homeで表示するTextViewを初期化
    private lateinit var bodyWeightView: TextView
    private lateinit var bodyFatPercentageView: TextView
    private lateinit var skeletalMusclePercentageView: TextView
    private lateinit var basalMetabolicRateView: TextView
    private lateinit var dateView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Homeで表示するTextViewを取得
        bodyWeightView = view.findViewById(R.id.bodyWeightView)
        bodyFatPercentageView = view.findViewById(R.id.bodyFatPercentageView)
        skeletalMusclePercentageView = view.findViewById(R.id.skeletalMusclePercentageView)
        basalMetabolicRateView = view.findViewById(R.id.basalMetabolicRateView)

        dateView = view.findViewById(R.id.dateView)

        model = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        // 表裏反転
        view.setRotationY(180F);

        // 現在時刻を取得
        val current = LocalDateTime.now()
        val yesterday = current.minusDays(1)
        // フォーマットを指定
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        // 現在時刻のフォーマットを指定
        val formatted = yesterday.format(formatter)

        //今日の日付
        model.dateYesterday = formatted

        // 今日の日付を表示
        dateView.text = formatted

        // データベースのクラスをインスタンス化
        val dbHelper = DBHelper(activity!!)

        // 読み込み専用で接続
        val db = dbHelper.readableDatabase

        // 今日の始まり
        val dateBegin = model.dateYesterday + " 00:00:00"
        // 今日の終わり
        val dateEnd = model.dateYesterday + " 23:59:59"

        // 今日のデータを取得するSQL文
        val sql = "select bodyWeight, bodyFatPercentage, skeletalMusclePercentage, basalMetabolicRate from physicalRecord where createdAt <= ? and createdAt >= ?  order by _id desc limit 1;"
        // データを取得
        val cursor = db.rawQuery(sql, arrayOf(dateEnd, dateBegin))

        with(cursor) {
            while (moveToNext()) {
                model.yesterdayData[0] = cursor.getString(0)
                model.yesterdayData[1] = cursor.getString(1)
                model.yesterdayData[2] = cursor.getString(2)
                model.yesterdayData[3] = cursor.getString(3)
            }
        }

        // viewmodelの値をtextViewに格納
        updateView()
    }

    // 値を表示する関数
    private fun updateView() {
        model.yesterdayData[0]?.let { bodyWeightView.changeSizeOfText(it, "  kg", 14) }
        model.yesterdayData[1]?.let { bodyFatPercentageView.changeSizeOfText(it, "  %", 14) }
        model.yesterdayData[2]?.let { skeletalMusclePercentageView.changeSizeOfText(it, "  %", 14) }
        model.yesterdayData[3]?.let { basalMetabolicRateView.changeSizeOfText(it, "  kcal", 14) }
    }

    // TextViewの一部のスタイルを変更する関数
    fun TextView.changeSizeOfText(number: String, unit: String, size: Int){

        if (number.isEmpty()) {
            text = "未入力"
            textSize = 14F

        } else {
            // 対象となる文字列を引数に渡し、SpannableStringBuilderのインスタンスを生成
            val spannable = SpannableStringBuilder(number + unit)

            // 単位のサイズを変更
            spannable.setSpan(
                AbsoluteSizeSpan(size, true),
                number.length, // start
                spannable.length, // end
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // 数値の色をに変更
            spannable.setSpan(
                ForegroundColorSpan(Color.parseColor("#FF8C00")),
                0, // start
                unit.length, // end
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // 数値をboldにする
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                0, // start
                number.length, // end
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // TextViewにSpannableStringBuilderをセット
            text = spannable
        }
    }
}