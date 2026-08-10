package edu.cnm.deepdive.coffeeshop.di;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import edu.cnm.deepdive.coffeeshop.repository.SessionManager;
import edu.cnm.deepdive.coffeeshop.service.openapi.AuthApi;
import edu.cnm.deepdive.coffeeshop.service.openapi.FavoriteApi;
import edu.cnm.deepdive.coffeeshop.service.openapi.HealthApi;
import edu.cnm.deepdive.coffeeshop.service.openapi.InterestApi;
import edu.cnm.deepdive.coffeeshop.service.openapi.MatchApi;
import edu.cnm.deepdive.coffeeshop.service.openapi.PreferenceApi;
import edu.cnm.deepdive.coffeeshop.service.openapi.ProfileApi;
import edu.cnm.deepdive.coffeeshop.service.openapi.RatingApi;
import edu.cnm.deepdive.coffeeshop.service.openapi.ShopApi;
import edu.cnm.deepdive.coffeeshop.service.openapi.VisitApi;
import javax.inject.Singleton;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public final class NetworkModule {

  // TODO Point at the deployed server URL; this targets the emulator's host loopback.
  private static final String BASE_URL = "http://10.0.2.2:8080/";

  private NetworkModule() {
  }

  @Provides
  @Singleton
  static Moshi provideMoshi() {
    return new Moshi.Builder()
        .add(new UuidJsonAdapter())
        .add(new OffsetDateTimeJsonAdapter())
        .addLast(new KotlinJsonAdapterFactory())
        .build();
  }

  @Provides
  @Singleton
  static OkHttpClient provideOkHttpClient(SessionManager sessionManager) {
    Interceptor authInterceptor = (chain) -> {
      Request original = chain.request();
      String token = sessionManager.getToken();
      Request request = (token != null)
          ? original.newBuilder().header("Authorization", "Bearer " + token).build()
          : original;
      return chain.proceed(request);
    };
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
    return new OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build();
  }

  @Provides
  @Singleton
  static Retrofit provideRetrofit(OkHttpClient client, Moshi moshi) {
    return new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build();
  }

  @Provides
  @Singleton
  static AuthApi provideAuthApi(Retrofit retrofit) {
    return retrofit.create(AuthApi.class);
  }

  @Provides
  @Singleton
  static InterestApi provideInterestApi(Retrofit retrofit) {
    return retrofit.create(InterestApi.class);
  }

  @Provides
  @Singleton
  static ShopApi provideShopApi(Retrofit retrofit) {
    return retrofit.create(ShopApi.class);
  }

  @Provides
  @Singleton
  static VisitApi provideVisitApi(Retrofit retrofit) {
    return retrofit.create(VisitApi.class);
  }

  @Provides
  @Singleton
  static RatingApi provideRatingApi(Retrofit retrofit) {
    return retrofit.create(RatingApi.class);
  }

  @Provides
  @Singleton
  static ProfileApi provideProfileApi(Retrofit retrofit) {
    return retrofit.create(ProfileApi.class);
  }

  @Provides
  @Singleton
  static FavoriteApi provideFavoriteApi(Retrofit retrofit) {
    return retrofit.create(FavoriteApi.class);
  }

  @Provides
  @Singleton
  static PreferenceApi providePreferenceApi(Retrofit retrofit) {
    return retrofit.create(PreferenceApi.class);
  }

  @Provides
  @Singleton
  static MatchApi provideMatchApi(Retrofit retrofit) {
    return retrofit.create(MatchApi.class);
  }

  @Provides
  @Singleton
  static HealthApi provideHealthApi(Retrofit retrofit) {
    return retrofit.create(HealthApi.class);
  }

}
