package com.example.contactsapp;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ContactRepositoryTest {

    // Проверяет, что контакты корректно группируются по первой букве имени
    @Test
    public void groupsContactsByFirstLetterCorrectly() {
        List<Contact> contacts = List.of(
                new Contact(1L, "Анна", "123", null),
                new Contact(2L, "Борис", "456", null),
                new Contact(3L, "Вика", "789", null),
                new Contact(4L, "David", "000", null)
        );

        ContactRepository repository = new TestableContactRepository(contacts);
        List<ListItem> items = repository.getGroupedContacts(null);

        // Ожидаем: 4 заголовка и 4 контакта
        assertEquals(8, items.size());
        assertEquals("А", ((HeaderItem) items.get(0)).getTitle());
        assertEquals("Б", ((HeaderItem) items.get(2)).getTitle());
        assertEquals("В", ((HeaderItem) items.get(4)).getTitle());
        assertEquals("D", ((HeaderItem) items.get(6)).getTitle());
    }

    // Проверяет, что "Ё" нормализуется в "Е"
    @Test
    public void normalizesYoToYe() {
        List<Contact> contacts = List.of(
                new Contact(1L, "Ёлка", "123", null),
                new Contact(2L, "Елена", "456", null)
        );

        ContactRepository repository = new TestableContactRepository(contacts);
        List<ListItem> items = repository.getGroupedContacts(null);

        // Ожидаем одну секцию "Е" и два контакта
        assertEquals(3, items.size());
        assertEquals("Е", ((HeaderItem) items.get(0)).getTitle());
    }

    // Проверяет, что сортировка внутри секции работает корректно по алфавиту
    @Test
    public void sortsContactsAlphabeticallyWithinSection() {
        List<Contact> contacts = List.of(
                new Contact(1L, "Вера", "111", null),
                new Contact(2L, "Василий", "222", null),
                new Contact(3L, "владимир", "333", null)
        );

        ContactRepository repository = new TestableContactRepository(contacts);
        List<ListItem> items = repository.getGroupedContacts(null);

        // Секция В, внутри: Василий, Вера, владимир (по алфавиту, без учёта регистра)
        assertEquals(4, items.size());
        assertEquals("В", ((HeaderItem) items.get(0)).getTitle());
        assertEquals("Василий", ((Contact) items.get(1)).name);
        assertEquals("Вера", ((Contact) items.get(2)).name);
        assertEquals("владимир", ((Contact) items.get(3)).name);
    }

    // Проверяет, что некорректные имена (null, пустые, с цифрами/символами) попадают в секцию "#"
    @Test
    public void groupsInvalidNamesIntoHashSection() {
        List<Contact> contacts = List.of(
                new Contact(1L, "", "111", null),
                new Contact(2L, "12345", "222", null),
                new Contact(3L, "@@@", "333", null),
                new Contact(4L, null, "444", null)
        );

        ContactRepository repository = new TestableContactRepository(contacts);
        List<ListItem> items = repository.getGroupedContacts(null);

        // Должна быть одна секция "#", и все 4 контакта под ней
        assertEquals(5, items.size());
        assertEquals("#", ((HeaderItem) items.get(0)).getTitle());
        for (int i = 1; i < 5; i++) {
            assertTrue(items.get(i) instanceof Contact);
        }
    }

    // Проверяет порядок следования секций: сначала кириллица, потом латиница
    @Test
    public void supportsLatinAndCyrillicLettersInSectionOrder() {
        List<Contact> contacts = List.of(
                new Contact(1L, "Антон", "111", null),
                new Contact(2L, "Zara", "222", null),
                new Contact(3L, "Борис", "333", null),
                new Contact(4L, "Alice", "444", null),
                new Contact(5L, "Ёжик", "555", null)
        );

        ContactRepository repository = new TestableContactRepository(contacts);
        List<ListItem> items = repository.getGroupedContacts(null);

        // Порядок секций: А, Б, Е (из Ё), A, Z
        assertEquals("А", ((HeaderItem) items.get(0)).getTitle());
        assertEquals("Б", ((HeaderItem) items.get(2)).getTitle());
        assertEquals("Е", ((HeaderItem) items.get(4)).getTitle());
        assertEquals("A", ((HeaderItem) items.get(6)).getTitle());
        assertEquals("Z", ((HeaderItem) items.get(8)).getTitle());
    }
    // Проверяет, что при пустом списке контактов возвращается пустой список ListItem
    @Test
    public void getGroupedContacts_returnsEmptyListIfNoContacts() {
        List<Contact> contacts = List.of(); // пустой список

        ContactRepository repository = new TestableContactRepository(contacts);
        List<ListItem> items = repository.getGroupedContacts(null);

        assertTrue(items.isEmpty()); // Ожидаем пустой список без заголовков и контактов
    }

}

