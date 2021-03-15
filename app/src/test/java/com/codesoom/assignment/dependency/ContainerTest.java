package com.codesoom.assignment.dependency;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ContainerTest {
    @Test
    void createContainer() {
        Container container = new Container();
        assertThat(container).isNotNull();
    }
}
