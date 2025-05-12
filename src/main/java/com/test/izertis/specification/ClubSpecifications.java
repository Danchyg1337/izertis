package com.test.izertis.specification;

import com.test.izertis.entity.Club;
import org.springframework.data.jpa.domain.Specification;

public class ClubSpecifications {
    public static Specification<Club> isPublic() {
        return (root, query, cb) -> cb.isTrue(root.get("isPublic"));
    }

    public static Specification<Club> byOfficialName(String officialName) {
        if (officialName == null || officialName.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("officialName"), officialName);
    }

    public static Specification<Club> byPopularName(String popularName) {
        if (popularName == null || popularName.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("popularName"), popularName);
    }

    public static Specification<Club> byFederation(String federation) {
        if (federation == null || federation.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("federation"), federation);
    }
}