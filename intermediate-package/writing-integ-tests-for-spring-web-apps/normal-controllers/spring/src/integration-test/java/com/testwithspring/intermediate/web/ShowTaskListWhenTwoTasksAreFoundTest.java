package com.testwithspring.intermediate.web;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.testwithspring.intermediate.IntegrationTest;
import com.testwithspring.intermediate.IntegrationTestContext;
import com.testwithspring.intermediate.ReplacementDataSetLoader;
import com.testwithspring.intermediate.Tasks;
import com.testwithspring.intermediate.config.Profiles;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestContext.class})
//@ContextConfiguration(locations = {"classpath:integration-test-context.xml"})
@WebAppConfiguration
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        ServletTestExecutionListener.class
})
@DatabaseSetup("/com/testwithspring/intermediate/tasks.xml")
@DbUnitConfiguration(dataSetLoader = ReplacementDataSetLoader.class)
@Category(IntegrationTest.class)
@ActiveProfiles(Profiles.INTEGRATION_TEST)
public class ShowTaskListWhenTwoTasksAreFoundTest {

    private static final String TASK_PROPERTY_NAME_ID = "id";
    private static final String TASK_PROPERTY_NAME_STATUS = "status";
    private static final String TASK_PROPERTY_NAME_TITLE = "title";

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    @Before
    public void configureSystemUnderTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext)
                .build();
    }

    @Test
    public void shouldReturnHttpStatusCodeOk() throws Exception {
        openTaskListPage()
                .andExpect(status().isOk());
    }

    @Test
    public void shouldRenderTaskListView() throws Exception {
        openTaskListPage()
                .andExpect(view().name("task/list"));
    }

    @Test
    public void shouldForwardUserToTaskListPageUrl() throws Exception {
        openTaskListPage()
                .andExpect(forwardedUrl("/WEB-INF/jsp/task/list.jsp"));
    }

    @Test
    public void shouldShowCorrectTasks() throws Exception {
        openTaskListPage()
                .andExpect(model().attribute(WebTestConstants.ModelAttributes.TASK_LIST, allOf(
                        hasItem(allOf(
                                hasProperty(TASK_PROPERTY_NAME_ID, is(Tasks.WriteExampleApp.ID)),
                                hasProperty(TASK_PROPERTY_NAME_TITLE, is(Tasks.WriteExampleApp.TITLE)),
                                hasProperty(TASK_PROPERTY_NAME_STATUS, is(Tasks.WriteExampleApp.STATUS))
                        )),
                        hasItem(allOf(
                                hasProperty(TASK_PROPERTY_NAME_ID, is(Tasks.WriteLesson.ID)),
                                hasProperty(TASK_PROPERTY_NAME_TITLE, is(Tasks.WriteLesson.TITLE)),
                                hasProperty(TASK_PROPERTY_NAME_STATUS, is(Tasks.WriteLesson.STATUS))
                        ))
                )));
    }

    private ResultActions openTaskListPage() throws Exception {
        return mockMvc.perform(get("/"));
    }
}
