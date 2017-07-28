package com.testwithspring.master.task

import com.testwithspring.master.UnitTest
import org.junit.experimental.categories.Category
import spock.lang.Specification

import static org.hamcrest.Matchers.hasSize

@Category(UnitTest.class)
class RepositoryTaskSearchServiceSpec extends Specification {

    private static TASK_ID = 1L
    private static TITLE = "Write an example test"
    private static SEARCH_TERM_NO_RESULTS = "notFound"
    private static SEARCH_TERM_ONE_RESULT = "examp"
    private static STATUS = TaskStatus.OPEN

    def repository = Stub(TaskRepository)
    def service = new RepositoryTaskSearchService(repository)

    def 'Search tasks'() {

        when: 'No tasks is found with the given search term'
        repository.search(SEARCH_TERM_NO_RESULTS) >> []

        and: 'We perform the search'
        def results = service.search(SEARCH_TERM_NO_RESULTS)

        then: 'Should return an empty list'
        results.isEmpty()

        when: 'One task is found with the given search term'
        repository.search(SEARCH_TERM_ONE_RESULT) >> [new TaskListDTO(id: TASK_ID, status: STATUS, title: TITLE)]

        and: 'We perform the search'
        results = service.search(SEARCH_TERM_ONE_RESULT)

        then: 'Should return one task'
        results hasSize(1)

        and: 'Should return a task with the correct information'
        def found = results[0]

        found.id == TASK_ID
        found.title == TITLE
        found.status == STATUS
    }
}
