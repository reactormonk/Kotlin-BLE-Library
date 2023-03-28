package no.nordicsemi.android.kotlin.ble.client.facade.errors

import no.nordicsemi.android.kotlin.ble.core.data.BleGattProperty

class MissingPropertyException(property: BleGattProperty) : Exception(
    "Operation cannot be performed because of the missing property: $property"
)