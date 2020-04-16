package com.example.project.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.project.R
import com.example.project.viewmodel.SharedViewModel

// Fragment クラスを継承
class RecordFragment : Fragment(R.layout.fragment_record) {

    private lateinit var model: SharedViewModel

    // 入力フォームを初期化
    private lateinit var bodyWeightForm: EditText // 体重入力フォーム
    private lateinit var bodyFatPercentageForm: EditText // 体脂肪入力フォーム
    private lateinit var skeletalMusclePercentageForm: EditText // 骨格筋率入力フォーム
    private lateinit var basalMetabolicRateForm: EditText // 基礎代謝入力フォーム

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 入力フォームを取得
        bodyWeightForm = view.findViewById(R.id.bodyWeightForm)
        bodyFatPercentageForm = view.findViewById(R.id.bodyFatPercentageFrom)
        skeletalMusclePercentageForm = view.findViewById(R.id.skeletalMusclePercentageForm)
        basalMetabolicRateForm = view.findViewById(R.id.basalMetabolicRateForm)

        model = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        // 登録ボタン
        val registerButton = view.findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            // 登録ボタンからHomeへの画面遷移
            val action = RecordFragmentDirections.actionNavigationRecordToNavigationHome()
            findNavController().navigate(action)

            // フォームに入力した値を反映
            model.bodyWeight = bodyWeightForm.text.toString()
            model.bodyFatPercentage = bodyFatPercentageForm.text.toString()
            model.skeletalMusclePercentage = skeletalMusclePercentageForm.text.toString()
            model.basalMetabolicRate = basalMetabolicRateForm.text.toString()

        }
    }
}