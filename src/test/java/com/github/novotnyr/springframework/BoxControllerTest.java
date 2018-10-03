package com.github.novotnyr.springframework;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BoxControllerTest extends AbstractControllerTest {

    @Test
    public void testPostOk() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", 1);
        JSONObject inner = new JSONObject();
        jsonObject.put("inner", inner);
        inner.put("innerId", "1");
        inner.put("innerName", "innerN");

        this.mvc.perform(
                post("/boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(200));
    }

    @Test
    public void testPostInner() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", 1);
        JSONObject inner = new JSONObject();
        jsonObject.put("inner", inner);

        this.mvc.perform(
                post("/boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("error.validation.field[0].code").value("required-field"))
                .andExpect(jsonPath("error.validation.field[0].name").value("inner.innerId"))
                .andExpect(jsonPath("error.validation.field[1].code").value("required-field"))
                .andExpect(jsonPath("error.validation.field[1].name").value("inner.innerName"))
                .andExpect(status().is(422));
    }

    @Test
    public void testPost() throws Exception {
        JSONObject jsonObject = new JSONObject();

        this.mvc.perform(
                post("/boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("error.validation.field[0].code").value("required-field"))
                .andExpect(jsonPath("error.validation.field[0].name").value("count"))
                .andExpect(status().is(422));
    }

    @Test
    public void testPostWithTypeMismatch() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", 12);
        jsonObject.put("id", "yes");

        this.mvc.perform(
                post("/boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error.validation.field[0].code").value("invalid-property"))
                .andExpect(jsonPath("error.validation.field[0].name").value("id"))
                .andDo(print())
                .andExpect(status().is(422));
    }

    @Test
    public void testPostLaxly() throws Exception {
        JSONObject jsonObject = new JSONObject();

        this.mvc.perform(
                post("/boxes?laxly=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(200));
    }


    @Test
    public void testPostLaxlyWithBodyAndErrors() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "smelly stuff");

        this.mvc.perform(
                post("/boxes?errors=true&laxly=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error.validation.field[0].code").value("required-field"))
                .andExpect(jsonPath("error.validation.field[1].code").value("nasty-box"))
                .andDo(print())
                .andExpect(status().is(200));
    }

    @Test
    public void testPostWithUnavailableSchema() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "smelly stuff");

        this.mvc.perform(
                post("/unavailable-schema")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error.validation.global[0].message").value("Internal validation error"))
                .andDo(print())
                .andExpect(status().is(422));
    }

}
