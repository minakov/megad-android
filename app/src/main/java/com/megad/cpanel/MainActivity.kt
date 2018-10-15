package com.megad.cpanel

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.megad.cpanel.core.DeviceManager
import com.megad.cpanel.extensions.addTo
import com.megad.cpanel.ui.DeviceAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_layout.*
import java.net.NetworkInterface
import android.net.DhcpInfo
import android.content.Context.WIFI_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.net.wifi.WifiManager
import java.io.IOException
import java.net.InetAddress


class MainActivity : AppCompatActivity() {

    private val adapter = DeviceAdapter()
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
//        DeviceManager
//                .listen()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(adapter::handleResponse)
//                .addTo(disposable)

        DeviceManager
                .discover(applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
//                .flatMapCompletable { DeviceManager.send(it, PingRequest()) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
                .addTo(disposable)
    }

    override fun onPause() {
        disposable.clear()
        super.onPause()
    }
}
