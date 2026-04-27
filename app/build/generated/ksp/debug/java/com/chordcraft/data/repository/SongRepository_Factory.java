package com.chordcraft.data.repository;

import com.chordcraft.data.local.SongDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SongRepository_Factory implements Factory<SongRepository> {
  private final Provider<SongDao> songDaoProvider;

  public SongRepository_Factory(Provider<SongDao> songDaoProvider) {
    this.songDaoProvider = songDaoProvider;
  }

  @Override
  public SongRepository get() {
    return newInstance(songDaoProvider.get());
  }

  public static SongRepository_Factory create(Provider<SongDao> songDaoProvider) {
    return new SongRepository_Factory(songDaoProvider);
  }

  public static SongRepository newInstance(SongDao songDao) {
    return new SongRepository(songDao);
  }
}
