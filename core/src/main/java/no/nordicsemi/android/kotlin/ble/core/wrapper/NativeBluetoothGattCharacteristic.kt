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

package no.nordicsemi.android.kotlin.ble.core.wrapper

import android.bluetooth.BluetoothGattCharacteristic
import no.nordicsemi.android.common.core.DataByteArray
import no.nordicsemi.android.common.core.toDisplayString
import java.util.UUID

/**
 * Native variant of a characteristic. It's a wrapper around [BluetoothGattCharacteristic].
 */
data class NativeBluetoothGattCharacteristic(
    val characteristic: BluetoothGattCharacteristic
) : IBluetoothGattCharacteristic {

    override val uuid: UUID
        get() = characteristic.uuid
    override val instanceId: Int
        get() = characteristic.instanceId
    override val permissions: Int
        get() = characteristic.permissions
    override val properties: Int
        get() = characteristic.properties
    override var writeType: Int
        get() = characteristic.writeType
        set(value) {
            characteristic.writeType = value
        }
    override var value: DataByteArray
        get() = DataByteArray(characteristic.value ?: byteArrayOf())
        set(value) {
            characteristic.value = value.value
        }
    override val descriptors: List<IBluetoothGattDescriptor>
        get() = characteristic.descriptors.map { NativeBluetoothGattDescriptor(it) }

    override fun toString(): String {
        return StringBuilder()
            .append(" { ")
            .append("uuid : $uuid, ")
            .append("instanceId : $instanceId, ")
            .append("permissions : $permissions, ")
            .append("properties : $properties, ")
            .append("writeType : $writeType, ")
            .append("value : $value, ")
            .append("descriptors : $descriptors ")
            .append("}")
            .toString()
    }
}