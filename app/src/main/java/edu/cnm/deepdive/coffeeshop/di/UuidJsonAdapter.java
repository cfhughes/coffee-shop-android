package edu.cnm.deepdive.coffeeshop.di;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import java.util.UUID;

/**
 * Moshi has no built-in adapter for {@link UUID}, and generated DTOs across this app declare
 * {@code UUID} id fields, so this adapter is registered globally on the shared {@code Moshi}
 * instance in {@link NetworkModule}.
 */
final class UuidJsonAdapter {

  @ToJson
  String toJson(UUID uuid) {
    return uuid.toString();
  }

  @FromJson
  UUID fromJson(String value) {
    return UUID.fromString(value);
  }

}
