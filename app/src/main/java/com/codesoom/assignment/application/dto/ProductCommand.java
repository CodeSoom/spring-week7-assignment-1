package com.codesoom.assignment.application.dto;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;

@Generated
public class ProductCommand {

    @Generated
    @Getter
    @Builder
    @ToString
    public static class Register {
        private final String name;

        private final String maker;

        private final Integer price;

        private final String imageUrl;

    }

    @Generated
    @Getter
    @Builder
    @ToString
    public static class Update {
        private final Long id;

        private final String name;

        private final String maker;

        private final Integer price;

        private final String imageUrl;

    }
}

