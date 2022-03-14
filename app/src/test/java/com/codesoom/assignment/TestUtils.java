package com.codesoom.assignment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;

public class TestUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    public static <T> T content(ResultActions resultActions, Class<T> type)
            throws UnsupportedEncodingException, JsonProcessingException {
        MvcResult mvcResult = resultActions.andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        return mapper.readValue(content, type);
    }
}
