package it.pleaseopen.authenticator.filterip;

import org.keycloak.models.GroupModel;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.models.utils.ModelToRepresentation;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GroupFilterRegexpMembershipMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper, TokenIntrospectionTokenMapper {

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<ProviderConfigProperty>();

    static {
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        ProviderConfigProperty property1 = new ProviderConfigProperty();
        property1.setName("full.path");
        property1.setLabel("Full group path");
        property1.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        property1.setDefaultValue("true");
        property1.setHelpText("Include full path to group i.e. /top/level1/level2, false will just specify the group name");
        ProviderConfigProperty property2 = new ProviderConfigProperty();
        property2.setName("regexp");
        property2.setLabel("Regexp for filtering on groups names");
        property2.setType(ProviderConfigProperty.STRING_TYPE);
        property2.setDefaultValue("");
        property2.setHelpText("A regexp to filter groups names");
        configProperties.add(property1);
        configProperties.add(property2);
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, GroupMembershipMapper.class);
    }

    public static final String PROVIDER_ID = "oidc-group-membership-regexp-mapper";


    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Group Membership with regexp";
    }

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getHelpText() {
        return "Map user group membership with regexp filtering";
    }

    public static boolean useFullPath(ProtocolMapperModel mappingModel) {
        return "true".equals(mappingModel.getConfig().get("full.path"));
    }


    /**
     * Adds the group membership information to the {@link IDToken#otherClaims}.
     * @param token
     * @param mappingModel
     * @param userSession
     */
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession) {
        Function<GroupModel, String> toGroupRepresentation = useFullPath(mappingModel) ?
                ModelToRepresentation::buildGroupPath : GroupModel::getName;
        //List<String> membership = userSession.getUser().getGroupsStream().map(toGroupRepresentation).collect(Collectors.toList());
        Pattern pattern = Pattern.compile(mappingModel.getConfig().get("regexp"));

        List<String> membership = userSession.getUser().getGroupsStream().map(toGroupRepresentation).collect(Collectors.toList()).stream()
                .filter(pattern.asPredicate())
                .collect(Collectors.toList());

        // force multivalued as the attribute is not defined for this mapper
        mappingModel.getConfig().put(ProtocolMapperUtils.MULTIVALUED, "true");
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, membership);
    }

    public static ProtocolMapperModel create(String name,
                                             String tokenClaimName,
                                             boolean consentRequired, String consentText,
                                             boolean accessToken, boolean idToken, boolean introspectionEndpoint) {
        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName(name);
        mapper.setProtocolMapper(PROVIDER_ID);
        mapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        Map<String, String> config = new HashMap<String, String>();
        config.put(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME, tokenClaimName);
        if (accessToken) config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ACCESS_TOKEN, "true");
        if (idToken) config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ID_TOKEN, "true");
        if (introspectionEndpoint) config.put(OIDCAttributeMapperHelper.INCLUDE_IN_INTROSPECTION, "true");
        mapper.setConfig(config);

        return mapper;
    }

}
