/*
 * Copyright (c) 2023, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.kotlin.ble.client.main.callback

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import no.nordicsemi.android.common.core.ApplicationScope
import no.nordicsemi.android.common.logger.BleLogger
import no.nordicsemi.android.common.logger.DefaultConsoleLogger
import no.nordicsemi.android.kotlin.ble.client.api.GattClientAPI
import no.nordicsemi.android.kotlin.ble.client.main.bonding.BondingBroadcastReceiver
import no.nordicsemi.android.kotlin.ble.client.mock.BleMockGatt
import no.nordicsemi.android.kotlin.ble.client.real.ClientBleGattCallback
import no.nordicsemi.android.kotlin.ble.client.real.NativeClientBleAPI
import no.nordicsemi.android.kotlin.ble.core.MockClientDevice
import no.nordicsemi.android.kotlin.ble.core.MockServerDevice
import no.nordicsemi.android.kotlin.ble.core.RealServerDevice
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import no.nordicsemi.android.kotlin.ble.core.data.BleGattConnectOptions
import no.nordicsemi.android.kotlin.ble.mock.MockEngine

/**
 * Factory class responsible for creating [ClientBleGatt] instance.
 */
@SuppressLint("InlinedApi")
internal object ClientBleGattFactory {

    /**
     * Creates [ClientBleGatt] and initialize connection based on its parameters.
     *
     * @param context An application context.
     * @param macAddress MAC address of a real server device.
     * @param options Connection configuration.
     * @param logger A logger responsible for displaying logs.
     * @return [ClientBleGatt] instance.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun connect(
        context: Context,
        macAddress: String,
        options: BleGattConnectOptions = BleGattConnectOptions(),
        logger: BleLogger = DefaultConsoleLogger(context),
        scope: CoroutineScope? = null
    ): ClientBleGatt {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val device = bluetoothAdapter.getRemoteDevice(macAddress)
        val realDevice = RealServerDevice(device)
        return connectDevice(realDevice, context, options, logger, scope ?: ApplicationScope)
    }

    /**
     * Creates [ClientBleGatt] and initialize connection based on its parameters.
     *
     * @param context An application context.
     * @param device Server device. It can be mocked or real BLE device.
     * @param options Connection configuration.
     * @param logger A logger responsible for displaying logs.
     * @return [ClientBleGatt] instance.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun connect(
        context: Context,
        device: ServerDevice,
        options: BleGattConnectOptions = BleGattConnectOptions(),
        logger: BleLogger = DefaultConsoleLogger(context),
        scope: CoroutineScope?
    ): ClientBleGatt {
        val scopeOrDefault = scope ?: ApplicationScope
        return when (device) {
            is MockServerDevice -> connectDevice(device, options, logger, scopeOrDefault)
            is RealServerDevice -> connectDevice(device, context, options, logger, scopeOrDefault)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private suspend fun connectDevice(
        device: MockServerDevice,
        options: BleGattConnectOptions,
        logger: BleLogger,
        scope: CoroutineScope
    ): ClientBleGatt {
        val clientDevice = MockClientDevice()
        val gatt = BleMockGatt(MockEngine, device, clientDevice, options.autoConnect, options.closeOnDisconnect)
        return ClientBleGatt(gatt, logger, scope = scope)
            .also { MockEngine.connectToServer(device, clientDevice, gatt, options) }
            .also { it.waitForConnection() }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private suspend fun connectDevice(
        device: RealServerDevice,
        context: Context,
        options: BleGattConnectOptions,
        logger: BleLogger,
        scope: CoroutineScope
    ): ClientBleGatt {
        val gatt = device.createConnection(context, options)
        return ClientBleGatt(gatt, logger, scope = scope)
            .also { it.waitForConnection() }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun RealServerDevice.createConnection(
        context: Context,
        options: BleGattConnectOptions,
    ): GattClientAPI {
        val gattCallback = ClientBleGattCallback()

        BondingBroadcastReceiver.register(context, this, gattCallback)

        val gatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            device.connectGatt(
                context,
                options.autoConnect,
                gattCallback,
                BluetoothDevice.TRANSPORT_LE,
                options.phy?.value ?: 0
            )
        } else {
            device.connectGatt(context, options.autoConnect, gattCallback)
        }

        return NativeClientBleAPI(gatt, gattCallback, options.autoConnect, options.closeOnDisconnect)
    }
}
