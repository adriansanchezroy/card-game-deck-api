package com.cardgamedeck.cli.menu;

import java.util.Scanner;

public interface IMenuItem {
    String getTitle();
    boolean execute(Scanner scanner);
}