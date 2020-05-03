package com.example.project.fragment

import android.os.Bundle
import android.provider.BaseColumns
import android.view.View
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.project.DBHelper
import com.example.project.PhysicalRecordContract
import com.example.project.viewmodel.SharedViewModel
import com.example.project.R
import java.text.SimpleDateFormat
import java.util.*


// Fragment クラスを継承
class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private lateinit var model: SharedViewModel

    private lateinit var calendarView: CalendarView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        calendarView = view.findViewById(R.id.calendar)

        val c = Calendar.getInstance()

        // 初期選択日を取得
        val defaultDate = calendarView.date
        model.dateDetail = format.format(defaultDate)

        // 日付変更イベントを追加
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // 月は0から数える仕様になっているので1をたす
            val year = year.toString()
            var month = arrangeFormatt(month + 1)
            var dayOfMonth = arrangeFormatt(dayOfMonth)
            val date = "$year-$month-$dayOfMonth"
            model.dateDetail = date

            val dbHelper = DBHelper(activity!!)

            val db = dbHelper.readableDatabase

            val projection = arrayOf(
                BaseColumns._ID,
                PhysicalRecordContract.PhysicalRecordEntry.COLUMN_NAME_BODY_WEIGHT,
                PhysicalRecordContract.PhysicalRecordEntry.COLUMN_NAME_BODY_FAT_PERCENTAGE,
                PhysicalRecordContract.PhysicalRecordEntry.COLUMN_NAME_CREATED_AT)

            val dateBegin = model.dateDetail + " 23:59:59"
            val dateEnd = model.dateDetail + " 00:00:00"

            val sql = "select bodyWeight, bodyFatPercentage, createdAt from physicalRecord where createdAt <= ? and createdAt >= ?  order by _id desc limit 1;"
            val cursor = db.rawQuery(sql, arrayOf(dateBegin, dateEnd))

            with(cursor) {
                while (moveToNext()) {
                    model.bodyWeight = cursor.getString(0)
                    model.bodyFatPercentage = cursor.getString(1)
                    model.basalMetabolicRate = cursor.getString(2)
                }
            }

            // 画面遷移
            val action = CalendarFragmentDirections.actionNavigationCalendarToNavigationDate()
            findNavController().navigate(action)
        }
    }

    private fun arrangeFormatt(x: Int): String {
        if (x >= 10) {
            return x.toString()
        } else {
            return "0$x"
        }
    }
}