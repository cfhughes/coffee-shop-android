package edu.cnm.deepdive.coffeeshop.model.conversion

import edu.cnm.deepdive.coffeeshop.model.domain.Shop
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.ShopDto

 internal fun ShopDto.toShop(): Shop {
    return Shop(
        id = this.id,
        name = this.name,
        address = this.address,
        hours = this.hours,
        lat = this.lat,
        lng = this.lng,
        phone = this.phone,
        imageUrl = this.imageUrl,
    )

}