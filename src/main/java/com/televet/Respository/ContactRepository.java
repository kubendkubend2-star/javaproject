package com.televet.Respository;

import com.televet.Models.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByContactTypeOrderByName(Contact.ContactType type);
    @Query("select c from Contact c where c.contactType = :type and " +
            "(lower(c.name) like lower(concat('%', :query, '%')) or " +
            "lower(coalesce(c.phone, '')) like lower(concat('%', :query, '%')) or " +
            "lower(coalesce(c.email, '')) like lower(concat('%', :query, '%')) or " +
            "lower(coalesce(c.district, '')) like lower(concat('%', :query, '%'))) order by c.name")
    List<Contact> searchByType(@Param("type") Contact.ContactType type, @Param("query") String query);
    boolean existsByContactCode(String contactCode);
    long countByContactType(Contact.ContactType type);
    long countByContactTypeAndStatus(Contact.ContactType type, String status);
}