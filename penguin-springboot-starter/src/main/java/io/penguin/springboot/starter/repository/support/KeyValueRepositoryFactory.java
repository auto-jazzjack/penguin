package io.penguin.springboot.starter.repository.support;

import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class KeyValueRepositoryFactory extends RepositoryFactorySupport {
    private static final Class<SpelQueryCreator> DEFAULT_QUERY_CREATOR = SpelQueryCreator.class;
    private final KeyValueOperations keyValueOperations;
    private final MappingContext<?, ?> context;
    private final Class<? extends AbstractQueryCreator<?, ?>> queryCreator;
    private final Class<? extends RepositoryQuery> repositoryQueryType;

    public KeyValueRepositoryFactory(KeyValueOperations keyValueOperations) {
        this(keyValueOperations, DEFAULT_QUERY_CREATOR);
    }

    public KeyValueRepositoryFactory(KeyValueOperations keyValueOperations, Class<? extends AbstractQueryCreator<?, ?>> queryCreator) {
        this(keyValueOperations, queryCreator, KeyValuePartTreeQuery.class);
    }

    public KeyValueRepositoryFactory(KeyValueOperations keyValueOperations, Class<? extends AbstractQueryCreator<?, ?>> queryCreator, Class<? extends RepositoryQuery> repositoryQueryType) {
        Assert.notNull(keyValueOperations, "KeyValueOperations must not be null!");
        Assert.notNull(queryCreator, "Query creator type must not be null!");
        Assert.notNull(repositoryQueryType, "RepositoryQueryType type must not be null!");
        this.queryCreator = queryCreator;
        this.keyValueOperations = keyValueOperations;
        this.context = keyValueOperations.getMappingContext();
        this.repositoryQueryType = repositoryQueryType;
    }

    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        PersistentEntity<T, ?> entity = this.context.getRequiredPersistentEntity(domainClass);
        return new PersistentEntityInformation(entity);
    }

    protected Object getTargetRepository(RepositoryInformation repositoryInformation) {
        EntityInformation<?, ?> entityInformation = this.getEntityInformation(repositoryInformation.getDomainType());
        return super.getTargetRepositoryViaReflection(repositoryInformation, new Object[]{entityInformation, this.keyValueOperations});
    }

    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleKeyValueRepository.class;
    }

    protected RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
        return this.getRepositoryFragments(metadata, this.keyValueOperations);
    }

    protected RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata, KeyValueOperations operations) {
        if (isQueryDslRepository(metadata.getRepositoryInterface())) {
            if (metadata.isReactiveRepository()) {
                throw new InvalidDataAccessApiUsageException("Cannot combine Querydsl and reactive repository support in a single interface");
            } else {
                return RepositoryFragments.just(new Object[]{new QuerydslKeyValuePredicateExecutor(this.getEntityInformation(metadata.getDomainType()), this.getProjectionFactory(), operations, SimpleEntityPathResolver.INSTANCE)});
            }
        } else {
            return RepositoryFragments.empty();
        }
    }

    private static boolean isQueryDslRepository(Class<?> repositoryInterface) {
        return QuerydslUtils.QUERY_DSL_PRESENT && QuerydslPredicateExecutor.class.isAssignableFrom(repositoryInterface);
    }

    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return Optional.of(new KeyValueRepositoryFactory.KeyValueQueryLookupStrategy(key, evaluationContextProvider, this.keyValueOperations, this.queryCreator, this.repositoryQueryType));
    }

    private static class KeyValueQueryLookupStrategy implements QueryLookupStrategy {
        private final QueryMethodEvaluationContextProvider evaluationContextProvider;
        private final KeyValueOperations keyValueOperations;
        private final Class<? extends AbstractQueryCreator<?, ?>> queryCreator;
        private final Class<? extends RepositoryQuery> repositoryQueryType;

        public KeyValueQueryLookupStrategy(@Nullable Key key, QueryMethodEvaluationContextProvider evaluationContextProvider, KeyValueOperations keyValueOperations, Class<? extends AbstractQueryCreator<?, ?>> queryCreator, Class<? extends RepositoryQuery> repositoryQueryType) {
            Assert.notNull(evaluationContextProvider, "EvaluationContextProvider must not be null!");
            Assert.notNull(keyValueOperations, "KeyValueOperations must not be null!");
            Assert.notNull(queryCreator, "Query creator type must not be null!");
            Assert.notNull(repositoryQueryType, "RepositoryQueryType type must not be null!");
            this.evaluationContextProvider = evaluationContextProvider;
            this.keyValueOperations = keyValueOperations;
            this.queryCreator = queryCreator;
            this.repositoryQueryType = repositoryQueryType;
        }

        public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
            QueryMethod queryMethod = new QueryMethod(method, metadata, factory);
            Constructor<? extends KeyValuePartTreeQuery> constructor = ClassUtils.getConstructorIfAvailable(this.repositoryQueryType, new Class[]{QueryMethod.class, QueryMethodEvaluationContextProvider.class, KeyValueOperations.class, Class.class});
            Assert.state(constructor != null, String.format("Constructor %s(QueryMethod, EvaluationContextProvider, KeyValueOperations, Class) not available!", ClassUtils.getShortName(this.repositoryQueryType)));
            return (RepositoryQuery)BeanUtils.instantiateClass(constructor, new Object[]{queryMethod, this.evaluationContextProvider, this.keyValueOperations, this.queryCreator});
        }
    }
}