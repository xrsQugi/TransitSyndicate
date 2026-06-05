package com.transitsyndicate.presentation.game;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

public class GameViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    public GameViewModelFactory(@NonNull Application app) {
        super(app);
    }
}
