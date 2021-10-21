package dev.alexengrig.myfe.view;

import dev.alexengrig.myfe.model.MyFile;

import javax.swing.*;

public class MyImage extends JLabel {

    public MyImage(MyFile file) {
        super(new ImageIcon(file.getPath()));
    }

}
