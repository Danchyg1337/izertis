package com.test.izertis.specification;

import com.test.izertis.entity.Player;
import org.springframework.data.jpa.domain.Specification;

public class PlayerSpecifications {
    public static Specification<Player> byClubId(Long clubId) {
        return (root, query, cb) -> cb.equal(root.get("club").get("id"), clubId);
    }

    public static Specification<Player> byGivenName(String givenName) {
        if (givenName == null || givenName.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("givenName"), givenName);
    }

    public static Specification<Player> byFamilyName(String familyName) {
        if (familyName == null || familyName.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("familyName"), familyName);
    }

    public static Specification<Player> byNationality(String nationality) {
        if (nationality == null || nationality.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("nationality"), nationality);
    }
}
