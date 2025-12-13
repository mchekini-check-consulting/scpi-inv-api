package fr.checkconsulting.scpiinvapi.repository;

import fr.checkconsulting.scpiinvapi.dto.request.ScpiSearchCriteriaDto;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import fr.checkconsulting.scpiinvapi.model.entity.Location;
import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import fr.checkconsulting.scpiinvapi.model.entity.Sector;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

interface ScpiRepositoryCustom {
    Page<Scpi> search(ScpiSearchCriteriaDto criteria, int page, int size);
}

@Repository
public class ScpiRepositoryImpl implements ScpiRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    public Page<Scpi> search(ScpiSearchCriteriaDto criteria, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Scpi> query = cb.createQuery(Scpi.class);
        Root<Scpi> root = query.from(Scpi.class);

        List<Predicate> predicates = buildPredicates(criteria, cb, root);
        query.select(root).distinct(true);
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(root.get("minimumSubscription")));

        TypedQuery<Scpi> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult(page * size);
        typedQuery.setMaxResults(size);
        List<Scpi> results = typedQuery.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Scpi> countRoot = countQuery.from(Scpi.class);
        List<Predicate> countPredicates = buildPredicates(criteria, cb, countRoot);
        countQuery.select(cb.countDistinct(countRoot))
                .where(countPredicates.toArray(new Predicate[0]));
        Long total = em.createQuery(countQuery).getSingleResult();

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(results, pageable, total);
    }

    private List<Predicate> buildPredicates(ScpiSearchCriteriaDto criteria, CriteriaBuilder cb, Root<Scpi> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (criteria.name() != null) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.name().toLowerCase() + "%"));
        }

        if (criteria.minimumSubscription() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("minimumSubscription"), criteria.minimumSubscription()));
        }

        if (criteria.yield() != null) {
            Subquery<Long> subquery = cb.createQuery().subquery(Long.class);
            Root<DistributionRate> subRoot = subquery.from(DistributionRate.class);
            subquery.select(subRoot.get("scpi").get("id"))
                    .where(
                            cb.and(
                                    cb.equal(subRoot.get("scpi"), root),
                                    cb.greaterThanOrEqualTo(subRoot.get("rate"), criteria.yield())
                            )
                    );
            predicates.add(cb.exists(subquery));
        }

        if (criteria.countries() != null && !criteria.countries().isEmpty()) {
            Join<Scpi, Location> locationJoin = root.join("locations", JoinType.LEFT);
            predicates.add(locationJoin.get("country").in(criteria.countries()));
        }

        if (criteria.sectors() != null && !criteria.sectors().isEmpty()) {
            Join<Scpi, Sector> sectorJoin = root.join("sectors", JoinType.LEFT);
            predicates.add(sectorJoin.get("name").in(criteria.sectors()));
        }

        if (criteria.rentFrequencies() != null && !criteria.rentFrequencies().isEmpty()) {
            predicates.add(root.get("rentFrequency").in(criteria.rentFrequencies()));
        }

        return predicates;
    }

}
