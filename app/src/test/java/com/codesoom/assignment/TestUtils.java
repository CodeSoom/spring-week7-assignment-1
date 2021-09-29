package com.codesoom.assignment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;

public class TestUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T content(ResultActions actions, Class<T> type)
            throws UnsupportedEncodingException, JsonProcessingException {
        MvcResult mvcResult = actions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        return objectMapper.readValue(content, type);
    }
}
