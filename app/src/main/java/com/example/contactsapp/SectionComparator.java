package com.example.contactsapp;
import java.util.Comparator;
public class SectionComparator implements Comparator<String> {

    @Override
    public int compare(String a, String b) {
        return getPriority(a) - getPriority(b);
    }

    private int getPriority(String letter) {
        if (letter.matches("[А-ЯЁ]")) return letter.charAt(0);       // русские буквы первыми
        if (letter.matches("[A-Z]")) return 1000 + letter.charAt(0); // латиница вторыми
        return 2000;                                                  // символы и цифры — в конце
    }
}
