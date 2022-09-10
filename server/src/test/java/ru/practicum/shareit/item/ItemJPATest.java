package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemJPATest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    TestEntityManager em;

    User user = new User(null, "testUser", "test@email.com");

    @Test
    void searchAvailableItemsByKeyword_shouldReturnItemWhenKeywordContainsInNameAndItemIsAvailable() {
        Item item = Item.builder().name("testItem").owner(user).description("qwe").available(true).build();
        Item item2 = Item.builder().name("qweTestqwe").owner(user).description("qwe").available(true).build();
        em.persist(user);
        em.persist(item);
        em.persist(item2);
//        TypedQuery<Item> query  = em.getEntityManager()
//                .createQuery("select i from Item i " +
//                        "where i.available = true and upper(i.name) like upper(concat('%', :text, '%')) " +
//                        "or i.available = true and upper(i.description) like upper(concat('%', :text, '%'))", Item.class);
//        query.setParameter("text", "tEsT");
//        List<Item> items = query.getResultList();

        List<Item> items = itemRepository
                .searchAvailableItemsByKeyword("tEsT", PageRequest.of(0 / 10, 10));
        assertThat(items, hasSize(2));
        assertThat(items.get(0), equalTo(item));
        assertThat(items.get(1), equalTo(item2));
    }

    @Test
    void searchAvailableItemsByKeyword_shouldReturnItemWhenKeywordContainsInDescriptionAndItemIsAvailable() {
        Item item = Item.builder().name("qwe").owner(user).description("testItem").available(true).build();
        Item item2 = Item.builder().name("qwe").owner(user).description("qwetestItem").available(true).build();
        em.persist(user);
        em.persist(item);
        em.persist(item2);
        List<Item> items = itemRepository.searchAvailableItemsByKeyword("tEsT", PageRequest.of(0 / 10, 10));
        assertThat(items, hasSize(2));
        assertThat(items.get(0), equalTo(item));
    }

    @Test
    void searchAvailableItemsByKeyword_shouldNotReturnUnavailableItemWhenKeywordContains() {
        Item item = Item.builder().name("test").owner(user).description("testItem").available(false).build();
        Item item2 = Item.builder().name("qwe").owner(user).description("qwetestItem").available(true).build();
        em.persist(user);
        em.persist(item);
        em.persist(item2);
        List<Item> items = itemRepository.searchAvailableItemsByKeyword("tEsT", PageRequest.of(0 / 10, 10));
        assertThat(items, hasSize(1));
        assertThat(items.get(0), equalTo(item2));
    }
}
