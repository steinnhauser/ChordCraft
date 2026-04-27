package com.chordcraft.ui.viewmodels;

import androidx.lifecycle.SavedStateHandle;
import com.chordcraft.data.repository.SongRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class PlaybackViewModel_Factory implements Factory<PlaybackViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<SongRepository> repositoryProvider;

  public PlaybackViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<SongRepository> repositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public PlaybackViewModel get() {
    return newInstance(savedStateHandleProvider.get(), repositoryProvider.get());
  }

  public static PlaybackViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<SongRepository> repositoryProvider) {
    return new PlaybackViewModel_Factory(savedStateHandleProvider, repositoryProvider);
  }

  public static PlaybackViewModel newInstance(SavedStateHandle savedStateHandle,
      SongRepository repository) {
    return new PlaybackViewModel(savedStateHandle, repository);
  }
}
