package com.example.nogorok.features.survey

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.R
import com.example.nogorok.databinding.ItemBannerFooterNextBinding
import com.example.nogorok.databinding.ItemBannerQuestionBinding

private const val TYPE_QUESTION = 0
private const val TYPE_FOOTER = 1

class QuestionAdapter(
    private val items: MutableList<Question>,
    private val options: List<String>,
    private val onComplete: (answers: List<Int>) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // ✅ 푸터 바인딩을 저장해서 어디서든 enable 상태 갱신
    private var footerBinding: ItemBannerFooterNextBinding? = null

    override fun getItemCount(): Int = items.size + 1
    override fun getItemViewType(position: Int): Int =
        if (position < items.size) TYPE_QUESTION else TYPE_FOOTER

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inf = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_QUESTION) {
            QuestionVH(ItemBannerQuestionBinding.inflate(inf, parent, false))
        } else {
            FooterVH(ItemBannerFooterNextBinding.inflate(inf, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is QuestionVH) holder.bind(items[position]) else (holder as FooterVH).bind()
    }

    private fun areAllAnswered(): Boolean = items.all { it.selectedIndex in 0..3 }
    private fun updateCompleteEnabled() {
        footerBinding?.btnComplete?.isEnabled = areAllAnswered()
    }

    inner class QuestionVH(private val b: ItemBannerQuestionBinding) :
        RecyclerView.ViewHolder(b.root) {

        private val bold by lazy { ResourcesCompat.getFont(b.root.context, R.font.pretendard_bold) }
        private val medium by lazy { ResourcesCompat.getFont(b.root.context, R.font.pretendard_medium) }

        fun bind(q: Question) {
            b.tvQuestion.text = q.text

            val radios = listOf(b.rb1, b.rb2, b.rb3, b.rb4)
            radios.forEachIndexed { index, rb ->
                rb.text = options[index]
                rb.isChecked = (q.selectedIndex == index)
                rb.typeface = if (rb.isChecked) bold else medium
                rb.setOnClickListener {
                    q.selectedIndex = index
                    radios.forEachIndexed { i, btn ->
                        btn.isChecked = (i == index)
                        btn.typeface = if (btn.isChecked) bold else medium
                    }
                    updateCompleteEnabled() // ✅ 매 선택 시 ‘다음’ 활성화 여부 갱신
                }
            }
        }
    }

    inner class FooterVH(private val b: ItemBannerFooterNextBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind() {
            footerBinding = b
            // 초기 상태 반영(처음엔 disabled)
            updateCompleteEnabled()

            b.btnComplete.setOnClickListener {
                if (areAllAnswered()) {
                    onComplete(items.map { it.selectedIndex })
                }
                // areAllAnswered가 false면 클릭 무시(버튼도 disabled라 실제로는 눌리지 않음)
            }
        }
    }
}

data class Question(
    val text: String,
    var selectedIndex: Int // 0..3, 미선택 -1
)
