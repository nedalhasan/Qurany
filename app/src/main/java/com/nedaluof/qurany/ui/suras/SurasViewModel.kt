package com.nedaluof.qurany.ui.suras

import com.nedaluof.qurany.data.model.Reciter
import com.nedaluof.qurany.data.model.Sura
import com.nedaluof.qurany.domain.repositories.SurasRepository
import com.nedaluof.qurany.ui.base.BaseViewModel
import com.nedaluof.qurany.util.SuraUtil
import com.nedaluof.qurany.util.getLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Created by nedaluof on 12/16/2020.
 */
@HiltViewModel
class SurasViewModel @Inject constructor(
    private val repository: SurasRepository
) : BaseViewModel() {

    val reciterName = MutableStateFlow("")
    val reciterSuras = MutableStateFlow<List<Sura>>(emptyList())
    val loading = MutableStateFlow(true)

    fun loadSurasToUI(reciterData: Reciter) {
        reciterName.value = reciterData.name ?: ""
        reciterSuras.value = getMappedReciterSuras(reciterData)
        loading.value = false
    }

    /**
     * Todo issue:
     *  only show language as user device and handle operations of save and play
     *  in english language
     *  */
    private fun getMappedReciterSuras(reciterData: Reciter): List<Sura> {
        val currentReciterSuras = listOf(
            *reciterData.suras!!.split(regex = "\\s*,\\s*".toRegex())
                .toTypedArray()
        )
        val mappedReciterSuras = ArrayList<Sura>()
        for (i in currentReciterSuras.indices) {
            val currentSura = currentReciterSuras[i].toInt()
            if (getLanguage() == "_arabic") {
                val suraName = SuraUtil.getArabicSuraName()[currentSura - 1].suraName
                val suraUrl = reciterData.server + "/" + SuraUtil.getSuraIndex(currentSura) + ".mp3"
                val subPath = "/Qurany/${reciterData.name}/${SuraUtil.getSuraName(currentSura)}.mp3"
                mappedReciterSuras.add(
                    Sura(
                        currentSura,
                        suraName,
                        "رواية : " + reciterData.rewaya,
                        suraUrl,
                        reciterData.name!!,
                        SuraUtil.getPlayerTitle(
                            currentSura,
                            reciterData
                                .name!!
                        ),
                        subPath
                    )
                )
            } else {
                val suraName = SuraUtil.getEnglishSuraName()[currentSura - 1].suraName
                val suraUrl = reciterData.server + "/" + SuraUtil.getSuraIndex(currentSura) + ".mp3"
                val subPath = "/Qurany/${reciterData.name}/${SuraUtil.getSuraName(currentSura)}.mp3"
                mappedReciterSuras.add(
                    Sura(
                        currentSura,
                        suraName,
                        reciterData.rewaya!!,
                        suraUrl,
                        reciterData.name!!,
                        SuraUtil.getPlayerTitle(currentSura, reciterData.name!!),
                        subPath
                    )
                )
            }
        }
        return mappedReciterSuras
    }

    private val _suraExist = MutableStateFlow<Boolean?>(null)
    val isSuraExist: StateFlow<Boolean?> = _suraExist

    fun checkSuraExist(sura: Sura) {
        _suraExist.value = repository.checkIfSuraExist(sura.suraSubPath)
    }
}
