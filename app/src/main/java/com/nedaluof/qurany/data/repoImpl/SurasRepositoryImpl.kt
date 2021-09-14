package com.nedaluof.qurany.data.repoImpl

import android.content.Context
import com.nedaluof.qurany.data.model.Reciter
import com.nedaluof.qurany.data.model.Sura
import com.nedaluof.qurany.domain.repositories.SurasRepository
import com.nedaluof.qurany.util.SuraUtil
import com.nedaluof.qurany.util.getLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

/**
 * Created by nedaluof on 12/11/2020.
 */
class SurasRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
) : SurasRepository {

    override fun checkIfSuraExist(subPath: String) =
        File(context.getExternalFilesDir(null).toString() + subPath).exists()


    /**
     * Todo issue:
     *  only show language as user device and handle operations
     *  of save and play in english language
     *  */
    override fun getMappedReciterSuras(reciterData: Reciter): List<Sura> {
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
                        reciterData.name ?: "",
                        SuraUtil.getPlayerTitle(
                            currentSura,
                            reciterData.name ?: ""
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
                        reciterData.rewaya ?: "",
                        suraUrl,
                        reciterData.name ?: "",
                        SuraUtil.getPlayerTitle(currentSura, reciterData.name ?: ""),
                        subPath
                    )
                )
            }
        }
        return mappedReciterSuras
    }
}
