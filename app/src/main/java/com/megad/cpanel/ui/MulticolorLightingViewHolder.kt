package com.megad.cpanel.ui

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.megad.cpanel.R
import com.megad.cpanel.core.Device
import com.megad.cpanel.core.DeviceManager
import com.megad.cpanel.core.requests.SetColorRequest
import com.megad.cpanel.core.requests.TurnOffRequest
import com.megad.cpanel.core.requests.TurnOnRequest
import com.megad.cpanel.core.states.MulticolorLightingState
import com.megad.cpanel.extensions.addTo
import com.megad.cpanel.extensions.showFlowable
import java.util.concurrent.TimeUnit

class MulticolorLightingViewHolder(itemView: View) : DeviceAdapter.ViewHolder(itemView) {
    private val chooseColorButton = itemView.findViewById<ImageButton>(R.id.multicolorLightingChooseColor)
    private val turnOnButton = itemView.findViewById<Button>(R.id.multicolorLightingOnButton)
    private val turnOffButton = itemView.findViewById<Button>(R.id.multicolorLightingOffButton)

    private lateinit var state: MulticolorLightingState

    init {
        chooseColorButton.setOnClickListener {
            ColorPickerDialogBuilder
                .with(itemView.context)
                .setTitle(device.state.name)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .lightnessSliderOnly()
                .showFlowable(state.toColor())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .debounce(20, TimeUnit.MILLISECONDS)
                .flatMapCompletable { DeviceManager.send(device.state.address, SetColorRequest.fromColor(it)) }
                .subscribe()
                .addTo(disposable)
        }
        turnOnButton.setOnClickListener {
            DeviceManager
                .send(device.state.address, TurnOnRequest())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
                .addTo(disposable)
        }
        turnOffButton.setOnClickListener {
            DeviceManager
                .send(device.state.address, TurnOffRequest())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
                .addTo(disposable)
        }
    }

    override fun bind(device: Device) {
        super.bind(device)
        state = device.state.customState as MulticolorLightingState
    }
}
