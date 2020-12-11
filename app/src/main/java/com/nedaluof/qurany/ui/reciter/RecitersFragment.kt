package com.nedaluof.qurany.ui.reciter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.nedaluof.qurany.R
import com.nedaluof.qurany.data.model.Reciter
import com.nedaluof.qurany.databinding.FragmentRecitersBinding
import com.nedaluof.qurany.ui.component.NewRecitersAdapter
import com.nedaluof.qurany.ui.sura.ReciterSurasActivity
import com.nedaluof.qurany.util.RxUtil
import com.nedaluof.qurany.util.Utility
import com.tapadoo.alerter.Alerter
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

/**
 * Created by nedaluof on 12/11/2020.
 */
@AndroidEntryPoint
class RecitersFragment : Fragment() {
    private var _binding: FragmentRecitersBinding? = null
    private val binding: FragmentRecitersBinding
        get() = _binding!!

    private var networkDisposable: Disposable? = null

    //private lateinit var reciterAdapter: ReciterAdapter
    private lateinit var reciterAdapter: NewRecitersAdapter

    private val viewModel: RecitersViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecitersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkDisposable = ReactiveNetwork
                .observeNetworkConnectivity(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ available ->
                    if (available.available()) {
                        initComponents()
                    } else {
                        binding.reciterListLayout.visibility = View.GONE
                        binding.noInternetLayout.visibility = View.VISIBLE
                    }
                }, { Log.d(TAG, "onViewCreated: ${it.message}") })
    }

    private fun initComponents() {
        if (binding.reciterListLayout.visibility == View.GONE) {
            binding.reciterListLayout.visibility = View.VISIBLE
        }

        reciterAdapter = NewRecitersAdapter().apply {
            listener = object : NewRecitersAdapter.ReciterAdapterListener {
                override fun onReciterClicked(reciter: Reciter) {
                    startActivity(Intent(context, ReciterSurasActivity::class.java)
                            .putExtra("reciterData", reciter))
                }

                override fun onAddToFavoriteClicked(reciter: Reciter) {
                    viewModel.addReciterToMyReciters(reciter)
                }

            }
        }
        binding.recitersRecyclerView.adapter = ScaleInAnimationAdapter(reciterAdapter).apply {
            setFirstOnly(false)
        }
        with(viewModel) {
            getReciters(Utility.getLanguage())
            reciters.observe(viewLifecycleOwner) {
                reciterAdapter.addReciters(it as ArrayList<Reciter>)
            }

            error.observe(viewLifecycleOwner) {
                Toast.makeText(activity, "Error: ${it.first}", Toast.LENGTH_LONG).show()
            }

            loading.observe(viewLifecycleOwner) { show ->
                if (show) {
                    binding.proReciters.visibility = View.VISIBLE
                } else {
                    binding.proReciters.visibility = View.GONE
                }
            }

            resultOfAddReciter.observe(viewLifecycleOwner) {
                if (it) {
                    Alerter.create(activity)
                            .setText(R.string.alrt_add_success_title)
                            .setText(R.string.alrt_add_success_msg)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.green_200)
                            .show()
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        RxUtil.dispose(networkDisposable)
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "RecitersFragmentNew"
    }
}