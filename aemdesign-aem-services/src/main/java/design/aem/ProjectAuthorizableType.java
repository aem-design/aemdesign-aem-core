package design.aem;

public enum ProjectAuthorizableType {
    OWNERS("role_owner"),
    APPROVERS("role_editor"),
    OBSERVERS("role_observer");

    private String propertyName;
    ProjectAuthorizableType(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public static ProjectAuthorizableType fromPropertyName(String propertyName) {
        for (ProjectAuthorizableType projectAuthorizableType : values()) {
            if (projectAuthorizableType.getPropertyName().equals(propertyName)) {
                return projectAuthorizableType;
            }
        }
        return null;
    }
}
