package com.chordcraft.di;

import com.chordcraft.data.local.AppDatabase;
import com.chordcraft.data.local.SongDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AppModule_ProvideSongDaoFactory implements Factory<SongDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideSongDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SongDao get() {
    return provideSongDao(databaseProvider.get());
  }

  public static AppModule_ProvideSongDaoFactory create(Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideSongDaoFactory(databaseProvider);
  }

  public static SongDao provideSongDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSongDao(database));
  }
}
