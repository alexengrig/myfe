package dev.alexengrig.myfe.view;

import javax.swing.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyText extends JTextArea {

    public void append(Stream<String> lines) {
        //FIXME: It's slow
        String text = lines.collect(Collectors.joining(System.lineSeparator(), getText(), System.lineSeparator()));
        setText(text);
    }

}
