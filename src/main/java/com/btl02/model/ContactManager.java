package com.btl02.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ContactManager {
    private final List<Contact> contacts;

    public ContactManager() {
        contacts = new ArrayList<>();
    }

    public void loadFromXmlFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists())
                return;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Contact");
            contacts.clear();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String fullName = element.getElementsByTagName("FullName").item(0).getTextContent();
                    String phoneNumber = element.getElementsByTagName("PhoneNumber").item(0).getTextContent();
                    String additionalNumbers = element.getElementsByTagName("AdditionalNumbers").item(0)
                            .getTextContent();
                    String group = element.getElementsByTagName("Group").item(0).getTextContent();
                    String addedTimeStr = element.getElementsByTagName("AddedTime").item(0).getTextContent();
                    LocalDateTime addedTime = LocalDateTime.parse(addedTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                    contacts.add(new Contact(fullName, phoneNumber, additionalNumbers, group, addedTime));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Ghi dữ liệu vào file XML
    public void saveToXmlFile(String filePath) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("Contacts");
            doc.appendChild(rootElement);
            for (Contact contact : contacts) {
                Element contactElement = doc.createElement("Contact");
                Element fullName = doc.createElement("FullName");
                fullName.appendChild(doc.createTextNode(contact.getFullName()));
                contactElement.appendChild(fullName);
                Element phoneNumber = doc.createElement("PhoneNumber");
                phoneNumber.appendChild(doc.createTextNode(contact.getPhoneNumber()));
                contactElement.appendChild(phoneNumber);
                Element additionalNumbers = doc.createElement("AdditionalNumbers");
                additionalNumbers.appendChild(doc.createTextNode(contact.getAdditionalNumbers()));
                contactElement.appendChild(additionalNumbers);
                Element group = doc.createElement("Group");
                group.appendChild(doc.createTextNode(contact.getGroup()));
                contactElement.appendChild(group);
                Element addedTime = doc.createElement("AddedTime");
                addedTime.appendChild(doc.createTextNode(contact.getAddedTime().toString()));
                contactElement.appendChild(addedTime);
                rootElement.appendChild(contactElement);
            }
            // Ghi ra file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public boolean addContact(Contact contact) {
        if (checkPhoneNumberExists(contact.getPhoneNumber())) {
            return false; // Số điện thoại đã tồn tại
        }
        contacts.add(contact);
        saveToXmlFile("src/main/resources/contacts.xml");
        return true; // Thêm thành công
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        saveToXmlFile("src/main/resources/contacts.xml");

    }

    public void updateContact(Contact oldContact, Contact newContact) {
        int index = contacts.indexOf(oldContact);
        if (index >= 0) {
            contacts.set(index, newContact);
        }
        saveToXmlFile("src/main/resources/contacts.xml");

    }
    // Loại bỏ dấu trong chuỗi kí tưj phục vụ cho tìm kiếm
    private String removeAccents(String text) {
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{M}");
        return pattern.matcher(text).replaceAll("");
    }

    public List<Contact> searchContacts(String query) {
        if (query == null || query.isEmpty()) {
            return getAllContacts(); // Nếu không có từ khóa, trả về toàn bộ danh bạ
        }
    
        String lowerCaseQuery = removeAccents(query.toLowerCase());
    
        return contacts.stream()
                .filter(contact -> {
                    String nameWithoutAccents = removeAccents(contact.getFirstName().toLowerCase());
                    String phoneWithoutAccents = removeAccents(contact.getPhoneNumber().toLowerCase());
                    return nameWithoutAccents.contains(lowerCaseQuery) || phoneWithoutAccents.contains(lowerCaseQuery);
                })
                .collect(Collectors.toList());
    }
    

    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts); // Trả về một bản sao của danh sách
    }

    public boolean checkPhoneNumberExists(String phoneNumber) {
        return contacts.stream().anyMatch(contact -> contact.getPhoneNumber().equals(phoneNumber));
    }

    public void mergeGroups(String targetGroup, List<Contact> contactsToMerge) {
        for (Contact contact : contactsToMerge) {
            Contact updatedContact = new Contact(contact.getFullName(), contact.getPhoneNumber(),
                    contact.getAdditionalNumbers(), targetGroup, contact.getAddedTime());
            updateContact(contact, updatedContact);
        }
        saveToXmlFile("src/main/resources/contacts.xml");
    }

    // lấy tất cả các nhóm
    public List<String> getAllGroups() {
        return contacts.stream().map(Contact::getGroup).filter(group -> group != null && !group.isEmpty()).distinct()
                .collect(Collectors.toList());
    }

    // lấy liên hệ trong 1 nhóm cụ thể
    public List<Contact> getContactsInGroup(String group) {
        return contacts.stream().filter(contact -> group.equals(contact.getGroup())).collect(Collectors.toList());
    }
}
