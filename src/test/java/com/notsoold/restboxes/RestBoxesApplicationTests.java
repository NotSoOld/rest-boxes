package com.notsoold.restboxes;

import com.notsoold.restboxes.controller.BoxNotFoundException;
import com.notsoold.restboxes.controller.GenericRestController;
import com.notsoold.restboxes.dao.RestBoxDao;
import com.notsoold.restboxes.dao.RestItemDao;
import com.notsoold.restboxes.model.RestBox;
import com.notsoold.restboxes.model.RestItem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(args = "example.xml",
                properties = { "spring.datasource.url=jdbc:postgresql://localhost:5432/restboxesdb" })
@AutoConfigureMockMvc
class RestBoxesApplicationTests {

    private RestBoxDao restBoxDao;
    private RestItemDao restItemDao;
    private GenericRestController genericRestController;
    private MockMvc mockMvc;

    @Autowired
    RestBoxesApplicationTests(RestBoxDao restBoxDao, RestItemDao restItemDao, GenericRestController genericRestController, MockMvc mockMvc) {
        this.restBoxDao = restBoxDao;
        this.restItemDao = restItemDao;
        this.genericRestController = genericRestController;
        this.mockMvc = mockMvc;
    }

    @Test
    void contextLoads() {
    }

    /**
     * Made solely for example.xml.
     */
    @Test
    void xmlParsed() {
        RestBox box = restBoxDao.findById(3L).orElseThrow(() -> new BoxNotFoundException(3L));
        RestItem item = restItemDao.findById(4L).orElseThrow(() -> new NullPointerException("Item 4 not found"));
        assert item.getContainedIn().equals(box);

        item = restItemDao.findById(6L).orElseThrow(() -> new NullPointerException("Item 6 not found"));
        assert item.getColor() == null;
        assert item.getContainedIn() == null;
    }

    @Test
    void testController() throws Exception {
        Assertions.assertThat(genericRestController).isNotNull();

        mockMvc.perform(MockMvcRequestBuilders.post("/test")
                        .content("{\"box\":\"1\",\"color\":\"red\"}").contentType(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("[2,3]"));

        mockMvc.perform(MockMvcRequestBuilders.post("/test")
                        .content("{}").contentType(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.post("/test")
                        .content("{\"box\":\"0\",\"color\":\"black\"}").contentType(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders.post("/test"))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

}
