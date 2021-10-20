package dev.alexengrig.myfe.service;

import dev.alexengrig.myfe.model.MyFile;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface MyPathPreviewBackgroundService {

    void loadTextPreview(MyFile file, Consumer<Stream<String>> handler);

}
