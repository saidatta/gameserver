package org.softwarewolf.gameserver.base.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.base.domain.Campaign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class CampaignRepositoryCustomImpl implements CampaignRepositoryCustom {
	
    @Autowired
    @Qualifier("mongoTemplate")
    MongoTemplate mongoTemplate;
    
    public static final String IS_BLANK = "isBlank";
    public static final String OWNER_MAP_ATTRIBUTE = "ownerId";

    private static final Map<String, String> bigDecimalRoundedAttributes;

    static{
        bigDecimalRoundedAttributes = new HashMap<>();
//        bigDecimalRoundedAttributes.put(PAY_RATE_MAP_ATTRIBUTE, ROUNDED_PAY_RATE_MAP_ATTRIBUTE );
    }    
    
    public enum CriteriaType {
        IN,
        IS,
        NIN,
        GT,
        GTE,
        LT,
        LTE,
        EXISTS;
    }    

    private void generateCriteria(CriteriaType criteriaType, Criteria criteria, Object value) {

        List<?> values = null;

        if (value instanceof List) {
            values = (List<?>) value;
            if (values.contains(IS_BLANK)) {
                values.remove(IS_BLANK);
                values.add(null);
            }
        }

        switch (criteriaType) {
            case IN:
                criteria.in(values);
                break;
            case NIN:
                criteria.nin(values);
                break;
            case GT:
                criteria.gt(value);
                break;
            case LT:
                criteria.lt(value);
                break;
            case GTE:
                criteria.gte(value);
                break;
            case LTE:
                criteria.lte(value);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    @Override
    public List<Campaign> findByKeyMapValueList(Map<String, List<?>> filter) {
        return findByKeyMapValueList(filter, null, null);
    }

    @Override
    public List<Campaign> findByKeyMapValueList(Map<String, List<?>> includeFilter, Map<String, List<?>> excludeFilter) {
        return findByKeyMapValueList(includeFilter, excludeFilter, null);
    }

    @Override
    public List<Campaign> findByKeyMapValueList(Map<String, List<?>> includeFilter, Map<String, List<?>> excludeFilter, List<String> fields) {
        Query query = new Query();

        if (includeFilter != null) {
            for (Map.Entry<String, List<?>> entry : includeFilter.entrySet()) {
                // A field cannot be in both includeFilter and excludeFilter
                if (excludeFilter != null && excludeFilter.containsKey(entry.getKey())) {
                    throw new IllegalArgumentException("includeFilter and excludeFilter cannot contain the same field");
                }
                query.addCriteria(Criteria.where(entry.getKey()).in(entry.getValue()));
            }
        }

        if (excludeFilter != null) {
            for (Map.Entry<String, List<?>> entry : excludeFilter.entrySet()) {
                query.addCriteria(Criteria.where(entry.getKey()).nin(entry.getValue()));
            }
        }
        if (fields != null) {
            for (String field : fields) {
                query.fields().include(field);
            }
        }
        return mongoTemplate.find(query, Campaign.class);
    }

    /**
     * A filter request looks like this:
     *
     * "attributeName": [ { "criteriaType": "IN", "value": [___, ___] } ],
     *
     * "attributeName2": [ { "criteriaType": "NIN", "value": [___, ___] } ]
     *
     * @param filter
     * @return Filtered list of people
     */
    @Override
    public List<Campaign> list(Map<String, List<?>> filter) {
        Map<String, List<?>> basicAttributeFilter = new HashMap<>();
        Map<String, List<?>> ownerFilter = new HashMap<>();
        Map<String, List<?>> bigDecimalFilter = new HashMap<>();
        Query filterQuery = new Query();
        if (filter != null) {
            // Iterate through the attributes that are specified in the filter
            // Each attribute will have a list of corresponding criteria.
            for (Map.Entry<String, List<?>> entry : filter.entrySet()) {
                if (entry.getKey().matches(OWNER_MAP_ATTRIBUTE)) {
                    ownerFilter.put(entry.getKey(), entry.getValue());
                } else {
                    basicAttributeFilter.put(entry.getKey(), entry.getValue());
                }
            }

            filterByBasicAttributes(basicAttributeFilter, filterQuery); // Filter by basic first.
            filterByOwners(ownerFilter, filterQuery);
            // If MongoTemplate handled BigDecimals better this should really be done as a 
            // basic attribute. Unfortunately this is what we have today.
            filterByBigDecimal(bigDecimalFilter, filterQuery);
        }
//        logger.info(filterQuery.toString());
        return mongoTemplate.find(filterQuery, Campaign.class);
    }    
    
    /**
     * Basic attributes are Strings and Enum types, not nested objects, dates, or collections.
     *
     * @param filter
     * @return Result set of Campaign records matching basic attribute criteria.
     */
    private void filterByBasicAttributes(Map<String, List<?>> filter, Query query) {
        //Query query = new Query();

        for (Map.Entry<String, List<?>> entry : filter.entrySet()) {
            // Each attribute can only have 1 Criteria object associated with it. This is needed
            // for MongoDB.
            Criteria fullCriteria = new Criteria();

            for (Object object : entry.getValue()) {
                if (object != null) {
                    Map<String, Object> criteriaMap = (Map<String, Object>) object;
                    CriteriaType criteriaType = CriteriaType.valueOf((String) criteriaMap.get("criteriaType"));
                    Object value = criteriaMap.get("value");
                    Criteria subCriteria = Criteria.where(entry.getKey());
                    generateCriteria(criteriaType, subCriteria, value);
                    fullCriteria = subCriteria; //Assigning to fullCriteria for future use.
                }
            }

            query.addCriteria(fullCriteria);
        }
    }
    
    /**
     * @param ownerFilters
     * @param currentResultSet
     * @return Result set of Campaign records matching the given Owner criteria.
     */
    private void filterByOwners(Map<String, List<?>> ownerFilters, Query q) {
        for (Map.Entry<String, List<?>> entry : ownerFilters.entrySet()) {
            //Query q = new Query();

            Map<String, Object> criteriaMap;

            List<Criteria> cList = new ArrayList<>();
            for (Object object : entry.getValue()) {
                if (object != null) {
                    criteriaMap = (Map<String, Object>) object;
                    CriteriaType criteriaType = CriteriaType.valueOf((String) criteriaMap.get("criteriaType"));
                    List<?> values = (List<?>) criteriaMap.get("value");
                    for (Object value : values) {
                    	if (criteriaType == CriteriaType.IN) { // && !supervisorEmploymentID.equals(IS_BLANK)) {
                            cList.add(Criteria.where(OWNER_MAP_ATTRIBUTE + "." + (String) value).exists(true));
                        }
                    }

                    Criteria orOp = new Criteria();
                    Criteria[] cArr = cList.toArray(new Criteria[cList.size()]);
                    orOp = orOp.orOperator(cArr);
                    q.addCriteria(orOp);
                }
            }
        }
    }    
    
    /**
     * This is what happens when you beat on a problem with a large blunt object. Apparently without special handling
     * mongodb doesn't store data types in a collection, which means that when we filter by a numeric value it is
     * treated as a string so filtering by "1" vs "1.0" return different results (sad). So this is a rather brute-force
     * attempt to iterate over the list of Employment records that have been previously filtered, compare them using
     * filter criteria for BigDecimal filters and then remove records from the incoming list as appropriate.
     *
     * @param bdFilters - The list of known BigDecimal filters (the ones we care about here)
     * @param query - global filter query reference
     * @return none
     */
    private void filterByBigDecimal(Map<String, List<?>> bdFilters, Query query) {

        // Iterate over all of the Employment records sent in
        // for (Employment employment : currentResultSet) {
        // Iterate over each filter sent in
        for (Map.Entry<String, List<?>> entry : bdFilters.entrySet()) {
            // Empty filter map
            if (bdFilters.entrySet().size() == 1 && entry.getValue().isEmpty()) {
                return;
            }

            Criteria fullCriteria = new Criteria();

            //For now, considering payRate check, going forward, need to create map pair of every BigDecimal attribute depending on needs. Also, no need to check for which bigdecimal attributes as its been taken care implicitly by incoming filters argument.
            Criteria subCriteria = Criteria.where(getBigDecimalRoundedAttribute((String) entry.getKey()));
            // The Object that we're iterating over here is an Entry in the criteria map
            for (Object object : entry.getValue()) {
                if (object != null) {
                    Map<String, Object> criteriaMap = (Map<String, Object>) object;
                    CriteriaType criteriaType = CriteriaType.valueOf((String) criteriaMap.get("criteriaType"));
                    //String key = entry.getKey();
                    // criteriaMap.value could be either a String or a List of Strings
                    // in the List case, we are ORing results of the criteria rather
                    // than ANDing. Note the value might be "Is Not Blank" so we need to do a little gymnastics here
                    Object value;
                    try {
                        value = criteriaMap.get("value");
                        List<Object> values = null;
                        if (value instanceof List) {
                            values = (List<Object>) value;
                            for (Object val : values) {
                                if (!val.equals(IS_BLANK) && !val.equals("")) {
                                    values.remove(val);
                                    values.add(getLongValueByDefaultScaleFactor(new BigDecimal((String) val)));
                                }
                            }
                            value = values;
                        } else {
                            value = getLongValueByDefaultScaleFactor(new BigDecimal((String) value));
                        }
                    } catch (ClassCastException e) {
                        value = criteriaMap.get("value");
                    }

                    generateCriteria(criteriaType, subCriteria, value);
                    fullCriteria = subCriteria; //Assigning to fullCriteria for future use.
                }
            }
            query.addCriteria(fullCriteria);
        }
    }
    
    /**
     * @param Takes in nonRoundedAttribute name for BigDecimal declared as part of domain object
     * @return its respective roundedAttribute name.
     */
    public static String getBigDecimalRoundedAttribute(String nonRoundedAttribute){
        return bigDecimalRoundedAttributes.get(nonRoundedAttribute);
    }    
    
    /**
     * Converts given BigDecimal by default fixed scale factor.
     * @param   value       BigDecimal value to convert
     * @return          Long value
     */
    public static Long getLongValueByDefaultScaleFactor(BigDecimal value)  {
    	BigDecimal scaleFactor = new BigDecimal(1000000);
        return value.multiply(scaleFactor).longValue();
    }    
}
