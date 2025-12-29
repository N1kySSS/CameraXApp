package com.ortin.camerax.di

import com.ortin.camerax.presenation.viewModel.GalleryScreenViewModel
import com.ortin.camerax.presenation.viewModel.PhotoScreenViewModel
import com.ortin.camerax.presenation.viewModel.VideoScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel<PhotoScreenViewModel> { PhotoScreenViewModel() }

    viewModel<VideoScreenViewModel> { VideoScreenViewModel() }

    viewModel<GalleryScreenViewModel> { GalleryScreenViewModel() }
}
