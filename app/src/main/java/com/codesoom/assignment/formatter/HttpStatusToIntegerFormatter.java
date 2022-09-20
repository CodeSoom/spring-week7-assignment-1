package com.codesoom.assignment.formatter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class HttpStatusToIntegerFormatter implements Formatter<Integer> {
    @Override
    public Integer parse(String text, Locale locale) throws ParseException {
        return null;
    }

    @Override
    public String print(Integer object, Locale locale) {
        return null;
    }
}
